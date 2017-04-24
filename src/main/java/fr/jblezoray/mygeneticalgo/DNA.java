package fr.jblezoray.mygeneticalgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class DNA extends ArrayList<Integer> {
  
  private static final long serialVersionUID = -834318714758648801L;
  
  private static final Map<Integer, int[]> CORRELATION_CACHE = 
      Collections.synchronizedMap(new HashMap<Integer, int[]>());
  
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
   * Copy constructor.
   * @param dna
   */
  public DNA(List<Integer> ints) {
    super(ints);
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
  
  
  /**
   * This method normalizes a float from range [currentMin, currentMax] to 
   * range [newMin, newMax].
   * 
   * @param value
   * @param currentMin
   * @param currentMax
   * @param newMin
   * @param newMax
   * @return
   */
  public static float normalizeFloat(float value, 
      float currentMin, float currentMax, float newMin, float newMax) {
    float scale = (value - currentMin) / currentMax; 
    return (scale * (newMax - newMin)) + newMin;
  }
  
  
  /**
   * This method normalizes a, integer from range [currentMin, currentMax] to 
   * range [newMin, newMax].
   * 
   * @param value
   * @param currentMin
   * @param currentMax
   * @param newMin
   * @param newMax
   * @return
   */
  public static int normalizeInteger(int value, int currentMin, int currentMax, 
      int newMin, int newMax) {
    float scale = (value - currentMin) / (float) currentMax;
    return (int)((scale * (newMax - newMin)) + newMin);
  }
  
  
  /**
   * This method takes two values 'a' and 'b' and correlates them so that a 
   * change in one is reflected as a change in the other one.
   * 
   * The result is deterministic, and has a uniform distribution of the 
   * possibilities in [0, maxA], [0,maxB]. This method is Thread safe.
   * 
   * @param a value to correlate. 
   * @param b value to correlate. 
   * @param maxA max value for the new A.
   * @param maxB max value for the new B.
   * @return an array of two integers where result[0] is the new A and result[1]
   *    is the new B.
   */
  public static int[] correlate(int a, int b, int maxA, int maxB) {
    final int p = a * b;
    int[] result =  CORRELATION_CACHE.get(p);
    if (result == null) {
      // seeding a PRNG has very good results in terms of speed, and meets our
      // needs in term of uniformity and determinicity.
      Random random = new Random(p);
      int newA = random.nextInt(maxA);
      int newB = random.nextInt(maxB);
      result = new int[] {newA,newB};
      CORRELATION_CACHE.put(p, result);
    }
    return result;
  }
}
