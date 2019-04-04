package fr.jblezoray.mygeneticalgosample.stringart_nogen.edge;

public class Edge {
  
  private final int pinA;
  private final int pinB;
  private final EdgeFactory factory;
  
  /**
   * This value is lazily constructed. 
   */
  private byte[] compressedDrawnEdgeData;
  

  Edge(int pinA, int pinB, EdgeFactory factory) {
    if (pinA == pinB) 
      throw new RuntimeException("cannot draw an edge if the two pins are identical");
    this.pinA = pinA;
    this.pinB = pinB;
    this.factory = factory;
  }
  
  public int getPinA() {
    return pinA;
  }
  
  public int getPinB() {
    return pinB;
  }

  public byte[] getCompressedDrawnEdgeData() {
    // lazy initialization.
    if (compressedDrawnEdgeData==null) {
      synchronized (this) {
        if (compressedDrawnEdgeData==null) {
          byte[] drawnEdge = this.factory.getDrawnEdge(pinA, pinB);
          this.compressedDrawnEdgeData = this.factory.compressDrawnEdgeData(
              drawnEdge); 
        }
      }
    }
      
    return compressedDrawnEdgeData;
  }
  
}