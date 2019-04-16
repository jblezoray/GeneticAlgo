package fr.jblezoray.mygeneticalgo.sample.stringart_nogen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.edge.Edge;

/**
 * The EdgeFactory maintains a collection of all the possible edges. 
 */
public class EdgeFactory {
  
  private final List<Edge> allPossibleEdges;
  
  public EdgeFactory(
      int minNailDiff, 
      int totalNumberOfNails, 
      EdgeDrawer drawer) {
    this.allPossibleEdges = new ArrayList<>();
    for (int i=0; i<totalNumberOfNails; i++) {
      for (int j=i; j<totalNumberOfNails; j++) {
        if (Math.abs(j-i) > minNailDiff) {
          // one for each possible connection between two nails.
          allPossibleEdges.add(new Edge(i, true,  j, true,  drawer));
          allPossibleEdges.add(new Edge(i, false, j, true,  drawer));
          allPossibleEdges.add(new Edge(i, true,  j, false, drawer));
          allPossibleEdges.add(new Edge(i, false, j, false, drawer));
        }
      }
    }
  }
  
  public Collection<Edge> getAllPossibleEdges() {
    return allPossibleEdges;
  }
  
}
