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

import fr.jblezoray.mygeneticalgo.DNA;

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
  
  public FaceImageFactory(FaceImage face, int numberOfBases) throws IOException {
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
  private void createAllSizes(FaceImage face) throws IOException {
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
  public FaceImage fromDNA(DNA dna) {
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
        int xGene = dna.get(i++);
        int yGene = dna.get(i++);
        int sizeGene = dna.get(i++);
        int rotGene = dna.get(i++);
        int alphaGene = dna.get(i++);
        drawTransformedImage(graphics2D, xGene, yGene, sizeGene, rotGene, 
            alphaGene);
      }
    } finally {
      if (graphics2D!=null)
        graphics2D.dispose();
    }
    return new FaceImage(image, false);
  }
  

  /**
   * Draws imgToDraw on target using genes as generators.
   * 
   * All parameters range must be in [0, this.numberOfBases[
   * 
   * @param target Canvas to draw on. 
   * @param xGene The x translation gene.
   * @param yGene The y translation gene.
   * @param sizeGene The size gene.
   * @param rotGene The rotation gene.
   * @param alphaGene The alpha gene.
   */
  private void drawTransformedImage(Graphics2D target, 
      int xGene, int yGene, int sizeGene, int rotGene, int alphaGene) {
    int x = xGene * this.w / this.numberOfBases;
    int y = yGene * this.h / this.numberOfBases;
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
