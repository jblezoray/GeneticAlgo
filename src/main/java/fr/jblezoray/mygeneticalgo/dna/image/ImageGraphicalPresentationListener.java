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
  private ImagePanel referenceImagePanel;
  private ImagePanel generatedImagePanel;

  static {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException |
        IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }
  }
  
  public ImageGraphicalPresentationListener(AbstractImageDNA referenceImage) {
    
    BufferedImage referenceImageBI = referenceImage.buildImage();
    
    referenceImagePanel = new ImagePanel();
    referenceImagePanel.setImage(referenceImageBI);
    referenceImagePanel.setSize(referenceImageBI.getWidth(), referenceImageBI.getHeight());
    
    generatedImagePanel = new ImagePanel();
    generatedImagePanel.setSize(referenceImageBI.getWidth(), referenceImageBI.getHeight());

    frame = new JFrame("Generated image VS reference image");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.getContentPane().setLayout(new GridLayout(1, 2));
    frame.getContentPane().add(generatedImagePanel);
    frame.getContentPane().add(referenceImagePanel);
    frame.getContentPane().setPreferredSize(
        new Dimension(referenceImageBI.getWidth()*2, referenceImageBI.getHeight()));
    frame.pack();
    frame.setResizable(false);
    frame.setVisible(true);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        referenceImagePanel.update(referenceImagePanel.getGraphics());
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
  
  private class ImagePanel extends JPanel {
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
