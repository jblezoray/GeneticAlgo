package fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed;

import java.io.ByteArrayOutputStream;

public class SequenceAccumulator implements IAccumulator {

  private static final int SIMILAR_VALUES_THRESHOLD = 5;
  private final AccumulatorType type;
  private final int byteShift;
  private final ByteArrayOutputStream sequenceOfBytes;
  private int similarValuesCounter = 0; 
  private int previousValue; 
  
  SequenceAccumulator(AccumulatorType type) {
    this.type = type;
    this.byteShift = 
        type==AccumulatorType.BYTE_1 ? 8 :
        type==AccumulatorType.BYTE_2 ? 16 :
        type==AccumulatorType.BYTE_4 ? 24 : 
        -1; 
    this.sequenceOfBytes = new ByteArrayOutputStream();
  }

  @Override 
  public AccumulatorType getType() {
    return this.type;
  }
  
  @Override
  public byte getHeaderByte() {
    return 0x01;
  }
  
  @Override
  public byte getSizeByte() {
    return (byte) (sequenceOfBytes.size() & 0xFF);
  }

  @Override
  public ByteArrayOutputStream getValuesBytes() {
    return sequenceOfBytes;
  }
  
  @Override
  public WriteResult write(int i) {
    
    if (this.similarValuesCounter==0) {
      this.similarValuesCounter++;
      this.previousValue = i;
    } else if (this.previousValue==i) {
      this.similarValuesCounter++;
    } else {
      this.similarValuesCounter=0;
    }
    
    WriteResult result; 
    if (similarValuesCounter>SIMILAR_VALUES_THRESHOLD) {
      result = WriteResult.MULT_DETECTED;
      
    } else if (this.sequenceOfBytes.size()>=0xFF) {
      result = WriteResult.FULL_CAPACITY;
      
    } else if (i>>byteShift!=0) {
      result = WriteResult.TYPE_TOO_SMALL;
      
    } else {
      this.sequenceOfBytes.write(i); // 1st byte
      if (type == AccumulatorType.BYTE_2) {
        this.sequenceOfBytes.write(i >> 8); // 2nd byte
      } else if (type == AccumulatorType.BYTE_4) {
        this.sequenceOfBytes.write(i >> 8); // 2nd byte
        this.sequenceOfBytes.write(i >> 16); // 3rd byte
        this.sequenceOfBytes.write(i >> 24); // 4th byte
      }
      result = WriteResult.OK;
    }
    return result;
  }

  @Override
  public void reset() {
    this.similarValuesCounter = 0;
    this.sequenceOfBytes.reset();
  }

}
