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
import java.nio.channels.FileChannel;
import java.util.*;

public class PortableExecutableFileChannel extends ReadOnlyBinaryFileChannel
{
  private final long startPosition_;
  private final DOSHeader dosHeader_;
  private final PEHeader peHeader_;
  private final OptionalHeader optionalHeader_;
  private final SectionHeader[] sections_;
  private final HashMap<String, List<SectionHeader>> sectionTable_;
  private final DataDirectory[] dataDirectories_;
  private final ExportDirectory exportDirectory_;
  private final ImportDirectory[] importDirectories_;

  private PortableExecutableFileChannel(FileChannel fileChannel)
    throws IOException, EndOfStreamException, BadExecutableFormatException
  {
    super(fileChannel);
    startPosition_ = position();
    dosHeader_ = new DOSHeader(this);
    dosHeader_.validate();
    peHeader_ = new PEHeader(this);
    peHeader_.validate();
    optionalHeader_ = new OptionalHeader(this);
    optionalHeader_.validate();
    sections_ = loadSectionHeaders(this);
    sectionTable_ = loadSectionTable(sections_);
    dataDirectories_ = loadDataDirectories(this);
    exportDirectory_ = loadExportDirectory(this);
    importDirectories_ = loadImportDirectories(this);
  }

  public static PortableExecutableFileChannel create(FileChannel fileChannel)
    throws IOException, EndOfStreamException, BadExecutableFormatException
  {
    return new PortableExecutableFileChannel(fileChannel);
  }

  public static PortableExecutableFileChannel create(
    FileInputStream fileInputStream)
    throws EndOfStreamException, BadExecutableFormatException, IOException
  {
    return create(fileInputStream.getChannel());
  }

  public static PortableExecutableFileChannel create(String filePath)
    throws IOException, EndOfStreamException, BadExecutableFormatException
  {
    return create((new FileInputStream(filePath)).getChannel());
  }

  public static PortableExecutableFileChannel create(File file)
    throws IOException, EndOfStreamException, BadExecutableFormatException
  {
    return create((new FileInputStream(file).getChannel()));
  }

  public static PortableExecutableFileChannel create(byte[] data)
    throws EndOfStreamException, BadExecutableFormatException, IOException
  {
    return create(new ReadOnlyBinaryFileChannel(data));
  }

  public static PortableExecutableFileChannel fromBase64(String base64String)
    throws EndOfStreamException, BadExecutableFormatException, IOException,
      IllegalArgumentException
  {
    byte[] decodedData = Base64.getDecoder().decode(base64String);
    return create(new ReadOnlyBinaryFileChannel(decodedData));
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

  public final HashMap<String, List<SectionHeader>> getSectionTable()
  {
    return sectionTable_;
  }

  public SectionHeader getSection(int index)
  {
    return getSections()[index];
  }

  public List<SectionHeader> getSections(String name)
  {
    List<SectionHeader> result = getSectionTable().getOrDefault(name,
      new ArrayList<>(Collections.emptyList()));
    return Collections.unmodifiableList(result);
  }

  public DataDirectory[] getDataDirectories()
  {
    return dataDirectories_;
  }

  public DataDirectory[] getExistingDataDirectories()
  {
    List<DataDirectory> dirs = new ArrayList<>();
    for (DataDirectory dir : dataDirectories_)
    {
      if (dir.getSize() > 0)
      {
        dirs.add(dir);
      }
    }

    return dirs.toArray(new DataDirectory[0]);
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

  public boolean hasImportDirectories()
  {
    return (importDirectories_ != null && importDirectories_.length > 0);
  }

  public boolean hasValidImportDirectories()
  {
    return (hasImportDirectories() &&
      Arrays.stream(importDirectories_)
        .allMatch(ImportDirectory::isValid)
      );
  }

  public ImportDirectory[] getImportDirectories()
  {
    return importDirectories_;
  }

  public TreeMap<String, String[]> getImportedNames(boolean fixNullNames)
    throws IOException, EndOfStreamException
  {
    TreeMap<String, String[]> importedNames = new TreeMap<>();
    if (!hasDataDirectory(DataDirectoryIndex.IMPORT_TABLE))
    {
      return importedNames;
    }

    for (ImportDirectory importDirectory : importDirectories_)
    {
      String importedName = importDirectory.getName();
      if (importedName == null && fixNullNames)
      {
        importedName = "";
      }

      ArrayList<String> nameList = new ArrayList<>();
      for (ImportLookup importLookup : importDirectory.getImportLookupTable())
      {
        nameList.add(importLookup.toString());
      }


      // If this import already exists (which is common with null names),
      // combine the imported symbols.
      String[] existingImports = importedNames.get(importedName);
      if (existingImports != null)
      {
        nameList.addAll(0, Arrays.asList(existingImports));
      }

      importedNames.put(importedName,
        nameList.toArray(new String[0]));
    }

    return importedNames;
  }

  public TreeMap<String, String[]> getImportedNames()
    throws IOException, EndOfStreamException
  {
    return getImportedNames(false);
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

  private static HashMap<String, List<SectionHeader>> loadSectionTable(
    SectionHeader[] sections)
  {
    HashMap<String, List<SectionHeader>> sectionTable = new HashMap<>();
    for (SectionHeader section : sections)
    {
      if (sectionTable.containsKey(section.getName()))
      {
        sectionTable.get(section.getName()).add(section);
      }
      else
      {
        sectionTable.put(section.getName(),
          new ArrayList<>(Arrays.asList(section)));
      }
    }

    return sectionTable;
  }

  private static DataDirectory[] loadDataDirectories(
    PortableExecutableFileChannel peFile)
    throws BadExecutableFormatException, EndOfStreamException, IOException
  {
    // IMAGE_DATA_DIRECTORY is *always* 16 entries in size (16 entries * 8 bytes
    // each = 128 bytes). Ignore what NumberOfRvaAndSizes says -- it can be
    // forged.
    int dataDirCount = DataDirectoryIndex.values().length - 1;
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
