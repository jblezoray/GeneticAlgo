package fr.jblezoray.mygeneticalgo;

import java.util.List;

import fr.jblezoray.mygeneticalgo.dna.IDNA;

public interface ISelection<X extends IDNA> {

  class MatingPair<X> {
    public X mate1;
    public X mate2;
  }
  
  void initialize(List<X> population);
  
  MatingPair<X> selectMatingPair();

}
