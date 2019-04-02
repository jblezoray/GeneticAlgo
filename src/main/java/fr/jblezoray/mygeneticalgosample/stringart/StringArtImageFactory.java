package fr.jblezoray.mygeneticalgosample.stringart;

import java.awt.Color;

import fr.jblezoray.mygeneticalgo.IDNAFactory;
import fr.jblezoray.mygeneticalgo.dna.image.AbstractImageDNA;

public class StringArtImageFactory implements IDNAFactory<StringArtImageDNA> {

//  private final int nbStrings;
  private final int nbHorizontalNails;
  private final int nbVerticalNails;
  private final int totalNumberOfNails; 
  private final int width;
  private final int height;
  private final Color stringColor; 
  private final Color backgroundColor; 
  
  public StringArtImageFactory(AbstractImageDNA image, // int nbStrings, 
      int nbNails, Color backgroundColor, Color stringColor) {
//    this.nbStrings = nbStrings;
    this.width = image.getImage().getWidth();
    this.height = image.getImage().getHeight();
    double whRatio = (double)width / (double)(width+height);
    this.nbHorizontalNails = (int)(((double)nbNails/2.0) * whRatio);
    this.nbVerticalNails = (int)((double)nbNails/2.0) - nbHorizontalNails;
    this.totalNumberOfNails = (nbVerticalNails+nbHorizontalNails)*2;
    this.stringColor = stringColor;
    this.backgroundColor = backgroundColor;
  }

  @Override
  public StringArtImageDNA createRandomIndividual() {
//    return new StringArtImageDNA(nbStrings, totalNumberOfNails, 
//        nbHorizontalNails, nbVerticalNails, width, height, 
//        backgroundColor, stringColor);
    return new StringArtImageDNA(1, totalNumberOfNails, 
        nbHorizontalNails, nbVerticalNails, width, height, 
        backgroundColor, stringColor);
  }
  
}
