package fr.jblezoray.mygeneticalgo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GeneticAlgo {

  private static final Random RANDOM = new Random();
  
  /** Value in [2, inf[ */
  private final int populationSize = 100;
  private final int numberOfBases;
  private final int dnaLength;
  
  private int generationCounter = 0;
  /**
   * Part of the population that gets involved in each tournament when finding
   * mates.  
   * Value in [2, populationSize].
   */
  private int tournamentSize;
  private List<Individual> population;
  
  
  private final double tournamentFraction = 0.6f;
  private final IEvolver evolver;
  private final int minCrossovers = 0;
  private final int maxCrossovers = 3;
  private float mutationRate = 0.0001f;
  private IResultListener resultListener = null;

  private final static Comparator<Individual> BETTER_FITNESS_FIRST = 
      new Comparator<Individual>() {
        @Override
        public int compare(Individual i1, Individual i2) {
          return (i1==null || i2 == null) ? 0
              : - Double.compare(i1.getFitness(), i2.getFitness());
        }
      };
  
  
  /**
   * Create a new Genetic algorithm with an initial population.
   * @param dnaLength
   * @param numberOfBases
   * @param evolver
   */
  public GeneticAlgo(int dnaLength, int numberOfBases,
      IEvolver evolver) {
    this.numberOfBases = numberOfBases;
    this.dnaLength = dnaLength;
    this.evolver = evolver;
    this.tournamentSize = (int) Math.ceil(populationSize*tournamentFraction);
    this.generationCounter = 0;
    if (this.tournamentSize<=1) this.tournamentSize=2;
    populatePopulation();
  }
  
  public void setListener(IResultListener resultListener) {
    this.resultListener = resultListener;
  }
  
  
  /**
   * Creates a random new population. 
   * 
   * Each individual of the population is created with a random list of 
   * integers.
   */
  private void populatePopulation() {
    this.population = new ArrayList<>(this.populationSize);
    for (int i=0; i<this.populationSize; i++) {
      DNA dna = DNA.create(RANDOM, this.dnaLength, this.numberOfBases);
      Individual individual = new Individual(dna);
      this.population.add(individual);
    }
  }
  

  /**
   * Evolve the population.
   * @param howManyGenerations
   *    The population will evolve for this much generations. 
   */
  public void evolve(int howManyGenerations) {
    for (int i=0; i<howManyGenerations; i++)
      evolve();
  }
  
  /**
   * Evolve the population for one generation.
   */
  public void evolve() {
    this.generationCounter++;
    
    // compute fitness
    this.population.forEach(individual -> {
      double fitness = this.evolver.computeFitness(individual);
      individual.setFitness(fitness);
    });
    
    // notify of best result for this generation
    if (resultListener!=null) {
      this.population.sort(BETTER_FITNESS_FIRST);
      Individual bestOne = this.population.get(0);
      resultListener.notificationOfBestMatch(generationCounter, bestOne.getFitness(), bestOne.getDNA());
    }
    
    // create a new population.
    List<Individual> newPopulation = new ArrayList<>(this.populationSize);
    for (int i=0; i<this.populationSize; i+=2) {
      MatingPair parentsPair = tournamentSelection();
      MatingPair childPair = doDnaCrossover(parentsPair);
      doMutate(childPair.getMate1().getDNA());
      doMutate(childPair.getMate2().getDNA());
      newPopulation.add(new Individual(childPair.getMate1().getDNA()));
      newPopulation.add(new Individual(childPair.getMate2().getDNA()));
    }
    this.population = newPopulation;
  }

  /**
   * Selects two individuals to mate using tournament selection.
   * 
   * This type of selection grabs the best two from a random subset of the 
   * population.
   * 
   * TODO this method could be externalized and abstracted to allow using other
   * types of selections. 
   * 
   * @see https://en.wikipedia.org/wiki/Tournament_selection
   * @return
   */
  private MatingPair tournamentSelection() {
    // Choose 'tournamentSize' elements randomly from the population.
    List<Individual> chosenOnes = new ArrayList<>(this.tournamentSize);
    List<Individual> populationCopy = new ArrayList<>(this.population);
    for (int i=0; i<this.tournamentSize; i++) {
      int randomIndex = RANDOM.nextInt(populationCopy.size());
      Individual chosenOne = populationCopy.get(randomIndex);
      populationCopy.remove(chosenOne);
      chosenOnes.add(chosenOne);
    }
    
    // Select the two best fit individuals from tournament
    // (size is always >2 at this moment).
    chosenOnes.sort(BETTER_FITNESS_FIRST);
    MatingPair matingPair = new MatingPair(); 
    matingPair.setMate1(chosenOnes.get(0));
    matingPair.setMate2(chosenOnes.get(1)); 
    return matingPair;
  }

  /**
   * Crossover the mating pair's DNA so many times according to crossover rate.
   * 
   * @param pair
   *    The pair to crossover.
   * @return
   *    A copy of the pair, crossovered.
   */
  private MatingPair doDnaCrossover(MatingPair pair) {

    // work on copies to not alter originals.
    DNA child1DNA = new DNA(pair.getMate1().getDNA());
    DNA child2DNA = new DNA(pair.getMate2().getDNA());
    
    int nbCrossovers = RANDOM.nextInt(this.maxCrossovers - this.minCrossovers) + this.minCrossovers;
    for (int i=0; i<nbCrossovers; i++) {
      int pivot = RANDOM.nextInt(this.dnaLength);
      for (int j=pivot; j<this.dnaLength; j++) {
        int element = child1DNA.get(j);
        child1DNA.set(j, child2DNA.get(j));
        child2DNA.set(j, element);
      }
    }
    
    MatingPair childs = new MatingPair();
    childs.setMate1(new Individual(child1DNA));
    childs.setMate2(new Individual(child2DNA));
    return childs;
  }

  
  /**
   * Mutate DNA so many times according to mutation rate.
   * @param dna
   */
  private void doMutate(DNA dna) {
    int nbMutations = (int)Math.ceil(this.dnaLength * this.mutationRate);
    for (int i=0; i<nbMutations; i++) {
      int mutationIndex = RANDOM.nextInt(this.dnaLength);
      int newValue = RANDOM.nextInt(this.numberOfBases);
      dna.set(mutationIndex, newValue);
    }
  }
  
}
