package fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed;

public class Decompressor {

  public void decompress(byte[] compressed, int[] target) {
    int curCompressedIndex = 0;
    int curDecompressedIndex = 0;
    
    while (curCompressedIndex<compressed.length){
      // read header.
      byte headerByte = compressed[curCompressedIndex++];
      
      // read type. 
      byte curType = compressed[curCompressedIndex++];
      
      // read size. 
      int size = Byte.toUnsignedInt(compressed[curCompressedIndex++]);
    
      if (headerByte == 0x01) { // sequence mode.
        
        // condition is outside of the loop for optimization.
        if (curType == 0x01) {
          while (size-- > 0) {
            target[curDecompressedIndex++] =  
                (compressed[curCompressedIndex++] & 0xFF);
          }
        } else if (curType == 0x02) {
          while (size-- > 0) {
            target[curDecompressedIndex++] =  
                (compressed[curCompressedIndex++] & 0xFF) |
                (compressed[curCompressedIndex++] & 0xFF) <<  8;
          }
        } else if (curType == 0x04) {
          while (size-- > 0) {
            target[curDecompressedIndex++] =  
                (compressed[curCompressedIndex++] & 0xFF) |
                (compressed[curCompressedIndex++] & 0xFF) <<  8 |
                (compressed[curCompressedIndex++] & 0xFF) << 16 |
                (compressed[curCompressedIndex++] & 0xFF) << 24;
          }
        } else {
          throw new RuntimeException("invalid type byte");
        }

      } else if (headerByte == 0x02) { // Mult mode
        int value;
        if (curType == 0x01) {
          value =  
              (compressed[curCompressedIndex++] & 0xFF);
        } else if (curType == 0x02) {
          value =
              (compressed[curCompressedIndex++] & 0xFF) |
              (compressed[curCompressedIndex++] & 0xFF) <<  8;
        } else if (curType == 0x04) {
          value =  
              (compressed[curCompressedIndex++] & 0xFF) |
              (compressed[curCompressedIndex++] & 0xFF) <<  8 |
              (compressed[curCompressedIndex++] & 0xFF) << 16 |
              (compressed[curCompressedIndex++] & 0xFF) << 24;
        } else {
          throw new RuntimeException("invalid type byte");
        }

        while (size-- > 0) {
          target[curDecompressedIndex++] = value;
        }
        
      } else {
        throw new RuntimeException("invalid header byte");
      }
    }
  }

}
