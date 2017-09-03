package fr.jblezoray.mygeneticalgo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GeneticAlgo<DNA extends DNAAbstract> {

  private static final Random RANDOM = new Random(System.currentTimeMillis());

  private static final double DEFAULT_TOURNAMENT_FRACTION = 0.6f;
  private static final float DEFAULT_MUTATION_RATE = 0.005f;
  private static final int DEFAULT_MIN_CROSSOVER = 1;
  private static final int DEFAULT_MAX_CROSSOVER = 3;
  
  private final IPhenotype<DNA> phenotype;
  
  private int generationCounter = 0;
  private int tournamentSize;
  private List<DNA> population;
  
  private int minCrossovers;
  private int maxCrossovers;
  private float mutationRate;

  private final static Comparator<DNAAbstract> BETTER_FITNESS_FIRST = 
      new Comparator<DNAAbstract>() {
        @Override
        public int compare(DNAAbstract i1, DNAAbstract i2) {
          return (i1==null || i2 == null) ? 0
              : - Double.compare(i1.getFitness(), i2.getFitness());
        }
      };
  
  
  /**
   * Create a new Genetic algorithm with an initial population.
   * @param phenotype
   */
  public GeneticAlgo(IPhenotype<DNA> phenotype) {
    Collection<DNA> population = phenotype.createInitialPopulation(RANDOM);
    if (population.size() < 2)
      throw new RuntimeException("population size must be >= 2");
    this.population = new ArrayList<DNA>(population.size());
    this.population.addAll(population);
    this.phenotype = phenotype;
    this.setTournamentFraction(DEFAULT_TOURNAMENT_FRACTION);
    this.setCrossoversRange(DEFAULT_MIN_CROSSOVER, DEFAULT_MAX_CROSSOVER);
    this.setMutationRate(DEFAULT_MUTATION_RATE);
  }
  
  /**
   * 
   * @param tournamentFraction
   */
  public void setTournamentFraction(double tournamentFraction) {
    this.tournamentSize = (int) Math.ceil(this.population.size() * tournamentFraction);
  }

  /**
   * 
   * @param minCrossovers
   * @param maxCrossovers
   */
  public void setCrossoversRange(int minCrossovers, int maxCrossovers) {
    this.minCrossovers = minCrossovers;
    this.maxCrossovers = maxCrossovers;
  }

  /**
   * Adjust the mutation rate.
   * 
   * The mutation rate is the probability for a DNA base to be mutated at each 
   * generation. Higher mutation rates enable faster convergence, but has less 
   * precision.
   * 
   * @param mutationRate
   */
  public void setMutationRate(float mutationRate) {
    this.mutationRate = mutationRate;
  }
  
  
  /**
   * getter of the current population. 
   * @return
   *    current population, ordered with best fitness first.
   */
  public List<DNA> getPopulation() {
    this.population.sort(BETTER_FITNESS_FIRST);
    return this.population;
  }
  

  /**
   * Evolve the population.
   * @param howManyGenerations
   *    The population will evolve for this much generations. 
   */
  public void evolve(int howManyGenerations) {
    for (int i=0; i<howManyGenerations; i++) {
      evolve();
    }
  }
  
  
  /**
   * Evolve the population for one generation.
   */
  public void evolve() {
    // compute fitness
    this.population.parallelStream().forEach(dna -> {
      double fitness = this.phenotype.computeFitness(dna);
      dna.setFitness(fitness);
    });
    
    // notification for the best result in this generation
    this.population.sort(BETTER_FITNESS_FIRST);
    DNA bestOne = this.population.get(0);
    phenotype.notificationOfBestMatch(++this.generationCounter, bestOne);
    
    // create a new population.
    List<DNA> newPopulation = new ArrayList<>(this.population.size());
    int i=0;
    while (i+2<=this.population.size()) {
      i+=2;
      // select 2 parents
      MatingPair parentsPair = tournamentSelection();
      
      // copy the DNA of the parents  
      MatingPair childPair = new MatingPair();
      childPair.mate1 = parentsPair.mate1.copy();
      childPair.mate2 = parentsPair.mate2.copy();
      
      // crossover and mutate the two children. 
      childPair.mate1.doDNACrossover(RANDOM, childPair.mate2, this.minCrossovers, this.maxCrossovers);
      childPair.mate1.doMutate(RANDOM, this.mutationRate);
      childPair.mate2.doMutate(RANDOM, this.mutationRate);
      
      // add to the new population.
      newPopulation.add(childPair.mate1);
      newPopulation.add(childPair.mate2);
    }
    this.population = newPopulation;
  }

  
  private class MatingPair {
    DNA mate1;
    DNA mate2;
  }

  
  /**
   * Selects two individuals to mate using tournament selection.
   * 
   * This type of selection grabs the best two from a random subset of the 
   * population.
   * 
   * TODO this method could be externalize and abstracted to allow using other
   * types of selections. 
   * 
   * @see https://en.wikipedia.org/wiki/Tournament_selection
   * @return
   */
  private MatingPair tournamentSelection() {
    // Choose 'tournamentSize' elements randomly from the population.
    List<DNA> chosenOnes = new ArrayList<>(this.tournamentSize);
    List<DNA> populationCopy = new ArrayList<>(this.population);
    for (int i=0; i<this.tournamentSize; i++) {
      int randomIndex = RANDOM.nextInt(populationCopy.size());
      DNA chosenOne = populationCopy.get(randomIndex);
      populationCopy.remove(chosenOne);
      chosenOnes.add(chosenOne);
    }
    
    // Select the two best fit individuals from tournament
    // (size is always >2 at this moment).
    chosenOnes.sort(BETTER_FITNESS_FIRST);
    MatingPair matingPair = new MatingPair(); 
    matingPair.mate1 = chosenOnes.get(0);
    matingPair.mate2 = chosenOnes.get(1);
    
    return matingPair;
  }
  
}
