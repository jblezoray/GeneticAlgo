package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Image {
  private final byte[] bytes;
  private final int w, h;
  
  public Image(int w, int h) {
    this.w = w;
    this.h = h;
    this.bytes = new byte[w*h];
    Arrays.fill(this.bytes, (byte)0xFF);
  }
  
  public Image(int w, int h, byte[] bytes) {
    this.w = w;
    this.h = h;
    this.bytes = bytes;
  }
  
  public Image(BufferedImage image) {
    this.w = image.getWidth();
    this.h = image.getHeight();
    // convert it if it's not of the expected type.  
    if (image.getType() != BufferedImage.TYPE_BYTE_GRAY) {
      BufferedImage convertedImg = new BufferedImage(
          this.w, this.h, BufferedImage.TYPE_BYTE_GRAY);
      convertedImg.getGraphics().drawImage(image, 0, 0, null);
      image = convertedImg;
    }
    this.bytes = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
  }

  public Image deepCopy() {
    return new Image(this.w, this.h, this.bytes.clone());
  }
  
  
  public byte[] getBytes() {
    return bytes;
  }

  public int getH() {
    return this.h;
  }

  public int getW() {
    return this.w;
  }

  public BufferedImage toBufferedImage() {
    BufferedImage result = new BufferedImage(this.w, this.h, BufferedImage.TYPE_BYTE_GRAY);
    result.getRaster().setDataElements(0, 0, this.w, this.h, bytes);
    return result;
  }

  public void writeToFile(File f) throws IOException {
    BufferedImage res = toBufferedImage();
    ImageIO.write(res, "png", f);
  }

}