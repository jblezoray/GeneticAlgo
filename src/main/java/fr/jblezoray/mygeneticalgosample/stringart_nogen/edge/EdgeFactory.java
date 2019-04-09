package fr.jblezoray.mygeneticalgosample.stringart_nogen.edge;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.jblezoray.mygeneticalgosample.stringart_nogen.image.ImageSize;
import fr.jblezoray.mygeneticalgosample.stringart_nogen.image.UnboundedImage;

public class EdgeFactory {

  private final ImageSize size;
  private final int totalNumberOfNails;
  private final float lineThickness;
  private final int pinPxRadiusInt;
  private final int minNailDiff;
  private final List<Edge> allPossibleEdges;

  
  public EdgeFactory(ImageSize size, int totalNumberOfNails, 
      float lineThickness, float pinDiameterInPx) {
    this.size = size;
    this.totalNumberOfNails = totalNumberOfNails; 
    this.lineThickness = lineThickness;
    this.pinPxRadiusInt = Math.max(1, (int)pinDiameterInPx);
    this.minNailDiff = Math.max(1, (int)totalNumberOfNails/4);
    
    this.allPossibleEdges = new ArrayList<>();
    for (int i=0; i<totalNumberOfNails; i++) {
      for (int j=i+minNailDiff; j<totalNumberOfNails-minNailDiff; j++) {
        // one for each possible connection between two nails.
        allPossibleEdges.add(new Edge(i, true,  j, true,  this));
        allPossibleEdges.add(new Edge(i, false, j, true,  this));
        allPossibleEdges.add(new Edge(i, true,  j, false, this));
        allPossibleEdges.add(new Edge(i, false, j, false, this));
      }
    }
  }
  

  public Collection<Edge> getAllPossibleEdges() {
    return allPossibleEdges;
  }
  
  
  /**
   * Draws all the pins in the image. 
   * 
   * The implementation is obviously suboptimal, but we don't care as it's meant
   * to be run only once.
   * 
   * @param original
   */
  public void drawAllPins(UnboundedImage original) {

    BufferedImage image = new BufferedImage(
        this.size.w, this.size.h, BufferedImage.TYPE_BYTE_GRAY);
    
    Graphics2D graphics2D = null;
    try {
      // create a new blank image. 
      graphics2D = image.createGraphics();
      graphics2D.setBackground(Color.WHITE);
      graphics2D.clearRect(0, 0, this.size.w, this.size.h);
      
      // draw all pins
      graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics2D.setColor(Color.BLACK);
      for (int i=0; i<this.totalNumberOfNails; i++) {
        int x = xNail2Position(i); 
        int y = yNail2Position(i); 
        graphics2D.fillOval(
            x-pinPxRadiusInt/2, y-pinPxRadiusInt/2, 
            pinPxRadiusInt, pinPxRadiusInt);
      }
      
    } finally {
      if (graphics2D!=null) graphics2D.dispose();
    }
    byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    
    // add all the drawn pins in the original image. 
    int[] bytes = original.getUnboundedBytes();
    for (int i=0; i<bytes.length; i++) {
      bytes[i] += Byte.toUnsignedInt(pixels[i]) - 0xFF;
    }
  }
  
  /**
   * returns a x position in the image for a nail index. 
   * @param nailIndex
   * @return
   */
  private int xNail2Position(int nailIndex) {
    double sinX = Math.sin(nailIndex*2*Math.PI/this.totalNumberOfNails);
    return (int)(sinX*(this.size.w/2) + (this.size.w/2));
  }


  /**
   * returns a y position in the image for a nail index. 
   * @param nailIndex
   * @return
   */
  private int yNail2Position(int nailIndex) {
    double cosY = Math.cos(nailIndex*2*Math.PI/this.totalNumberOfNails);
    return (int)(-cosY*(this.size.h/2) + (this.size.h/2));
  }
  
  
  /**
   * Take image 'img', and draw this edge in the image. 
   * 
   * When run on an edge for the first time, this method has an overhead due to 
   * the initial computation of the image.  
   * 
   * @param img image to be modified 
   * @param edge edge to draw.
   * @return the img object. 
   */
  public UnboundedImage drawEdgeInImage(UnboundedImage img, Edge edge) {
    int[] unboundedBytes = img.getUnboundedBytes();
    int bytesIndex = 0;
    byte[] data = edge.getCompressedDrawnEdgeData();
    for (int i=0; i<data.length; i+=2) {
      
      int howManyPixel = Byte.toUnsignedInt(data[i]);
      int pixel = Byte.toUnsignedInt(data[i+1]);
      
      // 0xFF is the identity transformation
      if (pixel!=0xFF) {
        for (int j=bytesIndex; j<bytesIndex+(howManyPixel); j++) {
          unboundedBytes[j] += pixel - 0xFF;
        }
      }
      bytesIndex += howManyPixel;
    }
    return img;
  }
  

  /**
   * Remove an edge from an image.
   * 
   * @param img image to be modified,  with the edge removed. 
   * @param edge edge to remove. 
   */
  public void undrawEdgeInImage(UnboundedImage img, Edge edge) {
    int[] unboundedBytes = img.getUnboundedBytes();
    int bytesIndex = 0;
    byte[] data = edge.getCompressedDrawnEdgeData();
    for (int i=0; i<data.length; i+=2) {
      
      int howManyPixel = Byte.toUnsignedInt(data[i]);
      int pixel = Byte.toUnsignedInt(data[i+1]);
      
      // 0xFF is the identity transformation
      if (pixel!=0xFF) {
        for (int j=bytesIndex; j<bytesIndex+(howManyPixel); j++) {
          unboundedBytes[j] -= pixel - 0xFF;
        }
      }
      bytesIndex += howManyPixel;
    }
  }
  
  
  /**
   * Rendering of the image.
   * 
   * This method is slow.  
   *   
   * @param pinA
   * @param pinAClockwise
   * @param pinB
   * @param pinBClockwise
   * @return
   */
  byte[] getDrawnEdge(int pinA, boolean pinAClockwise, 
      int pinB, boolean pinBClockwise) {
    BufferedImage image = new BufferedImage(
        this.size.w, this.size.h, BufferedImage.TYPE_BYTE_GRAY);
    
    Graphics2D graphics2D = null;
    try {
      // create a new blank image. 
      graphics2D = image.createGraphics();
      graphics2D.setBackground(Color.WHITE);
      graphics2D.clearRect(0, 0, this.size.w, this.size.h);
      
      // draw line 
      graphics2D.setColor(Color.BLACK);
      graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics2D.setStroke(new BasicStroke(this.lineThickness));
      graphics2D.drawLine(
          xNail2ThreadPosition(pinA, pinAClockwise), 
          yNail2ThreadPosition(pinA, pinAClockwise), 
          xNail2ThreadPosition(pinB, pinBClockwise), 
          yNail2ThreadPosition(pinB, pinBClockwise));
      
    } finally {
      if (graphics2D!=null) graphics2D.dispose();
    }

    byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    return pixels;
  }
  
  
  /**
   * returns a x position in the image for a string just after its revolution 
   * around a nail.
   *  
   * @param nailIndex the nail.
   * @param clockwise turn clockwise or anticlockwise.
   * @return
   */
  private int xNail2ThreadPosition(int nailIndex, boolean clockwise) {
    double imgCenter = this.size.w / 2.0d;
    double pinDeviation = 2.0d*Math.PI*nailIndex/this.totalNumberOfNails;
    double wayDeviation = (double)this.pinPxRadiusInt/this.size.w;
    double way = clockwise ? -1.0d : 1.0d;
    return (int)((1 + Math.sin(pinDeviation + way * wayDeviation)) * imgCenter);
  }


  /**
   * returns a y position in the image for a string just after its revolution 
   * around a nail.
   *  
   * @param nailIndex the nail.
   * @param clockwise turn clockwise or anticlockwise.
   * @return
   */
  private int yNail2ThreadPosition(int nailIndex, boolean clockwise) {
    double imgCenter = this.size.h / 2.0d;
    double pinDeviation = 2.0d*Math.PI*nailIndex/this.totalNumberOfNails;
    double wayDeviation = (double)this.pinPxRadiusInt/this.size.h;
    double way = clockwise ? -1.0d : 1.0d;
    return (int)((1 - Math.cos(pinDeviation + way * wayDeviation)) * imgCenter);
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
  byte[] compressDrawnEdgeData(byte[] drawnEdgeImage) {
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
  
}
