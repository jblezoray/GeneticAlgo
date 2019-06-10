package fr.jblezoray.mygeneticalgo.sample.stringart.genetic.fitness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import fr.jblezoray.mygeneticalgo.IGeneticAlgoListener;
import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;
import fr.jblezoray.mygeneticalgo.sample.stringart.genetic.EdgeListDNA;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.CompressedUnboundedImage;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ImageSize;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;

/**
 * Can compute a fitness score from a StringPathDNA.
 * 
 * Bufferizes parts of rendered images to find a fast way to draw them.
 *   
 * @author jbl
 */
public class FitnessFast2 extends Fitness implements IGeneticAlgoListener<EdgeListDNA>{

  private final Map<Integer, List<GeneratedElement>> categorizedBuffer;
  
  /** Max number of elements in the buffer. */
  private final int maxBufferSize;
  
  /** A classifier that can split a list of edges in distinct categories. */
  private final Collector<Edge, ?, Map<Integer, List<Edge>>> edgeGroupByClassifier;
  
  public FitnessFast2(
      UnboundedImage refImg, 
      Image importanceMappingImg,
      int maxBufferSize,
      int nbOfCategories,
      int nbNails) {
    super(refImg, importanceMappingImg);
    this.maxBufferSize = maxBufferSize;
    this.edgeGroupByClassifier = Collectors.groupingBy(edge ->
        nbOfCategories * edge.getNailA() / nbNails
    );
    this.categorizedBuffer = new HashMap<Integer, List<GeneratedElement>>();
    for (int i=0; i<nbOfCategories; i++)
      categorizedBuffer.put(i, new ArrayList<GeneratedElement>());
  }

  @Override
  public void notificationOfGeneration(int generation, EdgeListDNA dnaBestMatch, double[] allFitnessScores) {
    // trim buffer list to keep only the best ones.
    for (Integer key : categorizedBuffer.keySet()) {
      List<GeneratedElement> prunedBuffer = this.categorizedBuffer.get(key)
          .stream()
          .sorted()
          .limit(maxBufferSize/categorizedBuffer.size())
          .peek(GeneratedElement::resetReusabilityScore)
          .collect(Collectors.toList());
      this.categorizedBuffer.put(key, prunedBuffer);
    }
  }
  
  @Override
  public UnboundedImage drawImage(EdgeListDNA imageToDraw) {
    UnboundedImage finalImage = new UnboundedImage(this.refImg.getSize());
    
    List<Edge> edges = imageToDraw.getAllEdges();
    Map<Integer, List<Edge>> classifiedEdges = edges.stream()
        .collect(this.edgeGroupByClassifier);
    
    for (int categoryId : classifiedEdges.keySet()) {
      List<Edge> edgesClass = classifiedEdges.get(categoryId);
      edgesClass.sort(Edge.COMPARATOR);
      List<GeneratedElement> geList = categorizedBuffer.get(categoryId);
      
      UnboundedImage edgesImg;
      Optional<GeneratedElementOperations> bestOpt = buildOperations(geList, edges);
      if (bestOpt.isPresent()) {
        // create a copy of it 
        edgesImg = findAndAddGeneratedElements(bestOpt.get(), edgesClass);
        //save it if it worths it.
        if (bestOpt.get().contributionScore<0.8) {
          saveGeneratedElement(geList, edgesClass, edgesImg);
        }
        
      } else {
        edgesImg = addAllEdges(edgesClass, finalImage.getSize());
        // save it.
        saveGeneratedElement(geList, edgesClass, edgesImg);
      }
      finalImage.add(edgesImg);
      
    }
    
    return finalImage;
  }

  /**
   * Create an image out of a GeneratedElementOperations.
   * 
   * It does : 
   * a) Create a copy if the best generatedElement,
   * b) Remove all the edges that are not in 'edges'.
   * c) Add all the edges that are to be added, but that are not in 'best'
   * 
   * @param best 
   * @param buffer the list of available GeneratedElements.
   * @param edges the list of edges to draw.  The list will be modified, as some
   *     of the edges will have been drawn.
   * @return an image of "edges".
   */
  private static UnboundedImage findAndAddGeneratedElements( 
      GeneratedElementOperations best, List<Edge> edges) {
    best.generatedElement.incrementReusabilityScore();
    UnboundedImage geCopy = best.generatedElement.getGenerated().decompress().deepCopy();
    // this is not parallelizable: do not use parallelStream() here.
    best.diffToDel.stream().forEach(edgeToDelete -> 
      geCopy.remove(edgeToDelete.getDrawnEdgeData())
    );
    edges.stream().forEach(edgeToAdd -> 
      geCopy.add(edgeToAdd.getDrawnEdgeData())
    );
    return geCopy;
  }
  
  /**
   * Create an image out of "edges".
   * @param edges
   * @param size
   * @return
   */
  private static UnboundedImage addAllEdges(List<Edge> edges, ImageSize size) {
    UnboundedImage edgesToAddImg = new UnboundedImage(size);
    edges.stream().forEach(edgeToAdd -> 
      edgesToAddImg.add(edgeToAdd.getDrawnEdgeData())
    );
    return edgesToAddImg;
  }

  private static void saveGeneratedElement(
      List<GeneratedElement> buffer, List<Edge> sublist, UnboundedImage subImage) {
    CompressedUnboundedImage compressed = new CompressedUnboundedImage(subImage);
    GeneratedElement generatedElement = new GeneratedElement(sublist, compressed);
    generatedElement.incrementReusabilityScore();
    synchronized (buffer) {
      buffer.add(generatedElement);
    }
  }
  

  /**
   * Find the best GeneratedElement in buffer, and create a GeneratedElementOperations.
   * 
   * GeneratedElements that produce too much operations are discared. 
   *   
   * @param edges
   * @return a GeneratedElementOperations that contains information on how to 
   *        add the best GeneratedElement, or empty if none was found.
   */
  private static Optional<GeneratedElementOperations> buildOperations(
      List<GeneratedElement> buffer, List<Edge> edges) {

    int minScore = (int)(edges.size() * 0.7f);
    
    //Init a new list of GeneratedElementOperations, one per instance in the 
    // buffer.
    List<GeneratedElementOperations> elements;
    synchronized (buffer) {
      elements = buffer.stream()
          .map(GeneratedElementOperations::new)
          .collect(Collectors.toList());
    }
   
    // Fill it with the number of edges to delete if this element was to be 
    // added in the built image.
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
        if (ge.diffToDel.size()>minScore) {
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
      if (ge.diffToDel.size()>minScore) {
        badSmells.add(ge);
      }
    }
    elements.removeAll(badSmells);
    
    // keep only the one with the best constribution score.
    return elements.parallelStream()
        .peek(geo -> geo.contributionScore = geo.generatedElement.getEdges().size() - geo.diffToDel.size())
        .filter(geo -> geo.contributionScore > minScore)
        .max((a, b) -> a.contributionScore - b.contributionScore);
  }
}
