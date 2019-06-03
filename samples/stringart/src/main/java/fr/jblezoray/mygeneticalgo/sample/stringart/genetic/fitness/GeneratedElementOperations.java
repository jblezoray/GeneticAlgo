package fr.jblezoray.mygeneticalgo.sample.stringart.genetic.fitness;

import java.util.ArrayList;
import java.util.List;

import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;

public class GeneratedElementOperations {
  GeneratedElement generatedElement;
  
  int curIndex;
  
  List<Edge> diffToDel;
  
  List<Edge> diffAdded;
  
  int contributionScore;
  
  public GeneratedElementOperations(GeneratedElement wrappedGeneratedElement) {
    this.generatedElement = wrappedGeneratedElement;
    this.curIndex = 0;
    this.diffToDel = new ArrayList<>();
    this.diffAdded = new ArrayList<>();
    this.contributionScore = 0;
  }
}