package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import java.util.List;

import fr.jblezoray.mygeneticalgo.IDNAFactory;

public class StringPathDNAFactory implements IDNAFactory<StringPathDNA> {

  private final int nbNailsInStringPath;
  private final int minNailDiff;
  private final int nbNails;
  private final boolean edgeWayEnabled;
  private final boolean defaultEdgeWay;

  public StringPathDNAFactory(int nbNailsInStringPath, int minNailDiff, 
      int nbNails, boolean edgeWayEnabled, boolean defaultEdgeWay) {
    this.nbNailsInStringPath = nbNailsInStringPath;
    this.minNailDiff = minNailDiff;
    this.nbNails = nbNails;
    this.edgeWayEnabled = edgeWayEnabled;
    this.defaultEdgeWay = defaultEdgeWay;
  }
  
  @Override
  public StringPathDNA createRandomIndividual() {
    StringPathDNA dna = new StringPathDNA(this);
    for (int i=0; i<this.nbNailsInStringPath; i++) dna.addNail(i);
    return dna;
  }

  public boolean isValid(StringPathDNA dna) {
    boolean ok = true;
    int prevNail = dna.getBase(0).getNail();
    for (int i=1; ok && i<dna.getSize()-1; i++) {
      int curNail = dna.getBase(i).getNail();
      ok = Math.abs(prevNail - curNail) > minNailDiff;
      prevNail = curNail;
    }    
    return ok;
  }

  boolean isNailValueValid(List<StringPathBase> bases, int index) {
    boolean isValid = true;
    int curNail =  bases.get(index).getNail();
    if (index != 0) {
      int prevNail = bases.get(index-1).getNail();
      isValid = Math.abs(prevNail - curNail) > minNailDiff;
    } 
    
    if (isValid && index+1<bases.size()) {
      int nextNail = bases.get(index+1).getNail();
      isValid = Math.abs(curNail - nextNail) > minNailDiff;
    }
    
    return isValid;
  }

  public boolean isEdgeWayEnabled() {
    return this.edgeWayEnabled;
  }

  public boolean getDefaultEdgeWay() {
    return this.defaultEdgeWay;
  }

  public int getNbNails() {
    return this.nbNails;
  }

}
