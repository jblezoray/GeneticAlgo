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

  public FaceImage(File f) throws IOException {
    this(ImageIO.read(f));
  }
  
  public FaceImage(BufferedImage source) {
    BufferedImage sourceABGR = new BufferedImage(source.getWidth(), source.getHeight(), 
        BufferedImage.TYPE_INT_ARGB);
    sourceABGR.getGraphics().drawImage(source, 0, 0, null);
    sourceABGR.getGraphics().dispose();
    this.image = sourceABGR;
  }
  
  public BufferedImage getImage() {
    return this.image;
  }
  
  public void writeToFile(File dest) throws IOException {
    ImageIO.write(image, "png", dest);
  }


}
