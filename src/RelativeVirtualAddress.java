//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           RelativeVirtualAddress.java
//! @description    Represents an address in an executable file as it would
//!                 exist after the program has been loaded into memory as a
//!                 process. It also provides a way to map a virtual address
//!                 to a file offset within the executable file.
//!

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.channels.FileChannel;

public class RelativeVirtualAddress
{
  private int value_;
  private SectionHeader section_;
  private int sectionIndex_;
  private long filePosition_;

  public RelativeVirtualAddress(int value, Iterable<SectionHeader> sections)
    throws IOException, EndOfStreamException
  {
    value_ = value;
    int i = 0;
    for (SectionHeader section : sections)
    {
      if (value_ >= 0 && value_ >= section.getVirtualAddress() &&
        value_ < section.getVirtualAddress() + section.getVirtualSize())
      {
        int pos = value_ - section.getVirtualAddress();
        section_ = section;
        sectionIndex_ = i;
        filePosition_ = (long)pos + section.getPointerToRawData();
        break;
      }

      i++;
    }
  }

  public RelativeVirtualAddress(int value, SectionHeader[] sections)
    throws IOException, EndOfStreamException
  {
    this(value, java.util.Arrays.asList(sections));
  }

  public RelativeVirtualAddress(int value, SectionHeader section,
    int sectionIndex)
    throws IOException, EndOfStreamException
  {
    value_ = value;
    section_ = section;
    sectionIndex_ = sectionIndex;
    filePosition_ = (value_ - section_.getVirtualAddress()) +
      section_.getPointerToRawData();
  }

  public RelativeVirtualAddress(SectionHeader section, int sectionIndex)
    throws IOException, EndOfStreamException
  {
    this(section.getVirtualAddress(), section, sectionIndex);
  }

  public int getValue()
  {
    return value_;
  }

  public boolean hasSection()
  {
    return (section_ != null);
  }

  public SectionHeader getSection()
  {
    return section_;
  }

  public int getSectionIndex()
  {
    return sectionIndex_;
  }

  public long getFilePosition()
  {
    return filePosition_;
  }

  public RelativeVirtualAddress offset(int offset,
    Iterable<SectionHeader> sections)
    throws IOException, EndOfStreamException
  {
    return new RelativeVirtualAddress(value_ + offset, sections);
  }

  public RelativeVirtualAddress offset(int offset,
    SectionHeader[] sections)
    throws IOException, EndOfStreamException
  {
    return new RelativeVirtualAddress(value_ + offset, sections);
  }

  public boolean isValid(long fileSize)
  {
    return (DataUtil.isInBounds(getFilePosition(), fileSize) &&
      section_ != null);
  }

  public boolean isValid(FileChannel fileChannel)
    throws IOException
  {
    return isValid(fileChannel.size());
  }

  public boolean isValid()
  {
    if (section_ != null)
    {
      try
      {
        return isValid(section_.getPEFile());
      }
      catch (IOException e)
      {
      }
    }

    return false;
  }

  public String toDiagnosticString(PortableExecutableFileChannel peFile)
  {
    StringWriter sw = new StringWriter();
    PrintWriter w = new PrintWriter(sw);
    sw.append("{ ");
    w.printf("RVA: %d (0x%x)", value_, value_);
    w.printf("; file offset: %d (0x%x)", filePosition_, filePosition_);
    PortableExecutableFileChannel refPe = peFile;
    if (refPe == null && section_ != null)
    {
      refPe = section_.getPEFile();
    }

    if (refPe != null)
    {
      long fileSize = 0;
      try
      {
        fileSize = refPe.size();
        w.printf("; file size: %d (0x%x)", fileSize, fileSize);
      }
      catch (IOException e)
      {
        // do nothing
      }
    }

    if (section_ != null)
    {
      w.printf("; section: %d (%s)", sectionIndex_, section_);
    }
    else
    {
      w.printf("; section: %d (<invalid>)", sectionIndex_);
    }

    sw.append(" }");
    return sw.toString();
  }

  public String toDiagnosticString()
  {
    return toDiagnosticString(null);
  }

  @Override
  public String toString()
  {
    return toDiagnosticString();
  }

  public static RelativeVirtualAddress fromFileOffset(long offset,
    Iterable<SectionHeader> sections)
    throws IOException, EndOfStreamException
  {
    int i = 0;
    for (SectionHeader section : sections)
    {
      if ((offset >= section.getPointerToRawData()) &&
        (offset <= section.getPointerToRawData() + section.getSizeOfRawData()))
      {
        return new RelativeVirtualAddress(
          ((int)(offset + section.getVirtualAddress() -
            section.getPointerToRawData())), section, i);
      }

      i++;
    }

    throw new IllegalArgumentException(
      "Offset is out of the range of any sections.");
  }

  public static RelativeVirtualAddress fromFileOffset(long offset,
    SectionHeader[] sections)
    throws IOException, EndOfStreamException
  {
    return fromFileOffset(offset, java.util.Arrays.asList(sections));
  }
}
