package fr.jblezoray.mygeneticalgo;

import java.util.Collection;
import java.util.Random;

/**
 * The classes that use the {@GeneticAlgo} must implement this interface. 
 * @author jib
 */
public interface IPhenotype<DNA extends DNAAbstract> {

  /**
   * Create an initial population, size > 1.
   * 
   * @param rand an initialized Random object.
   * @return a population.
   */
  Collection<DNA> createInitialPopulation(Random rand);
  
  /**
   * Evaluate a the fitness of this DNA.
   *  
   * @param dna
   * @return an evaluation in range [0.0f, 1.0f]
   */
  double computeFitness(DNA dna);

  /**
   * This callback is invocated to notify of the best match at each generation.
   * 
   * @param generation the generation number.
   * @param dna
   */
  void notificationOfBestMatch(int generation, DNA dna);
  
}
