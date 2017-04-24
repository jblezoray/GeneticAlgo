package fr.jblezoray.mygeneticalgo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GeneticAlgo {

  private static final Random RANDOM = new Random();

  private static final double DEFAULT_TOURNAMENT_FRACTION = 0.6f;
  private static final float DEFAULT_MUTATION_RATE = 0.001f;
  private static final int DEFAULT_MIN_CROSSOVER = 1;
  private static final int DEFAULT_MAX_CROSSOVER = 3;
  
  private final int populationSize;
  private final int numberOfBases;
  private final IPhenotype phenotype;
  
  private int generationCounter = 0;
  private int tournamentSize;
  private List<DNA> population;
  
  private int minCrossovers;
  private int maxCrossovers;
  private float mutationRate;

  private final static Comparator<DNA> BETTER_FITNESS_FIRST = 
      new Comparator<DNA>() {
        @Override
        public int compare(DNA i1, DNA i2) {
          return (i1==null || i2 == null) ? 0
              : - Double.compare(i1.getFitness(), i2.getFitness());
        }
      };
  
  /**
   * Create a new Genetic algorithm with an initial population.
   * @param populationSize
   * @param numberOfBases
   * @param initialDNAlength
   * @param resultListener
   */
  public GeneticAlgo(int populationSize, int numberOfBases, int initialDNAlength,
      IPhenotype phenotype) {
    this.populationSize = populationSize;
    this.numberOfBases = numberOfBases;
    this.phenotype = phenotype;
    this.setTournamentFraction(DEFAULT_TOURNAMENT_FRACTION);
    this.setCrossoversRange(DEFAULT_MIN_CROSSOVER, DEFAULT_MAX_CROSSOVER);
    this.setMutationRate(DEFAULT_MUTATION_RATE);
    populatePopulation(initialDNAlength);
  }
  
  /**
   * 
   * @param tournamentFraction
   */
  public void setTournamentFraction(double tournamentFraction) {
    this.tournamentSize = (int) Math.ceil(populationSize*tournamentFraction);
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
   * The mutation rate is the probability of an injecting a mutation 
   * 
   * Higher mutation rate enables a faster convergence, but less precision.
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
  public List<DNA> getPopulation () {
    this.population.sort(BETTER_FITNESS_FIRST);
    return this.population;
  }
  
  
  /**
   * Creates a random new population. 
   * 
   * Each individual of the population is created with a random list of 
   * integers.
   */
  private void populatePopulation(int initialDNAlength) {
    this.population = new ArrayList<>(this.populationSize);
    for (int i=0; i<this.populationSize; i++) {
      DNA dna = DNA.create(RANDOM, initialDNAlength, this.numberOfBases);
      this.population.add(dna);
    }
  }
  

  /**
   * Evolve the population.
   * @param howManyGenerations
   *    The population will evolve for this much generations. 
   */
  public void evolve(int howManyGenerations) {
    boolean hasBestEvaluationResult = false; 
    for (int i=0; i<howManyGenerations && !hasBestEvaluationResult; i++)
      hasBestEvaluationResult = evolve();
  }
  
  
  /**
   * Evolve the population for one generation.
   * 
   * @return
   */
  public boolean evolve() {
    this.generationCounter++;
    
    // compute fitness
    this.population.parallelStream().forEach(dna -> {
      double fitness = this.phenotype.computeFitness(dna);
      dna.setFitness(fitness);
    });
    
    // notify of best result for this generation
    this.population.sort(BETTER_FITNESS_FIRST);
    DNA bestOne = this.population.get(0);
    phenotype.notificationOfBestMatch(generationCounter, bestOne);
    
    // create a new population.
    List<DNA> newPopulation = new ArrayList<>(this.populationSize);
    int i=0;
    while (i+2<=this.populationSize) {
      i+=2;
      MatingPair parentsPair = tournamentSelection();
      MatingPair childPair = doDnaCrossover(parentsPair);
      doMutate(childPair.getMate1());
      doMutate(childPair.getMate2());
      newPopulation.add(new DNA(childPair.getMate1()));
      newPopulation.add(new DNA(childPair.getMate2()));
    }
    this.population = newPopulation;
    return false;
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

    int nbCrossovers = RANDOM.nextInt(this.maxCrossovers - this.minCrossovers)
        + this.minCrossovers;
    
    // work on copies to not alter originals.
    boolean takeChild1atFirst = RANDOM.nextBoolean();
    DNA dnaA = new DNA( takeChild1atFirst ? pair.getMate1() : pair.getMate2());
    DNA dnaB = new DNA( takeChild1atFirst ? pair.getMate2() : pair.getMate1());
    
    int minSize = dnaA.size()>dnaB.size() ? dnaB.size() : dnaA.size(); 
    for (int i=0; i<nbCrossovers; i++) {
      int pivot = RANDOM.nextInt(minSize);
      List<Integer> a1 = dnaA.subList(0, pivot);
      List<Integer> a2 = dnaA.subList(pivot, dnaA.size());
      List<Integer> b1 = dnaB.subList(0, pivot);
      List<Integer> b2 = dnaB.subList(pivot, dnaB.size());
      a1.addAll(b2);
      b1.addAll(a2);
      dnaA = new DNA(a1);
      dnaB = new DNA(b1);
    }
    
    MatingPair childs = new MatingPair();
    childs.setMate1(new DNA(dnaA));
    childs.setMate2(new DNA(dnaB));
    return childs;
  }

  
  /**
   * Mutate DNA so many times according to mutation rate.
   * @param dna
   */
  private void doMutate(DNA dna) {
    
    // TODO this method should not be deterministic in terms of number of mutation.  
    int dnaLength = dna.size();
    int nbMutations = (int)Math.ceil(dnaLength * this.mutationRate);
    for (int i=0; i<nbMutations; i++) {
      int mutationIndex = RANDOM.nextInt(dnaLength);
      int newValue = RANDOM.nextInt(this.numberOfBases);
      dna.set(mutationIndex, newValue);
    }
  }
  
}
