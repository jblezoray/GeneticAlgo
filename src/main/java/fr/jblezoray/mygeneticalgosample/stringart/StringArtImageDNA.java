package fr.jblezoray.mygeneticalgosample.stringart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

import fr.jblezoray.mygeneticalgo.dna.IDNA;
import fr.jblezoray.mygeneticalgo.dna.image.AbstractImageDNA;
import fr.jblezoray.mygeneticalgo.dna.integer.DNAInteger;

public class StringArtImageDNA extends AbstractImageDNA {
  
  private final DNAInteger dna;
  private final int nbHorizontalNails;
  private final int nbVerticalNails;
  private final int totalNumberOfNails;
  private final int width;
  private final int height;
  private final Color stringColor;
  private final Color backgroundColor;
  
  /**
   * build a random image.
   * @param nbStrings
   * @param nbNails
   * @param nbHorizontalNails
   * @param nbVerticalNails
   * @param width
   * @param height
   */
  public StringArtImageDNA(
      int nbStrings, 
      int nbNails, 
      int nbHorizontalNails, int nbVerticalNails, 
      int width, int height,
      Color backgroundColor,
      Color stringColor) {
    this.dna = new DNAInteger(nbStrings+1, 0, nbNails-1);
    this.nbHorizontalNails = nbHorizontalNails;
    this.nbVerticalNails = nbVerticalNails;
    this.totalNumberOfNails = nbNails;
    this.width = width;
    this.height = height;
    this.backgroundColor = backgroundColor;
    this.stringColor = stringColor;
    
  }
  
  public StringArtImageDNA(StringArtImageDNA toCopy) {
    this.dna = toCopy.dna.copy();
    this.nbHorizontalNails = toCopy.nbHorizontalNails;
    this.nbVerticalNails = toCopy.nbVerticalNails;
    this.totalNumberOfNails = toCopy.totalNumberOfNails;
    this.width = toCopy.width;
    this.height = toCopy.height;
    this.stringColor = toCopy.stringColor;
    this.backgroundColor = toCopy.backgroundColor;
  }

  
  /**
   * Create the image.
   * 
   * @return the generated image.
   */
  @Override
  protected BufferedImage buildImage() {
    BufferedImage image = new BufferedImage(
        this.width, this.height, BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics2D = null;
    try {

      // create a new blank image. 
      graphics2D = image.createGraphics();
      graphics2D.setBackground(this.backgroundColor);
      graphics2D.clearRect(0, 0, this.width, this.height);

      // draw nails.
      graphics2D.setColor(new Color(0,0,0,255));
      int nailRadius = 6;
      for (int i=0; i<this.totalNumberOfNails; i++) {
        int x = xNail2Position(getXNailIndex(i)); 
        int y = yNail2Position(getYNailIndex(i)); 
        graphics2D.fillOval(x-nailRadius/2, y-nailRadius/2, nailRadius, nailRadius);
      }
      
      // draw lines. 
      List<Integer> nailIndexes = dna.getList();
      int dnaLength = nailIndexes.size();
      int[] xPoints = new int[dnaLength];
      int[] yPoints = new int[dnaLength];
      for (int i=0; i<dnaLength; i++) {
        xPoints[i] = xNail2Position(getXNailIndex(nailIndexes.get(i)));
        yPoints[i] = yNail2Position(getYNailIndex(nailIndexes.get(i)));
      }
      graphics2D.setColor(this.stringColor);
      graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      if (this.stringColor.getAlpha() == 0xFF) {
        // this is fast, but does not render transparency. 
        graphics2D.drawPolyline(xPoints, yPoints, dnaLength);
      } else { 
        // slow but renders transparency. 
        for (int i=1; i<dnaLength; i++) {
          graphics2D.drawLine(xPoints[i-1], yPoints[i-1], xPoints[i], yPoints[i]);
        }
      }
      
    } finally {
      if (graphics2D!=null) graphics2D.dispose();
    }
    return image;
  }
  
  private int getXNailIndex(int nailIndex) {
    if (nailIndex < nbHorizontalNails) {
      return nailIndex; // top side. 
          
    } else if (nailIndex < nbHorizontalNails + nbVerticalNails){
      return nbHorizontalNails; // right side

    } else if (nailIndex < nbHorizontalNails*2 + nbVerticalNails){
      return nbHorizontalNails*2 + nbVerticalNails - nailIndex; // bottom side
      
    } else { 
      return 0; // left side.
    }
  }
  
  private int getYNailIndex(int nailIndex) {
    if (nailIndex<nbHorizontalNails) {
      return 0; // top side. 
          
    } else if (nailIndex < nbHorizontalNails + nbVerticalNails){
      return nailIndex - nbHorizontalNails;// right side

    } else if (nailIndex < nbHorizontalNails*2 + nbVerticalNails){
      return nbVerticalNails; // bottom side
      
    } else { 
      return nbVerticalNails*2 + nbHorizontalNails*2 - nailIndex; // left side.
    }
  }

  private int xNail2Position(int xNailIndex) {
    return (int)((float)(this.width * xNailIndex) / (float)(this.nbHorizontalNails));
  }

  private int yNail2Position(int yNailIndex) {
    return (int)((float)(this.height * yNailIndex) / (float)(this.nbVerticalNails));
  }

  
  /**
   * Mutate AND add an element per generation.
   * @param mutationRates
   */
  @Override
  public void doMutate(float mutationRates) {
    this.dna.doMutate(mutationRates);
    this.dna.addRandomElement();
  }

  @Override
  public <X extends IDNA> void doDNACrossover(X other, int minCrossovers, int maxCrossovers) {
    this.dna.doDNACrossover(((StringArtImageDNA)other).dna, minCrossovers, maxCrossovers);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <X extends IDNA> X copy() {
    return (X) new StringArtImageDNA(this);
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer("[");
    for (Integer dnaBase : this.dna.getList()) {
      String dnaBaseStr = String.format("%2d", dnaBase);
      sb = sb.append(dnaBaseStr).append(" ");
    }
    sb = sb.deleteCharAt(sb.length()-1).append("]");
    return sb.toString();
  }
  
}
