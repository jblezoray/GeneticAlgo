package fr.jblezoray.mygeneticalgo.sample.disksimage;

public class DiskShape {
  
  private int originX;
  private int originY;
  private int radius;
  private int red;
  private int green;
  private int blue;
  private int alpha;
  
  public DiskShape(int originX, int originY, int radius, int red, int green,
      int blue, int alpha) {
    this.originX = originX;
    this.originY = originY;
    this.radius = radius;
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;
  }
  
  public DiskShape(DiskShape toCopy) {
    this.originX = toCopy.originX;
    this.originY = toCopy.originY;
    this.radius = toCopy.radius;
    this.red = toCopy.red;
    this.green = toCopy.green;
    this.blue = toCopy.blue;
    this.alpha = toCopy.alpha;
  }
  public int getOriginX() {
    return originX;
  }
  public void setOriginX(int originX) {
    this.originX = originX;
  }
  public int getOriginY() {
    return originY;
  }
  public void setOriginY(int originY) {
    this.originY = originY;
  }
  public int getRadius() {
    return radius;
  }
  public void setRadius(int radius) {
    this.radius = radius;
  }
  public int getRed() {
    return red;
  }
  public void setRed(int red) {
    this.red = red;
  }
  public int getGreen() {
    return green;
  }
  public void setGreen(int green) {
    this.green = green;
  }
  public int getBlue() {
    return blue;
  }
  public void setBlue(int blue) {
    this.blue = blue;
  }
  public int getAlpha() {
    return alpha;
  }
  public void setAlpha(int alpha) {
    this.alpha = alpha;
  }
}
