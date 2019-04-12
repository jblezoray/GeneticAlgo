package fr.jblezoray.mygeneticalgo.dna;

import java.util.Comparator;

public class DNAFitnessComparator<X extends IDNA> implements Comparator<X> {
  
  @Override
  public int compare(X i1, X i2) {
    return (i1==null || i2 == null) ? 0
        : - Double.compare(i1.getFitness(), i2.getFitness());
  }
  
}
