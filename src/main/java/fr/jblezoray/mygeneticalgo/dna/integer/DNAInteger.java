package fr.jblezoray.mygeneticalgo.dna.integer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.jblezoray.mygeneticalgo.crossover.EasyListCrossover;
import fr.jblezoray.mygeneticalgo.dna.AbstractDNA;
import fr.jblezoray.mygeneticalgo.dna.IDNA;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

/**
 * This class represents the DNA as a list of integers. 
 * 
 * @author jib
 */
public class DNAInteger extends AbstractDNA {
  
  private List<Integer> dnaList;
  private final int minValue;
  private final int maxValue;
  
  private final static Random rand = RandomSingleton.instance();
  
  /**
   * Creates a DNA that contains a list of random integers.
   * @param dnaLength The created DNA will be of this size.
   * @param minValue Minimum value for an integer in the list (inclusive).
   * @param maxValue Maximum value for an integer in the list (exclusive). 
   *    a DNA fragment. 
   */
  public DNAInteger(int dnaLength, int minValue, int maxValue) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.dnaList = new ArrayList<Integer>(dnaLength);
    for(int i=0; i<dnaLength; i++)
      this.dnaList.add(getRandomBase());
  }

  private DNAInteger(DNAInteger toCopy) {
    this.minValue = toCopy.minValue;
    this.maxValue = toCopy.maxValue;
    this.dnaList = new ArrayList<Integer>(toCopy.dnaList.size());
    this.dnaList.addAll(toCopy.dnaList);
  }
  
  
  private int getRandomBase() {
    return rand.nextInt(maxValue - minValue) + minValue;
  }

  @Override
  @SuppressWarnings("unchecked")
  public DNAInteger copy() {
    return new DNAInteger(this);
  }
  
  public int size() {
    return this.dnaList.size();
  }
  
  public int getElementAt(int position) {
    return this.dnaList.get(position);
  }

  public List<Integer> getList() {
    return this.dnaList;
  }
  
  public void addRandomElement() {
    this.dnaList.add(getRandomBase());
  }
  
  @Override
  public void doMutate(float mutationRate) {
    int dnaLength = dnaList.size();
    int nbMutations = (int)Math.ceil(dnaLength * mutationRate);
    Random rand = RandomSingleton.instance();
    for (int i=0; i<nbMutations; i++) {
      int mutationIndex = rand.nextInt(dnaLength);
      int newValue = rand.nextInt(maxValue - minValue) + minValue;
      dnaList.set(mutationIndex, newValue);
    }
  }

  
  @Override
  public void doDNACrossover(IDNA otherDNAInteger, 
      int minCrossovers, int maxCrossovers) {

    if (! (otherDNAInteger instanceof DNAInteger))
      throw new RuntimeException("'otherDNAInteger' must be of type DNAInteger");
    DNAInteger other = (DNAInteger) otherDNAInteger;
    
    List<Integer> out1 = new ArrayList<>(), out2 = new ArrayList<>();
    EasyListCrossover.<Integer>doCrossover(minCrossovers, maxCrossovers,
        this.dnaList, ((DNAInteger)other).dnaList, out1, out2);
    this.dnaList = out1;
    ((DNAInteger)other).dnaList = out2;
  }

}
