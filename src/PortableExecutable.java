//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           PortableExecutable.java
//! @description    Represents a complete Windows portable executable image.
//!

import java.util.HashMap;

public class PortableExecutable
{
  private DOSHeader dosHeader_;
  private PEHeader peHeader_;
  private OptionalHeader optionalHeader_;
  private DataDirectory[] dataDirectories_;
  private SectionHeader[] sections_;
  private HashMap<String, SectionHeader> sectionTable_;
  private ExportDirectory exportDirectory_;
  private ImportDirectory[] importDirectories_;

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

  public DataDirectory[] getDataDirectories()
  {
    return dataDirectories_;
  }

  public SectionHeader[] getSections()
  {
    return sections_;
  }

  public ImportDirectory[] getImportDirectories()
  {
    return importDirectories_;
  }

  public ExportDirectory getExportDirectory()
  {
    return exportDirectory_;
  }

//  public static PortableExecutable fromStream(ByteIOStream stream)
//    throws BadExecutableFormatException
//  {
//    PortableExecutable pe = new PortableExecutable();
//    pe.dosHeader_ = DOSHeader.fromStream(stream);
//    if (pe.dosHeader_.isValid())
//    {
//      stream.setPosition(pe.dosHeader_.getPEHeaderOffset());
//      pe.peHeader_ = PEHeader.fromStream(stream, pe.dosHeader_);
//      if (pe.peHeader_.isValid())
//      {
//        pe.optionalHeader_ = OptionalHeader.fromStream(stream, pe.peHeader_);
//        pe.dataDirectories_ =
//          DataDirectory.fromStream(stream, pe.optionalHeader_);
//        pe.sections_ = SectionHeader.fromStream(stream, pe.optionalHeader_);
//        // fill the section table
//        pe.sectionTable_ = new HashMap<>();
//        for (SectionHeader section : pe.sections_)
//        {
//          pe.sectionTable_.put(section.getName(), section);
//        }
//
//        for (DataDirectory dataDirectory : pe.dataDirectories_)
//        {
//          dataDirectory.setRVA(new RelativeVirtualAddress(
//            dataDirectory.getVirtualAddress(), pe.sections_));
//          if (dataDirectory.getSize() == 0 ||
//            dataDirectory.getRVA().getValue() == 0)
//          {
//            continue;
//          }
//
//          int rewindPos = stream.getPosition();
//          switch (dataDirectory.getIndex())
//          {
//            case EXPORT_TABLE:
//              stream.setPosition(
//                (int)(dataDirectory.getRVA().getFilePosition()));
//              pe.exportDirectory_ =
//                ExportDirectory.fromStream(stream, pe.sections_);
//              break;
//
//            case IMPORT_TABLE:
//              stream.setPosition(
//                (int)(dataDirectory.getRVA().getFilePosition()));
//              pe.importDirectories_ = ImportDirectory.fromStream(stream,
//                pe.optionalHeader_, pe.sections_);
//              break;
//          }
//
//          stream.setPosition(rewindPos);
//        }
//      }
//    }
//
//    return pe;
//  }
}
