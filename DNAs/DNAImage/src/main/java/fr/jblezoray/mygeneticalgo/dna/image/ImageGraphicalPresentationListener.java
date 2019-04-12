package fr.jblezoray.mygeneticalgo.dna.image;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import fr.jblezoray.mygeneticalgo.IGeneticAlgoListener;

public class ImageGraphicalPresentationListener<X extends AbstractImageDNA> 
implements IGeneticAlgoListener<X> {

  private JFrame frame = null;
  private ImagePanel[] referenceImagePanels;
  private ImagePanel generatedImagePanel;

  static {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException |
        IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }
  }
  
  public ImageGraphicalPresentationListener(
      AbstractImageDNA referenceImage, 
      AbstractImageDNA... additionnalReferenceImages) {
    
    referenceImagePanels = new ImagePanel[1+additionnalReferenceImages.length];

    BufferedImage referenceImageBI = referenceImage.buildImage();
    referenceImagePanels[0] = new ImagePanel();
    referenceImagePanels[0].setImage(referenceImageBI);
    referenceImagePanels[0].setSize(referenceImageBI.getWidth(), referenceImageBI.getHeight());
    
    for (int i = 0; i<additionnalReferenceImages.length; i++) {
      referenceImagePanels[i+1] = new ImagePanel();
      referenceImagePanels[i+1].setImage(additionnalReferenceImages[i].buildImage());
      referenceImagePanels[i+1].setSize(referenceImageBI.getWidth(), referenceImageBI.getHeight());
    }
    
    generatedImagePanel = new ImagePanel();
    generatedImagePanel.setSize(referenceImageBI.getWidth(), referenceImageBI.getHeight());

    frame = new JFrame("Generated image VS reference image");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.getContentPane().setLayout(new GridLayout(1, 1+referenceImagePanels.length));
    frame.getContentPane().add(generatedImagePanel);
    for (ImagePanel imagePanel : referenceImagePanels)
      frame.getContentPane().add(imagePanel);
    frame.getContentPane().setPreferredSize(new Dimension(
        referenceImageBI.getWidth()*(1+referenceImagePanels.length), 
        referenceImageBI.getHeight()));
    frame.pack();
    frame.setResizable(false);
    frame.setVisible(true);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        for (ImagePanel imagePanel : referenceImagePanels)
          imagePanel.update(imagePanel.getGraphics());
      }
    });
  }
  
  @Override
  public void notificationOfGeneration(int generation, X dnaBestMatch, 
      double[] allFitnessScores) {
    generatedImagePanel.setImage(dnaBestMatch.buildImage());
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        generatedImagePanel.update(generatedImagePanel.getGraphics());
      }
    });
    
  }
  
  private static class ImagePanel extends JPanel {
    private static final long serialVersionUID = 461553419890866956L;
    private BufferedImage image = null;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) g.drawImage(image, 0, 0, this);            
    }

    public void setImage(BufferedImage image) {
      this.image = image;
    }

  }
}
