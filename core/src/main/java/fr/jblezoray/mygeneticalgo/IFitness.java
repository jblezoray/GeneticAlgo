package fr.jblezoray.mygeneticalgo;

import fr.jblezoray.mygeneticalgo.dna.IDNA;

public interface IFitness<X extends IDNA> {

  /**
   * Computes the fitness of {@code candidateToEvaluate}.
   * 
   * @param candidateToEvaluate
   * @return an evaluation note in range [0.0f, 1.0f] 
   */
  public double computeFitnessOf(X candidateToEvaluate);
  
}
