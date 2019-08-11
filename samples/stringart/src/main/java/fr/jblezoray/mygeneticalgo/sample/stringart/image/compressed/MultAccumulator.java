package fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed;

import java.io.ByteArrayOutputStream;

public class MultAccumulator implements IAccumulator {

  private final AccumulatorType type;
  private final int byteShift;
  private int counter = 0;
  private int value = 0;
  
  MultAccumulator(AccumulatorType type) {
    this.type = type;
    this.byteShift = 
        type==AccumulatorType.BYTE_1 ? 8 :
        type==AccumulatorType.BYTE_2 ? 16 :
        type==AccumulatorType.BYTE_4 ? 24 : 
        -1; 
  }

  @Override 
  public AccumulatorType getType() {
    return this.type;
  }
  
  @Override
  public byte getHeaderByte() {
    return 0x02;
  }
  
  @Override
  public byte getSizeByte() {
    return (byte) (counter & 0xFF);
  }

  @Override
  public ByteArrayOutputStream getValuesBytes() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
    baos.write((byte)(value)); // 1st byte
    if (type == AccumulatorType.BYTE_2) {
      baos.write((byte)(value >> 8)); // 2nd byte
    } else if (type == AccumulatorType.BYTE_4) {
      baos.write((byte)(value >> 8)); // 2nd byte
      baos.write((byte)(value >> 16)); // 3rd byte
      baos.write((byte)(value >> 24)); // 4th byte
    }
    return baos;
  }
  
  @Override
  public WriteResult write(int i) {
    WriteResult result; 
    if (counter == 0) {
      result = (i>>byteShift!=0) ? WriteResult.TYPE_TOO_SMALL : WriteResult.OK;
      if (result == WriteResult.OK) {
        this.value = i;
        this.counter = 1;
      }
      
    } else if (value != i) {
      if (this.counter==1) {
        result = WriteResult.SEQUENCE_DETECTED;
      } else {
        result = WriteResult.NOT_ACCEPTABLE;
      }
      
    } else if (this.counter==0xFF){
      result = WriteResult.FULL_CAPACITY;
      
    } else {
      this.counter++;
      result = WriteResult.OK;
    }
    return result;
  }

  @Override
  public void reset() {
    this.value = -1;
    this.counter = 0;
  }

}
