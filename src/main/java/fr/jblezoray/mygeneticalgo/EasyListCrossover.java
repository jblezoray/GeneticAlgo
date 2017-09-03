package fr.jblezoray.mygeneticalgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EasyListCrossover {

  /**
   * This method implements a straightforward algorithm for making crossovers 
   * between two lists. 
   * 
   * After having run this method, {@code out1} and {@code out2} will 
   * contain crossovered copies of {@code in1} and {@code in1}. There will be a
   * random number of crossover points between {@code minCrossovers} and 
   * {@code maxCrossovers}. 
   * 
   * Both {@code in1} and {@code in1} are not modified. 
   * 
   * @param rand
   * @param minCrossovers minimum number of crossover points 
   * @param maxCrossovers maximum number of crossover points
   * @param in1 the 1st list to crossover. 
   * @param in2 the 2nd list to crossover. 
   * @param out1 pass an empty list in this parameter. 
   * @param out2 pass an empty list in this parameter. 
   */
  public static <A> void doCrossover(Random rand, 
      int minCrossovers, int maxCrossovers,
      List<A> in1, List<A> in2, 
      List<A> out1, List<A> out2) {
    
    int minSize = in1.size()>in2.size() ? in2.size() : in1.size();
    int maxSize = in1.size()>in2.size() ? in1.size() : in2.size();
    
    // output lists must be clean.
    out1.clear();
    out2.clear();
    
    
    // build a list of random crossover points. 
    List<Integer> crossoversList = new ArrayList<Integer>();
    if (minSize>0) {
      int nbCrossovers = rand.nextInt(maxCrossovers - minCrossovers) + minCrossovers;
      for (int i=0; i<nbCrossovers; i++) {
        int crossoverIndex = rand.nextInt(minSize);
        crossoversList.add(crossoverIndex);
      }
    }
    
    // copy each elements, making crossovers while copying.
    boolean invert = false;
    for (int i=0; i<maxSize; i++) {
      if (crossoversList.contains(new Integer(i))) 
        invert = !invert;
      if (invert) {
        if (in2.size()>i) out1.add(i, in2.get(i));
        if (in1.size()>i) out2.add(i, in1.get(i));
      } else {
        if (in1.size()>i) out1.add(i, in1.get(i));
        if (in2.size()>i) out2.add(i, in2.get(i));
      }
    }
  }
  
  
}
