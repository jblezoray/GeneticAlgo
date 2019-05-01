package fr.jblezoray.mygeneticalgo;

import java.util.List;

import fr.jblezoray.mygeneticalgo.dna.IDNA;

public interface ISelection<X extends IDNA> {

  class MatingPair<X> {
    public X mate1;
    public X mate2;
  }
  
  /**
   * Reinitializes the population used in the selection. 
   * 
   * This method is called for each new generation.
   * 
   * @param population
   */
  void initialize(List<X> population);
  
  /**
   * Select a pair of two individuals in the population.
   * @return
   */
  MatingPair<X> selectMatingPair();

}
