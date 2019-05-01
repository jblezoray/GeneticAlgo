package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import java.util.List;

import fr.jblezoray.mygeneticalgo.IDNAFactory;

public class StringPathDNAFactory implements IDNAFactory<StringPathDNA> {

  private final int nbNailsPerIndividual;
  private final int minNailDiff;

  public StringPathDNAFactory(int nbNailsPerIndividual, int minNailDiff) {
    this.nbNailsPerIndividual = nbNailsPerIndividual;
    this.minNailDiff = minNailDiff;
  }
  
  @Override
  public StringPathDNA createRandomIndividual() {
    StringPathDNA dna = new StringPathDNA(this);
    for (int i=0; i<this.nbNailsPerIndividual; i++) dna.addNail(i);
    return dna;
  }


  boolean isNailValueValid(List<StringPathBase> bases, int index) {
    boolean isValid = true;
    int curNail =  bases.get(index).getNail();
    if (index != 0) {
      int prevNail = bases.get(index-1).getNail();
      if (Math.abs(prevNail - curNail) <= minNailDiff) {
        isValid = false;
      }
    } 
    
    if (isValid && index<bases.size()-1) {
      int nextNail = bases.get(index+1).getNail();
      if (Math.abs(curNail - nextNail) <= minNailDiff) {
        isValid = false;
      }
    }
    
    return isValid;
  }

}
