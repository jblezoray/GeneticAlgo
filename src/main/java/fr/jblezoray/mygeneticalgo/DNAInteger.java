package fr.jblezoray.mygeneticalgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents the DNA as a list of integers. 
 * 
 * @author jib
 */
public class DNAInteger extends DNAAbstract {
  
  private List<Integer> dnaList;
  private final int minValue;
  private final int maxValue;
  
  /**
   * Creates a DNA that contains a list of random integers.
   * @param rand an initialized Random object.
   * @param dnaLength The created DNA will be of this size.
   * @param minValue Minimum value for an integer in the list.
   * @param maxValue Maximum value for an integer in the list. 
   *    a DNA fragment. 
   */
  public DNAInteger(Random rand, int dnaLength, int minValue, int maxValue) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.dnaList = new ArrayList<Integer>(dnaLength);
    for(int i=0; i<dnaLength; i++){
      int value = rand.nextInt(maxValue) - minValue;
      this.dnaList.add(value);
    }
  }

  private DNAInteger(DNAInteger toCopy) {
    this.minValue = toCopy.minValue;
    this.maxValue = toCopy.maxValue;
    this.dnaList = new ArrayList<Integer>(toCopy.dnaList.size());
    this.dnaList.addAll(toCopy.dnaList);
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
  
  @Override
  public void doMutate(Random rand, float mutationRate) {
    int dnaLength = dnaList.size();
    int nbMutations = (int)Math.ceil(dnaLength * mutationRate);
    for (int i=0; i<nbMutations; i++) {
      int mutationIndex = rand.nextInt(dnaLength);
      int newValue = rand.nextInt(maxValue) - minValue;
      dnaList.set(mutationIndex, newValue);
    }
  }

  
  @Override
  public void doDNACrossover(Random rand, DNAAbstract otherDNAInteger, 
      int minCrossovers, int maxCrossovers) {

    if (! (otherDNAInteger instanceof DNAInteger))
      throw new RuntimeException("'otherDNAInteger' must be of type DNAInteger");
    DNAInteger other = (DNAInteger) otherDNAInteger;
    
    List<Integer> out1 = new ArrayList<>(), out2 = new ArrayList<>();
    EasyListCrossover.<Integer>doCrossover(rand, minCrossovers, maxCrossovers,
        this.dnaList, ((DNAInteger)other).dnaList, out1, out2);
    this.dnaList = out1;
    ((DNAInteger)other).dnaList = out2;
  }

}
