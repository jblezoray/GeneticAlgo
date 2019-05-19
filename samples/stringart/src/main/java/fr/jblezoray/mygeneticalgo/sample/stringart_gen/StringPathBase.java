package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

public class StringPathBase {

  private int nail;
  private boolean turnClockwise;
  
  StringPathBase(int nail, boolean turnClockwise) {
    this.nail = nail;
    this.turnClockwise = turnClockwise;
  }
  
  StringPathBase copy() {
    return new StringPathBase(this.nail, this.turnClockwise);
  }
  
  public void setNail(int nail) {
    this.nail = nail;
  }
  
  public int getNail() {
    return nail;
  }
  
  public void setTurnClockwise(boolean turnClockwise) {
    this.turnClockwise = turnClockwise;
  }
  
  public boolean isTurnClockwise() {
    return turnClockwise;
  }
  
  @Override
  public String toString() {
    return Integer.toString(this.nail) + (this.turnClockwise ? '+' : '-');
  }
  
}
