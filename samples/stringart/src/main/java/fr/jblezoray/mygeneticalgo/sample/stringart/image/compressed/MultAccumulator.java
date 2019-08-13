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
    if (counter == 0) {
      if (i>>byteShift!=0) 
        return WriteResult.TYPE_TOO_SMALL;
      if (type != AccumulatorType.BYTE_1 && i>>(byteShift-8)==0) 
        return WriteResult.TYPE_TOO_BIG;
      this.value = i;
      this.counter = 1;
      
    } else if (value != i) {
      return (this.counter==1) ? WriteResult.SEQUENCE_DETECTED 
          : WriteResult.NOT_ACCEPTABLE;
      
    } else if (this.counter==0xFF){
      return WriteResult.FULL_CAPACITY;
      
    } else {
      this.counter++;
    }
    return WriteResult.OK;
  }

  @Override
  public void reset() {
    this.value = -1;
    this.counter = 0;
  }

}
