package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import fr.jblezoray.mygeneticalgo.IFitness;
import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;

/**
 * Can compute a fitness score from a StringPathDNA.
 * 
 * This implementation is naive and slow, but easy to understand.
 * 
 * @author jbl
 *
 */
public class Fitness implements IFitness<EdgeListDNA> {

  protected final UnboundedImage refImg;
  protected final Image importanceMappingImg; 
  
  public Fitness(UnboundedImage refImg,
                 Image importanceMappingImg) {
    this.refImg = refImg;
    this.importanceMappingImg = importanceMappingImg;
  }

  @Override
  public double computeFitnessOf(EdgeListDNA candidateToEvaluate) {
    
    // draw candidate. 
    UnboundedImage constructed = drawImage(candidateToEvaluate);
    
    // get fitness score.
    int l2Norm = (int) constructed
        .differenceWith(refImg)
        .multiplyWith(importanceMappingImg)
        .l2norm();
    
    return 1.0 / Math.sqrt(Math.sqrt(l2Norm));
  }

  public UnboundedImage drawImage(EdgeListDNA candidateToEvaluate) {
    UnboundedImage constructed = new UnboundedImage(this.refImg.getSize());
    for (Edge edge : candidateToEvaluate.getAllEdges())
      constructed.add(edge.getDrawnEdgeData());
    return constructed;
  }
}
