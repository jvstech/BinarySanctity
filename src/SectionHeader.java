//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           SectionHeader.java
//! @description    Represents a row in a PE section table.
//!

import java.io.IOException;

public class SectionHeader implements Header
{
  private static final int HEADER_SIZE = 40;

  private final PortableExecutableFileChannel peFile_;

  enum Offsets
  {
    NAME                    (0x00),
    VIRTUAL_SIZE            (0x08),
    VIRTUAL_ADDRESS         (0x0C),
    SIZE_OF_RAW_DATA        (0x10),
    POINTER_TO_RAW_DATA     (0x14),
    POINTER_TO_RELOCATIONS  (0x18),
    POINTER_TO_LINE_NUMBERS (0x1C),
    NUMBER_OF_RELOCATIONS   (0x20),
    NUMBER_OF_LINE_NUMBERS  (0x22),
    CHARACTERISTICS         (0x24)
    ;

    public final int position;

    private Offsets(int offset)
    {
      position = offset;
    }
  }

  private final String name_;
  private final int index_;
  private final RelativeVirtualAddress rva_;

  public SectionHeader(PortableExecutableFileChannel peFile, int sectionIndex)
    throws IOException, EndOfStreamException
  {
    peFile_ = peFile;
    index_ = sectionIndex;
    name_ = peFile_.readCString(relpos(Offsets.NAME.position), 8);
    rva_ = new RelativeVirtualAddress(this, index_);
  }

  @Override
  public int getHeaderSize()
  {
    return HEADER_SIZE;
  }

  @Override
  public long getStartOffset()
    throws IOException, EndOfStreamException
  {
    return peFile_.getOptionalHeader().getFullEndOffset() +
      (getHeaderSize() * index_);
  }

  @Override
  public long getEndOffset()
    throws IOException, EndOfStreamException
  {
    return getStartOffset() + getHeaderSize();
  }

  // An 8-byte, null-padded UTF-8 encoded string. If the string is exactly 8
  // characters long, there is no terminating null. For longer names, this field
  // contains a slash (/) that is followed by an ASCII representation of a
  // decimal number that is an offset into the string table. Executable images
  // do not use a string table and do not support section names longer than 8
  // characters. Long names in object files are truncated if they are emitted to
  // an executable file.
  public String getName()
  {
    return name_;
  }

  // The total size of the section when loaded into memory. If this value is
  // greater than SizeOfRawData, the section is zero-padded. This field is valid
  // only for executable images and should be set to zero for object files.
  public int getVirtualSize()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.VIRTUAL_SIZE.position));
  }

  // For executable images, the address of the first byte of the section
  // relative to the image base when the section is loaded into memory. For
  // object files, this field is the address of the first byte before relocation
  // is applied; for simplicity, compilers should set this to zero. Otherwise,
  // it is an arbitrary value that is subtracted from offsets during relocation.
  public int getVirtualAddress()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.VIRTUAL_ADDRESS.position));
  }

  // The size of the section (for object files) or the size of the initialized
  // data on disk (for image files). For executable images, this must be a
  // multiple of FileAlignment from the optional header. If this is less than
  // VirtualSize, the remainder of the section is zero-filled. Because the
  // SizeOfRawData field is rounded but the VirtualSize field is not, it is
  // possible for SizeOfRawData to be greater than VirtualSize as well. When a
  // section contains only uninitialized data, this field should be zero.
  public int getSizeOfRawData()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.SIZE_OF_RAW_DATA.position));
  }

  // The file pointer to the first page of the section within the COFF file. For
  // executable images, this must be a multiple of FileAlignment from the
  // optional header. For object files, the value should be aligned on a 4-byte
  // boundary for best performance. When a section contains only uninitialized
  // data, this field should be zero.
  public int getPointerToRawData()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.POINTER_TO_RAW_DATA.position));
  }

  // The file pointer to the beginning of relocation entries for the section.
  // This is set to zero for executable images or if there are no relocations.
  public int getPointerToRelocations()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.POINTER_TO_RELOCATIONS.position));
  }

  // The file pointer to the beginning of line-number entries for the section.
  // This is set to zero if there are no COFF line numbers. This value should be
  // zero for an image because COFF debugging information is deprecated.
  public int getPointerToLineNumbers()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.POINTER_TO_LINE_NUMBERS.position));
  }

  // The number of relocation entries for the section. This is set to zero for
  // executable images.
  public int getNumberOfRelocations()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(relpos(Offsets.NUMBER_OF_RELOCATIONS.position));
  }

  // The number of line-number entries for the section. This value should be
  // zero for an image because COFF debugging information is deprecated.
  public int getNumberOfLineNumbers()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(relpos(Offsets.NUMBER_OF_LINE_NUMBERS.position));
  }

  // The flags that describe the characteristics of the section.
  public int getCharacteristics()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.CHARACTERISTICS.position));
  }

  public RelativeVirtualAddress getRVA()
  {
    return rva_;
  }

  private long relpos(long pos)
    throws IOException, EndOfStreamException
  {
    return peFile_.getDOSHeader().getPEHeaderOffset() +
      peFile_.getPEHeader().getHeaderSize() +
      peFile_.getPEHeader().getSizeOfOptionalHeader() +
      (HEADER_SIZE * index_) +
      pos;
  }
}
