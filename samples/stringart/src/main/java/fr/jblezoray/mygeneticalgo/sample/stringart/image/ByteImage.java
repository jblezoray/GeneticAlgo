package fr.jblezoray.mygeneticalgo.sample.stringart.image;

import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ByteImage implements Image {

  private final byte[] bytes;
  private final ImageSize size;
  
  public ByteImage(ImageSize size, byte[] bytes) {
    this.size = size;
    this.bytes = bytes;
  }

  public ByteImage deepCopy() {
    return new ByteImage(this.size, this.bytes.clone());
  }
  
  public byte[] getRawBytes() {
    return bytes;
  }

  @Override
  public ByteImage asByteImage() {
    return this;
  }
  
  @Override
  public ImageSize getSize() {
    return this.size;
  }
}
