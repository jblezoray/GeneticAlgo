package fr.jblezoray.mygeneticalgo.sample.stringart_nogen.edge;

public class Edge {

  private final int pinA;
  private final boolean pinAClockwise;
  private final int pinB;
  private final boolean pinBClockwise;
  private final EdgeFactory factory;
  
  /**
   * This value is lazily constructed. 
   */
  private byte[] compressedDrawnEdgeData;
  

  Edge(int pinA, boolean pinAClockwise, 
      int pinB, boolean pinBClockwise, 
      EdgeFactory factory) {
    if (pinA == pinB) 
      throw new RuntimeException("cannot draw an edge if the two pins are identical");
    this.pinA = pinA;
    this.pinAClockwise = pinAClockwise;
    this.pinB = pinB;
    this.pinBClockwise = pinBClockwise;
    this.factory = factory;
  }
  
  public int getPinA() {
    return pinA;
  }
  
  public int getPinB() {
    return pinB;
  }

  public boolean isPinAClockwise() {
    return pinAClockwise;
  }
  
  public boolean isPinBClockwise() {
    return pinBClockwise;
  }
  
  public byte[] getCompressedDrawnEdgeData() {
    // lazy initialization.
    if (compressedDrawnEdgeData==null) {
      synchronized (this) {
        if (compressedDrawnEdgeData==null) {
          byte[] drawnEdge = this.factory.getDrawnEdge(
              pinA, pinAClockwise, pinB, pinBClockwise);
          this.compressedDrawnEdgeData = this.factory.compressDrawnEdgeData(
              drawnEdge); 
        }
      }
    }
      
    return compressedDrawnEdgeData;
  }

  public boolean contains(int pin, boolean clockwise) {
    return (this.pinA==pin && this.pinAClockwise == clockwise) 
        || (this.pinB==pin && this.pinBClockwise == clockwise);
  }
  
}