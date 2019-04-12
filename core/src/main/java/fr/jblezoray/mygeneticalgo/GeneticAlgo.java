package fr.jblezoray.mygeneticalgo;

import java.util.ArrayList;
import java.util.List;

import fr.jblezoray.mygeneticalgo.ISelection.MatingPair;
import fr.jblezoray.mygeneticalgo.dna.DNAFitnessComparator;
import fr.jblezoray.mygeneticalgo.dna.IDNA;

public class GeneticAlgo<X extends IDNA> {


  private static final float DEFAULT_MUTATION_RATE = 0.005f;
  private static final int DEFAULT_MIN_CROSSOVER = 1;
  private static final int DEFAULT_MAX_CROSSOVER = 3;
  
  private final IFitness<X> fitness;
  private final ISelection<X> selection;
  
  private int generationCounter = 0;
  private List<X> population;
  
  private int minCrossovers;
  private int maxCrossovers;
  private float mutationRate;
  private List<IGeneticAlgoListener<X>> listeners = new ArrayList<>();

  
  /**
   * Create a new Genetic algorithm with an initial population.
   * 
   * @param fitness    the fitness method. 
   * @param dnaFactory a factory to generate random individuals.  
   * @param selection  a method to select parents to breed.
   * @param populationSize
   */
  public GeneticAlgo(IFitness<X> fitness, IDNAFactory<X> dnaFactory, 
      ISelection<X> selection, int populationSize) {
    if (populationSize < 2)
      throw new RuntimeException("population size must be >= 2");
    this.population = new ArrayList<>();
    for (int i=0; i<populationSize; i++)
      this.population.add(dnaFactory.createRandomIndividual());
    this.fitness = fitness;
    this.selection = selection;
    this.setCrossoversRange(DEFAULT_MIN_CROSSOVER, DEFAULT_MAX_CROSSOVER);
    this.setMutationRate(DEFAULT_MUTATION_RATE);
  }
  
  public void addListener(IGeneticAlgoListener<X> listener) {
    this.listeners.add(listener);
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
  public List<X> getPopulation() {
    this.population.sort(new DNAFitnessComparator<X>());
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
      double fitness = this.fitness.computeFitnessOf(dna);
      dna.setFitness(fitness);
    });
    
    // notification for the results for this generation
    this.generationCounter++;
    this.population.sort(new DNAFitnessComparator<X>());
    X bestOne = this.population.get(0);
    double[] fitnessScores = this.population.stream()
        .mapToDouble(indiv -> indiv.getFitness())
        .toArray();
    this.listeners.forEach(listener -> {
      listener.notificationOfGeneration(this.generationCounter, bestOne, fitnessScores);
    });
    
    // create a new population.
    List<X> newPopulation = new ArrayList<>(this.population.size());
    int i=0;
    this.selection.initialize(this.population);
    while (i+2<=this.population.size()) {
      i+=2;
      // select 2 parents
      MatingPair<X> parentsPair = this.selection.selectMatingPair();
      
      // copy the DNA of the parents  
      MatingPair<X> childPair = new MatingPair<>();
      childPair.mate1 = parentsPair.mate1.copy();
      childPair.mate2 = parentsPair.mate2.copy();
      
      // crossover and mutate the two children. 
      childPair.mate1.doDNACrossover(childPair.mate2, this.minCrossovers, this.maxCrossovers);
      childPair.mate1.doMutate(this.mutationRate);
      childPair.mate2.doMutate(this.mutationRate);
      
      // add to the new population.
      newPopulation.add(childPair.mate1);
      newPopulation.add(childPair.mate2);
    }
    this.population = newPopulation;
  }
  
}
