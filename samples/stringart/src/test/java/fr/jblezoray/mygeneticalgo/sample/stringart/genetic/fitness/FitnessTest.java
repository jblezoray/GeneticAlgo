package fr.jblezoray.mygeneticalgo.sample.stringart.genetic.fitness;

import static fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Constants.MIN_NAILS_DIFF;
import static fr.jblezoray.mygeneticalgo.sample.stringart.genetic.Constants.NB_NAILS;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeDrawer;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeImageIO;
import fr.jblezoray.mygeneticalgo.sample.stringart.edge.Edge;
import fr.jblezoray.mygeneticalgo.sample.stringart.genetic.EdgeListDNA;
import fr.jblezoray.mygeneticalgo.sample.stringart.genetic.EdgeListDNAFactory;
import fr.jblezoray.mygeneticalgo.sample.stringart.genetic.fitness.FitnessFast.FitnessFastStats;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ByteImage;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;
import fr.jblezoray.mygeneticalgo.utils.RandomSingleton;

public class FitnessTest {

  private static final String REF_IMG_RESOURCE_NAME = "test_image.png";
  private static final String POI_IMG_RESOURCE_NAME = "test_image.poi.png";
  private static UnboundedImage REF_IMG; 
  private static ByteImage POI_IMG; 
  private static EdgeFactory EDGE_FACTORY;
  
  @BeforeClass
  public static void before_class() throws IOException {
    RandomSingleton.setDeterministic();
    ByteImage refImgByte = EdgeImageIO.readResource(REF_IMG_RESOURCE_NAME);
    REF_IMG = new UnboundedImage(refImgByte.getSize()).add(refImgByte);
    POI_IMG = EdgeImageIO.readResource(POI_IMG_RESOURCE_NAME);
    EdgeDrawer edgeDrawer = new EdgeDrawer(REF_IMG.getSize(), NB_NAILS, 0.15f, 2f);
    EDGE_FACTORY = new EdgeFactory(MIN_NAILS_DIFF, NB_NAILS, false, false, edgeDrawer); 
  }
  
  /**
   * Creates a population composed of random albeit similar individuals. 
   * @param size
   * @return
   */
  private EdgeListDNA[] createRelatedPopulation(int size, float mutationRate) {
    EdgeListDNA[] dnas = new EdgeListDNA[size];
    dnas[0] = this.createIndividual(300);
    Assert.assertTrue(dnas[0].isValid()); 
    for (int i=1; i<dnas.length; i++) {
      dnas[i] = dnas[i-1].copy();
      dnas[i].doMutate(mutationRate);
    }
    for (EdgeListDNA dna : dnas) 
      Assert.assertTrue(dna.isValid()); 
    return dnas;
  }
  
  private EdgeListDNA createIndividual(int size) {
    EdgeListDNAFactory factory = new EdgeListDNAFactory(EDGE_FACTORY, size, 
        MIN_NAILS_DIFF, NB_NAILS, false, false);
    return factory.createRandomIndividual();
  }
  
  @Test
  @Ignore("remove this ignore")
  public void test_FitnessFast_has_same_result_as_Fitness() throws IOException {
    Fitness f = new Fitness(REF_IMG, POI_IMG);
    FitnessFast fo = new FitnessFast(REF_IMG, POI_IMG, 100, 20, NB_NAILS);
    EdgeListDNA[] dnas = createRelatedPopulation(3, 0.015f);

    for (EdgeListDNA dna : dnas) {
      UnboundedImage uif = f.drawImage(dna);
      UnboundedImage uifo = fo.drawImage(dna);
      Image diff = uif.differenceWith(uifo);
//    EdgeImageIO.writeToFile(uif, new File("__uif.png"));
//    EdgeImageIO.writeToFile(uifo, new File("__uifo.png"));
//    EdgeImageIO.writeToFile(diff, new File("__diff.png"));
      Assert.assertEquals(0.0, diff.l2norm(), 0.00001);
    }
  }

  @Test
  @Ignore("remove this ignore")
  public void check_full_match() {
    // having 
    int nbCategories = 5;
    FitnessFast f = new FitnessFast(REF_IMG, POI_IMG, 100, nbCategories, NB_NAILS);
    EdgeListDNA dna = createIndividual(10);

    // when the same image is drawn twice
    f.drawImage(dna);
    long nbGeneratedElementsBy1stDraw = f.buildStats().nbGeneratedElement; 
    System.out.println(f.buildStats().toString());
    f.resetScores();
    f.drawImage(dna);
    
    // then 
    FitnessFastStats stats = f.buildStats();
    System.out.println(stats.toString());
    // all the generated elements of the 1st draw resulted in a "no match". 
    Assert.assertEquals(0, stats.unmatchcpt);
    // all the generated elements of the 1st draw are reused in the 2nd. 
    Assert.assertEquals(nbGeneratedElementsBy1stDraw, stats.matchcpt);
    // all no new generated element.
    Assert.assertEquals(nbGeneratedElementsBy1stDraw, stats.nbGeneratedElement);
    // there is approx. one generated element per category, not more than that.
    Assert.assertTrue(nbCategories/2 < stats.nbGeneratedElement);
  }

  @Test
  @Ignore("remove this ignore")
  public void check_no_match_if_different_images() {
    // having 
    int nbCategories = 5;
    FitnessFast f = new FitnessFast(REF_IMG, POI_IMG, 100, nbCategories, NB_NAILS);
    EdgeListDNA dna1 = createIndividual(10);
    EdgeListDNA dna2 = createIndividual(10); // very hopefully different.

    // when the same image is drawn twice
    f.drawImage(dna1);
    f.drawImage(dna2);
    
    // then no element of the 1st draw is reused in the 2nd. 
    FitnessFastStats stats = f.buildStats();
    Assert.assertEquals(0, stats.matchcpt);
  }

  @Test
  public void test_even_distribution_in_categories() {
    // having
    int nbCategories = 5;
    FitnessFast f = new FitnessFast(REF_IMG, POI_IMG, 100, nbCategories, NB_NAILS);
    int nbBases = 1000; 
    EdgeListDNA dna = createIndividual(nbBases);

    // when 
    Map<Integer, List<Edge>> collect = dna.getAllEdges().stream()
        .collect(f.getEdgeGroupByClassifier());

    // then 
    int minCategorySize = Integer.MAX_VALUE;
    int maxCategorySize = 0;
    for (List<Edge> edgeClass : collect.values()) {
      Map<Integer, AtomicInteger> counts = new HashMap<>();
      for (int i=0; i<NB_NAILS; i++) counts.put(i, new AtomicInteger(0));
      for (Edge e : edgeClass) counts.get(e.getNailA()).incrementAndGet();
      if (edgeClass.size()>maxCategorySize) maxCategorySize = edgeClass.size();
      if (edgeClass.size()<minCategorySize) minCategorySize = edgeClass.size(); 
    }
    int sizeDiff = maxCategorySize - minCategorySize;
    int perfectCategorySize = nbBases / nbCategories;
    float expectedMaxDeviation = 0.2f;
    Assert.assertTrue(sizeDiff < perfectCategorySize * expectedMaxDeviation);
  }
  
  
}
