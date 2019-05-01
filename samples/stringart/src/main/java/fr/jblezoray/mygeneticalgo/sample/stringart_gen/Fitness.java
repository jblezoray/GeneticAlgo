package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import java.util.Optional;

import fr.jblezoray.mygeneticalgo.IFitness;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;

public class Fitness implements IFitness<StringPathDNA> {

  private final EdgeFactory edgeFactory; 
  private final UnboundedImage refImg;
  private final Image importanceMappingImg; 
  
  public Fitness(EdgeFactory edgeFactory,
                 UnboundedImage refImg,
                 Image importanceMappingImg) {
    this.edgeFactory = edgeFactory;
    this.refImg = refImg;
    this.importanceMappingImg = importanceMappingImg;
  }

  @Override
  public double computeFitnessOf(StringPathDNA candidateToEvaluate) {
    
    // draw candidate. 
    UnboundedImage constructed = drawImage(candidateToEvaluate);
    
    // get fitness score.
    return - (int) constructed
        .differenceWith(refImg)
        .multiplyWith(importanceMappingImg)
        .l2norm();
  }

  public UnboundedImage drawImage(StringPathDNA candidateToEvaluate) {
    UnboundedImage constructed = new UnboundedImage(this.refImg.getSize());
    
    StringPathBase prevBase = candidateToEvaluate.getBase(0);
    
    for (int i=1; i<candidateToEvaluate.getSize(); i++) {
      StringPathBase curBase = candidateToEvaluate.getBase(i);
      
      Optional<Edge> oe = edgeFactory.getEdge(
          prevBase.getNail(), prevBase.isTurnClockwise(), 
          curBase.getNail(),  curBase.isTurnClockwise());
      if (!oe.isPresent()) 
        throw new RuntimeException("no edge matched between nail "+prevBase.getNail()+" and nail "+curBase.getNail()+"!");
      Edge edge = oe.get();
      constructed.add(edge.getDrawnEdgeData());

      prevBase = curBase;
    }
    return constructed;
  }
}
