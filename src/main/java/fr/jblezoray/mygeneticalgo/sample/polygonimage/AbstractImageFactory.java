package fr.jblezoray.mygeneticalgo.sample.polygonimage;

import fr.jblezoray.mygeneticalgo.DNA;
import fr.jblezoray.mygeneticalgo.sample.imagefitness.FitableImage;

public abstract class AbstractImageFactory {

  protected static final int GENES_PER_IMAGE = 7;

  public abstract FitableImage fromDNA(DNA dna);

  
}
