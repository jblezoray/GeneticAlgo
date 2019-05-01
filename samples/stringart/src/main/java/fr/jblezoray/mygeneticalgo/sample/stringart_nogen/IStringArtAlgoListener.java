package fr.jblezoray.mygeneticalgo.sample.stringart_nogen;

import fr.jblezoray.mygeneticalgo.sample.stringart.edge.ScoredEdge;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;

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
      ScoredEdge scoredEdge);
  
}
