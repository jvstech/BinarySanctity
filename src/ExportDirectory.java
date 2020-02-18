import java.io.IOException;
import java.time.Instant;
import java.util.Date;

public class ExportDirectory
{
  private int exportFlags_;
  private int timeDateStampValue_;
  private short majorVersion_;
  private short minorVersion_;
  private RelativeVirtualAddress nameRVA_;
  private int ordinalBase_;
  private int numberOfAddressEntries_;
  private int numberOfNamePointers_;
  private RelativeVirtualAddress exportAddressTableRVA_;
  private RelativeVirtualAddress namePointerRVA_;
  private RelativeVirtualAddress ordinalTableRVA_;
  private String name_;
  private RelativeVirtualAddress[] namePointerTable_;
  private String[] nameTable_;
  private Ordinal[] ordinalTable_;

  public int getExportFlags()
  {
    return exportFlags_;
  }

  public int getTimeDateStampValue()
  {
    return timeDateStampValue_;
  }

  public short getMajorVersion()
  {
    return majorVersion_;
  }

  public short getMinorVersion()
  {
    return minorVersion_;
  }

  public RelativeVirtualAddress getNameRVA()
  {
    return nameRVA_;
  }

  public int getOrdinalBase()
  {
    return ordinalBase_;
  }

  public int getNumberOfAddressEntries()
  {
    return numberOfAddressEntries_;
  }

  public int getNumberOfNamePointers()
  {
    return numberOfNamePointers_;
  }

  public RelativeVirtualAddress getExportAddressTableRVA()
  {
    return exportAddressTableRVA_;
  }

  public RelativeVirtualAddress getNamePointerRVA()
  {
    return namePointerRVA_;
  }

  public RelativeVirtualAddress getOrdinalTableRVA()
  {
    return ordinalTableRVA_;
  }

  public String getName()
  {
    return name_;
  }

  public RelativeVirtualAddress[] getNamePointerTable()
  {
    return namePointerTable_;
  }

  public String[] getNameTable()
  {
    return nameTable_;
  }

  public Ordinal[] getOrdinalTable()
  {
    return ordinalTable_;
  }

  public Date getTimeDateStamp()
  {
    return Date.from(Instant.ofEpochSecond(timeDateStampValue_));
  }

  @Override
  public String toString()
  {
    return name_;
  }

  public static ExportDirectory fromStream(ByteIOStream stream,
    Iterable<SectionHeader> sections)
    throws BadExecutableFormatException, IOException
  {
    if (stream == null)
    {
      throw new IllegalArgumentException("Stream is null.");
    }

    if (sections == null)
    {
      throw new IllegalArgumentException("Sections list is null.");
    }

    int rewindPos = -1;
    try
    {
      ExportDirectory edir = new ExportDirectory();
      edir.exportFlags_ = stream.readInt32();
      edir.timeDateStampValue_ = stream.readInt32();
      edir.majorVersion_ = stream.readInt16();
      edir.minorVersion_ = stream.readInt16();
      edir.nameRVA_ = new RelativeVirtualAddress(stream.readInt32(), sections);
      edir.ordinalBase_ = stream.readInt32();
      edir.numberOfAddressEntries_ = stream.readInt32();
      edir.numberOfNamePointers_ = stream.readInt32();
      edir.exportAddressTableRVA_ =
        new RelativeVirtualAddress(stream.readInt32(), sections);
      edir.namePointerRVA_ =
        new RelativeVirtualAddress(stream.readInt32(), sections);
      edir.ordinalTableRVA_ =
        new RelativeVirtualAddress(stream.readInt32(), sections);

      rewindPos = stream.getPosition();
      edir.name_ = stream.readCString();
      stream.setPosition((int)(edir.namePointerRVA_.getFilePosition()));
      edir.namePointerTable_ =
        new RelativeVirtualAddress[edir.numberOfNamePointers_];
      edir.nameTable_ = new String[edir.numberOfNamePointers_];
      edir.ordinalTable_ = new Ordinal[edir.numberOfNamePointers_];
      for (int i = 0; i < edir.numberOfNamePointers_; i++)
      {
        edir.namePointerTable_[i] =
          new RelativeVirtualAddress(stream.readInt32(), sections);
        int nextNamePos = stream.getPosition();
        edir.nameTable_[i] = stream.readCString();
        stream.setPosition(
          (int)(edir.ordinalTableRVA_.getFilePosition() + (i * 2)));
        edir.ordinalTable_[i] =
          new Ordinal(stream.readUInt16(), edir.ordinalBase_);
        stream.setPosition(nextNamePos);
      }

      return edir;
    }
    catch (EndOfStreamException eofEx)
    {
      throw new BadExecutableFormatException(
        "Invalid export directory table data.");
    }
    finally
    {
      if (rewindPos > 0)
      {
        stream.setPosition(rewindPos);
      }
    }
  }

  public static ExportDirectory fromStream(ByteIOStream stream,
    SectionHeader[] sections)
    throws BadExecutableFormatException, IOException
  {
    return fromStream(stream, java.util.Arrays.asList(sections));
  }
}
