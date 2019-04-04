package fr.jblezoray.mygeneticalgosample.stringart_nogen.image;

public class ImageSize {
  
  public final int w;
  public final int h;
  public final int nbPixels;
  
  public ImageSize(int w, int h) {
    this.w = w;
    this.h = h;
    this.nbPixels = w*h;
  }
  
  @Override
  public boolean equals(Object o) {
    return o instanceof ImageSize && this.nbPixels==((ImageSize)o).nbPixels;
  }

}
