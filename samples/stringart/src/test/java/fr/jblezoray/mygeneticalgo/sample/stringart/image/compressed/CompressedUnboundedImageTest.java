package fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import fr.jblezoray.mygeneticalgo.sample.stringart.core.EdgeImageIO;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.ByteImage;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.Image;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.UnboundedImage;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed.CompressedUnboundedImage;

public class CompressedUnboundedImageTest {
  
  @Test
  public void test_idempotency() throws IOException {
    // having 
    ByteImage raw = EdgeImageIO.readResource("test_image.png");
    UnboundedImage original = new UnboundedImage(raw.getSize());
    original.add(raw);
    EdgeImageIO.writeToFile(original, new File("__original.png"));
    
    // when 
    CompressedUnboundedImage compressed = new CompressedUnboundedImage(original);
    EdgeImageIO.writeToFile(compressed, new File("__compressed.png"));
    
    // then 
    UnboundedImage decompressed = compressed.decompress();
    EdgeImageIO.writeToFile(decompressed, new File("__decompressed.png"));
    Image diff = decompressed.differenceWith(original);
    EdgeImageIO.writeToFile(diff, new File("__diff.png"));
    double l2norm = diff.l2norm();
    System.out.println(l2norm);
  }
}
