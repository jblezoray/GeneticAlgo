package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.jblezoray.mygeneticalgo.crossover.EasyListCrossover;
import fr.jblezoray.mygeneticalgo.dna.AbstractDNA;
import fr.jblezoray.mygeneticalgo.dna.IDNA;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

public class StringPathDNA extends AbstractDNA {

  private static Random RANDOM = RandomSingleton.instance();

  private List<StringPathBase> bases;
  private StringPathDNAFactory factory;

  private StringPathDNA(StringPathDNA toCopy) {
    this.bases = new ArrayList<StringPathBase>(toCopy.bases.size());
    for (StringPathBase b : toCopy.bases) this.bases.add(b.copy());
    this.factory = toCopy.factory;
  }
  
  public StringPathDNA(StringPathDNAFactory factory) {
    this.bases = new ArrayList<>();
    this.factory = factory;
    
  }

  public int getSize() {
    return bases.size();
  }

  public StringPathBase getBase(int index) {
    return this.bases.get(index);
  }

  @Override
  public void doMutate(float mutationRate) {
    int nbMutations = (int)Math.ceil(this.bases.size() * mutationRate);
    
    for (int i=0; i<nbMutations; i++) {
      int dnaLength = this.bases.size(); // size may have changed.
      int mutationIndex = RANDOM.nextInt(dnaLength);
      switch(RANDOM.nextInt(4)) {
      case 0: 
        addNail(mutationIndex); break;
      case 1: 
        deleteNail(mutationIndex); break;
      case 2:
        mutateNail(mutationIndex); break;
      case 3:
        mutateTurn(mutationIndex); break;
      default: 
      }
    }
  }


  private void mutateNail(int mutationIndex) {
    do {
      this.bases.get(mutationIndex).setNail(RANDOM.nextInt(Constants.NB_NAILS));
    } while (!this.factory.isNailValueValid(this.bases, mutationIndex));
  }
  
  private void mutateTurn(int mutationIndex) {
    StringPathBase b = this.bases.get(mutationIndex);
    b.setTurnClockwise(!b.isTurnClockwise());
  }

  private void deleteNail(int mutationIndex) {
    this.bases.remove(mutationIndex);
    // removing a nail may result in an invalid path.  Therefore, we have to 
    // mutate the next node to a valid value. 
    if (mutationIndex!=0 && mutationIndex!=this.bases.size() && 
        !this.factory.isNailValueValid(this.bases, mutationIndex)) {
      mutateNail(mutationIndex);
    }
  }

  void addNail(int mutationIndex) {
    // add node. 
    StringPathBase b = new StringPathBase(
        RANDOM.nextInt(Constants.NB_NAILS), RANDOM.nextBoolean());
    this.bases.add(mutationIndex, b);
    // mutate it until is has a valid value.
    if (!this.factory.isNailValueValid(this.bases, mutationIndex))
      mutateNail(mutationIndex);
  }

  @Override
  public void doDNACrossover(IDNA other, int minCrossovers, int maxCrossovers) {

    if (! (other instanceof StringPathDNA))
      throw new RuntimeException("'other' must be of type EdgeListDNA");

    List<StringPathBase> out1 = new ArrayList<>();
    List<StringPathBase> out2 = new ArrayList<>();
    List<Integer> crossoverPoints = 
        EasyListCrossover.<StringPathBase>doCrossover(
        minCrossovers, maxCrossovers, this.bases, ((StringPathDNA)other).bases, 
        out1, out2);
    this.bases = out1;
    ((StringPathDNA)other).bases = out2;
    
    // mutate until there are only valid values.
    for (Integer crossoverPoint : crossoverPoints) {
      if (!this.factory.isNailValueValid(this.bases, crossoverPoint))
        this.mutateNail(crossoverPoint);
      if (!this.factory.isNailValueValid(((StringPathDNA)other).bases, crossoverPoint))
        ((StringPathDNA)other).mutateNail(crossoverPoint);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public StringPathDNA copy() {
    return new StringPathDNA(this);
  }
  
}
