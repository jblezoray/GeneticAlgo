package fr.jblezoray.mygeneticalgo.sample.stringart_nogen;

import java.util.concurrent.atomic.AtomicInteger;

import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.edge.ScoredEdge;
import fr.jblezoray.mygeneticalgo.sample.stringart_nogen.image.Image;

/**
 * This listener is invoked after each round.
 * @author jbl
 */
public interface IStringArtAlgoListener {

  void notifyRoundResults(
      int iteration, 
      Image curImg, 
      Image importanceMappingImg, 
      Image refImg, 
      ScoredEdge scoredEdge, 
      AtomicInteger counterOfEvaluatedEdges,
      long timeTookForThisRound);
  
}
