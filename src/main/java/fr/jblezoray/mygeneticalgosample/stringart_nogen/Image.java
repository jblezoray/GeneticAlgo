package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public interface Image {

  byte[] getBytes();
  
  ImageSize getSize();
  

  default BufferedImage toBufferedImage() {
    ImageSize size = getSize();
    byte[] bytes = getBytes();
    BufferedImage result = new BufferedImage(
        size.w, size.h, BufferedImage.TYPE_BYTE_GRAY);
    result.getRaster().setDataElements(0, 0, size.w, size.h, bytes);
    return result;
  }
  
  default void writeToFile(File f) throws IOException {
    BufferedImage res = toBufferedImage();
    ImageIO.write(res, "png", f);
  }
}