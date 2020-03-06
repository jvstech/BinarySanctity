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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Date;

public class PEHeader implements Header
{
  private final int HEADER_SIZE = 0x18;
  private final PortableExecutableFileChannel peFile_;

  enum Offsets
  {
    SIGNATURE               (0x00),
    MACHINE_VALUE           (0x04),
    NUMBER_OF_SECTIONS      (0x06),
    TIME_DATE_STAMP_VALUE   (0x08),
    POINTER_TO_SYMBOL_TABLE (0x0C),
    NUMBER_OF_SYMBOLS       (0x10),
    SIZE_OF_OPTIONAL_HEADER (0x14),
    CHARACTERISTICS         (0x16)
    ;

    public final int position;

    private Offsets(int offset)
    {
      position = offset;
    }
  }

  public PEHeader(PortableExecutableFileChannel peFile)
  {
    if (peFile == null)
    {
      throw new NullPointerException();
    }

    peFile_ = peFile;
  }

  public PortableExecutableFileChannel getPEFile()
  {
    return peFile_;
  }

  @Override
  public int getHeaderSize()
  {
    return HEADER_SIZE;
  }

  @Override
  public long getStartOffset()
    throws IOException, EndOfStreamException
  {
    return peFile_.getDOSHeader().getStartOffset() +
      peFile_.getDOSHeader().getPEHeaderOffset();
  }

  @Override
  public long getEndOffset()
    throws IOException, EndOfStreamException
  {
    return getStartOffset() + getHeaderSize();
  }

  // Four bytes expected to be"PE\0\0"
  public byte[] getSignature()
    throws IOException, EndOfStreamException
  {
    return peFile_.read(relpos(Offsets.SIGNATURE.position), 4);
  }

  // Number that identifies the type of target machine. This number represents
  // a CPU type, and this executable file can only be run on the specified
  // machine or on a system that emulates the specified machine.
  public int getMachineValue()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(relpos(Offsets.MACHINE_VALUE.position));
  }

  // Indicates the size of the section table, which immediately follows the
  // executable headers.
  public int getNumberOfSections()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(relpos(Offsets.NUMBER_OF_SECTIONS.position));
  }

  // Indicates when the file was created. Represents the low 32-bits of the
  // number of seconds since the Unix epoch (00:00 1 January 1970).
  public int getTimeDateStampValue()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.TIME_DATE_STAMP_VALUE.position));
  }

  // File offset of the COFF symbol table, or zero if no COFF symbol table is
  // present. This value should be zero for all PE executable files.
  public int getPointerToSymbolTable()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.POINTER_TO_SYMBOL_TABLE.position));
  }

  // Number of entries in the symbol table. This data can be used to locate the
  // string table, which immediately follows the symbol table. This value should
  // be zero for all PE executable files.
  public int getNumberOfSymbols()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.NUMBER_OF_SYMBOLS.position));
  }

  // Size of the options header (officially named the 'Optional' header), which
  // is required for executable files but not for object files. This value
  // should be zero for object files.
  public int getSizeOfOptionalHeader()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(relpos(Offsets.SIZE_OF_OPTIONAL_HEADER.position));
  }

  // The flags that indicate the attributes of the file.
  public int getCharacteristics()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(relpos(Offsets.CHARACTERISTICS.position));
  }

  public Date getTimeDateStamp()
    throws IOException, EndOfStreamException
  {
    return Date.from(Instant.ofEpochSecond(getTimeDateStampValue()));
  }

  public MachineType getMachine()
    throws IOException, EndOfStreamException
  {
    return MachineType.fromInt(getMachineValue());
  }

  public long getOptionalHeaderOffset()
    throws IOException, EndOfStreamException
  {
    return peFile_.getDOSHeader().getPEHeaderOffset() + HEADER_SIZE;
  }

  public boolean isValid()
    throws IOException
  {
    try
    {
      byte[] sig = getSignature();
      return (sig != null && sig.length == 4 &&
        sig[0] == 0x50 /*'P'*/ && sig[1] == 0x45 /*E*/ &&
        sig[2] == 0 && sig[3] == 0);
    }
    catch (EndOfStreamException eofEx)
    {
      return false;
    }
  }

  @Override
  public String toString()
  {
    StringWriter sw = new StringWriter();
    PrintWriter w = new PrintWriter(sw);
    try
    {
      w.printf("Machine type:            %s\n", getMachine());
      w.printf("Number of sections:      %d\n", getNumberOfSections());
      w.printf("Timestamp:               %s\n", getTimeDateStamp());
      w.printf("Pointer to symbol table: 0x%x\n", getPointerToSymbolTable());
      w.printf("Number of symbols:       %d\n", getNumberOfSymbols());
      w.printf("Size of optional header: %d\n", getSizeOfOptionalHeader());
      w.printf("Characteristics:         0x%x\n", getCharacteristics());
      for (String peCharType :
        PECharacteristicTypes.getStrings(getCharacteristics()))
      {
        w.printf("                         %s\n", peCharType);
      }
    }
    catch (IOException e)
    {
      w.println("I/O exception while reading the PE header:");
      e.printStackTrace(w);
    }
    catch (EndOfStreamException e)
    {
      w.println("Reached the end of data before finishing the PE header.");
      e.printStackTrace(w);
    }

    return sw.toString();
  }

  void validate()
    throws IOException, BadExecutableFormatException
  {
    if (!isValid())
    {
      throw new BadExecutableFormatException("Invalid PE header.");
    }
  }

  private long relpos(long pos)
    throws IOException, EndOfStreamException
  {
    return peFile_.getDOSHeader().getPEHeaderOffset() + pos;
  }
}
