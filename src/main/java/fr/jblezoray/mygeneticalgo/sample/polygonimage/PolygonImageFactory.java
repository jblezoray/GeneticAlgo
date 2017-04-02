package fr.jblezoray.mygeneticalgo.sample.polygonimage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import fr.jblezoray.mygeneticalgo.DNA;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitableImage;

public class PolygonImageFactory {
  
  public static final int GENES_PER_IMAGE = 17; 
  
  /**
   * Maximum size of the square in which the polygon is included.
   */
  private final int maxPolygonSize;
  
  /**
   * Minimum size of the square in which the polygon is included.
   */
  private final int minPolygonSize;
  
  private int w;
  private int h;
  private int numberOfBases;

  
  public PolygonImageFactory(int w, int h, int numberOfBases) {
    this.maxPolygonSize = w / 8;
    this.minPolygonSize = w / 12;
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
        int point1DX = dna.get(i++);
        int point1DY = dna.get(i++);
        int point2DX = dna.get(i++);
        int point2DY = dna.get(i++);
        int point3DX = dna.get(i++);
        int point3DY = dna.get(i++);
        int point4DX = dna.get(i++);
        int point4DY = dna.get(i++);
        int point5DX = dna.get(i++);
        int point5DY = dna.get(i++);
        int red = dna.get(i++);
        int green = dna.get(i++);
        int blue = dna.get(i++);
        int alpha = dna.get(i++);
        int scale = dna.get(i++);
        drawTransformedImageFromGenes(graphics2D, originX, originY, 
            point1DX, point1DY, point2DX, point2DY, point3DX, point3DY, 
            point4DX, point4DY, point5DX, point5DY, 
            red, green, blue, alpha, scale);
      }
    } finally {
      if (graphics2D!=null)
        graphics2D.dispose();
    }
    return new FitableImage(image, false);
  }

  private void drawTransformedImageFromGenes(
      Graphics2D graphics2d, 
      int originX, int originY, int point1dx, int point1dy,  
      int point2dx, int point2dy, int point3dx, int point3dy, 
      int point4dx, int point4dy, int point5dx, int point5dy, 
      int red, int green, int blue, int alpha, int scale) {

    int[] correlated = DNA.correlate(originX, originY, this.w, this.h);
    int x = correlated[0];
    int y = correlated[1];
    
    Polygon p = new Polygon();
    p.addPoint(scale(x, point1dx), scale(y, point1dy));
    p.addPoint(scale(x, point2dx), scale(y, point2dy));
    p.addPoint(scale(x, point3dx), scale(y, point3dy));
    p.addPoint(scale(x, point4dx), scale(y, point4dy));
    p.addPoint(scale(x, point5dx), scale(y, point5dy));
    graphics2d.setColor(new Color(red%0xFF, green%0xFF, blue%0xFF, alpha%0xFF));
    graphics2d.fillPolygon(p);
  }

  private int scale(int origin, int d) {
    int normalized = DNA.normalizeInteger(d, 0, this.numberOfBases, 
        (d < this.numberOfBases /2) ? this.minPolygonSize 
          : this.maxPolygonSize - this.minPolygonSize, 
        this.maxPolygonSize);
    return origin - (this.maxPolygonSize/2) + normalized;
  }
  
}
