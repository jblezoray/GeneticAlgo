package fr.jblezoray.mygeneticalgo;

import fr.jblezoray.mygeneticalgo.dna.IDNA;

public interface IGeneticAlgoListener<X extends IDNA> {

  /**
   * This callback is invocated to notify of the best match at each generation.
   * 
   * @param generation the generation number.
   * @param dnaBestMatch the best DNA. 
   * @param allFitnessScores fitness scores of all individuals. 
   */
  void notificationOfGeneration(int generation, X dnaBestMatch, 
      double[] allFitnessScores);

}
