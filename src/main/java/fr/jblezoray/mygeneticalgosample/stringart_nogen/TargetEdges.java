package fr.jblezoray.mygeneticalgosample.stringart_nogen;

import java.util.ArrayList;
import java.util.List;

public class TargetEdges {
  
  private List<Edge> edges = new ArrayList<>();
  
  public static TargetEdges factory() {
    return new TargetEdges();
  }
  
  public List<Edge> getEdges() {
    return edges;
  }
  
}