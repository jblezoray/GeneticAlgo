package fr.jblezoray.mygeneticalgo.dna;

public interface IDNA {

  void setFitness(double fitness);
  
  double getFitness();

  /**
   * Mutate DNA so many times according to mutation rate.
   * 
   * @param rand an initialized Random object.
   * @param mutationRate probability for each DNA base to be mutated.
   */
  public abstract void doMutate(float mutationRate);
  
  /**
   * Crossover this object with the {@code other} one.
   * 
   * There will be a random number of crossovers between {@code minCrossovers}
   * and {@code maxCrossovers}. 
   * 
   * @param other
   * @param minCrossovers
   * @param maxCrossovers
   */
  public abstract <X extends IDNA> void doDNACrossover(X other, 
      int minCrossovers, int maxCrossovers);
  
  /**
   * Build a deep copy of itself. 
   * 
   * @return a copy of itself.
   */
  public abstract <X extends IDNA> X copy();
  
}
