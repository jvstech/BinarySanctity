import java.io.IOException;
import java.util.ArrayList;

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
  private int timeDateStamp_;
  private int forwarderChain_;
  private RelativeVirtualAddress nameRVA_;
  private RelativeVirtualAddress importAddressTableRVA_;
  private String name_;
  private ImportLookup[] importLookupTable_;

  public ImportDirectory(PortableExecutableFileChannel peFile, int row)
    throws IOException, EndOfStreamException
  {
    if (!peFile.hasDataDirectory(DataDirectoryIndex.IMPORT_TABLE))
    {
      // FYI, this should *never* happen... EVERYTHING links against kernel32.
      throw new IllegalArgumentException();
    }

    peFile_ = peFile;
    row_ = row;
    rva_ = peFile_
      .getDataDirectories()[DataDirectoryIndex.IMPORT_TABLE.ordinal()]
      .getRVA();
    importLookupTableRVA_ =
      new RelativeVirtualAddress(peFile_.readInt32(relpos(
        Offsets.IMPORT_ADDRESS_TABLE_RVA.position)), peFile_.getSections());
    nameRVA_ =
      new RelativeVirtualAddress(peFile_.readInt32(relpos(
        Offsets.NAME_RVA.position)), peFile_.getSections());
    importAddressTableRVA_ =
      new RelativeVirtualAddress(peFile_.readInt32(relpos(
        Offsets.IMPORT_ADDRESS_TABLE_RVA.position)), peFile_.getSections());
    if (isValid())
    {
      name_ = peFile_.readCString(nameRVA_.getFilePosition());
      ArrayList<ImportLookup> ilt = new ArrayList<>();
      for (int i = 0; /* no condition */; i++)
      {
        ImportLookup il = new ImportLookup(peFile_, this, i);
        if (il.getBits() == 0)
        {
          break;
        }

        ilt.add(il);
      }

      importLookupTable_ = ilt.toArray(new ImportLookup[0]);
    }
  }

  public RelativeVirtualAddress getImportLookupTableRVA()
  {
    return importLookupTableRVA_;
  }

  // Stamp that is set to zero until the image is bound. After the image is
  // bound, this field is set to the time/data stamp of the DLL.
  public int getTimeDateStamp()
  {
    return timeDateStamp_;
  }

  public int getForwarderChain()
  {
    return forwarderChain_;
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
    return (importLookupTableRVA_.getValue() > 0);
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
