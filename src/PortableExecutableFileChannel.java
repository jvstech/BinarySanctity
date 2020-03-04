//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           PortableExecutableFileChannel.java
//! @description    File or memory buffer that represents a complete portable
//!                 executable program image.
//!

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
  private final ImportDirectory[] importDirectories_;

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
    importDirectories_ = loadImportDirectories(this);
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
    importDirectories_ = loadImportDirectories(this);
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
    importDirectories_ = loadImportDirectories(this);
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
    importDirectories_ = loadImportDirectories(this);
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

  public ExportDirectory getExportDirectory()
  {
    return exportDirectory_;
  }

  public ImportDirectory[] getImportDirectories()
  {
    return importDirectories_;
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

  private static ImportDirectory[] loadImportDirectories(
    PortableExecutableFileChannel peFile)
    throws BadExecutableFormatException, IOException
  {
    if (!peFile.hasDataDirectory(DataDirectoryIndex.IMPORT_TABLE))
    {
      return null;
    }

    ArrayList<ImportDirectory> importDirectoryTable = new ArrayList<>();
    int i = 0;
    for (;; i++)
    {
      try
      {
        ImportDirectory idt = new ImportDirectory(peFile, i);
        if (!idt.isValid())
        {
          break;
        }

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
}
