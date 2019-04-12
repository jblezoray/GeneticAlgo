package fr.jblezoray.mygeneticalgo.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.jblezoray.mygeneticalgo.ISelection;
import fr.jblezoray.mygeneticalgo.dna.AbstractDNA;
import fr.jblezoray.mygeneticalgo.dna.DNAFitnessComparator;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

/**
 * Selects two individuals to mate using a Fitness proportionate selection, aka
 * roulette wheel selection.
 * 
 * @see https://en.wikipedia.org/wiki/Fitness_proportionate_selection
 */
public class FitnessProportionateSelection<X extends AbstractDNA> implements ISelection<X> {
  
  private List<X> population;
  private Double totalSumOfFitnesses;

  @Override
  public void initialize(List<X> population) {
    // create a copy of the population, sorted by fitness.
    this.population = new ArrayList<>(population);
    this.population.sort(new DNAFitnessComparator<X>());
    
    // sum of all fitnesses. 
    this.totalSumOfFitnesses = this.population.stream()
        .mapToDouble(X::getFitness)
        .sum();
  }

  @Override
  public MatingPair<X> selectMatingPair() {
    MatingPair<X> matingPair = new MatingPair<>(); 
    matingPair.mate1 = getIndividual();
    matingPair.mate2 = getIndividual();
    return matingPair;
  }
  
  private X getIndividual() {
    
    // random double in [0, this.totalSumOfFitnesses]
    Random random = RandomSingleton.instance();
    Double value = random.nextDouble() * this.totalSumOfFitnesses;
    
    // locate the individual based on the fitness.
    for (X individual : this.population) {
      value -= individual.getFitness();
      if (value < 0) {
        return individual;
      }
    }
    
    return this.population.get(this.population.size()-1);
  }
}
