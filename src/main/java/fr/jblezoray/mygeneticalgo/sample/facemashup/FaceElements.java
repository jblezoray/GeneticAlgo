package fr.jblezoray.mygeneticalgo.sample.facemashup;

public class FaceElements {


  private int positionX; 
  private int positionY; 
  private int size; 
  private float rotation;
  private float alpha; 
  
  /**
   * constructor 
   * @param positionX value from [0, maxWidth]
   * @param positionY value from [0, maxHeight]
   * @param size an index in the array 'allsizes'
   * @param rotation value from [0.0f, 2*pi]
   * @param alpha value from [0, 1.0f]
   */
  public FaceElements(int positionX, int positionY, int size, float rotation, 
      float alpha) {
    this.positionX = positionX;
    this.positionY = positionY;
    this.size = size;
    this.rotation = rotation;
    this.alpha = alpha;
  }
  
  /**
   * Copy constructor.
   * @param toCopy
   */
  public FaceElements(FaceElements toCopy) {
    this.positionX = toCopy.positionX;
    this.positionY = toCopy.positionY;
    this.size = toCopy.size;
    this.rotation = toCopy.rotation;
    this.alpha = toCopy.alpha;
  }
  

  public int getPositionX() {
    return positionX;
  }

  public void setPositionX(int positionX) {
    this.positionX = positionX;
  }

  public int getPositionY() {
    return positionY;
  }

  public void setPositionY(int positionY) {
    this.positionY = positionY;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public float getAlpha() {
    return alpha;
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }
  
  
}
