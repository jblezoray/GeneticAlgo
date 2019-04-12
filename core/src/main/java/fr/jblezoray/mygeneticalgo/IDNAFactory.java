package fr.jblezoray.mygeneticalgo;

import fr.jblezoray.mygeneticalgo.dna.IDNA;

public interface IDNAFactory<X extends IDNA> {

  /**
   * Create a randomly initialized individual.
   * 
   * @return an individual.
   */
  X createRandomIndividual();
  
}
