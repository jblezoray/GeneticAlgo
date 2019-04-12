package fr.jblezoray.mygeneticalgo.dna;

public abstract class AbstractDNA implements IDNA {
  
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
  
  @Override
  public final double getFitness() {
    return this.fitness;
  }
  
}
