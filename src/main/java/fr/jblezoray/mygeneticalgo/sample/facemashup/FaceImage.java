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
    BufferedImage sourceARGB = new BufferedImage(
        source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
    sourceARGB.getGraphics().drawImage(source, 0, 0, null);
    sourceARGB.getGraphics().dispose();
    this.image = sourceARGB;
  }
  
  public BufferedImage getImage() {
    return this.image;
  }
  
  public void writeToFile(File dest) throws IOException {
    ImageIO.write(image, "png", dest);
  }

  public double computeFitnessOf(FaceImage constructed) {
//  BufferedImage phenotype = phenotypeObject.createPhenotype(DNA);
//  double similarity = calculateImageSimilarity(phenotype, source);
//  return ((similarity - similarityMin) / (similarityMax - similarityMin))*100;
    return 0.0;
  }


}
