package fr.jblezoray.mygeneticalgo.sample.stringart.edge;

import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeDrawer;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.CompressedByteImage;

public class Edge {

  private final int nailA;
  private final boolean nailAClockwise;
  private final int nailB;
  private final boolean nailBClockwise;
  private final EdgeDrawer edgeDrawer;
  
  /**
   * This value is lazily constructed. 
   */
  private CompressedByteImage imageData;
  

  public Edge(int nailA, boolean nailAClockwise, 
      int nailB, boolean nailBClockwise, 
      EdgeDrawer edgeDrawer) {
    if (nailA == nailB) 
      throw new RuntimeException("cannot draw an edge if the two nails are identical");
    this.nailA = nailA;
    this.nailAClockwise = nailAClockwise;
    this.nailB = nailB;
    this.nailBClockwise = nailBClockwise;
    this.edgeDrawer = edgeDrawer;
  }
  
  public int getNailA() {
    return nailA;
  }
  
  public int getNailB() {
    return nailB;
  }

  public boolean isNailAClockwise() {
    return nailAClockwise;
  }
  
  public boolean isNailBClockwise() {
    return nailBClockwise;
  }
  
  public CompressedByteImage getDrawnEdgeData() {
    // lazy initialization.
    if (imageData==null) {
      synchronized (this) {
        if (imageData==null) {
          this.imageData = this.edgeDrawer.drawEdge(this);
        }
      }
    }
      
    return imageData;
  }

  public boolean contains(int nail, boolean clockwise) {
    return (this.nailA==nail && this.nailAClockwise == clockwise) 
        || (this.nailB==nail && this.nailBClockwise == clockwise);
  }
  
}