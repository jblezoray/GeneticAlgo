package fr.jblezoray.mygeneticalgo;

import java.util.ArrayList;
import java.util.Random;

public final class DNA extends ArrayList<Integer> {
  
  private static final long serialVersionUID = -834318714758648801L;

  /**
   * Fitness when not evaluated. 
   * Valid range is [0,1[, with zero being the worst and one the better. 
   * Therefore -1.0 is not a valid value. 
   */
  private static final double NOT_EVALUATED = -1.0;

  private double fitness = NOT_EVALUATED;
  
  public DNA(int dnaSize) {
    super(dnaSize);
  }

  /**
   * Copy constructor.
   * @param dna
   */
  public DNA(DNA dna) {
    super(dna);
  }
  
  /**
   * Creates a random string of DNA.
   * @param rand
   * @param dnaLength
   *    The created DNA will be of this size.
   * @param numberOfBases
   *    The exploited range for the random bases. 
   * @return 
   *    a DNA fragment. 
   */
  public static DNA create(Random rand, int dnaLength, int numberOfBases) {
    DNA dna = new DNA(dnaLength);
    for(int i=0; i<dnaLength; i++){
      int base = rand.nextInt(numberOfBases);
      dna.add(base);
    }
    return dna;
  }

  public void setFitness(double fitness) {
    this.fitness = fitness;
  }
  
  public double getFitness() {
    return this.fitness;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i=0; i<this.size(); i++) {
      int nb = this.get(i);
      if (nb < Character.MAX_RADIX) 
        sb.append('0');
      sb.append(Integer.toString(nb, Character.MAX_RADIX).toUpperCase());
      if ((i+1)%5 == 0)
        sb.append(' ');
    }
    return sb.toString();
  }
  
}
