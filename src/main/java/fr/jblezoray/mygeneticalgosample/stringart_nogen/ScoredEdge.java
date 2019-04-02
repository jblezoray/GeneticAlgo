package fr.jblezoray.mygeneticalgosample.stringart_nogen;

class ScoredEdge  {
  private final Edge edge;
  private final double norm;
  
  public ScoredEdge(Edge edge, double norm) {
    super();
    this.edge = edge;
    this.norm = norm;
  }
  
  public Edge getEdge() {
    return edge;
  }
  
  public double getNorm() {
    return norm;
  }
  
}
