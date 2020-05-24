/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;

import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;

/**
 * PANOSE classification number.
 *      
 * @see "https://docs.microsoft.com/en-us/typography/opentype/spec/os2#panose"
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class Panose implements Writable {

  private byte bFamilyType;
  private byte bSerifStyle;
  private byte bWeight;
  private byte bProportion;
  private byte bContrast;
  private byte bStrokeVariation;
  private byte bArmStyle;
  private byte bLetterform;
  private byte bMidline;
  private byte bXHeight;

  /**
   * Creates {@link Panose} from the given input.
   */
  public Panose(DataInput di) throws IOException {
    bFamilyType = di.readByte();
    bSerifStyle = di.readByte();
    bWeight = di.readByte();
    bProportion = di.readByte();
    bContrast = di.readByte();
    bStrokeVariation = di.readByte();
    bArmStyle = di.readByte();
    bLetterform = di.readByte();
    bMidline = di.readByte();
    bXHeight = di.readByte();
  }
  
  @Override
  public void write(BinaryOutput out) throws IOException {
      out.writeByte(bFamilyType);
      out.writeByte(bSerifStyle);
      out.writeByte(bWeight);
      out.writeByte(bProportion);
      out.writeByte(bContrast);
      out.writeByte(bStrokeVariation);
      out.writeByte(bArmStyle);
      out.writeByte(bLetterform);
      out.writeByte(bMidline);
      out.writeByte(bXHeight);
  }

  public byte getFamilyType() {
    return bFamilyType;
  }
  
  public byte getSerifStyle() {
    return bSerifStyle;
  }
  
  public byte getWeight() {
    return bWeight;
  }

  public byte getProportion() {
    return bProportion;
  }
  
  public byte getContrast() {
    return bContrast;
  }
  
  public byte getStrokeVariation() {
    return bStrokeVariation;
  }
  
  public byte getArmStyle() {
    return bArmStyle;
  }
  
  public byte getLetterForm() {
    return bLetterform;
  }
  
  public byte getMidline() {
    return bMidline;
  }
  
  public byte getXHeight() {
    return bXHeight;
  }
  
  public String toString() {
    String sb = String.valueOf(bFamilyType) + " " +
            String.valueOf(bSerifStyle) + " " +
            String.valueOf(bWeight) + " " +
            String.valueOf(bProportion) + " " +
            String.valueOf(bContrast) + " " +
            String.valueOf(bStrokeVariation) + " " +
            String.valueOf(bArmStyle) + " " +
            String.valueOf(bLetterform) + " " +
            String.valueOf(bMidline) + " " +
            String.valueOf(bXHeight);
    return sb;
  }
}
