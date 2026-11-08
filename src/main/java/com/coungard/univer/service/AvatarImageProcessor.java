package com.coungard.univer.service;

import com.coungard.univer.exception.MediaRejectedException;
import com.coungard.univer.exception.TooManyRequestsException;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Превращает загруженное фото в аватар (issue #137): квадратные JPEG 512×512 и 128×128 без метаданных.
 * <ul>
 *   <li>Формат определяется по содержимому (сигнатуре), а не по {@code Content-Type} или расширению: JPEG, PNG,
 *   WebP. HEIC не поддерживается — клиент перекодирует его в JPEG на устройстве (MOBILE.md).</li>
 *   <li>Размеры проверяются по заголовку до декодирования, а большие фото декодируются сразу уменьшенными
 *   (subsampling) — в памяти не оказывается полноразмерный bitmap 12-мегапиксельного снимка.</li>
 *   <li>EXIF {@code Orientation} применяется к пикселям: ImageIO его игнорирует, без этого фото с телефона
 *   получались бы повёрнутыми. Остальные метаданные (включая геолокацию) при перекодировании отбрасываются.</li>
 *   <li>Прозрачность заливается белым, не квадрат обрезается по центру.</li>
 * </ul>
 * Одновременно обрабатываются не больше {@value #MAX_CONCURRENT} фото — обработка ест CPU и heap.
 */
@Slf4j
@Component
public class AvatarImageProcessor {

  public static final long MAX_FILE_BYTES = 5L * 1024 * 1024;
  public static final int MAX_SOURCE_SIDE = 8000;
  public static final int FULL_SIZE = 512;
  public static final int THUMBNAIL_SIZE = 128;
  public static final String CONTENT_TYPE = "image/jpeg";

  static final int MAX_CONCURRENT = 2;

  /** Большие фото декодируются с прореживанием, но так, чтобы меньшая сторона оставалась не меньше этой. */
  private static final int DECODE_MIN_SIDE = 2 * FULL_SIZE;
  private static final float JPEG_QUALITY = 0.85f;
  private static final Set<String> FORMATS = Set.of("jpeg", "png", "webp");
  private static final Duration WAIT_FOR_SLOT = Duration.ofSeconds(10);

  private final Semaphore slots = new Semaphore(MAX_CONCURRENT);

  public AvatarImageProcessor() {
    // В исполняемом jar Spring Boot плагины TwelveMonkeys (WebP, JPEG) находятся только при сканировании с
    // загрузчиком классов приложения — ImageIO мог инициализироваться раньше с другим
    ImageIO.scanForPlugins();
    ImageIO.setUseCache(false);
  }

  public record ProcessedAvatar(byte[] full, byte[] thumbnail) {
  }

  /**
   * @throws MediaRejectedException   {@code 413}/{@code 415} — файл не принят
   * @throws TooManyRequestsException все слоты обработки заняты дольше {@link #WAIT_FOR_SLOT}
   */
  public ProcessedAvatar process(byte[] data) {
    if (data.length > MAX_FILE_BYTES) {
      throw MediaRejectedException.tooLarge("Файл больше 5 МБ");
    }
    acquireSlot();
    try {
      BufferedImage image = toRgb(decode(data));
      image = square(applyOrientation(image, readOrientation(data)));
      return new ProcessedAvatar(
          encodeJpeg(resize(image, FULL_SIZE)),
          encodeJpeg(resize(image, THUMBNAIL_SIZE)));
    } finally {
      slots.release();
    }
  }

  private void acquireSlot() {
    try {
      if (!slots.tryAcquire(WAIT_FOR_SLOT.toMillis(), TimeUnit.MILLISECONDS)) {
        throw new TooManyRequestsException("Сервер обрабатывает другие фото, повторите позже", Duration.ofSeconds(5));
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new TooManyRequestsException("Сервер обрабатывает другие фото, повторите позже", Duration.ofSeconds(5));
    }
  }

  private static BufferedImage decode(byte[] data) {
    try (ImageInputStream in = ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
      Iterator<ImageReader> readers = in == null ? null : ImageIO.getImageReaders(in);
      if (readers == null || !readers.hasNext()) {
        throw unsupported();
      }
      ImageReader reader = readers.next();
      try {
        if (!FORMATS.contains(reader.getFormatName().toLowerCase(Locale.ROOT))) {
          throw unsupported();
        }
        reader.setInput(in, true, true);
        int width = reader.getWidth(0);
        int height = reader.getHeight(0);
        if (width > MAX_SOURCE_SIDE || height > MAX_SOURCE_SIDE) {
          throw MediaRejectedException.tooLarge(
              "Изображение больше " + MAX_SOURCE_SIDE + "×" + MAX_SOURCE_SIDE + " пикселей");
        }
        if (width < 1 || height < 1) {
          throw unsupported();
        }
        ImageReadParam param = reader.getDefaultReadParam();
        int step = Math.max(1, Math.min(width, height) / DECODE_MIN_SIDE);
        if (step > 1) {
          param.setSourceSubsampling(step, step, 0, 0);
        }
        return reader.read(0, param);
      } finally {
        reader.dispose();
      }
    } catch (MediaRejectedException e) {
      throw e;
    } catch (IOException | RuntimeException e) {
      // Битый или обрезанный файл, сигнатура формата без валидного содержимого
      log.info("Не удалось декодировать загруженное изображение: {}", e.toString());
      throw unsupported();
    }
  }

  private static MediaRejectedException unsupported() {
    return MediaRejectedException.unsupported("Файл не является изображением JPEG, PNG или WebP");
  }

  /** Значение EXIF Orientation (1–8); 1 — если тега нет или метаданные не читаются. */
  private static int readOrientation(byte[] data) {
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(data), data.length);
      ExifIFD0Directory exif = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
      if (exif != null && exif.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
        int orientation = exif.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        return orientation >= 1 && orientation <= 8 ? orientation : 1;
      }
    } catch (Exception e) {
      log.debug("EXIF не прочитан: {}", e.toString());
    }
    return 1;
  }

  /** Непрозрачный RGB: прозрачные области — белые (JPEG прозрачности не поддерживает). */
  private static BufferedImage toRgb(BufferedImage source) {
    BufferedImage rgb = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g = rgb.createGraphics();
    try {
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
      g.drawImage(source, 0, 0, null);
    } finally {
      g.dispose();
    }
    return rgb;
  }

  /**
   * Поворот и отражение пикселей по EXIF Orientation: куда переходит пиксель исходника {@code (x, y)}.
   */
  static BufferedImage applyOrientation(BufferedImage source, int orientation) {
    if (orientation == 1) {
      return source;
    }
    int w = source.getWidth();
    int h = source.getHeight();
    boolean swap = orientation >= 5;
    BufferedImage result = new BufferedImage(swap ? h : w, swap ? w : h, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int rgb = source.getRGB(x, y);
        switch (orientation) {
          case 2 -> result.setRGB(w - 1 - x, y, rgb);              // отражение по горизонтали
          case 3 -> result.setRGB(w - 1 - x, h - 1 - y, rgb);      // поворот на 180°
          case 4 -> result.setRGB(x, h - 1 - y, rgb);              // отражение по вертикали
          case 5 -> result.setRGB(y, x, rgb);                      // транспонирование
          case 6 -> result.setRGB(h - 1 - y, x, rgb);              // поворот на 90° по часовой
          case 7 -> result.setRGB(h - 1 - y, w - 1 - x, rgb);      // антитранспонирование
          case 8 -> result.setRGB(y, w - 1 - x, rgb);              // поворот на 90° против часовой
          default -> throw new IllegalArgumentException("Orientation " + orientation);
        }
      }
    }
    return result;
  }

  /** Центральный квадрат по меньшей стороне. */
  private static BufferedImage square(BufferedImage image) {
    int side = Math.min(image.getWidth(), image.getHeight());
    return image.getSubimage((image.getWidth() - side) / 2, (image.getHeight() - side) / 2, side, side);
  }

  /**
   * Квадрат {@code size}×{@code size}. Уменьшение — последовательными половинками, затем бикубически: одна
   * бикубическая интерполяция с большим коэффициентом даёт «лесенки» и муар.
   */
  private static BufferedImage resize(BufferedImage image, int size) {
    BufferedImage current = image;
    while (current.getWidth() / 2 >= size) {
      current = draw(current, current.getWidth() / 2, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
    return current.getWidth() == size ? current : draw(current, size, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
  }

  private static BufferedImage draw(BufferedImage source, int size, Object interpolation) {
    BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = result.createGraphics();
    try {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.drawImage(source, 0, 0, size, size, null);
    } finally {
      g.dispose();
    }
    return result;
  }

  /** JPEG без метаданных: пишется только пиксельный растр, EXIF исходника сюда не попадает. */
  private static byte[] encodeJpeg(BufferedImage image) {
    ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (MemoryCacheImageOutputStream stream = new MemoryCacheImageOutputStream(out)) {
      writer.setOutput(stream);
      ImageWriteParam param = writer.getDefaultWriteParam();
      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      param.setCompressionQuality(JPEG_QUALITY);
      writer.write(null, new IIOImage(image, null, null), param);
    } catch (IOException e) {
      throw new UncheckedIOException("Не удалось закодировать JPEG", e);
    } finally {
      writer.dispose();
    }
    return out.toByteArray();
  }
}
