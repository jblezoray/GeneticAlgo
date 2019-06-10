package fr.jblezoray.mygeneticalgo.sample.stringart.genetic.fitness;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.jblezoray.mygeneticalgo.IGeneticAlgoListener;
import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;
import fr.jblezoray.mygeneticalgo.sample.stringart.genetic.EdgeListDNA;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.CompressedUnboundedImage;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;


/**
 * Can compute a fitness score from a StringPathDNA.
 * 
 * Bufferizes parts of rendered images to find a fast way to draw them.
 *   
 * @author jbl
 */
public class FitnessFast extends Fitness implements IGeneticAlgoListener<EdgeListDNA>{
 
  private List<GeneratedElement> buffer;
  
  /** Max number of elements in the buffer. */
  private final int maxBufferSize;

  private List<String> reusedStats = new ArrayList<>(); 
  private List<Integer> listSizeStats = new ArrayList<>();
  private List<Long> timeStats = new ArrayList<>(); 
  
  /** size of each element of the buffer. */
  private final static int GENERATED_ELEMENT_SIZE = 100;
  
  private final static int MIN_SCORE = (int)(GENERATED_ELEMENT_SIZE * 0.60f);

  public FitnessFast(
        UnboundedImage refImg,
        Image importanceMappingImg,
        int maxBufferSize) {
    super(refImg, importanceMappingImg);
    this.maxBufferSize = maxBufferSize;
    this.buffer = new LinkedList<>();
  }

  
  @Override
  public void notificationOfGeneration(int _1, EdgeListDNA _2, double[] _3) {
    int buffSizeBeforePrune = this.buffer.size();
    
    // trim buffer list to keep only the best ones. 
    this.buffer = this.buffer.stream()
        .sorted()
        .limit(maxBufferSize)
        .peek(GeneratedElement::resetReusabilityScore)
        .collect(Collectors.toList());
    
    String reused = reusedStats.stream()
        .collect(Collectors.groupingBy(String::toString))
        .values().stream()
        .sorted((la,lb) -> lb.size() - la.size())
        .map(list -> list.get(0) + "(x"+list.size()+")")
        .reduce("", (a,b) -> a + " " + b);
    reusedStats.clear();
    
    int meanDnaSize = listSizeStats.size()==0 ? -1 :  
        listSizeStats.stream() .reduce(0, (a,b) -> a+b) / listSizeStats.size();
    
    long meantime = timeStats.size()==0 ? -1 :  
      timeStats.stream().reduce(0L, (a,b) -> a+b) / timeStats.size();
    
    System.out.println(
        "buf:"+buffSizeBeforePrune+" (after:"+this.buffer.size()+")"
        +" meanDnaSize="+meanDnaSize
        +" meanTime="+meantime+"ms"
        +" "+reused);
  }
  
   
  @Override
  public UnboundedImage drawImage(EdgeListDNA imageToDraw) {
    long start = new Date().getTime();
    
    List<Edge> edges = imageToDraw.getAllEdges();
    this.listSizeStats.add(edges.size());
    
    edges = edges.stream().sorted(Edge.COMPARATOR).collect(Collectors.toList());
    
    UnboundedImage finalImage = new UnboundedImage(this.refImg.getSize());
    
    int sizeBefore = edges.size();
    findAndAddGeneratedElements(finalImage, edges);
//    this.reusedStats.add("" + (sizeBefore - edges.size()) + "/" + sizeBefore);
    this.reusedStats.add("" + (sizeBefore - edges.size()) );
    
    addEdges(finalImage, edges);
    
    long  timeTook = new Date().getTime() - start;
    this.timeStats.add(timeTook);
    
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
          .peek(geo -> geo.contributionScore = geo.generatedElement.getEdges().size() - geo.diffToDel.size())
          .filter(geo -> geo.contributionScore > MIN_SCORE)
          .max((a, b) -> a.contributionScore - b.contributionScore);
      
      if (bestOpt.isPresent()) {
        GeneratedElementOperations best = bestOpt.get();
        best.generatedElement.incrementReusabilityScore();
        
        UnboundedImage geCopy = best.generatedElement.getGenerated().decompress().deepCopy();
        // this is not parallelizable: do not use parallelStream() here.
        best.diffToDel.stream().forEach(edgeToDelete -> 
          geCopy.remove(edgeToDelete.getDrawnEdgeData())
        );
        
        finalImage.add(geCopy);
        deleteContributedEdges(edges, best.generatedElement.getEdges());

        saveGeneratedElement(best.diffAdded, geCopy);
      }
    } while (edges.size()>GENERATED_ELEMENT_SIZE && bestOpt.isPresent());
    return;
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
    CompressedUnboundedImage compressed = new CompressedUnboundedImage(subImage);
    GeneratedElement generatedElement = new GeneratedElement(sublist, compressed);
    generatedElement.incrementReusabilityScore();
    synchronized (this.buffer) {
      this.buffer.add(0, generatedElement);
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
          .map(GeneratedElementOperations::new)
          .collect(Collectors.toList());
    }
   
    for (Edge edge : edges) {
      List<GeneratedElementOperations> badSmells = new ArrayList<>();
      for (GeneratedElementOperations ge : elements) {
        int comparison = -1;
        while (comparison < 0 && ge.generatedElement.getEdges().size()>ge.curIndex) {
          Edge curEdge = ge.generatedElement.getEdges().get(ge.curIndex);
          comparison = Edge.COMPARATOR.compare(curEdge, edge);
          // if comparison > 0, then the 'curEdge' is after 'edge' : we don't
          // increment the pointer to keep it for comparison with the next 
          // 'edge'. 
          if (comparison <= 0) ge.curIndex++;
          if (comparison < 0) ge.diffToDel.add(curEdge);
          if (comparison == 0) ge.diffAdded.add(curEdge);
        }
        // Does this GeneratedElementOperations start to smell bad due to too 
        // much implied operations ?
        if (ge.diffToDel.size()>MIN_SCORE) {
          badSmells.add(ge);
        }
      }
      elements.removeAll(badSmells);
    }
    // All subsequent edge are to be deleted.
    List<GeneratedElementOperations> badSmells = new ArrayList<>();
    for (GeneratedElementOperations ge : elements) {
      while (ge.generatedElement.getEdges().size()>ge.curIndex) {
        ge.diffToDel.add(ge.generatedElement.getEdges().get(ge.curIndex++));
      }
      if (ge.diffToDel.size()>MIN_SCORE) {
        badSmells.add(ge);
      }
    }
    elements.removeAll(badSmells);
    return elements;
  }
}
