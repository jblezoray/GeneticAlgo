package fr.jblezoray.mygeneticalgo;

import java.util.ArrayList;
import java.util.Random;

public final class DNA extends ArrayList<Integer> {
  
  private static final long serialVersionUID = -834318714758648801L;
  
  DNA(int dnaSize) {
    super(dnaSize);
  }

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
  static DNA create(Random rand, int dnaLength, int numberOfBases) {
    DNA dna = new DNA(dnaLength);
    for(int i=0; i<dnaLength; i++){
      int base = rand.nextInt(numberOfBases);
      dna.add(base);
    }
    return dna;
  }
}
