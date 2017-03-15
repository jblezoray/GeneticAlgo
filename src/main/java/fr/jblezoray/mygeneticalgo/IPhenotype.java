package fr.jblezoray.mygeneticalgo;

/**
 * The classes that use the {@GeneticAlgo} must implement this interface. 
 * @author jib
 */
public interface IPhenotype {

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
  public void notificationOfBestMatch(int generation, DNA dna);
  
}
