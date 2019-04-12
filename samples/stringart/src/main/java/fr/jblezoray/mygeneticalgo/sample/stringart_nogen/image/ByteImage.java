package fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class ByteImage implements Image {

  private final byte[] bytes;
  private final ImageSize size;
  
  public ByteImage(ImageSize size) {
    this.size = size;
    this.bytes = new byte[this.size.nbPixels];
    Arrays.fill(this.bytes, (byte)0xFF);
  }
  
  public ByteImage(ImageSize size, byte[] bytes) {
    this.size = size;
    this.bytes = bytes;
  }

  public ByteImage(String imagePath) throws IOException {
    this(new File(imagePath));
  }
  
  public ByteImage(File imageFile) throws IOException {
    this(ImageIO.read(imageFile));
  }
  
  public ByteImage(BufferedImage image) {
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

  public ByteImage deepCopy() {
    return new ByteImage(this.size, this.bytes.clone());
  }
  
  @Override
  public ByteImage asByteImage() {
    return this;
  }
  
  public byte[] getBytes() {
    return bytes;
  }
  
  @Override
  public ImageSize getSize() {
    return this.size;
  }


}
