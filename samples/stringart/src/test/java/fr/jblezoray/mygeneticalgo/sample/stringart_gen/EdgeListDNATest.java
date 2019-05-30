package fr.jblezoray.mygeneticalgo.sample.stringart_gen;

import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.MIN_NAILS_DIFF;
import static fr.jblezoray.mygeneticalgo.sample.stringart_gen.Constants.NB_NAILS;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeDrawer;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ImageSize;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

public class EdgeListDNATest {
  
  private static EdgeFactory EDGE_FACTORY;
  
  @Before
  public void before() {
    // reset PRNG to get deterministic values regardless of tests order. 
    RandomSingleton.setDeterministic();
    // reinit everything with this new PRNG.
    ImageSize imageSize = new ImageSize(100, 100);
    EdgeDrawer edgeDrawer = new EdgeDrawer(imageSize, NB_NAILS, 0.15f, 2f);
    EDGE_FACTORY = new EdgeFactory(MIN_NAILS_DIFF, NB_NAILS, false, false, edgeDrawer);
  }

  @Test
  public void test_mutateEdge() {
    EdgeListDNA dna = new EdgeListDNA(EDGE_FACTORY, 10, 100, false, false);
    for (int i=0; i<4; i++) dna.addEdge(i);
    Assert.assertEquals("[(48-,60-)(60-,47-)(47-,91-)(91-,19-)]", dna.toString());
    
    dna.mutateEdge(1);

    Assert.assertEquals("[(48-,60-)(60-,77-)(77-,91-)(91-,19-)]", dna.toString());
  }
  

  @Test(expected = RuntimeException.class)
  public void test_mutateEdge_emptyList() {
    EdgeListDNA dna = new EdgeListDNA(EDGE_FACTORY, 10, 100, false, false);
    
    dna.mutateEdge(0);
  }
  

  @Test
  public void test_mutateEdge_1st_element() {
    EdgeListDNA dna = new EdgeListDNA(EDGE_FACTORY, 10, 100, false, false);
    for (int i=0; i<3; i++) dna.addEdge(i);
    Assert.assertEquals("[(48-,60-)(60-,47-)(47-,91-)]", dna.toString());
    
    dna.mutateEdge(0);

    Assert.assertEquals("[(48-,61-)(61-,47-)(47-,91-)]", dna.toString());
  }
  

  @Test
  public void test_mutateEdge_last_element() {
    EdgeListDNA dna = new EdgeListDNA(EDGE_FACTORY, 10, 100, false, false);
    for (int i=0; i<3; i++) dna.addEdge(i);
    Assert.assertEquals("[(48-,60-)(60-,47-)(47-,91-)]", dna.toString());
    
    dna.mutateEdge(2);

    Assert.assertEquals("[(48-,60-)(60-,47-)(47-,61-)]", dna.toString());
  }


  @Test
  public void test_deleteEdge_1st_element() {
    EdgeListDNA dna = new EdgeListDNA(EDGE_FACTORY, 10, 100, false, false);
    for (int i=0; i<3; i++) dna.addEdge(i);
    Assert.assertEquals("[(48-,60-)(60-,47-)(47-,91-)]", dna.toString());
    
    dna.deleteEdge(0);

    Assert.assertEquals("[(60-,47-)(47-,91-)]", dna.toString());
  }

  @Test
  public void test_deleteEdge_last_element() {
    EdgeListDNA dna = new EdgeListDNA(EDGE_FACTORY, 10, 100, false, false);
    for (int i=0; i<3; i++) dna.addEdge(i);
    Assert.assertEquals("[(48-,60-)(60-,47-)(47-,91-)]", dna.toString());
    
    dna.deleteEdge(2);

    Assert.assertEquals("[(48-,60-)(60-,47-)]", dna.toString());
  }

  @Test
  public void test_deleteEdge() {
    EdgeListDNA dna = new EdgeListDNA(EDGE_FACTORY, 10, 100, false, false);
    for (int i=0; i<3; i++) dna.addEdge(i);
    Assert.assertEquals("[(48-,60-)(60-,47-)(47-,91-)]", dna.toString());
    
    dna.deleteEdge(1);

    Assert.assertEquals("[(48-,60-)(60-,91-)]", dna.toString());
  }

  @Test
  public void test_addEdge_empty() {
    EdgeListDNA dna = new EdgeListDNA(EDGE_FACTORY, 10, 100, false, false);

    dna.addEdge(0);
    
    Assert.assertEquals("[(48-,60-)]", dna.toString());
  }

  @Test
  public void test_addEdge_first() {
    EdgeListDNA dna = new EdgeListDNA(EDGE_FACTORY, 10, 100, false, false);
    for (int i=0; i<3; i++) dna.addEdge(i);
    Assert.assertEquals("[(48-,60-)(60-,47-)(47-,91-)]", dna.toString());
    
    dna.addEdge(0);
    
    Assert.assertEquals("[(48-,19-)(19-,60-)(60-,47-)(47-,91-)]", dna.toString());
  }

  @Test
  public void test_addEdge_middle() {
    EdgeListDNA dna = new EdgeListDNA(EDGE_FACTORY, 10, 100, false, false);
    for (int i=0; i<2; i++) dna.addEdge(i);
    Assert.assertEquals("[(48-,60-)(60-,47-)]", dna.toString());
    
    dna.addEdge(1);
    
    Assert.assertEquals("[(48-,60-)(60-,91-)(91-,47-)]", dna.toString());

  }

  @Test
  public void test_addEdge_last() {
    EdgeListDNA dna = new EdgeListDNA(EDGE_FACTORY, 10, 100, false, false);
    for (int i=0; i<3; i++) dna.addEdge(i);
    Assert.assertEquals("[(48-,60-)(60-,47-)(47-,91-)]", dna.toString());
    
    dna.addEdge(3);
    
    Assert.assertEquals("[(48-,60-)(60-,47-)(47-,91-)(91-,19-)]", dna.toString());
    
  }
  
  
}

