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

public class OptionalHeader
{
  // From Microsoft documentation
  // (https://docs.microsoft.com/en-us/windows/win32/debug/pe-format):
  //
  // Every image file has an optional header that provides information to the
  // loader. This header is optional in the sense that some files (specifically,
  // object files) do not have it. For image files, this header is required. An
  // object file can have an optional header, but generally this header has no
  // function in an object file except to increase its size.


  private PEHeader peHeader_;
  private int magicValue_;
  private short majorLinkerVersion_;
  private short minorLinkerVersion_;
  private int sizeOfCode_;
  private int sizeOfInitializedData_;
  private int sizeofUninitializedData_;
  private int addressOfEntryPoint_;
  private int baseOfCode_;
  private int baseOfData_;
  private long imageBase_;
  private int sectionAlignment_;
  private int fileAlignment_;
  private short majorOperatingSystemVersion_;
  private short minorOperatingSystemVersion_;
  private short majorImageVersion_;
  private short minorImageVersion_;
  private short majorSubsystemVersion_;
  private short minorSubsystemVersion_;
  private int win32VersionValue_;
  private int sizeOfImage_;
  private int sizeOfHeaders_;
  private int checksum_;
  private int subsystemValue_;
  private int dllCharacteristicsValue_;
  private long sizeOfStackReserve_;
  private long sizeOfStackCommit_;
  private long sizeOfHeapReserve_;
  private long sizeOfHeapCommit_;
  private int loaderFlags_;
  private int numberOfRvaAndSizes_;

  public PEHeader getPEHeader()
  {
    return peHeader_;
  }

  // Determines whether an executable image file is PE32, PE32+, or a ROM image.
  public int getMagicValue()
  {
    return magicValue_;
  }

  // Determines whether an executable image file is PE32, PE32+, or a ROM image.
  public ImageStateType getImageState()
  {
    return ImageStateType.fromInt(magicValue_);
  }

  public short getMajorLinkerVersion()
  {
    return majorLinkerVersion_;
  }

  public short getMinorLinkerVersion()
  {
    return minorLinkerVersion_;
  }

  // Size of the code section (.text), or the sum of all code sections if there
  // are multiple sections.
  public int getSizeOfCode()
  {
    return sizeOfCode_;
  }

  // Size of the initialized data section (.data/.rdata), or the sum of all such
  // sections if there are multiple data sections.
  public int getSizeOfInitializedData()
  {
    return sizeOfInitializedData_;
  }

  // Size of the uninitialized data section (.bss), or the sum of all such
  // sections if there are multiple BSS sections.
  public int getSizeofUninitializedData()
  {
    return sizeofUninitializedData_;
  }

  // The address of the entry point relative to the image base when the
  // executable file is loaded into memory. For program images, this is the
  // starting address. For device drivers, this is the address of the
  // initialization function. An entry point is optional for DLLs. When no entry
  // point is present, this field must be zero.
  public int getAddressOfEntryPoint()
  {
    return addressOfEntryPoint_;
  }

  // The address that is relative to the image base of the beginning-of-code
  // section when it is loaded into memory.
  public int getBaseOfCode()
  {
    return baseOfCode_;
  }

  // The address that is relative to the image base of the beginning-of-data
  // section when it is loaded into memory. This value is only available in
  // 32-bit executable images (PE32 files).
  public int getBaseOfData()
  {
    return baseOfData_;
  }

  // The preferred address of the first byte of image when loaded into memory;
  // must be a multiple of 64 K. The default for DLLs is 0x10000000. The default
  // for Windows CE EXEs is 0x00010000. The default for Windows NT,
  // Windows 2000, Windows XP, Windows 95, Windows 98, and Windows Me is
  // 0x00400000.
  public long getImageBase()
  {
    return imageBase_;
  }

  // The alignment factor (in bytes) that is used to align the raw data of
  // sections in the image file. The value should be a power of 2 between 512
  // and 64 K, inclusive. The default is 512. If the SectionAlignment is less
  // than the architecture's page size, then FileAlignment must match
  // SectionAlignment.
  public int getSectionAlignment()
  {
    return sectionAlignment_;
  }

  // The alignment factor (in bytes) that is used to align the raw data of
  // sections in the image file. The value should be a power of 2 between 512
  // and 64 K, inclusive. The default is 512. If the SectionAlignment is less
  // than the architecture's page size, then FileAlignment must match
  // SectionAlignment.
  public int getFileAlignment()
  {
    return fileAlignment_;
  }

  // Major version number of the required operating system
  public short getMajorOperatingSystemVersion()
  {
    return majorOperatingSystemVersion_;
  }

  // Minor version number of the required operating system
  public short getMinorOperatingSystemVersion()
  {
    return minorOperatingSystemVersion_;
  }

  // Major version number of the executable image
  public short getMajorImageVersion()
  {
    return majorImageVersion_;
  }

  // Minor version number of the executable image
  public short getMinorImageVersion()
  {
    return minorImageVersion_;
  }

  // Major version number of the target subsystem
  public short getMajorSubsystemVersion()
  {
    return majorSubsystemVersion_;
  }

  // Minor version number of the target subsystem
  public short getMinorSubsystemVersion()
  {
    return minorSubsystemVersion_;
  }

  // Reserved value; must be zero
  public int getWin32VersionValue()
  {
    return win32VersionValue_;
  }

  // The size (in bytes) of the image, including all headers, as the image is
  // loaded in memory. It must be a multiple of SectionAlignment.
  public int getSizeOfImage()
  {
    return sizeOfImage_;
  }

  // The combined size of an MS-DOS stub, PE header, and section headers rounded
  // up to a multiple of FileAlignment.
  public int getSizeOfHeaders()
  {
    return sizeOfHeaders_;
  }

  // Image file checksum
  public int getChecksum()
  {
    return checksum_;
  }

  // Value representing the Windows subsystem that is required to run this
  // image.
  public int getSubsystemValue()
  {
    return subsystemValue_;
  }

  public int getDllCharacteristicsValue()
  {
    return dllCharacteristicsValue_;
  }

  // The size of the stack to reserve. Only SizeOfStackCommit is committed; the
  // rest is made available one page at a time until the reserve size is
  // reached.
  public long getSizeOfStackReserve()
  {
    return sizeOfStackReserve_;
  }

  // The size of the stack to commit.
  public long getSizeOfStackCommit()
  {
    return sizeOfStackCommit_;
  }

  // The size of the local heap space to reserve. Only SizeOfHeapCommit is
  // committed; the rest is made available one page at a time until the reserve
  // size is reached.
  public long getSizeOfHeapReserve()
  {
    return sizeOfHeapReserve_;
  }

  // The size of the local heap space to commit.
  public long getSizeOfHeapCommit()
  {
    return sizeOfHeapCommit_;
  }

  // Reserved; must be zero
  public int getLoaderFlags()
  {
    return loaderFlags_;
  }

  // The number of data-directory entries in the remainder of the optional
  // header. Each describes a location and size.
  public int getNumberOfRvaAndSizes()
  {
    return numberOfRvaAndSizes_;
  }

  public boolean isValid()
  {
    return (magicValue_ == 0x107 || magicValue_ == 0x10b ||
      magicValue_ == 0x20b);
  }

  // returns the DLL characteristics value without the first four reserved bits
  public int getDllCharacteristics()
  {
    return (dllCharacteristicsValue_ & DllCharacteristicTypes.FIELD_MASK);
  }

  public int getHeaderSize()
  {
    return peHeader_.getSizeOfOptionalHeader();
  }

  public WindowsSubsystemType getSubsystem()
  {
    return WindowsSubsystemType.fromInt(subsystemValue_);
  }

  public long getSectionsTableOffset()
  {
    return peHeader_.getOptionalHeaderOffset() +
      peHeader_.getSizeOfOptionalHeader();
  }

  public static OptionalHeader fromStream(ByteIOStream stream,
                                          PEHeader peHeader)
    throws BadExecutableFormatException
  {
    if (peHeader == null)
    {
      throw new IllegalArgumentException("PE header is null.");
    }

    OptionalHeader opt = new OptionalHeader();

    // Minimum header size is 24 bytes for PE32. If there is no header size,
    // this is either an object file, a ROM image, or invalid data.
    if (peHeader.getSizeOfOptionalHeader() < 24)
    {
      return opt;
    }

    opt.peHeader_ = peHeader;
    boolean readValid = false;
    try
    {
      opt.magicValue_ = stream.readUInt16();
      if (!opt.isValid())
      {
        throw new BadExecutableFormatException(
          "Invalid image state type/magic value.");
      }

      opt.majorLinkerVersion_ = (short) stream.readUByte();
      opt.minorLinkerVersion_ = (short) stream.readUByte();
      opt.sizeOfCode_ = stream.readInt32();
      opt.sizeOfInitializedData_ = stream.readInt32();
      opt.sizeofUninitializedData_ = stream.readInt32();
      opt.addressOfEntryPoint_ = stream.readInt32();
      opt.baseOfCode_ = stream.readInt32();
      if (opt.getHeaderSize() == 24)
      {
        return opt;
      }

      readValid = true;
      if (opt.getImageState() == ImageStateType.PE64)
      {
        opt.imageBase_ = stream.readInt64();
        opt.baseOfData_ = 0;
      } else
      {
        opt.baseOfData_ = stream.readInt32();
        opt.imageBase_ = stream.readInt32();
      }

      opt.sectionAlignment_ = stream.readInt32();
      opt.fileAlignment_ = stream.readInt32();
      opt.majorOperatingSystemVersion_ = stream.readInt16();
      opt.minorOperatingSystemVersion_ = stream.readInt16();
      opt.majorImageVersion_ = stream.readInt16();
      opt.minorImageVersion_ = stream.readInt16();
      opt.majorSubsystemVersion_ = stream.readInt16();
      opt.minorSubsystemVersion_ = stream.readInt16();
      opt.win32VersionValue_ = stream.readInt32();
      opt.sizeOfImage_ = stream.readInt32();
      opt.sizeOfHeaders_ = stream.readInt32();
      opt.checksum_ = stream.readInt32();
      opt.subsystemValue_ = stream.readUInt16();
      opt.dllCharacteristicsValue_ = stream.readUInt16();
      boolean is64Bit = opt.getImageState() == ImageStateType.PE64;
      opt.sizeOfStackReserve_ = is64Bit
        ? stream.readInt64()
        : stream.readInt32();
      opt.sizeOfStackCommit_ = is64Bit
        ? stream.readInt64()
        : stream.readInt32();
      opt.sizeOfHeapReserve_ = is64Bit
        ? stream.readInt64()
        : stream.readInt32();
      opt.sizeOfHeapCommit_ = is64Bit
        ? stream.readInt64()
        : stream.readInt32();
      opt.loaderFlags_ = stream.readInt32();
      opt.numberOfRvaAndSizes_ = stream.readInt32();
    }
    catch (BadExecutableFormatException exeEx)
    {
      // rethrow
      throw exeEx;
    }
    catch (EndOfStreamException eofEx)
    {
      if (!readValid)
      {
        throw new BadExecutableFormatException(
          "Optional header too short for its given size; " +
          opt.getHeaderSize() + " bytes expected.");
      }
    }
    catch (Exception ex)
    {
      return new OptionalHeader();
    }

    return opt;
  }
}
