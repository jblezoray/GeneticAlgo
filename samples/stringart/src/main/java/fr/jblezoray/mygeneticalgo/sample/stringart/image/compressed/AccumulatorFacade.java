package fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed.IAccumulator.AccumulatorType;
import fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed.IAccumulator.WriteResult;

public class AccumulatorFacade {
  
  private final ByteArrayOutputStream compressedBytes;


  private final MultAccumulator multAccumulator1 = new MultAccumulator(AccumulatorType.BYTE_1);
  private final MultAccumulator multAccumulator2 = new MultAccumulator(AccumulatorType.BYTE_2);
  private final MultAccumulator multAccumulator4 = new MultAccumulator(AccumulatorType.BYTE_4);
  private final SequenceAccumulator sequenceAccumulator1 = new SequenceAccumulator(AccumulatorType.BYTE_1);  
  private final SequenceAccumulator sequenceAccumulator2 = new SequenceAccumulator(AccumulatorType.BYTE_2);  
  private final SequenceAccumulator sequenceAccumulator4 = new SequenceAccumulator(AccumulatorType.BYTE_4);  
  private IAccumulator currentAccumulator = multAccumulator1;
  
  public AccumulatorFacade() {
    this.compressedBytes = new ByteArrayOutputStream();
  }
  
  public void write(int i) {
    final WriteResult res = currentAccumulator.write(i);
    
    if (res!=WriteResult.OK) {
      
      // flush current accumulator.
      writeCompressedBytes(currentAccumulator);
      
      // update to another accumulator.
      switch(res) {
      case MULT_DETECTED: useMultAccumulator(); break;
      case SEQUENCE_DETECTED: useSequenceAccumulator(); break;
      case TYPE_TOO_SMALL: useAnAccumulatorOfABiggerType(); break;
      case TYPE_TOO_BIG: useAnAccumulatorOfASmallerType(); break;
      case NOT_ACCEPTABLE: //useAnotherKindOfAccumulator(); break;
      case FULL_CAPACITY: /* no-op, reuse this one after reset. */ break;
      default: throw new RuntimeException("Invalid return type");
      }
      
      // reinitialize it.
      this.currentAccumulator.reset();
      
      // retry writting this int.
      this.write(i);
    }
  }

  private void useMultAccumulator() {
    this.currentAccumulator = this.multAccumulator1; 
  }

  private void useSequenceAccumulator() {
    this.currentAccumulator = this.sequenceAccumulator1; 
  }

  private void writeCompressedBytes(IAccumulator accumulator) {
    if (accumulator.getSizeByte()!=0) { 
      compressedBytes.write(accumulator.getHeaderByte());
      compressedBytes.write(accumulator.getTypeByte());
      compressedBytes.write(accumulator.getSizeByte());
      try {
        accumulator.getValuesBytes().writeTo(compressedBytes);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
  
  private void useAnAccumulatorOfASmallerType() {
    AccumulatorType curType = this.currentAccumulator.getType();
    if (curType == AccumulatorType.BYTE_2) {
      currentAccumulator = (currentAccumulator instanceof MultAccumulator) ? 
          this.multAccumulator1 : this.sequenceAccumulator1;
      
    } else if (curType == AccumulatorType.BYTE_4) {
      currentAccumulator = (currentAccumulator instanceof MultAccumulator) ? 
          this.multAccumulator2 : this.sequenceAccumulator2;
    } else {
      throw new RuntimeException("Invalid Write Result");
    }
  }

  private void useAnAccumulatorOfABiggerType() {
    AccumulatorType curType = this.currentAccumulator.getType();
    if (curType == AccumulatorType.BYTE_1) {
      currentAccumulator = (currentAccumulator instanceof MultAccumulator) ? 
          this.multAccumulator2 : this.sequenceAccumulator2;
      
    } else if (curType == AccumulatorType.BYTE_2) {
      currentAccumulator = (currentAccumulator instanceof MultAccumulator) ? 
          this.multAccumulator4 : this.sequenceAccumulator4;
    } else {
      throw new RuntimeException("Invalid Write Result");
    }
  }
  
//  private void useAnotherKindOfAccumulator() {
//    if (currentAccumulator instanceof MultAccumulator) {
//      currentAccumulator = sequenceAccumulator1;
//    } else if (currentAccumulator instanceof SequenceAccumulator) {
//      currentAccumulator = multAccumulator1;
//    } else {
//      throw new RuntimeException("Invalid return type");
//    }
//  }

  public void concat(AccumulatorFacade acc2) {
    this.flush();
    acc2.flush();
    try {
      acc2.compressedBytes.writeTo(this.compressedBytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void flush() {
    writeCompressedBytes(currentAccumulator);
  }

  public byte[] toByteArray() {
    this.flush();
    return this.compressedBytes.toByteArray();
  }
  
}
