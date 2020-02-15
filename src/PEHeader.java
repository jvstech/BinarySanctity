//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           PEHeader.java
//! @description    Represents the second part of a portable executable file -
//!                 the PE header (also known as the COFF or NT header) - which
//!                 contains information about the executable file's target
//!                 machine type, its symbols, its image attributes, and the
//!                 size of the options header that follows the PE header.
//!

import java.time.Instant;
import java.util.Date;

public class PEHeader
{
  private DOSHeader dosHeader_;
  private long headerSize_;
  private byte[] signature_;
  private int machineValue_;
  private int numberOfSections_;
  private int timeDateStampValue_;
  private int pointerToSymbolTable_;
  private int numberOfSymbols_;
  private int sizeOfOptionalHeader_;
  private int characteristics_;

  public DOSHeader getDOSHeader()
  {
    return dosHeader_;
  }

  public long getHeaderSize()
  {
    return headerSize_;
  }

  // Four bytes expected to be"PE\0\0"
  public byte[] getSignature()
  {
    return signature_;
  }

  // Number that identifies the type of target machine. This number represents
  // a CPU type, and this executable file can only be run on the specified
  // machine or on a system that emulates the specified machine.
  public int getMachineValue()
  {
    return machineValue_;
  }

  // Indicates the size of the section table, which immediately follows the
  // executable headers.
  public int getNumberOfSections()
  {
    return numberOfSections_;
  }

  // Indicates when the file was created. Represents the low 32-bits of the
  // number of seconds since the Unix epoch (00:00 1 January 1970).
  public int getTimeDateStampValue()
  {
    return timeDateStampValue_;
  }

  // File offset of the COFF symbol table, or zero if no COFF symbol table is
  // present. This value should be zero for all PE executable files.
  public int getPointerToSymbolTable()
  {
    return pointerToSymbolTable_;
  }

  // Number of entries in the symbol table. This data can be used to locate the
  // string table, which immediately follows the symbol table. This value should
  // be zero for all PE executable files.
  public int getNumberOfSymbols()
  {
    return numberOfSymbols_;
  }

  // Size of the options header (officially named the 'Optional' header), which
  // is required for executable files but not for object files. This value
  // should be zero for object files.
  public int getSizeOfOptionalHeader()
  {
    return sizeOfOptionalHeader_;
  }

  // The flags that indicate the attributes of the file.
  public int getCharacteristics()
  {
    return characteristics_;
  }

  public Date getTimeDateStamp()
  {
    return Date.from(Instant.ofEpochSecond(timeDateStampValue_));
  }

  public MachineType getMachine()
  {
    return MachineType.fromInt(machineValue_);
  }

  public long getOptionalHeaderOffset()
  {
    return getDOSHeader().getPEHeaderOffset() + headerSize_;
  }

  public boolean isValid()
  {
    return (signature_ != null && signature_.length == 4 &&
      signature_[0] == 0x50 /*'P'*/ && signature_[1] == 0x45 /*E*/ &&
      signature_[2] == 0 && signature_[3] == 0);
  }

  public static PEHeader fromStream(ByteIOStream stream, DOSHeader dosHeader)
    throws BadExecutableFormatException
  {
    PEHeader pe = new PEHeader();
    try
    {
      stream.setPosition(dosHeader.getPEHeaderOffset());
      pe.dosHeader_ = dosHeader;
      int startPos = stream.getPosition();

      pe.signature_ = stream.read(4);
      if (!pe.isValid())
      {
        throw new BadExecutableFormatException("Invalid PE header.");
      }

      pe.machineValue_ = stream.readUInt16();
      pe.numberOfSections_ = stream.readUInt16();
      pe.timeDateStampValue_ = stream.readInt32();
      pe.pointerToSymbolTable_ = stream.readInt32();
      pe.numberOfSymbols_ = stream.readInt32();
      pe.sizeOfOptionalHeader_ = stream.readUInt16();
      pe.characteristics_ = stream.readUInt16();

      int readCount = stream.getPosition() - startPos;
      pe.headerSize_ = readCount;
    }
    catch (BadExecutableFormatException exeEx)
    {
      // rethrow
      throw exeEx;
    }
    catch (Exception ex)
    {
      return new PEHeader();
    }

    return pe;
  }
}
