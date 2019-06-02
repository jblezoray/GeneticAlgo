package fr.jblezoray.mygeneticalgo.sample.stringart.genetic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;


/**
 * Can compute a fitness score from a StringPathDNA.
 * 
 * Bufferizes parts of rendered images to find a fast way to draw them.
 *   
 * @author jbl
 */
public class FitnessFast extends Fitness {
 
  private LinkedList<GeneratedElement> buffer;
  
  /** Max number of elements in the buffer. */
  private final int maxBufferSize; 
  
  /** size of each element of the buffer. */
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
  
  
  public FitnessFast(
        UnboundedImage refImg,
        Image importanceMappingImg,
        int maxBufferSize) {
    super(refImg, importanceMappingImg);
    this.maxBufferSize = maxBufferSize;
    this.buffer = new LinkedList<>();
  }
   
  @Override
  public UnboundedImage drawImage(EdgeListDNA imageToDraw) {
    List<Edge> edges = imageToDraw.getAllEdges();
    edges = edges.stream().sorted(Edge.COMPARATOR).collect(Collectors.toList());
    UnboundedImage finalImage = new UnboundedImage(this.refImg.getSize());
    findAndAddGeneratedElements(finalImage, edges);
    addEdges(finalImage, edges);
    return finalImage;
  }


  /**
   * Find and add GeneratedElements from the cache.
   * 
   * At each round, find the GeneratedElement that, if added, results in the
   * easiest contribution to make (that has the less edges to remove).  Then,
   * add it to the image, and remove the edges as man edges that are in excess.
   * 
   * @param finalImage the image to draw onto.
   * @param edges the list of edges to draw.  The list will be modified, as some
   *     of the edges will have been drawn. 
   */
  private void findAndAddGeneratedElements(
      UnboundedImage finalImage, List<Edge> edges) {
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
        deleteContributedEdges(edges, best.generatedElement.edges);
      }
    } while (edges.size()>GENERATED_ELEMENT_SIZE && bestOpt.isPresent());
  }

  
  private static void addToImage(UnboundedImage finalImage, GeneratedElementOperations geo) {
    finalImage.add(geo.generatedElement.generated);
    // this is not parallelizable. do not use parallelStream() here.
    geo.diffToDel.stream().forEach(edgeToDelete -> 
      finalImage.remove(edgeToDelete.getDrawnEdgeData())
    );
  }

  private static void deleteContributedEdges(List<Edge> edges, List<Edge> toRemove) {
    toRemove.forEach(edge -> {
      edges.remove(edge);
    });
  }

  /**
   * Draw all the edges one by one. 
   * 
   * Will eventually save the result for subsequent calculous.
   * 
   * @param finalImage the image to draw onto.
   * @param edges the list of edges to draw.  Will be emty after the execution.
   */
  private void addEdges(UnboundedImage finalImage, List<Edge> edges) {
    while(edges.size()>0) {
      int sublistSize = Math.min(edges.size(), GENERATED_ELEMENT_SIZE);
      List<Edge> sublist = new ArrayList<>(edges.subList(0, sublistSize));
      UnboundedImage subImage = drawEdges(sublist);
      deleteContributedEdges(edges, sublist);
      finalImage.add(subImage);
      if (sublistSize == GENERATED_ELEMENT_SIZE) {
        saveGeneratedElement(sublist, subImage);
      }
    }
  }

  private UnboundedImage drawEdges(List<Edge> sublist) {
    UnboundedImage subImage = new UnboundedImage(this.refImg.getSize());
    for (Edge edge : sublist) {
      subImage.add(edge.getDrawnEdgeData());
    }
    return subImage;
  }

  private void saveGeneratedElement(List<Edge> sublist, UnboundedImage subImage) {
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

}
