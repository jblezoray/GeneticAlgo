package fr.jblezoray.mygeneticalgo.sample.polygonimage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.jblezoray.mygeneticalgo.DNA;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitableImage;

public class PolygonImageFactory {
  
  public static final int GENES_PER_IMAGE = 7;

  private static final float MIN_RADIUS_RATIO = 0.01f; 
  private static final float MAX_RADIUS_RATIO = 0.10f; 
  
  /**
   * Minimum size of the circle in which the polygon is included.
   */
  private final int minRadius;
  
  /**
   * Maximum size of the circle in which the polygon is included.
   */
  private final int maxRadius;
  
  private int w;
  private int h;
  private int numberOfBases;

  
  public PolygonImageFactory(int w, int h, int numberOfBases) {
    this.minRadius = (int)(w * MIN_RADIUS_RATIO) * 2;
    this.maxRadius = (int)(w * MAX_RADIUS_RATIO) * 2;
    this.w = w;
    this.h = h;
    this.numberOfBases = numberOfBases;
  }
  
  public FitableImage fromDNA(DNA dna) {
    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics2D = null;
    try {
      graphics2D = image.createGraphics();
      
      // bg color consumes 3 bases. 
      int i=0;
      int bgRed = dna.get(i++);
      int bgGreen = dna.get(i++);
      int bgBlue = dna.get(i++);
      graphics2D.setBackground(new Color(bgRed%0xFF, bgGreen%0xFF, bgBlue%0xFF));
      
      graphics2D.clearRect(0, 0, w, h);
      
      // read genes to generate a polygon.
      while (i+GENES_PER_IMAGE<=dna.size()) {
        int originX = dna.get(i++);
        int originY = dna.get(i++);
        int radius = dna.get(i++);
        int red = dna.get(i++);
        int green = dna.get(i++);
        int blue = dna.get(i++);
        int alpha = dna.get(i++);
        drawTransformedImageFromGenes(graphics2D, originX, originY, 
            radius, red, green, blue, alpha);
      }
    } finally {
      if (graphics2D!=null)
        graphics2D.dispose();
    }
    return new FitableImage(image, false);

  }

  private void drawTransformedImageFromGenes(Graphics2D graphics2d, 
      int originX, int originY, int radius, 
      int red, int green, int blue, int alpha) {
  
    int[] correlated = DNA.correlate(originX, originY, this.w, this.h);
    int x = correlated[0];
    int y = correlated[1];
    int radiusPx = DNA.normalizeInteger(radius, 0, this.numberOfBases, 
        this.minRadius, this.maxRadius);
  
    graphics2d.setColor(new Color(red%0xFF, green%0xFF, blue%0xFF, alpha%0xFF));
    graphics2d.fillOval(x-radiusPx/2, y-radiusPx/2, radiusPx, radiusPx);
  }
}
