package fr.jblezoray.mygeneticalgo;

import java.util.Random;

public abstract class DNAAbstract {
  
  /**
   * Fitness when not evaluated. 
   * Valid range is [0,1[, with zero being the worst and one the better. 
   * Therefore -1.0 is not a valid value. 
   */
  private static final double NOT_EVALUATED = -1.0;

  private double fitness = NOT_EVALUATED;
  
  public final void setFitness(double fitness) {
    this.fitness = fitness;
  }
  
  public final double getFitness() {
    return this.fitness;
  }

  /**
   * Mutate DNA so many times according to mutation rate.
   * 
   * @param rand an initialized Random object.
   * @param mutationRate probability for each DNA base to be mutated.
   */
  public abstract void doMutate(Random rand, float mutationRate);
  
  /**
   * Crossover this object with the {@code other} one.
   * 
   * There will be a random number of crossovers between {@code minCrossovers}
   * and {@code maxCrossovers}. 
   * 
   * @param rand an initialized Random object.
   * @param other
   * @param minCrossovers
   * @param maxCrossovers
   */
  public abstract void doDNACrossover(Random rand, DNAAbstract other, 
      int minCrossovers, int maxCrossovers);
  
  /**
   * Build a deep copy of itself. 
   * 
   * @return a copy of itself.
   */
  public abstract <DNA extends DNAAbstract> DNA copy();
  
  
  /**
   * This method normalizes a float from range [currentMin, currentMax] to 
   * range [newMin, newMax].
   * 
   * @param valueToNormalize
   * @param currentMin
   * @param currentMax
   * @param newMin
   * @param newMax
   * @return
   */
  public static float normalizeFloat(float valueToNormalize, 
      float currentMin, float currentMax, float newMin, float newMax) {
    float scale = (valueToNormalize - currentMin) / currentMax; 
    return (scale * (newMax - newMin)) + newMin;
  }
  
  
  /**
   * This method normalizes a, integer from range [currentMin, currentMax] to 
   * range [newMin, newMax].
   * 
   * @param valueToNormalize
   * @param currentMin 
   * @param currentMax
   * @param newMin
   * @param newMax
   * @return
   */
  public static int normalizeInteger(int valueToNormalize, int currentMin, 
      int currentMax, int newMin, int newMax) {
    float scale = (valueToNormalize - currentMin) / (float) currentMax;
    return (int)((scale * (newMax - newMin)) + newMin);
  }
  
}
