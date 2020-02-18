import java.io.IOException;
import java.util.ArrayList;

public class ImportDirectory
{
  private RelativeVirtualAddress importLookupTableRVA_;
  private int timeDateStamp_;
  private int forwarderChain_;
  private RelativeVirtualAddress nameRVA_;
  private RelativeVirtualAddress importAddressTableRVA_;
  private String name_;
  private ImportLookup[] importLookupTable_;

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

  @Override
  public String toString()
  {
    return name_;
  }

  public static ImportDirectory[] fromStream(ByteIOStream stream,
    OptionalHeader optionalHeader, Iterable<SectionHeader> sections)
    throws BadExecutableFormatException, IOException
  {
    if (sections == null)
    {
      throw new IllegalArgumentException("Section list is null.");
    }

    ArrayList<ImportDirectory> importDirectoryTable = new ArrayList<>();
    for (;;)
    {
      try
      {
        ImportDirectory idt = new ImportDirectory();
        idt.importLookupTableRVA_ =
          new RelativeVirtualAddress(stream.readInt32(), sections);
        idt.timeDateStamp_ = stream.readInt32();
        idt.forwarderChain_ = stream.readInt32();
        idt.nameRVA_ =
          new RelativeVirtualAddress(stream.readInt32(), sections);
        idt.importAddressTableRVA_ =
          new RelativeVirtualAddress(stream.readInt32(), sections);
        if (idt.importLookupTableRVA_.getValue() == 0)
        {
          // indicates the end of the import directory table
          break;
        }

        int rewindPos = stream.getPosition();
        idt.name_ = stream.readCString();
        stream.setPosition((int)(idt.importLookupTableRVA_.getFilePosition()));
        idt.importLookupTable_ =
          ImportLookup.fromStream(stream, optionalHeader, sections);
        stream.setPosition(rewindPos);

        importDirectoryTable.add(idt);
      }
      catch (EndOfStreamException eofEx)
      {
        throw
          new BadExecutableFormatException("Invalid import directory table.");
      }
    }

    return importDirectoryTable.toArray(new ImportDirectory[0]);
  }

  public static ImportDirectory[] fromStream(ByteIOStream stream,
    OptionalHeader optionalHeader, SectionHeader[] sections)
    throws BadExecutableFormatException, IOException
  {
    return fromStream(stream, optionalHeader,
      java.util.Arrays.asList(sections));
  }
}
