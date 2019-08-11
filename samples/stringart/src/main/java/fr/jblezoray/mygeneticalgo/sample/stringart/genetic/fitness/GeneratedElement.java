package fr.jblezoray.mygeneticalgo.sample.stringart.genetic.fitness;

import java.util.Collections;
import java.util.List;

import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed.CompressedUnboundedImage;

class GeneratedElement implements Comparable<GeneratedElement> {
  private final List<Edge> edges;
  private final UnboundedImage generated;
  private int reusabilityScore;
  
  public GeneratedElement(List<Edge> edges, UnboundedImage generated) {
    this.edges = Collections.unmodifiableList(edges);
    this.generated = generated;
    this.reusabilityScore = 0;
  }
  
  @Override
  public String toString() {
    return edges.toString();
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public UnboundedImage getGenerated() {
    return generated;
  }

  public void resetReusabilityScore() {
    this.reusabilityScore = 0;
  }
  
  public void incrementReusabilityScore() {
    synchronized (this) {
      this.reusabilityScore++;
    }
  }

  public int getReusabilityScore() {
    return reusabilityScore;
  }

  public void setReusabilityScore(int reusabilityScore) {
    this.reusabilityScore = reusabilityScore;
  }

  @Override
  public int compareTo(GeneratedElement o) {
    return o.reusabilityScore - this.reusabilityScore;
  }
  
  
}