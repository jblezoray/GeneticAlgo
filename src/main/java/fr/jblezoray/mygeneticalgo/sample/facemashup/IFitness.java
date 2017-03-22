package fr.jblezoray.mygeneticalgo.sample.facemashup;

public interface IFitness {

  /**
   * Computes the fitness of {@code toEvaluate} FaceImage relatively to this
   * one. 
   * @param candidateToEvaluate
   * @return an evaluation note in range [0.0f, 1.0f] 
   */
  public double computeFitnessOf(FaceImage candidateToEvaluate);
  
}