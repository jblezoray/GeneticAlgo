package fr.jblezoray.mygeneticalgo.dna.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import fr.jblezoray.mygeneticalgo.dna.AbstractDNA;

/**
 * Abstraction of a generated image, with a fitness computation feature.
 *
 *  Subclass hold all the parameters that enables the implementation of the 
 *  {@code buildImage()}.  For instance: nature and number of shapes, lines, 
 *  etc. Any update in those parameters <em>must</em> lead to a call to 
 *  {@code notifyImageUpdated()}, which will lead to a lazy rebuild of the 
 *  image. 
 * 
 * 
 * @author jib
 */
public abstract class AbstractImageDNA extends AbstractDNA {

  private BufferedImage image = null;
  private boolean imageMustBeRebuilt = false;

  /**
   * If you use this constructor, then you *must* initialize the Object with 
   * the {@code setImage()} method.
   */
  protected AbstractImageDNA() {}
  
  protected abstract BufferedImage buildImage();

  protected void notifyImageUpdated() {
    this.imageMustBeRebuilt = true;
  }
  
  public BufferedImage getImage() {
    if (this.imageMustBeRebuilt || this.image == null) {
      this.image = buildImage();
    }
    return this.image;
  }
  
  public void writeToFile(File dest) throws IOException {
    ImageIO.write(getImage(), "png", dest);
  }

}