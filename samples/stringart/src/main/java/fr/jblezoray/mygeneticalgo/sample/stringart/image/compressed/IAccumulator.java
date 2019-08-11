package fr.jblezoray.mygeneticalgo.sample.stringart.image.compressed;

import java.io.ByteArrayOutputStream;

interface IAccumulator {

  enum WriteResult {OK, FULL_CAPACITY, TYPE_TOO_SMALL, TYPE_TOO_BIG, 
      NOT_ACCEPTABLE, MULT_DETECTED, SEQUENCE_DETECTED}
  enum AccumulatorType {BYTE_1, BYTE_2, BYTE_4}
  enum Mode {SEQUENCE, MULT}

  AccumulatorType getType();
  
  byte getHeaderByte();

  default byte getTypeByte() {
    switch (getType()) {
    case BYTE_1: return 0x01;
    case BYTE_2: return 0x02;
    case BYTE_4: return 0x04;
    }
    return 0x00; // invalid value;
  }
  
  byte getSizeByte();

  ByteArrayOutputStream getValuesBytes();
  
  WriteResult write(int i);

  void reset();
}
