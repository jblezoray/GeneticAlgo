package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Image abstraction for the {@code FaceMashupGenerator}.
 * 
 * @author jib
 */
public class FaceImage {

  private final BufferedImage image;

  public FaceImage(File f, boolean hasAlpha) throws IOException {
    this(ImageIO.read(f), hasAlpha);
  }

  
  public FaceImage(BufferedImage source, boolean hasAlpha) {
    
    int type = hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR;
    
    if (source.getType() == type) {
      this.image = source;
    } else {
      int srcW = source.getWidth();
      int srcH = source.getHeight();
      BufferedImage sourceConverted = new BufferedImage(srcW, srcH, type);
      sourceConverted.getGraphics().drawImage(source, 0, 0, null);
      sourceConverted.getGraphics().dispose();
      this.image = sourceConverted;
    }
  }
  
  public BufferedImage getImage() {
    return this.image;
  }
  
  public void writeToFile(File dest) throws IOException {
    ImageIO.write(image, "png", dest);
  }

}