package fr.jblezoray.mygeneticalgosample.disksimage;

import fr.jblezoray.mygeneticalgo.IDNAFactory;
import fr.jblezoray.mygeneticalgo.dna.image.AbstractImageDNA;

public class DisksImageDNAFactory implements IDNAFactory<DiskImageDNA> {

  private final int width;
  private final int height;
  
  public DisksImageDNAFactory(AbstractImageDNA referenceImage) {
    width = referenceImage.getImage().getWidth();
    height = referenceImage.getImage().getHeight();
  }

  @Override
  public DiskImageDNA createRandomIndividual() {
    return new DiskImageDNA(width, height);
  }
  
}
