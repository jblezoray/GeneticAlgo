package fr.jblezoray.mygeneticalgo.sample.stringart_nogen.edge;

import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.core.EdgeDrawer;

public class Edge {

  private final int nailA;
  private final boolean nailAClockwise;
  private final int nailB;
  private final boolean nailBClockwise;
  private final EdgeDrawer edgeDrawer;
  
  /**
   * This value is lazily constructed. 
   */
  private byte[] compressedDrawnEdgeData;
  

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
  
  public byte[] getCompressedDrawnEdgeData() {
    // lazy initialization.
    if (compressedDrawnEdgeData==null) {
      synchronized (this) {
        if (compressedDrawnEdgeData==null) {
          byte[] drawnEdge = this.edgeDrawer.getDrawnEdge(
              nailA, nailAClockwise, nailB, nailBClockwise);
          this.compressedDrawnEdgeData = this.edgeDrawer.compressDrawnEdgeData(
              drawnEdge); 
        }
      }
    }
      
    return compressedDrawnEdgeData;
  }

  public boolean contains(int nail, boolean clockwise) {
    return (this.nailA==nail && this.nailAClockwise == clockwise) 
        || (this.nailB==nail && this.nailBClockwise == clockwise);
  }
  
}