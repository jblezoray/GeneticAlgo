package fr.jblezoray.mygeneticalgo.sample.stringart.genetic;

import fr.jblezoray.mygeneticalgo.IDNAFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeFactory;

public class EdgeListDNAFactory implements IDNAFactory<EdgeListDNA> {

  private final EdgeFactory edgeFactory;
  private final int nbEdgesInStringPath;
  private final int minNailDiff;
  private final int nbNails;
  private final boolean edgeWayEnabled;
  private final boolean defaultEdgeWay;

  public EdgeListDNAFactory(EdgeFactory edgeFactory, int nbEdgesInStringPath,
      int minNailDiff, int nbNails, boolean edgeWayEnabled, 
      boolean defaultEdgeWay) {
    this.edgeFactory = edgeFactory;
    this.nbEdgesInStringPath = nbEdgesInStringPath;
    this.minNailDiff = minNailDiff;
    this.nbNails = nbNails;
    this.edgeWayEnabled = edgeWayEnabled;
    this.defaultEdgeWay = defaultEdgeWay;
  }
  
  
  
  @Override
  public EdgeListDNA createRandomIndividual() {
    EdgeListDNA dna = new EdgeListDNA(this.edgeFactory, this.minNailDiff, 
        this.nbNails, this.edgeWayEnabled, this.defaultEdgeWay);
    for (int i=0; i<this.nbEdgesInStringPath; i++) {
      dna.addEdge(i);
    }
    return dna;
  }

}
