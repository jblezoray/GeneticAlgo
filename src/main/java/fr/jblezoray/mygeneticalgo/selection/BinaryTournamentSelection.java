package fr.jblezoray.mygeneticalgo.selection;

import java.util.List;
import java.util.Random;

import fr.jblezoray.mygeneticalgo.ISelection;
import fr.jblezoray.mygeneticalgo.dna.AbstractDNA;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

/**
 * Selects two individuals to mate using a binary tournament selection.
 * 
 * For selecting each member of the matingPair, the binary tournament selection
 * takes two random individuals, and selects the best of both.
 * 
 * @see https://en.wikipedia.org/wiki/Tournament_selection
 * @see https://cstheory.stackexchange.com/a/14760
 */
public class BinaryTournamentSelection<X extends AbstractDNA> implements ISelection<X> {
  
  private List<X> population;

  @Override
  public void initialize(List<X> population) {
    this.population = population;
  }

  @Override
  public MatingPair<X> selectMatingPair() {
    MatingPair<X> matingPair = new MatingPair<>(); 
    matingPair.mate1 = getIndividual();
    matingPair.mate2 = getIndividual();
    return matingPair;
  }
  
  private X getIndividual() {
    Random random = RandomSingleton.instance();
    X a = population.get(random.nextInt(population.size()));
    X b = population.get(random.nextInt(population.size()));
    X best = a.getFitness() > b.getFitness() ? a : b;
    return best;
  }
}
