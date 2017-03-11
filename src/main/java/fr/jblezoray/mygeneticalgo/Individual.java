package fr.jblezoray.mygeneticalgo;

public class Individual {

  /**
   * Fitness when not evaluated. 
   * Valid range is [0,1[, with zero being the worst and one the better. 
   * Therefore -1.0 is not a valid value. 
   */
  private static final double NOT_EVALUATED = -1.0;
  
  private DNA dna;
  private double fitness = NOT_EVALUATED;

  public Individual(DNA dna) {
    this.dna = dna;
  }

  public DNA getDNA() {
    return dna;
  }

  public void setFitness(double fitness) {
    this.fitness = fitness;
  }
  public double getFitness() {
    return this.fitness;
  }
  
  
}
