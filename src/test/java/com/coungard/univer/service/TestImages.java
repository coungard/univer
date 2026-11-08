package com.coungard.univer.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import javax.imageio.ImageIO;

/** Картинки для тестов аватаров (issue #137). */
public final class TestImages {

  /** WebP 1×1 с потерями — стандартный образец для проверки поддержки WebP браузером. */
  public static final byte[] WEBP_LOSSY =
      Base64.getDecoder().decode("UklGRiQAAABXRUJQVlA4IBgAAAAwAQCdASoBAAEAAwA0JaQAA3AA/vuUAAA=");

  /** WebP 1×1 без потерь. */
  public static final byte[] WEBP_LOSSLESS =
      Base64.getDecoder().decode("UklGRhoAAABXRUJQVlA4TA0AAAAvAAAAEAcQERGIiP4HAA==");

  private TestImages() {
  }

  /** Левая половина красная, правая — синяя. */
  public static BufferedImage halves(int width, int height) {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    g.setColor(Color.RED);
    g.fillRect(0, 0, width / 2, height);
    g.setColor(Color.BLUE);
    g.fillRect(width / 2, 0, width - width / 2, height);
    g.dispose();
    return image;
  }

  public static byte[] encode(BufferedImage image, String format) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      if (!ImageIO.write(image, format, out)) {
        throw new IllegalStateException("Нет ImageIO writer для " + format);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return out.toByteArray();
  }

  public static byte[] jpeg(int width, int height) {
    return encode(halves(width, height), "jpeg");
  }

  /**
   * JPEG с EXIF: тег Orientation и GPS-указатель — как у фото с телефона. APP1 вставляется сразу за JFIF APP0.
   */
  public static byte[] jpegWithExif(BufferedImage image, int orientation) {
    byte[] jpeg = encode(image, "jpeg");
    // SOI (2 байта) + APP0: маркер (2) + длина (включает свои 2 байта)
    int app0Length = ((jpeg[4] & 0xFF) << 8) | (jpeg[5] & 0xFF);
    int insertAt = 4 + app0Length;

    byte[] tiff = {
        'M', 'M', 0, 42, 0, 0, 0, 8,           // big-endian TIFF, IFD0 по смещению 8
        0, 2,                                   // две записи
        0x01, 0x12, 0, 3, 0, 0, 0, 1, 0, (byte) orientation, 0, 0,   // Orientation, SHORT
        (byte) 0x88, 0x25, 0, 4, 0, 0, 0, 1, 0, 0, 0, 0,            // GPSInfo IFD pointer, LONG
        0, 0, 0, 0                              // следующего IFD нет
    };
    byte[] header = {'E', 'x', 'i', 'f', 0, 0};
    int segmentLength = 2 + header.length + tiff.length;

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    out.write(jpeg, 0, insertAt);
    out.write(0xFF);
    out.write(0xE1);
    out.write(segmentLength >> 8);
    out.write(segmentLength & 0xFF);
    out.writeBytes(header);
    out.writeBytes(tiff);
    out.write(jpeg, insertAt, jpeg.length - insertAt);
    return out.toByteArray();
  }
}
