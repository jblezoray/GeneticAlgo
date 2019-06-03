package fr.jblezoray.mygeneticalgo.sample.stringart.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.jblezoray.mygeneticalgo.crossover.EasyListCrossover;
import fr.jblezoray.mygeneticalgo.dna.AbstractDNA;
import fr.jblezoray.mygeneticalgo.dna.IDNA;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

public class EdgeListDNA extends AbstractDNA {

  private final EdgeFactory edgeFactory;
  private final int minNailDiff;
  private final int nbNails;
  private final boolean edgeWayEnabled;
  private final boolean defaultEdgeWay;
  private List<DirectedEdge> bases;

  private EdgeListDNA(EdgeListDNA toCopy) {
    this.edgeFactory = toCopy.edgeFactory;
    this.minNailDiff = toCopy.minNailDiff;
    this.nbNails = toCopy.nbNails;
    this.edgeWayEnabled = toCopy.edgeWayEnabled;
    this.defaultEdgeWay = toCopy.defaultEdgeWay;
    this.bases = new ArrayList<DirectedEdge>(toCopy.bases.size());
    for (DirectedEdge b : toCopy.bases) 
      this.bases.add(b);
  }
  
  public EdgeListDNA(EdgeFactory edgeFactory, int minNailDiff, int nbNails, 
      boolean edgeWayEnabled, boolean defaultEdgeWay) {
    this.edgeFactory = edgeFactory;
    this.minNailDiff = minNailDiff;
    this.nbNails = nbNails;
    this.edgeWayEnabled = edgeWayEnabled;
    this.defaultEdgeWay = defaultEdgeWay;
    this.bases = new ArrayList<>();
  }
  
  public int getSize() {
    return bases.size();
  }

  public Edge getBase(int index) {
    return this.bases.get(index).edge;
  }

  public List<Edge> getAllEdges() {
    return this.bases.stream()
        .map(DirectedEdge::getEdge)
        .collect(Collectors.toList());
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("[");
    int i;
    DirectedEdge de = null;
    for(i=0; i<this.bases.size(); i++) {
      de = this.resolveEdge(i).get();
      sb.append(de.getStartNail())
          .append(de.getStartNailWay() ? '+' : '-')
          .append(',');
    }
    if (i>0) {
      sb.append(de.getEndNail())
        .append(de.getEndNailWay() ? '+' : '-');
    }
    return sb.append("]").toString();
  }
  
  String toStringDebug() {
    StringBuilder sb = new StringBuilder("[");
    for (DirectedEdge e : this.bases) {
      sb.append('(')
          .append(e.getStartNail())
          .append(e.getStartNailWay() ? '+' : '-')
          .append(',')
          .append(e.getEndNail())
          .append(e.getEndNailWay() ? '+' : '-')
          .append(')');
    }
    return sb.append("]").toString();
  }


  @Override
  public void doMutate(float mutationRate) {
    int nbMutations = (int)Math.ceil(this.bases.size() * mutationRate);
    
    for (int i=0; i<nbMutations; i++) {
      int dnaLength = this.bases.size(); // size may change on each iteration.
      int mutationIndex = RandomSingleton.instance().nextInt(dnaLength);
      switch(RandomSingleton.instance().nextInt(this.edgeWayEnabled ? 4 : 3)) {
      case 0: 
        addEdge(mutationIndex); break;
      case 1: 
        deleteEdge(mutationIndex); break;
      case 2:
        mutateEdge(mutationIndex); break;
      case 3:
      default: 
        mutateTurn(mutationIndex); break;
      }
    }
  }
  
  public Optional<DirectedEdge> resolveEdge(int index) {
    return index>=bases.size() || index<0
        ? Optional.empty() 
        : Optional.of(this.bases.get(index));
  }

  private Optional<DirectedEdge> findDirectedEdge(
      int startNail, boolean startWay, int endNail, boolean endWay) {
    return this.edgeFactory
        .getEdge(startNail, startWay, endNail, endWay)
        .map(e -> new DirectedEdge(e, e.getNailA()==startNail));
  }
  
  private class DirectedEdge {
    private final Edge edge;
    private final boolean startNailIsA;

    DirectedEdge(Edge edge, boolean startNailIsA) {
      this.edge = edge;
      this.startNailIsA = startNailIsA;
    }
    
    Edge getEdge() {
      return edge;
    }
    
    int getStartNail() {
      return startNailIsA ? edge.getNailA() : edge.getNailB();
    }
    
    boolean getStartNailWay() {
      return startNailIsA ? edge.isNailAClockwise() : edge.isNailBClockwise();
    }
    
    int getEndNail() {
      return startNailIsA ? edge.getNailB() : edge.getNailA();
    }
    
    boolean getEndNailWay() {
      return startNailIsA ? edge.isNailBClockwise() : edge.isNailAClockwise();
    }
  }

  private boolean randomWay() {
    return edgeWayEnabled ? RandomSingleton.instance().nextBoolean() : defaultEdgeWay;
  };
  
  private int randomNail() {
    return RandomSingleton.instance().nextInt(this.nbNails);
  };
  
  
  
  /**
   * Mutate the end nail of the edge at intex 'mutationIndex'
   * <p/>
   *  The next edge, if any, will also be mutated for consistency.
   *  
   * @param mutationIndex
   */
  void mutateEdge(int mutationIndex) {
    Optional<DirectedEdge> curOpt = resolveEdge(mutationIndex);
    if (!curOpt.isPresent()) return ;
    DirectedEdge cur = curOpt.get(); 
    Optional<DirectedEdge> oNext = resolveEdge(mutationIndex+1);
    
    boolean done = false;
    do {
      // this nail will replace the end Nail of 'cur'.
      int newNail = randomNail();
      boolean newNailWay = randomWay();
      
      // replace the edge that is at the mutation index. 
      Optional<DirectedEdge> curReplacement = findDirectedEdge(
          cur.getStartNail(), cur.getStartNailWay(), newNail, newNailWay);
      if (curReplacement.isPresent()) {
        // if there is a next, it also must have a suitable replacement.
        if (oNext.isPresent()) {
          DirectedEdge next = oNext.get();
          Optional<DirectedEdge> nextRemplacement = findDirectedEdge(
              newNail, newNailWay, next.getEndNail(), next.getEndNailWay());
          if (nextRemplacement.isPresent()) {
            this.bases.set(mutationIndex, curReplacement.get());
            this.bases.set(mutationIndex+1, nextRemplacement.get());
            done = true;
          } 
          
        } else {
          this.bases.set(mutationIndex, curReplacement.get());
          done = true;
        }
      }
    } while (!done);
  }

  /**
   * Mutate the way of the end nail of the edge at index 'mutationIndex'.
   * <p/>
   *  The way of the start nail of the next edge, if any, will also be mutated
   *  for consistency.
   *  
   * @param mutationIndex
   */
  void mutateTurn(int mutationIndex) {
    if (!this.edgeWayEnabled) return;
    
    DirectedEdge cur = resolveEdge(mutationIndex)
        .orElseThrow(() -> new RuntimeException());

    DirectedEdge curReplacement = findDirectedEdge(
            cur.getStartNail(), cur.getStartNailWay(), 
            cur.getEndNail(), ! cur.getEndNailWay())
        .orElseThrow(() -> new RuntimeException());
    this.bases.set(mutationIndex, curReplacement);
    
    // if a next is present, it must be replaced as well.
    Optional<DirectedEdge> nextOpt = resolveEdge(mutationIndex+1);
    if (nextOpt.isPresent()) {
      DirectedEdge next = nextOpt.get();
      DirectedEdge nextReplacement = findDirectedEdge(
              next.getStartNail(), ! next.getStartNailWay(), 
              next.getEndNail(), next.getEndNailWay())
          .orElseThrow(() -> new RuntimeException());
      this.bases.set(mutationIndex, nextReplacement);
    }
  }

  
  void deleteEdge(int mutationIndex) {
    if (mutationIndex>=0) {
      if (mutationIndex<this.bases.size()) this.bases.remove(mutationIndex);
      // if it is in this.bases, but not at the last position. 
      if (mutationIndex<this.bases.size()) mutateEdge(mutationIndex-1);
    }
  }

  
  void addEdge(int mutationIndex) {
    Optional<DirectedEdge> prevOpt = resolveEdge(mutationIndex -1);
    Optional<DirectedEdge> curOpt = resolveEdge(mutationIndex);

    // read the start nail, or the end of the previous one, or get a random one.  
    int startNail = curOpt.map(DirectedEdge::getStartNail)
        .orElse(
            prevOpt.map(DirectedEdge::getEndNail)
                .orElse(randomNail())
        );
    boolean startWay = curOpt.map(DirectedEdge::getStartNailWay)
        .orElse(
            ! prevOpt.map(DirectedEdge::getEndNailWay)
                .orElse(randomWay())
        );
    
    boolean done = false;
    do {
      int endNail = randomNail();
      boolean endWay = randomWay();
      Optional<DirectedEdge> insertedEdge = findDirectedEdge(
          startNail, startWay, endNail, endWay);
      
      if (insertedEdge.isPresent()) {

        if (curOpt.isPresent()) {
          // cur will shift to the next position.  It must be replaced as well 
          // to match the new nail.
          DirectedEdge cur = curOpt.get();
          Optional<DirectedEdge> replacedCur = findDirectedEdge(
              endNail, this.edgeWayEnabled? !endWay : endWay, 
              cur.getEndNail(), cur.getEndNailWay());
          if (replacedCur.isPresent()) {
            this.bases.set(mutationIndex, replacedCur.get());
            this.bases.add(mutationIndex, insertedEdge.get());
            done = true;
          }
          
        } else {
          // no curOpt, insertion at the end of the list is easy.  
          this.bases.add(insertedEdge.get());
          done = true;
        }
      }
    } while (!done);
  }
  

  @Override
  public void doDNACrossover(IDNA other, int minCrossovers, int maxCrossovers) {
    if (! (other instanceof EdgeListDNA))
      throw new RuntimeException("'other' must be of type EdgeListDNA");
    EdgeListDNA othr = (EdgeListDNA) other;

    List<DirectedEdge> out1 = new ArrayList<>();
    List<DirectedEdge> out2 = new ArrayList<>();
    List<Integer> crossoverPoints = EasyListCrossover.<DirectedEdge>doCrossover(
        minCrossovers, maxCrossovers, 
        this.bases, ((EdgeListDNA)other).bases, 
        out1, out2);
    this.bases = out1;
    othr.bases = out2;
    
    // mutate until there are only valid values.
    for (Integer crossoverPoint : crossoverPoints) {
      this.mutateEdge(crossoverPoint-1);
      othr.mutateEdge(crossoverPoint-1);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public EdgeListDNA copy() {
    return new EdgeListDNA(this);
  }

  public boolean isValid() {
    Optional<DirectedEdge> edge = this.resolveEdge(0);
    boolean ok = true;
    int prevEndNail = edge.map(DirectedEdge::getEndNail).orElse(-1);
    boolean prevEndWay  = edge.map(DirectedEdge::getEndNailWay).orElse(true);
    for (int i=1; i<this.bases.size() && ok; i++) {
      DirectedEdge e = resolveEdge(i).orElseThrow(()->new RuntimeException());
      
      ok = prevEndNail == e.getStartNail() && 
          this.edgeWayEnabled ^ prevEndWay==e.getStartNailWay();
      
      prevEndNail = e.getEndNail();
      prevEndWay = e.getEndNailWay();
    }
    return ok;
  }
  
}
