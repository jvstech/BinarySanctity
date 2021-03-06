//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           ImportDirectory.java
//! @description    Represents the portable executable data directory
//!                 responsible for listing the imported function and their
//!                 associated DLLs as used by the program.
//!


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportDirectory implements Header
{
  public static final int HEADER_SIZE = 20;

  enum Offsets
  {
    IMPORT_LOOKUP_TABLE_RVA   (0x00),
    TIME_DATE_STAMP           (0x04),
    FORWARDER_CHAIN           (0x08),
    NAME_RVA                  (0x0C),
    IMPORT_ADDRESS_TABLE_RVA  (0x10)
    ;

    public final int position;

    private Offsets(int offset)
    {
      position = offset;
    }
  }

  private final PortableExecutableFileChannel peFile_;
  private final RelativeVirtualAddress rva_;
  private final int row_;

  private RelativeVirtualAddress importLookupTableRVA_;
  private RelativeVirtualAddress nameRVA_;
  private RelativeVirtualAddress importAddressTableRVA_;
  private String name_;
  private ImportLookup[] importLookupTable_;
  private boolean valid_ = true;
  private ArrayList<String> parseErrors_ = new ArrayList<>();

  public ImportDirectory(PortableExecutableFileChannel peFile, int row)
    throws IOException, EndOfStreamException
  {
    peFile_ = peFile;
    row_ = row;
    rva_ = peFile_
      .getDataDirectories()[DataDirectoryIndex.IMPORT_TABLE.ordinal()]
      .getRVA();
    if (!rva_.isValid(peFile_) || rva_.getSection() == null)
    {
      valid_ = false;
      parseErrors_.add(String.format("Invalid import directory RVA. (%s)",
        rva_.toDiagnosticString(peFile_)));
    }

    long iltRvaPos = relpos(Offsets.IMPORT_LOOKUP_TABLE_RVA.position);
    if (DataUtil.isInBounds(iltRvaPos, peFile_))
    {
      int iltRva = peFile_.readInt32(iltRvaPos);
      importLookupTableRVA_ =
        new RelativeVirtualAddress(iltRva, peFile_.getSections());
      if (!importLookupTableRVA_.isValid(peFile_))
      {
        // If the ILT isn't valid, the IAT *should* be valid (at least, on
        // disk). The IAT is a clone of the ILT until its functions are mapped
        // in by the loader. If the IAT isn't valid, there is something very
        // wrong with the executable.
        boolean isILTValid = false;
        iltRvaPos = relpos(Offsets.IMPORT_ADDRESS_TABLE_RVA.position);
        if (DataUtil.isInBounds(iltRvaPos, peFile_))
        {
          iltRva = peFile_.readInt32(iltRvaPos);
          importLookupTableRVA_ =
            new RelativeVirtualAddress(iltRva, peFile_.getSections());
          isILTValid = importLookupTableRVA_.isValid(peFile_);
        }

        if (!isILTValid)
        {
          valid_ = false;
          parseErrors_.add(String.format("Import lookup table file position is " +
              "out of bounds. (%s)",
            importLookupTableRVA_.toDiagnosticString(peFile_)));
        }
      }
    }
    else
    {
      valid_ = false;
      parseErrors_.add(String.format("Import lookup table RVA offset is out " +
        "of bounds. (pos: %d; size: %d)", iltRvaPos, peFile_.size()));
    }

    long nameRvaPos = relpos(Offsets.NAME_RVA.position);
    if (DataUtil.isInBounds(nameRvaPos, peFile_))
    {
      int nameRva = peFile_.readInt32(nameRvaPos);
      nameRVA_ = new RelativeVirtualAddress(nameRva, peFile_.getSections());
      if (!nameRVA_.isValid(peFile_))
      {
        valid_ = false;
        parseErrors_.add(String.format(
          "Import library name file position is out of bounds. (%s)",
          nameRVA_.toDiagnosticString(peFile_)));
      }
    }
    else
    {
      valid_ = false;
      parseErrors_.add(String.format("Import library name RVA offset is out " +
        "of bounds. (pos: %d; size: %d)", nameRvaPos, peFile_.size()));
    }

    long iatRvaPos = relpos(Offsets.IMPORT_ADDRESS_TABLE_RVA.position);
    if (DataUtil.isInBounds(iatRvaPos, peFile_))
    {
      int iatRva = peFile_.readInt32(iatRvaPos);
      importAddressTableRVA_ =
        new RelativeVirtualAddress(iatRva, peFile_.getSections());
      if (!importAddressTableRVA_.isValid(peFile_))
      {
        valid_ = false;
        parseErrors_.add(String.format("Import address table file position " +
            "is out of bounds. (%s)",
          importAddressTableRVA_.toDiagnosticString(peFile_)));
      }
    }
    else
    {
      valid_ = false;
      parseErrors_.add(String.format("Import address table RVA offset is out " +
        "of bounds. (pos: %d; size: %d)", iatRvaPos, peFile_.size()));
    }

    if (nameRVA_ != null && nameRVA_.isValid(peFile))
    {
      name_ = peFile_.readCString(nameRVA_.getFilePosition());
    }
    else
    {
      name_ = null;
    }

    if (isValid())
    {
      ArrayList<ImportLookup> ilt = new ArrayList<>();
      for (int i = 0; /* no condition */; i++)
      {
        ImportLookup il = new ImportLookup(peFile_, this, i);
        if (!il.isValid())
        {
          break;
        }

        ilt.add(il);
      }

      importLookupTable_ = ilt.toArray(new ImportLookup[0]);
    }
  }

  public List<String> getParseErrors()
  {
    return Collections.unmodifiableList(parseErrors_);
  }

  public RelativeVirtualAddress getImportLookupTableRVA()
  {
    return importLookupTableRVA_;
  }

  // Stamp that is set to zero until the image is bound. After the image is
  // bound, this field is set to the time/data stamp of the DLL.
  public int getTimeDateStamp()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.TIME_DATE_STAMP.position));
  }

  public int getForwarderChain()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.FORWARDER_CHAIN.position));
  }

  public RelativeVirtualAddress getNameRVA()
  {
    return nameRVA_;
  }

  public RelativeVirtualAddress getImportAddressTableRVA()
  {
    return importAddressTableRVA_;
  }

  public RelativeVirtualAddress getFirstThunk()
  {
    return getImportAddressTableRVA();
  }

  public RelativeVirtualAddress getOriginalFirstThunk()
  {
    return getImportLookupTableRVA();
  }

  public String getName()
  {
    return name_;
  }

  public ImportLookup[] getImportLookupTable()
  {
    return importLookupTable_;
  }

  public boolean isValid()
  {
    return (valid_ && importLookupTableRVA_ != null &&
      importLookupTableRVA_.getValue() > 0 &&
      importLookupTableRVA_.getSection() != null);
  }

  @Override
  public int getHeaderSize()
  {
    return HEADER_SIZE;
  }

  @Override
  public long getStartOffset()
  {
    return rva_.getFilePosition() + (getHeaderSize() * row_);
  }

  @Override
  public long getEndOffset()
  {
    return getStartOffset() + getHeaderSize();
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
