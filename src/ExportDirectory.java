//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           ExportDirectory.java
//! @description    Represents a table of symbols (names and addresses) made
//!                 externally available by a PE file. The export directory
//!                 table contains address information that is used to resolve
//!                 imports to the entry points within an executable image.
//!

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ExportDirectory implements Header
{
  public static final int HEADER_SIZE = 44;

  enum Offsets
  {
    EXPORT_FLAGS              (0x00),
    TIME_DATE_STAMP_VALUE     (0x04),
    MAJOR_VERSION             (0x08),
    MINOR_VERSION             (0x0A),
    NAME_RVA                  (0x0C),
    ORDINAL_BASE              (0x10),
    NUMBER_OF_ADDRESS_ENTRIES (0x14),
    NUMBER_OF_NAME_POINTERS   (0x18),
    EXPORT_ADDRESS_TABLE_RVA  (0x1C),
    NAME_POINTER_RVA          (0x20),
    ORDINAL_TABLE_RVA         (0x24)
    ;

    public final int position;

    private Offsets(int offset)
    {
      position = offset;
    }
  }

  private final PortableExecutableFileChannel peFile_;
  private final RelativeVirtualAddress rva_;

  private RelativeVirtualAddress nameRVA_;
  private RelativeVirtualAddress exportAddressTableRVA_;
  private RelativeVirtualAddress namePointerRVA_;
  private RelativeVirtualAddress ordinalTableRVA_;
  private String name_;
  private RelativeVirtualAddress[] namePointerTable_;
  private String[] nameTable_;
  private Ordinal[] ordinalTable_;
  private boolean valid_ = true;
  private ArrayList<String> parseErrors_ = new ArrayList<>();

  public ExportDirectory(PortableExecutableFileChannel peFile)
    throws IOException, EndOfStreamException
  {
    if (!peFile.hasDataDirectory(DataDirectoryIndex.EXPORT_TABLE))
    {
      throw new IllegalArgumentException();
    }

    peFile_ = peFile;
    rva_ = peFile_
      .getDataDirectories()[DataDirectoryIndex.EXPORT_TABLE.ordinal()]
      .getRVA();
    if (!rva_.isValid(peFile_) || rva_.getSection() == null)
    {
      valid_ = false;
      parseErrors_.add(String.format("Invalid export directory RVA. (%s)",
        rva_.toDiagnosticString(peFile_)));
    }

    long pos = relpos(Offsets.NAME_RVA.position);
    if (DataUtil.isInBounds(pos, peFile_))
    {
      nameRVA_ = new RelativeVirtualAddress(peFile_.readInt32(pos),
        peFile_.getSections());
      if (!nameRVA_.isValid() || nameRVA_.getSection() == null)
      {
        valid_ = false;
        parseErrors_.add(String.format(
          "Export library name RVA is invalid. (%s)",
          nameRVA_.toDiagnosticString(peFile_)));
        name_ = null;
      }
      else
      {
        name_ = peFile_.readCString(nameRVA_.getFilePosition());
      }
    }
    else
    {
      valid_ = false;
      parseErrors_.add(String.format("Export library name RVA offset is out " +
        "of bounds. (pos: %d; size: %d)", pos, peFile_.size()));
    }

    pos = relpos(Offsets.EXPORT_ADDRESS_TABLE_RVA.position);
    if (DataUtil.isInBounds(pos, peFile_))
    {
      exportAddressTableRVA_ = new RelativeVirtualAddress(
        peFile_.readInt32(pos), peFile_.getSections());
      if (!exportAddressTableRVA_.isValid(peFile_))
      {
        valid_ = false;
        parseErrors_.add(String.format("Export address table file position " +
          "is out of bounds. (%s)",
          exportAddressTableRVA_.toDiagnosticString(peFile_)));
      }
    }
    else
    {
      valid_ = false;
      parseErrors_.add(String.format("Export address table RVA offset is out " +
        "of bounds. (pos: %d; size: %d)", pos, peFile_.size()));
    }

    pos = relpos(Offsets.NAME_POINTER_RVA.position);
    if (DataUtil.isInBounds(pos, peFile_))
    {
      namePointerRVA_ = new RelativeVirtualAddress(
        peFile_.readInt32(relpos(Offsets.NAME_POINTER_RVA.position)),
        peFile_.getSections());
      if (!namePointerRVA_.isValid(peFile_) ||
        namePointerRVA_.getSection() == null)
      {
        valid_ = false;
        parseErrors_.add(String.format("Name pointer table file position is " +
          "out of bounds. (%s)", namePointerRVA_.toDiagnosticString(peFile_)));
      }
    }
    else
    {
      valid_ = false;
      parseErrors_.add(String.format("Name pointer table RVA offset is out " +
        "of bounds. (pos: %d; size: %d)", pos, peFile_.size()));
    }

    pos = relpos(Offsets.ORDINAL_TABLE_RVA.position);
    if (DataUtil.isInBounds(pos, peFile_))
    {
      ordinalTableRVA_ = new RelativeVirtualAddress(peFile_.readInt32(pos),
        peFile_.getSections());
      if (!ordinalTableRVA_.isValid(peFile_) ||
        ordinalTableRVA_.getSection() == null)
      {
        valid_ = false;
        parseErrors_.add(String.format("Ordinal table file position is out " +
          "of bounds. (%s)", ordinalTableRVA_.toDiagnosticString(peFile_)));
      }
    }
    else
    {
      valid_ = false;
      parseErrors_.add(String.format("Ordinal table RVA offset is out " +
        "of bounds. (pos: %d; size: %d)", pos, peFile_.size()));
    }

    if (namePointerRVA_ != null && namePointerRVA_.getSection() != null &&
      ordinalTableRVA_ != null && ordinalTableRVA_.getSection() != null)
    {
      int numberOfNamePointers = getNumberOfNamePointers();
      namePointerTable_ = new RelativeVirtualAddress[numberOfNamePointers];
      nameTable_ = new String[numberOfNamePointers];
      ordinalTable_ = new Ordinal[numberOfNamePointers];
      for (int i = 0; i < numberOfNamePointers; i++)
      {
        namePointerTable_[i] = new RelativeVirtualAddress(
          peFile_.readInt32(namePointerRVA_.getFilePosition()),
          peFile_.getSections());
        nameTable_[i] =
          peFile_.readCString(namePointerTable_[i].getFilePosition());
        ordinalTable_[i] = new Ordinal(peFile_.readUInt16(
          ordinalTableRVA_.getFilePosition() + (i * 2)),
          getOrdinalBase());
      }
    }
    else
    {
      namePointerTable_ = new RelativeVirtualAddress[0];
      nameTable_ = new String[0];
      ordinalTable_ = new Ordinal[0];
    }
  }

  @Override
  public int getHeaderSize()
  {
    return HEADER_SIZE;
  }

  @Override
  public long getStartOffset()
  {
    return rva_.getFilePosition();
  }

  @Override
  public long getEndOffset()
  {
    return getStartOffset() + getHeaderSize();
  }

  public int getExportFlags()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.EXPORT_FLAGS.position));
  }

  public int getTimeDateStampValue()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.TIME_DATE_STAMP_VALUE.position));
  }

  public short getMajorVersion()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt16(relpos(Offsets.MAJOR_VERSION.position));
  }

  public short getMinorVersion()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt16(relpos(Offsets.MINOR_VERSION.position));
  }

  public RelativeVirtualAddress getNameRVA()
  {
    return nameRVA_;
  }

  public int getOrdinalBase()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.ORDINAL_BASE.position));
  }

  public int getNumberOfAddressEntries()
    throws IOException, EndOfStreamException
  {
    return
      peFile_.readInt32(relpos(Offsets.NUMBER_OF_ADDRESS_ENTRIES.position));
  }

  public int getNumberOfNamePointers()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.NUMBER_OF_NAME_POINTERS.position));
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
    throws IOException, EndOfStreamException
  {
    return Date.from(Instant.ofEpochSecond(getTimeDateStampValue()));
  }

  public boolean isValid()
  {
    return valid_;
  }

  public List<String> getParseErrors()
  {
    return Collections.unmodifiableList(parseErrors_);
  }

  @Override
  public String toString()
  {
    return name_;
  }

  private long relpos(long pos)
  {
    return getStartOffset() + pos;
  }
}
