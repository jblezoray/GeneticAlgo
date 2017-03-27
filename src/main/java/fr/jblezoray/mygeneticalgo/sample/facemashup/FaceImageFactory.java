package fr.jblezoray.mygeneticalgo.sample.facemashup;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.jblezoray.mygeneticalgo.DNA;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitableImage;

/**
 * Builds an Image from some DNA.
 */
public class FaceImageFactory {

  private static final float MIN_SCALE = 0.1f; // [0.0f, 1.0f]
  private static final float MAX_SCALE = 0.5f; // [0.0f, 1.0f]
  private static final float MIN_OPACITY = 0.5f; // [0.0f, 1.0f]

  private List<BufferedImage> allSizes;
  private int w;
  private int h;
  private int numberOfBases;
  
  public FaceImageFactory(FitableImage face, int numberOfBases) throws IOException {
    this.w = face.getImage().getWidth();
    this.h = face.getImage().getHeight();
    this.numberOfBases = numberOfBases;
    createAllSizes(face);
  }

  /**
   * Initializes allSizes by creating 'numberOfBases' images, whose ratio in 
   * size is between MIN_SCALE and MAX_SCALE, proportionally to the original
   * image.
   *
   * @param face
   * @throws IOException
   */
  private void createAllSizes(FitableImage face) throws IOException {
    this.allSizes = new ArrayList<>(this.numberOfBases);
    BufferedImage sourceImage = face.getImage();
    for (int n=0; n<this.numberOfBases; n++) {
      float scale = n / (float)this.numberOfBases;// in [0.0, 1.0] 
      scale = (scale * (MAX_SCALE - MIN_SCALE)) + MIN_SCALE;// in [MIN_SCALE, MAX_SCALE]
      
      int newWidth = Math.round(this.w * scale);
      int newHeight = Math.round(this.h * scale);
      BufferedImage resizedImg = new BufferedImage(newWidth, newHeight, BufferedImage.TRANSLUCENT);
      
      Graphics2D g = resizedImg.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(sourceImage, 0, 0, newWidth, newHeight, null);
      g.dispose();
      
      this.allSizes.add(resizedImg);
    }
  }

  /**
   * Builds an image from some DNA.
   * 
   * @param dna
   * @return
   */
  public FitableImage fromDNA(DNA dna) {
    // create a new blank image. 
    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics2D = null;
    try {
      graphics2D = image.createGraphics();
      graphics2D.setBackground(Color.WHITE);
      graphics2D.clearRect(0, 0, w, h);
      
      // read five genes to generate an image.
      int i=0;
      while (i+5<=dna.size()) {
        int positionGene1 = dna.get(i++);
        int positionGene2 = dna.get(i++);
        int sizeGene = dna.get(i++);
        int rotGene = dna.get(i++);
        int alphaGene = dna.get(i++);
        drawTransformedImageFromGenes(graphics2D, positionGene1, positionGene2, sizeGene,
            rotGene, alphaGene);
      }
    } finally {
      if (graphics2D!=null)
        graphics2D.dispose();
    }
    return new FitableImage(image, false);
  }
  

  /**
   * Draws imgToDraw on target using genes as generators.
   * 
   * All parameters range must be in [0, this.numberOfBases[
   * 
   * @param target Canvas to draw on. 
   * @param positionGene The 1st position gene.
   * @param positionGene The 2nd position gene.
   * @param sizeGene The size gene.
   * @param rotGene The rotation gene.
   * @param alphaGene The alpha gene.
   */
  private void drawTransformedImageFromGenes(Graphics2D target, int positionGene1, 
      int positionGene2, int sizeGene, int rotGene, int alphaGene) {
    // We must correlate X and Y values, otherwise the mutations on the position
    // affect either one or the other, which is undesirable as a mutation should
    // affect both for a nice shift of the image in the canvas.
    // 
    // The constraints are that we must have :
    // - a uniform distribution of the possibilities in the w*h space.  Doing 
    //   (positionGene1*positionGene2) % (w*h) is NOT valid. 
    // - a deterministic result. 
    // 
    // I ended seeding a PRNG, with very good results in terms of speed:
    Random random = new Random(positionGene1 * positionGene2);
    int x = random.nextInt(this.w);
    int y = random.nextInt(this.h);
    
    BufferedImage toDraw = allSizes.get(sizeGene);
    
    double angleRad = rotGene * (2.0f * Math.PI) / (float)this.numberOfBases;
    
    float opacity = alphaGene / ((float)this.numberOfBases); // in [0.0f, 1.0f]
    opacity = opacity * (1.0f - MIN_OPACITY) + MIN_OPACITY; // in [MIN_OPACITY, 1.0f]
    
    drawTransformedImage(target, toDraw, x, y, angleRad, opacity);
  }
  
  
  /**
   * Draws imgToDraw on target.
   * 
   * @param target Canvas to draw on. 
   * @param imgToDraw What to draw. 
   * @param x The x translation, range [0, this.w]. 
   * @param y The y translation, range [0, this.h].
   * @param angleRad, range [0, 2pi].
   * @param opacity, range [0.0f, 1.0f].
   */
  private static void drawTransformedImage(Graphics2D target, 
      BufferedImage imgToDraw, int x, int y, double angleRad, float opacity) {
    int imgW = imgToDraw.getWidth();
    int imgH = imgToDraw.getHeight();
    AffineTransform transformation = new AffineTransform();
    transformation.translate(x-imgW/2, y-imgH/2);
    transformation.rotate(angleRad, imgW/2, imgH/2);
    target.setComposite(
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
    target.drawImage(imgToDraw, transformation, null);
  }
}
