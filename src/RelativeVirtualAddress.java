import java.io.IOException;

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
      int pos = value_ - section.getVirtualAddress();
      if (pos >= 0 && pos < section.getVirtualAddress())
      {
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
