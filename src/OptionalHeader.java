//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           OptionalHeader.java
//! @description    Represents the third part of a portable executable file -
//!                 the options header. Although it's called 'optional', it's
//!                 actually a required header. 'Optional' in this case simply
//!                 means it contains information about optional parts of the
//!                 rest of the executable file.
//!

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class OptionalHeader implements Header
{
  // From Microsoft documentation
  // (https://docs.microsoft.com/en-us/windows/win32/debug/pe-format):
  //
  // Every image file has an optional header that provides information to the
  // loader. This header is optional in the sense that some files (specifically,
  // object files) do not have it. For image files, this header is required. An
  // object file can have an optional header, but generally this header has no
  // function in an object file except to increase its size.

  private final PortableExecutableFileChannel peFile_;
  private final ImageStateType imageState_;

  enum Offsets
  {
    MAGIC                       (0x00),
    MAJOR_LINKER_VERSION        (0x02),
    MINOR_LINKER_VERSION        (0x03),
    SIZE_OF_CODE                (0x04),
    SIZE_OF_INITIALIZED_DATA    (0x08),
    SIZE_OF_UNINITIALIZED_DATA  (0x0C),
    ADDRESS_OF_ENTRY_POINT      (0x10),
    BASE_OF_CODE                (0x14),
    BASE_OF_DATA                (0x18),
    IMAGE_BASE_64               (0x18),
    IMAGE_BASE_32               (0x1C),
    SECTION_ALIGNMENT           (0x20),
    FILE_ALIGNMENT              (0x24),
    MAJOR_OS_VERSION            (0x28),
    MINOR_OS_VERSION            (0x2A),
    MAJOR_IMAGE_VERSION         (0x2C),
    MINOR_IMAGE_VERSION         (0x2E),
    MAJOR_SUBSYSTEM_VERSION     (0x30),
    MINOR_SUBSYSTEM_VERSION     (0x32),
    WIN32_VERSION_VALUE         (0x34),
    SIZE_OF_IMAGE               (0x38),
    SIZE_OF_HEADERS             (0x3C),
    CHECKSUM                    (0x40),
    SUBSYSTEM_VALUE             (0x44),
    DLL_CHARACTERISTICS_VALUE   (0x46),
    SIZE_OF_STACK_RESERVE_32    (0x48),
    SIZE_OF_STACK_RESERVE_64    (0x48),
    SIZE_OF_STACK_COMMIT_32     (0x4C),
    SIZE_OF_STACK_COMMIT_64     (0x50),
    SIZE_OF_HEAP_RESERVE_32     (0x50),
    SIZE_OF_HEAP_RESERVE_64     (0x58),
    SIZE_OF_HEAP_COMMIT_32      (0x54),
    SIZE_OF_HEAP_COMMIT_64      (0x60),
    LOADER_FLAGS_32             (0x58),
    LOADER_FLAGS_64             (0x68),
    NUMBER_OF_RVA_AND_SIZES_32  (0x5C),
    NUMBER_OF_RVA_AND_SIZES_64  (0x6C)
    ;

    public final int position;

    private Offsets(int offset)
    {
      position = offset;
    }
  }

  private PEHeader peHeader_;

  public OptionalHeader(PortableExecutableFileChannel peFile)
    throws IOException, EndOfStreamException
  {
    peFile_ = peFile;
    imageState_ = getImageState();
  }

  // Determines whether an executable image file is PE32, PE32+, or a ROM image.
  public int getMagicValue()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(relpos(Offsets.MAGIC.position));
  }

  // Determines whether an executable image file is PE32, PE32+, or a ROM image.
  public ImageStateType getImageState()
    throws IOException, EndOfStreamException
  {
    return ImageStateType.fromInt(getMagicValue());
  }

  public short getMajorLinkerVersion()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt8(relpos(Offsets.MAJOR_LINKER_VERSION.position));
  }

  public short getMinorLinkerVersion()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt8(relpos(Offsets.MINOR_LINKER_VERSION.position));
  }

  // Size of the code section (.text), or the sum of all code sections if there
  // are multiple sections.
  public int getSizeOfCode()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.SIZE_OF_CODE.position));
  }

  // Size of the initialized data section (.data/.rdata), or the sum of all such
  // sections if there are multiple data sections.
  public int getSizeOfInitializedData()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.SIZE_OF_INITIALIZED_DATA.position));
  }

  // Size of the uninitialized data section (.bss), or the sum of all such
  // sections if there are multiple BSS sections.
  public int getSizeofUninitializedData()
    throws IOException, EndOfStreamException
  {
    return
      peFile_.readInt32(relpos(Offsets.SIZE_OF_UNINITIALIZED_DATA.position));
  }

  // The address of the entry point relative to the image base when the
  // executable file is loaded into memory. For program images, this is the
  // starting address. For device drivers, this is the address of the
  // initialization function. An entry point is optional for DLLs. When no entry
  // point is present, this field must be zero.
  public int getAddressOfEntryPoint()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.ADDRESS_OF_ENTRY_POINT.position));
  }

  // The address that is relative to the image base of the beginning-of-code
  // section when it is loaded into memory.
  public int getBaseOfCode()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.BASE_OF_CODE.position));
  }

  // The address that is relative to the image base of the beginning-of-data
  // section when it is loaded into memory. This value is only available in
  // 32-bit executable images (PE32 files).
  public int getBaseOfData()
    throws IOException, EndOfStreamException
  {
    if (imageState_ != ImageStateType.PE32)
    {
      return 0;
    }

    return peFile_.readInt32(relpos(Offsets.BASE_OF_DATA.position));
  }

  // The preferred address of the first byte of image when loaded into memory;
  // must be a multiple of 64 K. The default for DLLs is 0x10000000. The default
  // for Windows CE EXEs is 0x00010000. The default for Windows NT,
  // Windows 2000, Windows XP, Windows 95, Windows 98, and Windows Me is
  // 0x00400000.
  public long getImageBase()
    throws IOException, EndOfStreamException
  {
    if (imageState_ == ImageStateType.PE64)
    {
      return peFile_.readInt64(relpos(Offsets.IMAGE_BASE_64.position));
    }

    return peFile_.readInt32(relpos(Offsets.IMAGE_BASE_32.position));
  }

  // The alignment factor (in bytes) that is used to align the raw data of
  // sections in the image file. The value should be a power of 2 between 512
  // and 64 K, inclusive. The default is 512. If the SectionAlignment is less
  // than the architecture's page size, then FileAlignment must match
  // SectionAlignment.
  public int getSectionAlignment()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.SECTION_ALIGNMENT.position));
  }

  // The alignment factor (in bytes) that is used to align the raw data of
  // sections in the image file. The value should be a power of 2 between 512
  // and 64 K, inclusive. The default is 512. If the SectionAlignment is less
  // than the architecture's page size, then FileAlignment must match
  // SectionAlignment.
  public int getFileAlignment()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.FILE_ALIGNMENT.position));
  }

  // Major version number of the required operating system
  public short getMajorOperatingSystemVersion()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt16(relpos(Offsets.MAJOR_OS_VERSION.position));
  }

  // Minor version number of the required operating system
  public short getMinorOperatingSystemVersion()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt16(relpos(Offsets.MINOR_OS_VERSION.position));
  }

  // Major version number of the executable image
  public short getMajorImageVersion()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt16(relpos(Offsets.MAJOR_IMAGE_VERSION.position));
  }

  // Minor version number of the executable image
  public short getMinorImageVersion()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt16(relpos(Offsets.MINOR_IMAGE_VERSION.position));
  }

  // Major version number of the target subsystem
  public short getMajorSubsystemVersion()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt16(relpos(Offsets.MAJOR_SUBSYSTEM_VERSION.position));
  }

  // Minor version number of the target subsystem
  public short getMinorSubsystemVersion()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt16(relpos(Offsets.MINOR_SUBSYSTEM_VERSION.position));
  }

  // Reserved value; must be zero
  public int getWin32VersionValue()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.WIN32_VERSION_VALUE.position));
  }

  // The size (in bytes) of the image, including all headers, as the image is
  // loaded in memory. It must be a multiple of SectionAlignment.
  public long getSizeOfImage()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt32(relpos(Offsets.SIZE_OF_IMAGE.position));
  }

  // The combined size of an MS-DOS stub, PE header, and section headers rounded
  // up to a multiple of FileAlignment.
  public long getSizeOfHeaders()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt32(relpos(Offsets.SIZE_OF_HEADERS.position));
  }

  // Image file checksum
  public int getChecksum()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(relpos(Offsets.CHECKSUM.position));
  }

  // Value representing the Windows subsystem that is required to run this
  // image.
  public int getSubsystemValue()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(relpos(Offsets.SUBSYSTEM_VALUE.position));
  }

  public int getDllCharacteristicsValue()
    throws IOException, EndOfStreamException
  {
    return
      peFile_.readUInt16(relpos(Offsets.DLL_CHARACTERISTICS_VALUE.position));
  }

  // The size of the stack to reserve. Only SizeOfStackCommit is committed; the
  // rest is made available one page at a time until the reserve size is
  // reached.
  public long getSizeOfStackReserve()
    throws IOException, EndOfStreamException
  {
    return (imageState_ == ImageStateType.PE64
      ? peFile_.readInt64(relpos(Offsets.SIZE_OF_STACK_RESERVE_64.position))
      : peFile_.readInt32(relpos(Offsets.SIZE_OF_STACK_RESERVE_32.position)));
  }

  // The size of the stack to commit.
  public long getSizeOfStackCommit()
    throws IOException, EndOfStreamException
  {
    return (imageState_ == ImageStateType.PE64
      ? peFile_.readInt64(relpos(Offsets.SIZE_OF_STACK_COMMIT_64.position))
      : peFile_.readInt32(relpos(Offsets.SIZE_OF_STACK_COMMIT_32.position)));
  }

  // The size of the local heap space to reserve. Only SizeOfHeapCommit is
  // committed; the rest is made available one page at a time until the reserve
  // size is reached.
  public long getSizeOfHeapReserve()
    throws IOException, EndOfStreamException
  {
    return (imageState_ == ImageStateType.PE64
      ? peFile_.readInt64(relpos(Offsets.SIZE_OF_HEAP_RESERVE_64.position))
      : peFile_.readInt32(relpos(Offsets.SIZE_OF_HEAP_RESERVE_32.position)));
  }

  // The size of the local heap space to commit.
  public long getSizeOfHeapCommit()
    throws IOException, EndOfStreamException
  {
    return (imageState_ == ImageStateType.PE64
      ? peFile_.readInt64(relpos(Offsets.SIZE_OF_HEAP_COMMIT_64.position))
      : peFile_.readInt32(relpos(Offsets.SIZE_OF_HEAP_COMMIT_32.position)));
  }

  // Reserved; must be zero
  public int getLoaderFlags()
    throws IOException, EndOfStreamException
  {
    return (imageState_ == ImageStateType.PE64
      ? peFile_.readInt32(relpos(Offsets.LOADER_FLAGS_64.position))
      : peFile_.readInt32(relpos(Offsets.LOADER_FLAGS_32.position)));
  }

  // The number of data-directory entries in the remainder of the optional
  // header. Each describes a location and size.
  public int getNumberOfRvaAndSizes()
    throws IOException, EndOfStreamException
  {
    return (imageState_ == ImageStateType.PE64
      ? peFile_.readInt32(relpos(Offsets.NUMBER_OF_RVA_AND_SIZES_64.position))
      : peFile_.readInt32(relpos(Offsets.NUMBER_OF_RVA_AND_SIZES_32.position)));
  }

  public boolean isValid()
    throws IOException
  {
    try
    {
      int magicValue = getMagicValue();
      int fileAlignment = getFileAlignment();
      return ((magicValue == 0x107 || magicValue == 0x10b ||
        magicValue == 0x20b) &&
        // File alignment must be a power of 2 (which can be checked with a
        // popcount -- in this case, Integer.bitCount()) and must be inclusively
        // between 512 and 64K.
        (Integer.bitCount(fileAlignment) == 1 && fileAlignment <= 65536 &&
          fileAlignment >= 512) &&
        // Section alignment must be greater than or equal to file alignment.
        (getSectionAlignment() >= fileAlignment));
    }
    catch (EndOfStreamException eofEx)
    {
      return false;
    }
  }

  // returns the DLL characteristics value without the first four reserved bits
  public int getDllCharacteristics()
    throws IOException, EndOfStreamException
  {
    return (getDllCharacteristicsValue() & DllCharacteristicTypes.FIELD_MASK);
  }

  @Override
  public int getHeaderSize()
  {
    if (imageState_ == ImageStateType.PE64)
    {
      return Offsets.NUMBER_OF_RVA_AND_SIZES_64.position + 4;
    }

    return Offsets.NUMBER_OF_RVA_AND_SIZES_32.position + 4;
  }

  @Override
  public long getStartOffset()
    throws IOException, EndOfStreamException
  {
    return peFile_.getPEHeader().getEndOffset();
  }

  @Override
  public long getEndOffset()
    throws IOException, EndOfStreamException
  {
    return getStartOffset() + getHeaderSize();
  }

  public int getFullHeaderSize()
    throws IOException, EndOfStreamException
  {
    return peHeader_.getSizeOfOptionalHeader();
  }

  public long getFullEndOffset()
    throws IOException, EndOfStreamException
  {
    return getStartOffset() + getFullHeaderSize();
  }

  public WindowsSubsystemType getSubsystem()
    throws IOException, EndOfStreamException
  {
    return WindowsSubsystemType.fromInt(getSubsystemValue());
  }

  public long getSectionsTableOffset()
    throws IOException, EndOfStreamException
  {
    return peHeader_.getOptionalHeaderOffset() +
      peHeader_.getSizeOfOptionalHeader();
  }

  public PEHeader getPEHeader()
  {
    return peFile_.getPEHeader();
  }
  
  @Override
  public String toString()
  {
    StringWriter sw = new StringWriter();
    PrintWriter w = new PrintWriter(sw);
    try
    {
      w.printf("Image type:              %s\n", getImageState());
      w.printf("Linker version:          %d.%d\n", getMajorLinkerVersion(), 
        getMinorLinkerVersion());
      w.printf("Size of code:            %d\n", getSizeOfCode());
      w.printf("Size of data section(s): %d\n", getSizeOfInitializedData());
      w.printf("Size of BSS section(s):  %d\n", getSizeofUninitializedData());
      w.printf("Address of entry point:  0x%x\n", getAddressOfEntryPoint());
      w.printf("Base of code:            0x%x\n", getBaseOfCode());
      w.printf("Base of data:            0x%x\n", getBaseOfData());
      w.printf("Image base:              0x%x\n", getImageBase());
      w.printf("Section alignment:       %d\n", getSectionAlignment());
      w.printf("File alignment:          %d\n", getFileAlignment());
      w.printf("OS version:              %d.%d\n",
        getMajorOperatingSystemVersion(), getMinorOperatingSystemVersion());
      w.printf("Image/program version:   %d.%d\n",
        getMajorImageVersion(), getMinorImageVersion());
      w.printf("Subsystem version:       %d.%d\n",
        getMajorSubsystemVersion(), getMinorSubsystemVersion());
      w.printf("Win32 version:           %d\n", getWin32VersionValue());
      w.printf("Size of image:           %d\n", getSizeOfImage());
      w.printf("Size of headers:         %d\n", getSizeOfHeaders());
      w.printf("Checksum:                0x%x\n", getChecksum());
      w.printf("Windows subsystem:       %s\n", getSubsystem());
      w.printf("DLL characteristics:     0x%x\n", getDllCharacteristics());
      for (String dllCharType :
        DllCharacteristicTypes.getStrings(getDllCharacteristics()))
      {
        w.printf("                         %s\n", dllCharType);
      }

      w.printf("Size of stack (reserve): %d\n", getSizeOfStackReserve());
      w.printf("Size of stack (commit):  %d\n", getSizeOfStackCommit());
      w.printf("Size of heap (reserve):  %d\n", getSizeOfHeapReserve());
      w.printf("Size of heap (commit):   %d\n", getSizeOfHeapCommit());
      w.printf("Loader flags:            %d\n", getLoaderFlags());
      w.printf("Data directory count:    %d\n", getNumberOfRvaAndSizes());
    }
    catch (IOException e)
    {
      w.println("I/O exception while reading the Optional header:");
      e.printStackTrace(w);
    }
    catch (EndOfStreamException e)
    {
      w.println("Reached the end of data before finishing reading the " +
        "Optional header.");
      e.printStackTrace(w);
    }

    return sw.toString();
  }

  void validate()
    throws IOException, BadExecutableFormatException
  {
    if (!isValid())
    {
      throw new BadExecutableFormatException("Invalid optional header.");
    }
  }

  private long relpos(long pos)
    throws IOException, EndOfStreamException
  {
    return peFile_.getDOSHeader().getPEHeaderOffset() +
      peFile_.getPEHeader().getHeaderSize() +
      pos;
  }
}
