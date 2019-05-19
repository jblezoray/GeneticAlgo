package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;

public class FitnessOptimized extends Fitness {
 
  private final int maxBufferSize; 
  private LinkedList<GeneratedElement> buffer;
  
  public final static int GENERATED_ELEMENT_SIZE = 400;
  
  static class GeneratedElement {
    List<Edge> edges;
    UnboundedImage generated;
    int reusabilityScore;
    @Override
    public String toString() {
      return edges.toString();
    }
  }
  
  static class GeneratedElementOperations {
    GeneratedElement generatedElement;
    int curIndex;
    List<Edge> diffToDel;
    int contributionScore;
    public GeneratedElementOperations(GeneratedElement wrappedGeneratedElement) {
      this.generatedElement = wrappedGeneratedElement;
      this.curIndex = 0;
      this.diffToDel = new ArrayList<>();
      this.contributionScore = 0;
    }
  }
  
  
  public FitnessOptimized(EdgeFactory edgeFactory,
                 UnboundedImage refImg,
                 Image importanceMappingImg,
                 int maxBufferSize) {
    super(edgeFactory, refImg, importanceMappingImg);
    this.maxBufferSize = maxBufferSize;
    this.buffer = new LinkedList<>();
  }
   
  @Override
  public UnboundedImage drawImage(StringPathDNA imageToDraw) {
    List<Edge> edges = getAllEdges(imageToDraw);
    edges.sort(Edge.COMPARATOR);
    
    UnboundedImage finalImage = new UnboundedImage(this.refImg.getSize());
    
    // At each round, find the GeneratedElement that, if added, results in the 
    // easiest contribution to make (that has the less edges to remove).  Then,
    // add it to the image, and remove the edges as man edges that are in excess.
    Optional<GeneratedElementOperations> bestOpt = Optional.empty();
    do {
      bestOpt = buildOperationsFor(edges).parallelStream()
          .peek(geo -> geo.contributionScore = geo.generatedElement.edges.size() - geo.diffToDel.size())
          .filter(geo -> geo.contributionScore > GENERATED_ELEMENT_SIZE/2)
          .max((a, b) -> a.contributionScore - b.contributionScore);
      
      if (bestOpt.isPresent()) {
        GeneratedElementOperations best = bestOpt.get();
        best.generatedElement.reusabilityScore+=2;
        addToImage(finalImage, best);
        deleteContributedEdges(edges, best);
      }
    } while (edges.size()>GENERATED_ELEMENT_SIZE && bestOpt.isPresent());
    
    // Draw remaining edges one by one.  Eventually, save the result for 
    // subsequent calculous.
    while(edges.size()>0) {
      
      int sublistSize = Math.min(edges.size(), GENERATED_ELEMENT_SIZE);
      List<Edge> sublist = new ArrayList<>(edges.subList(0, sublistSize));
      UnboundedImage subImage = new UnboundedImage(this.refImg.getSize());
      for (Edge edge : sublist) {
        subImage.add(edge.getDrawnEdgeData());
        edges.remove(edge);
      }
      finalImage.add(subImage);
      
      if (sublistSize == GENERATED_ELEMENT_SIZE) {
        GeneratedElement generatedElement = new GeneratedElement();
        generatedElement.edges = sublist;
        generatedElement.generated = subImage;
        generatedElement.reusabilityScore = 3;
        synchronized (this.buffer) {
          this.buffer.add(0, generatedElement);
          if (this.buffer.size()>this.maxBufferSize) {
            this.buffer.stream()
                .max((a,b)->a.reusabilityScore-b.reusabilityScore)
                .ifPresent(ge -> this.buffer.remove(ge));
          }
        }
      }
    }
    
    return finalImage;
  }


  private void deleteContributedEdges(List<Edge> edges, GeneratedElementOperations geo) {
    geo.generatedElement.edges.forEach(edge -> {
      edges.remove(edge);
    });
  }

  
  private void addToImage(UnboundedImage finalImage, GeneratedElementOperations geo) {
    finalImage.add(geo.generatedElement.generated);
    // this is not parallelizable. do not use parallelStream() here.
    geo.diffToDel.stream().forEach(edgeToDelete -> 
      finalImage.remove(edgeToDelete.getDrawnEdgeData())
    );
  }
  

  /**
   * Init a new list of GeneratedElementOperations, one per instance in the 
   * buffer.
   * 
   * Fill it with the number of edges to delete if this element was to be 
   * added in the built image.
   *   
   * @param edges
   * @return
   */
  private List<GeneratedElementOperations> buildOperationsFor(
      List<Edge> edges) {
    
    List<GeneratedElementOperations> elements;
    synchronized (this.buffer) {
      elements = this.buffer.stream()
          .peek(ge -> ge.reusabilityScore--)
          .map(GeneratedElementOperations::new)
          .collect(Collectors.toList());
    }
   
    for (Edge edge : edges) {
      List<GeneratedElementOperations> badSmells = new ArrayList<>();
      for (GeneratedElementOperations ge : elements) {
        int comparison = -1;
        while (comparison < 0 && ge.generatedElement.edges.size()>ge.curIndex) {
          Edge curEdge = ge.generatedElement.edges.get(ge.curIndex);
          comparison = Edge.COMPARATOR.compare(curEdge, edge);
          // if comparison > 0, then the 'curEdge' is after 'edge' : we don't
          // increment the pointer to keep it for comparison with the next 
          // 'edge'. 
          if (comparison <= 0) ge.curIndex++;
          if (comparison < 0) ge.diffToDel.add(curEdge);
        }
        // Does this GeneratedElementOperations start to smell bad due to too 
        // much implied operations ?
        if (ge.diffToDel.size()>GENERATED_ELEMENT_SIZE/2) {
          badSmells.add(ge);
        }
      }
      elements.removeAll(badSmells);
    }
    // All subsequent edge are to be deleted.
    for (GeneratedElementOperations ge : elements) {
      while (ge.generatedElement.edges.size()>ge.curIndex) {
        ge.diffToDel.add(ge.generatedElement.edges.get(ge.curIndex++));
      }
    }
    return elements;
  }

  
  private List<Edge> getAllEdges(StringPathDNA imageToDraw) {
    List<Edge> edges = new LinkedList<>();
    
    StringPathBase prevBase = imageToDraw.getBase(0);
    for (int i=1; i<imageToDraw.getSize(); i++) {
      StringPathBase curBase = imageToDraw.getBase(i);
      
      Optional<Edge> oe = edgeFactory.getEdge(
          prevBase.getNail(), prevBase.isTurnClockwise(), 
          curBase.getNail(),  curBase.isTurnClockwise());
      if (!oe.isPresent()) 
        throw new RuntimeException("no edge matched between nail "+prevBase+" and nail "+curBase+"!");
      edges.add(oe.get());
      
      prevBase = curBase;
    }
    
    return edges;
  }
  
}
