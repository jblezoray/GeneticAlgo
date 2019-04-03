package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;

public class Edge {
  private final int pinA;
  private final int pinB;
  private final int w;
  private final int h;
  private final int totalNumberOfNails;
  private byte[] compressedDrawnEdgeData;
  private float lineThickness;
  
  public Edge(int pinA, int pinB, int w, int h, int totalNumberOfNails, 
      float lineThickness) {
    if (pinA == pinB) 
      throw new RuntimeException("cannot draw an edge if the two pins are identical");
    this.pinA = pinA;
    this.pinB = pinB;
    this.w = w;
    this.h = h;
    this.totalNumberOfNails = totalNumberOfNails;
    this.lineThickness = lineThickness;
  }
  
  public int getPinA() {
    return pinA;
  }

  
  public int getPinB() {
    return pinB;
  }
  
  
  /**
   * Rendering of the image.
   * 
   * This method is slow.  
   *   
   * @return
   */
  byte[] getDrawnEdge() {
    BufferedImage image = new BufferedImage(
        this.w, this.h, BufferedImage.TYPE_BYTE_GRAY);
    
    Graphics2D graphics2D = null;
    try {
      // create a new blank image. 
      graphics2D = image.createGraphics();
      graphics2D.setBackground(Color.WHITE);
      graphics2D.clearRect(0, 0, this.w, this.h);
      
      // draw line 
      graphics2D.setColor(Color.BLACK);
      graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


      graphics2D.setStroke(new BasicStroke(this.lineThickness));
      graphics2D.drawLine(
          xNail2Position(pinA), yNail2Position(pinA), 
          xNail2Position(pinB), yNail2Position(pinB));

      // draw nails.
      graphics2D.setColor(new Color(0,0,0,255));
      int nailRadius = 2;
      for (int i=0; i<this.totalNumberOfNails; i++) {
        int x = xNail2Position(i); 
        int y = yNail2Position(i); 
        graphics2D.fillOval(x-nailRadius/2, y-nailRadius/2, nailRadius, nailRadius);
      }
      
    } finally {
      if (graphics2D!=null) graphics2D.dispose();
    }

    byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    return pixels;
  }
  
  
  /**
   * returns a x position in the image for a nail index. 
   * @param nailIndex
   * @return
   */
  private int xNail2Position(int nailIndex) {
    double sinX = Math.sin(nailIndex*2*Math.PI/this.totalNumberOfNails);
    return (int)(sinX*(this.w/2) + (this.w/2));
  }


  /**
   * returns a y position in the image for a nail index. 
   * @param nailIndex
   * @return
   */
  private int yNail2Position(int nailIndex) {
    double cosY = Math.cos(nailIndex*2*Math.PI/this.totalNumberOfNails);
    return (int)(-cosY*(this.h/2) + (this.h/2));
  }
  
  
  /**
   * The format returned will be an array of bytes having each odd byte being a
   * numerator that indicates how many times the next byte must appear.
   * 
   * For instance: <code>03 AE 01 04 02 FF</code> encodes the byte array  
   * <code>AE AE AE 04 FF FF</code>.  This method is efficient for compressing 
   * arrays iif the byte array has a lots of repeating elements.   
   *    
   * @return a compressed array.
   */
  static byte[] compressDrawnEdgeData(byte[] drawnEdgeImage) {
    if (drawnEdgeImage.length<=0) 
      throw new RuntimeException("invalid image: size is 0");
    
    short prevPixel = (short)Byte.toUnsignedInt(drawnEdgeImage[0]);
    short counter = 1;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (int i=1; i<drawnEdgeImage.length; i++) {
      short pixel = (short)Byte.toUnsignedInt(drawnEdgeImage[i]);
      if (pixel==prevPixel && counter<0xFF) {
        counter++;
      } else {
        baos.write(counter);
        baos.write(prevPixel);
        prevPixel = pixel;
        counter = 1;
      }
    }
    baos.write(counter);
    baos.write(prevPixel);
    byte[] bytes = baos.toByteArray();
    return bytes; 
  }
  
  /**
   * Take image 'img', and draw this edge in the image. 
   * 
   * When run on an edge for the first time, this method has an overhead due to 
   * the initial computation of the image.  
   * 
   * @param img original image (will not be modified) 
   * @return a copy of the original image, including the edge.   
   */
  public Image drawEdgeInImage(Image img) {
    // lazily initialize compressDrawnEdgeData 
    if (compressedDrawnEdgeData==null) {
      synchronized(this) {
        if (compressedDrawnEdgeData==null) {
          byte[] drawnEdgeImage = getDrawnEdge();
          this.compressedDrawnEdgeData = compressDrawnEdgeData(drawnEdgeImage);
        }
      }
    }
    
    // draw edge in image.
    byte[] bytes = img.getBytes().clone();
    int bytesIndex = 0;
    for (int i=0; i<this.compressedDrawnEdgeData.length; i+=2) {
      int howManyPixel = Byte.toUnsignedInt(this.compressedDrawnEdgeData[i]);
      int pixel = Byte.toUnsignedInt(this.compressedDrawnEdgeData[i+1]);
      if (pixel!=0xFF) { // 0xFF is the identity transformation
        for (int j=bytesIndex; j<bytesIndex+(howManyPixel); j++) {
          int pixelImage = Byte.toUnsignedInt(bytes[j]);
          bytes[j] = (byte)Math.max(0x00, pixelImage + pixel - 0xFF);
        }
      }
      bytesIndex += howManyPixel;
    }
    
    return new Image(this.w, this.h, bytes);
  }
  
  
}