import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class PortableExecutableFileChannel extends ReadOnlyBinaryFileChannel
{
  private final long startPosition_;
  private final DOSHeader dosHeader_;
  private final PEHeader peHeader_;
  private final OptionalHeader optionalHeader_;
  private final SectionHeader[] sections_;
  private final HashMap<String, SectionHeader> sectionTable_;
  private final DataDirectory[] dataDirectories_;
  private final ExportDirectory exportDirectory_;
  //private final ImportDirectory[] importDirectories_;

  public PortableExecutableFileChannel(FileInputStream fileInputStream)
    throws IOException, EndOfStreamException, BadExecutableFormatException
  {
    super(fileInputStream);
    startPosition_ = position();
    dosHeader_ = new DOSHeader(this);
    peHeader_ = new PEHeader(this);
    optionalHeader_ = new OptionalHeader(this);
    sections_ = loadSectionHeaders(this);
    sectionTable_ = loadSectionTable(sections_);
    dataDirectories_ = loadDataDirectories(this);
    exportDirectory_ = loadExportDirectory(this);
  }

  public PortableExecutableFileChannel(String filePath)
    throws IOException, EndOfStreamException, BadExecutableFormatException
  {
    super(filePath);
    startPosition_ = position();
    dosHeader_ = new DOSHeader(this);
    peHeader_ = new PEHeader(this);
    optionalHeader_ = new OptionalHeader(this);
    sections_ = loadSectionHeaders(this);
    sectionTable_ = loadSectionTable(sections_);
    dataDirectories_ = loadDataDirectories(this);
    exportDirectory_ = loadExportDirectory(this);
  }

  public PortableExecutableFileChannel(File file)
    throws IOException, EndOfStreamException, BadExecutableFormatException
  {
    super(file);
    startPosition_ = position();
    dosHeader_ = new DOSHeader(this);
    peHeader_ = new PEHeader(this);
    optionalHeader_ = new OptionalHeader(this);
    sections_ = loadSectionHeaders(this);
    sectionTable_ = loadSectionTable(sections_);
    dataDirectories_ = loadDataDirectories(this);
    exportDirectory_ = loadExportDirectory(this);
  }

  public PortableExecutableFileChannel(byte[] data)
    throws IOException, EndOfStreamException, BadExecutableFormatException
  {
    super(data);
    startPosition_ = position();
    dosHeader_ = new DOSHeader(this);
    peHeader_ = new PEHeader(this);
    optionalHeader_ = new OptionalHeader(this);
    sections_ = loadSectionHeaders(this);
    sectionTable_ = loadSectionTable(sections_);
    dataDirectories_ = loadDataDirectories(this);
    exportDirectory_ = loadExportDirectory(this);
  }

  public long getStartPosition()
  {
    return startPosition_;
  }

  public DOSHeader getDOSHeader()
  {
    return dosHeader_;
  }

  public PEHeader getPEHeader()
  {
    return peHeader_;
  }

  public OptionalHeader getOptionalHeader()
  {
    return optionalHeader_;
  }

  public SectionHeader[] getSections()
  {
    return sections_;
  }

  public final HashMap<String, SectionHeader> getSectionTable()
  {
    return sectionTable_;
  }

  public SectionHeader getSection(int index)
  {
    return getSections()[index];
  }

  public SectionHeader getSection(String name)
  {
    return getSectionTable().get(name);
  }

  public DataDirectory[] getDataDirectories()
  {
    return dataDirectories_;
  }

  public boolean hasDataDirectory(DataDirectoryIndex dirIndex)
  {
    int idx = dirIndex.ordinal();
    if (idx >= dataDirectories_.length)
    {
      return false;
    }

    return (dataDirectories_[idx] != null &&
      dataDirectories_[idx].getSize() > 0);
  }

  private void validateAll()
    throws IOException, BadExecutableFormatException
  {
    dosHeader_.validate();
    peHeader_.validate();
    optionalHeader_.validate();
  }

  private static SectionHeader[] loadSectionHeaders(
    PortableExecutableFileChannel peFile)
    throws IOException, EndOfStreamException
  {
    int sectionCount = peFile.getPEHeader().getNumberOfSections();
    SectionHeader[] sectionHeaders = new SectionHeader[sectionCount];
    for (int i = 0; i < sectionCount; ++i)
    {
      SectionHeader section = new SectionHeader(peFile, i);
      sectionHeaders[i] = section;
    }

    return sectionHeaders;
  }

  private static HashMap<String, SectionHeader> loadSectionTable(
    SectionHeader[] sections)
  {
    HashMap<String, SectionHeader> sectionTable = new HashMap<>();
    for (SectionHeader section : sections)
    {
      sectionTable.put(section.getName(), section);
    }

    return sectionTable;
  }

  private static DataDirectory[] loadDataDirectories(
    PortableExecutableFileChannel peFile)
    throws BadExecutableFormatException, EndOfStreamException, IOException
  {
    int dataDirCount = peFile.getOptionalHeader().getNumberOfRvaAndSizes();
    DataDirectory[] dataDirectories = new DataDirectory[dataDirCount];
    for (int i = 0; i < dataDirCount; ++i)
    {
      DataDirectory dataDirectory = new DataDirectory(peFile, i);
      dataDirectories[i] = dataDirectory;
    }

    return dataDirectories;
  }

  private static ExportDirectory loadExportDirectory(
    PortableExecutableFileChannel peFile)
    throws BadExecutableFormatException, EndOfStreamException, IOException
  {
    if (!peFile.hasDataDirectory(DataDirectoryIndex.EXPORT_TABLE))
    {
      return null;
    }

    return new ExportDirectory(peFile);
  }
}
