package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Image {
  private final byte[] bytes;
  private final ImageSize size;
  
  public static class ImageSize {
    final int w, h, nbPixels;
    ImageSize(int w, int h) {
      this.w = w;
      this.h = h;
      this.nbPixels = w*h;
    }
    @Override
    public boolean equals(Object o) {
      return o instanceof ImageSize && this.nbPixels==((ImageSize)o).nbPixels;
    }
  }
  
  public Image(ImageSize size) {
    this.size = size;
    this.bytes = new byte[this.size.nbPixels];
    Arrays.fill(this.bytes, (byte)0xFF);
  }
  
  public Image(ImageSize size, byte[] bytes) {
    this.size = size;
    this.bytes = bytes;
  }
  
  public Image(BufferedImage image) {
    this.size = new ImageSize(image.getWidth(), image.getHeight());
    // convert it if it's not of the expected type.  
    if (image.getType() != BufferedImage.TYPE_BYTE_GRAY) {
      BufferedImage convertedImg = new BufferedImage(
          this.size.w, this.size.h, BufferedImage.TYPE_BYTE_GRAY);
      convertedImg.getGraphics().drawImage(image, 0, 0, null);
      image = convertedImg;
    }
    this.bytes = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
  }

  public Image deepCopy() {
    return new Image(this.size, this.bytes.clone());
  }
  
  public byte[] getBytes() {
    return bytes;
  }

  public ImageSize getSize() {
    return this.size;
  }

  public BufferedImage toBufferedImage() {
    BufferedImage result = new BufferedImage(
        this.size.w, this.size.h, BufferedImage.TYPE_BYTE_GRAY);
    result.getRaster().setDataElements(0, 0, this.size.w, this.size.h, bytes);
    return result;
  }

  public void writeToFile(File f) throws IOException {
    BufferedImage res = toBufferedImage();
    ImageIO.write(res, "png", f);
  }

}