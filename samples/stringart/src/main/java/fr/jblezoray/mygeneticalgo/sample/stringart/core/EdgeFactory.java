package fr.jblezoray.mygeneticalgo.sample.stringart.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;

/**
 * The EdgeFactory maintains a collection of all the possible edges. 
 */
public class EdgeFactory {
  
  private final List<Edge> allPossibleEdges;
  private final boolean edgeWayEnabled;
  
  /**
   * 
   * 
   *  If the 'edgeWayEnabled' boolean option is not setted to true, then only 
   *  the edges that goes clockwise are kept.  Otherwise, the predicates 
   *  considers that the string has make a 'turn' around the nail, and therefore
   *  arises at the other side.
   * 
   * @param minNailDiff
   * @param totalNumberOfNails
   * @param edgeWayEnabled If enabled, then the way a thread turns around a nail
   *  is considered.
   * @param drawer
   */
  public EdgeFactory(
      int minNailDiff, 
      int totalNumberOfNails, 
      boolean edgeWayEnabled,
      EdgeDrawer drawer) {
    this.allPossibleEdges = new ArrayList<>();
    for (int i=0; i<totalNumberOfNails; i++) {
      for (int j=i; j<totalNumberOfNails; j++) {
        if (Math.abs(j-i) > minNailDiff) {
          // one for each possible connection between two nails.
          allPossibleEdges.add(new Edge(i, true,  j, true,  drawer));
          if (edgeWayEnabled) {
            allPossibleEdges.add(new Edge(i, false, j, true,  drawer));
            allPossibleEdges.add(new Edge(i, true,  j, false, drawer));
            allPossibleEdges.add(new Edge(i, false, j, false, drawer));
          }
        }
      }
    }
    this.edgeWayEnabled = edgeWayEnabled;
  }
  
  public Stream<Edge> streamEdges(
      int nail, boolean nailClockwise) {
    return allPossibleEdges.stream()
        .parallel()
        .filter(edge -> edge.contains(nail, !edgeWayEnabled||nailClockwise));
  }

  public Optional<Edge> getEdge(
      int nailA, boolean nailAClockwise,
      int nailB, boolean nailBClockwise) {
    return allPossibleEdges.stream()
        .parallel()
        .filter(edge -> edge.contains(nailA, !edgeWayEnabled||nailAClockwise) 
                     && edge.contains(nailB, !edgeWayEnabled||nailBClockwise))
        .findFirst();
  }
  
}
