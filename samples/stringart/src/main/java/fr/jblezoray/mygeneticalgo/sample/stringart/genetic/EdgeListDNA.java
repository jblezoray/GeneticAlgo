package fr.jblezoray.mygeneticalgo.sample.stringart.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
  private List<Edge> bases;

  private EdgeListDNA(EdgeListDNA toCopy) {
    this.edgeFactory = toCopy.edgeFactory;
    this.minNailDiff = toCopy.minNailDiff;
    this.nbNails = toCopy.nbNails;
    this.edgeWayEnabled = toCopy.edgeWayEnabled;
    this.defaultEdgeWay = toCopy.defaultEdgeWay;
    this.bases = new ArrayList<Edge>(toCopy.bases.size());
    for (Edge b : toCopy.bases) 
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
    return this.bases.get(index);
  }

  public List<Edge> getAllEdges() {
    return Collections.unmodifiableList(this.bases);
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("[");
    Optional<EnhancedEdge> oee = this.resolveEdge(0);
    while (oee.isPresent()) {
      EnhancedEdge ee = oee.get();
      sb.append(ee.getStartNail())
          .append(ee.getStartNailWay() ? '+' : '-')
          .append(',');
      oee = ee.getNext();
      if (!oee.isPresent()) {
        sb.append(ee.getEndNail())
            .append(ee.getEndNailWay() ? '+' : '-');
      }
    }
    return sb.append("]").toString();
  }
  
  String toStringDebug() {
    StringBuilder sb = new StringBuilder("[");
    for (Edge e : this.bases) {
      sb.append('(')
          .append(e.getNailA())
          .append(e.isNailAClockwise() ? '+' : '-')
          .append(',')
          .append(e.getNailB())
          .append(e.isNailBClockwise() ? '+' : '-')
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
  
  public Optional<EnhancedEdge> resolveEdge(int index) {
    return index>=bases.size() || index<0
        ? Optional.empty() 
        : Optional.of(new EnhancedEdge(this.bases.get(index), index));
  }

  private static int findCommonNail(Edge e1, Edge e2) {
    int commonNail =  
        e1.getNailA() == e2.getNailA() ? e1.getNailA() :
        e1.getNailA() == e2.getNailB() ? e1.getNailA() : 
        e1.getNailB() == e2.getNailA() ? e1.getNailB() : 
        e1.getNailB() == e2.getNailB() ? e1.getNailB() :
        -1;
    if (commonNail==-1) 
      throw new RuntimeException("No common nail between "+e1+" and "+e2+" !");
    return commonNail;
  }
  
  private static int findDifferentNail(Edge fromEdge, Edge notInThisEdge) {
    int commonNail = findCommonNail(fromEdge, notInThisEdge);
    int differentNail = 
        fromEdge.getNailA() == commonNail ? fromEdge.getNailB() : 
        fromEdge.getNailB() == commonNail ? fromEdge.getNailA() :  
        -1;
    if (differentNail==-1)
      throw new RuntimeException("No different nail !");
    return differentNail;
  }
  
  private class EnhancedEdge {
    private Edge edge;
    private int index;
    private Integer startNail;
    
    EnhancedEdge(Edge edge, int index) {
      this.edge = edge;
      this.index = index;
    }
    
    int getStartNail() {
      if (startNail==null){
        Edge current = bases.get(index);
        if (index==0) {
          if (bases.size()>=2) {
            Edge next = bases.get(index+1);
            startNail = findDifferentNail(current, next);
          } else {
            startNail = current.getNailA();
          }
        } else {
          Edge previous = bases.get(index-1);
          startNail = findCommonNail(current, previous);
        }
      }
      return startNail;
    }
    
    boolean getStartNailWay() {
      return edge.wayOf(getStartNail());
    }
    
    int getEndNail() {
      return edge.getNailA() == getStartNail() 
          ? edge.getNailB() : edge.getNailA();
    }
    
    boolean getEndNailWay() {
      return edge.wayOf(getEndNail());
    }
    
    Optional<EnhancedEdge> getPrevious() {
      return index-1 < 0 ? Optional.empty()
          : Optional.of(new EnhancedEdge(bases.get(index-1), index-1));
    }
    
    Optional<EnhancedEdge> getNext() {
      return index+1 >= bases.size() ? Optional.empty()
          : Optional.of(new EnhancedEdge(bases.get(index+1), index+1));
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
    EnhancedEdge cur = resolveEdge(mutationIndex)
        .orElseThrow(() -> new RuntimeException());
    // Don't use cur.getNext() here.  By design, we have chosen to not rely on 
    // the fact that the two nodes share a nail.
    Optional<EnhancedEdge> oNext = resolveEdge(mutationIndex+1);
    
    boolean done = false;
    do {
      // this nail will replace the end Nail of 'cur'.
      int newNail = randomNail();
      boolean newNailWay = randomWay();
      
      // replace the edge that is at the mutation index. 
      Optional<Edge> curReplacement = edgeFactory
          .getEdge(cur.getStartNail(), cur.getStartNailWay(), newNail, newNailWay)
          .filter(r -> cur.getPrevious().map(p->p.edge!=r).orElse(true));
      if (curReplacement.isPresent()) {
        // if there is a next, it also must have a suitable replacement.
        if (oNext.isPresent()) {
          
          // we can't rely on 'next.getEndNail()'.  We have to find the common 
          // nail here. 
          Optional<EnhancedEdge> nno = resolveEdge(mutationIndex+2);
          int endNail = nno.map(EnhancedEdge::getStartNail).orElse(randomNail());
          boolean endWay = nno.map(EnhancedEdge::getStartNailWay).map(b->!b).orElse(randomWay());
          
          Optional<Edge> nextRemplacement = this.edgeFactory
              .getEdge(newNail, newNailWay, endNail, endWay)
              .filter(r -> cur.getNext().map(n->n.edge!=r).orElse(true));
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
   * Mutate the way of the end nail of the edge at intex 'mutationIndex'.
   * <p/>
   *  The way of the start nail of the next edge, if any, will also be mutated
   *  for consistency.
   *  
   * @param mutationIndex
   */
  void mutateTurn(int mutationIndex) {
    EnhancedEdge cur = resolveEdge(mutationIndex)
        .orElseThrow(() -> new RuntimeException());

    Edge curReplacement = edgeFactory.getEdge(
            cur.getStartNail(), cur.getStartNailWay(), 
            cur.getEndNail(), ! cur.getEndNailWay())
        .orElseThrow(() -> new RuntimeException());
    this.bases.set(mutationIndex, curReplacement);
    
    // if a next is present, it must be replaced as well.
    Optional<EnhancedEdge> nextOpt = cur.getNext();
    if (nextOpt.isPresent()) {
      EnhancedEdge next = nextOpt.get();
      Edge nextReplacement = edgeFactory.getEdge(
              next.getStartNail(), ! next.getStartNailWay(), 
              next.getEndNail(), next.getEndNailWay())
          .orElseThrow(() -> new RuntimeException());
      this.bases.set(mutationIndex, nextReplacement);
    }
  }

  
  void deleteEdge(int mutationIndex) {
    EnhancedEdge cur = resolveEdge(mutationIndex)
        .orElseThrow(() -> new RuntimeException());

    Optional<EnhancedEdge> oNext = cur.getNext(); 
    Optional<EnhancedEdge> oPrev = cur.getPrevious();
    if (!oNext.isPresent() || !oPrev.isPresent()) {
      // removal at extremums is straightforward. 
      this.bases.remove(mutationIndex);
      
    } else {
      EnhancedEdge prev = oPrev.get();
      EnhancedEdge next = oNext.get();

      int nail = cur.getStartNail();
      boolean way = cur.getStartNailWay();
      
      // this avoids a blocking case.  We have an implementation constraint that
      // forbids cases like A -> B -> A. This ensures that A -> B -> C -> A does
      // not fall into this when C is removed. 
      while (oPrev.get().getStartNail() == oNext.get().getEndNail())
        mutateEdge(oNext.get().index);
      
      // find x such as A -> x -> B is a valid path. 
      boolean done = false;
      Optional<Edge> oNewPrev, oNewNext;
      do {
        oNewPrev = this.edgeFactory
            .getEdge(prev.getStartNail(), prev.getStartNailWay(), nail, way)
            .filter(newPrev -> cur.getPrevious()
                .map(p->p.getPrevious().map(pp->pp.edge!=newPrev).orElse(true))
                .orElse(true));
        oNewNext = this.edgeFactory
            .getEdge(nail, way, next.getEndNail(), next.getEndNailWay())
            .filter(newNext -> cur.getNext()
                .map(n->n.getNext().map(nn->nn.edge!=newNext).orElse(true))
                .orElse(true));
        done = oNewNext.isPresent() && oNewPrev.isPresent() 
            && oNewNext.get()!=oNewPrev.get();
        if (!done) {
          nail = randomNail();
          way = randomWay();
        }
      } while (!done);

      this.bases.set(mutationIndex-1, oNewPrev.get());
      this.bases.set(mutationIndex+1, oNewNext.get());
      this.bases.remove(mutationIndex);
    }
  }

  
  void addEdge(int mutationIndex) {
    Optional<EnhancedEdge> curOpt = resolveEdge(mutationIndex);
    Optional<EnhancedEdge> prevOpt = resolveEdge(mutationIndex -1);

    // read the start nail, or the end of the previous one, or get a random one.  
    int startNail = curOpt.map(EnhancedEdge::getStartNail)
        .orElse(
            prevOpt.map(EnhancedEdge::getEndNail)
                .orElse(randomNail())
        );
    boolean startWay = curOpt.map(EnhancedEdge::getStartNailWay)
        .orElse(
            ! prevOpt.map(EnhancedEdge::getEndNailWay)
                .orElse(randomWay())
        );
    
    boolean done = false;
    do {
      int endNail = randomNail();
      boolean endWay = randomWay();
      Optional<Edge> insertedEdge = this.edgeFactory
          .getEdge(startNail, startWay, endNail, endWay)
          .filter(inserted -> prevOpt.map(prev->prev.edge!=inserted).orElse(true));
      
      if (insertedEdge.isPresent()) {

        if (curOpt.isPresent()) {
          EnhancedEdge cur = curOpt.get();
          // cur will shift to the next position.  It must be replaced as well 
          // to match the new nail.
          Optional<Edge> replacedCur = this.edgeFactory
              .getEdge(endNail, !endWay, cur.getEndNail(), cur.getEndNailWay())
              .filter(replaced -> cur.edge!=replaced)
              .filter(replaced -> cur.getNext().map(next->next.edge!=replaced).orElse(true));
          
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

    List<Edge> out1 = new ArrayList<>();
    List<Edge> out2 = new ArrayList<>();
    List<Integer> crossoverPoints = EasyListCrossover.<Edge>doCrossover(
        minCrossovers, maxCrossovers, 
        this.bases, ((EdgeListDNA)other).bases, 
        out1, out2);
    this.bases = out1;
    ((EdgeListDNA)other).bases = out2;
    
    // mutate until there are only valid values.
    for (Integer crossoverPoint : crossoverPoints) {
      this.mutateEdge(crossoverPoint);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public EdgeListDNA copy() {
    return new EdgeListDNA(this);
  }

  boolean isValid() {
    Optional<EnhancedEdge> edge = this.resolveEdge(0);
    boolean ok = true;
    int prevEndNail = edge.map(EnhancedEdge::getStartNail).orElse(-1);
    boolean prevEndWay  = edge.map(EnhancedEdge::getStartNailWay).orElse(true);
    while (edge.isPresent() && ok) {
      EnhancedEdge eEdge = edge.get();
      
      ok = prevEndNail == eEdge.getStartNail() 
           && (!this.edgeWayEnabled || prevEndWay != eEdge.getStartNailWay());
      
      prevEndNail = eEdge.getEndNail();
      prevEndWay = eEdge.getEndNailWay();
      edge = eEdge.getNext();
    }
    return ok;
  }
  
}
