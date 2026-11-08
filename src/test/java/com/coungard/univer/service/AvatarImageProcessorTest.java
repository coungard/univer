package com.coungard.univer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.coungard.univer.exception.MediaRejectedException;
import com.coungard.univer.service.AvatarImageProcessor.ProcessedAvatar;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class AvatarImageProcessorTest {

  private final AvatarImageProcessor processor = new AvatarImageProcessor();

  @Test
  void shouldProduceSquareJpegsOfBothSizes() throws IOException {
    ProcessedAvatar result = processor.process(TestImages.jpeg(1600, 1200));

    assertSquareJpeg(result.full(), AvatarImageProcessor.FULL_SIZE);
    assertSquareJpeg(result.thumbnail(), AvatarImageProcessor.THUMBNAIL_SIZE);
  }

  @Test
  void shouldCropToCenterSquare() throws IOException {
    // 300×100: по центру 100×100 из колонок 100..200 — левая половина квадрата красная, правая синяя
    BufferedImage full = read(processor.process(TestImages.jpeg(300, 100)).full());

    assertThat(isRed(full.getRGB(10, 256))).isTrue();
    assertThat(isBlue(full.getRGB(500, 256))).isTrue();
  }

  @Test
  void shouldUpscaleSmallImage() throws IOException {
    assertSquareJpeg(processor.process(TestImages.jpeg(40, 40)).full(), AvatarImageProcessor.FULL_SIZE);
  }

  @Test
  void shouldDecodeLargePhotoWithSubsampling() throws IOException {
    assertSquareJpeg(processor.process(TestImages.jpeg(4000, 3000)).full(), AvatarImageProcessor.FULL_SIZE);
  }

  @Test
  void shouldAcceptPngAndFillTransparencyWithWhite() throws IOException {
    BufferedImage transparent = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

    BufferedImage full = read(processor.process(TestImages.encode(transparent, "png")).full());

    Color pixel = new Color(full.getRGB(256, 256));
    assertThat(pixel.getRed()).isGreaterThan(245);
    assertThat(pixel.getGreen()).isGreaterThan(245);
    assertThat(pixel.getBlue()).isGreaterThan(245);
  }

  @Test
  void shouldAcceptWebp() throws IOException {
    assertSquareJpeg(processor.process(TestImages.WEBP_LOSSY).full(), AvatarImageProcessor.FULL_SIZE);
    assertSquareJpeg(processor.process(TestImages.WEBP_LOSSLESS).full(), AvatarImageProcessor.FULL_SIZE);
  }

  @Test
  void shouldApplyExifOrientation() throws IOException {
    // 200×100, слева красное, справа синее. Orientation=6 — повернуть на 90° по часовой: красное наверху
    byte[] photo = TestImages.jpegWithExif(TestImages.halves(200, 100), 6);

    BufferedImage full = read(processor.process(photo).full());

    assertThat(isRed(full.getRGB(256, 10))).isTrue();
    assertThat(isBlue(full.getRGB(256, 500))).isTrue();
  }

  @Test
  void shouldStripExifIncludingGps() throws Exception {
    byte[] photo = TestImages.jpegWithExif(TestImages.halves(200, 200), 1);
    assertThat(ImageMetadataReader.readMetadata(new ByteArrayInputStream(photo))
        .getFirstDirectoryOfType(ExifIFD0Directory.class)).isNotNull();

    ProcessedAvatar result = processor.process(photo);

    for (byte[] jpeg : new byte[][] {result.full(), result.thumbnail()}) {
      Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(jpeg));
      assertThat(metadata.getFirstDirectoryOfType(ExifIFD0Directory.class)).isNull();
      assertThat(new String(jpeg, StandardCharsets.ISO_8859_1)).doesNotContain("Exif");
    }
  }

  @Test
  void shouldMapPixelsForEveryOrientation() {
    // 3×2: пиксель (0,0) — красный; куда он попадает после каждого преобразования
    BufferedImage source = new BufferedImage(3, 2, BufferedImage.TYPE_INT_RGB);
    source.setRGB(0, 0, Color.RED.getRGB());
    int[][] expected = {
        {0, 0}, {2, 0}, {2, 1}, {0, 1},   // 1–4: 3×2
        {0, 0}, {1, 0}, {1, 2}, {0, 2}    // 5–8: 2×3
    };
    for (int orientation = 1; orientation <= 8; orientation++) {
      BufferedImage result = AvatarImageProcessor.applyOrientation(source, orientation);
      int[] xy = expected[orientation - 1];
      assertThat(result.getWidth()).as("orientation %d", orientation).isEqualTo(orientation >= 5 ? 2 : 3);
      assertThat(result.getRGB(xy[0], xy[1]) & 0xFFFFFF).as("orientation %d", orientation).isEqualTo(0xFF0000);
    }
  }

  @Test
  void shouldRejectNonImages() {
    assertRejected(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "это не картинка".getBytes(StandardCharsets.UTF_8));
    assertRejected(HttpStatus.UNSUPPORTED_MEDIA_TYPE, new byte[0]);
    // Сигнатура JPEG без содержимого
    assertRejected(HttpStatus.UNSUPPORTED_MEDIA_TYPE, new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0, 1, 2});
    // Поддерживаемое ImageIO, но не принимаемое нами
    assertRejected(HttpStatus.UNSUPPORTED_MEDIA_TYPE, TestImages.encode(TestImages.halves(10, 10), "gif"));
    assertRejected(HttpStatus.UNSUPPORTED_MEDIA_TYPE, TestImages.encode(TestImages.halves(10, 10), "bmp"));
  }

  @Test
  void shouldRejectTooLargeFileOrDimensions() {
    assertRejected(HttpStatus.PAYLOAD_TOO_LARGE, new byte[(int) AvatarImageProcessor.MAX_FILE_BYTES + 1]);
    // Маленький по байтам PNG, но с огромной шириной — отказ по заголовку, без декодирования
    BufferedImage wide = new BufferedImage(AvatarImageProcessor.MAX_SOURCE_SIDE + 1, 1, BufferedImage.TYPE_BYTE_BINARY);
    assertRejected(HttpStatus.PAYLOAD_TOO_LARGE, TestImages.encode(wide, "png"));
  }

  private void assertRejected(HttpStatus status, byte[] data) {
    assertThatThrownBy(() -> processor.process(data))
        .isInstanceOfSatisfying(MediaRejectedException.class, e -> assertThat(e.getStatus()).isEqualTo(status));
  }

  private static void assertSquareJpeg(byte[] data, int size) throws IOException {
    assertThat(data[0]).isEqualTo((byte) 0xFF);
    assertThat(data[1]).isEqualTo((byte) 0xD8);
    BufferedImage image = read(data);
    assertThat(image.getWidth()).isEqualTo(size);
    assertThat(image.getHeight()).isEqualTo(size);
  }

  private static BufferedImage read(byte[] data) throws IOException {
    return ImageIO.read(new ByteArrayInputStream(data));
  }

  private static boolean isRed(int rgb) {
    Color c = new Color(rgb);
    return c.getRed() > 200 && c.getGreen() < 60 && c.getBlue() < 60;
  }

  private static boolean isBlue(int rgb) {
    Color c = new Color(rgb);
    return c.getBlue() > 200 && c.getRed() < 60 && c.getGreen() < 60;
  }
}
