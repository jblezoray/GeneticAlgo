package fr.jblezoray.mygeneticalgo.sample.stringart.edge;

import org.junit.Assert;
import org.junit.Test;

public class EdgeTest {

  @Test
  public void test_equality_if_same_object() {
    Edge e1 = new Edge(1, true, 2, false, null);
    boolean eq = e1.equals(e1);
    Assert.assertTrue(eq);
  }
  
  @Test
  public void test_equality_if_identical() {
    Edge e1 = new Edge(1, true, 2, false, null);
    Edge e2 = new Edge(1, true, 2, false, null);
    boolean eq = e1.equals(e2);
    Assert.assertTrue(eq);
  }


  @Test
  public void test_equality_if_inverted() {
    Edge e1 = new Edge(1, true, 2, false, null);
    Edge e2 = new Edge(2, false, 1, true, null);
    boolean eq = e1.equals(e2);
    Assert.assertTrue(eq);
    
  }
}
