//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           HintName.java
//! @description    Represents a hint/name value. A 'hint' is an index into the
//!                 export name pointer table. A 'name' is an ASCII string that
//!                 contains the symbol name to import.
//!

import java.io.IOException;

public class HintName implements Header
{
  private final PortableExecutableFileChannel peFile_;
  private final RelativeVirtualAddress rva_;
  private final int hint_;
  private final String name_;
  private final int headerSize_;

  public HintName(PortableExecutableFileChannel peFile,
    RelativeVirtualAddress rva)
    throws IOException, EndOfStreamException
  {
    if (peFile == null || rva == null)
    {
      throw new NullPointerException();
    }

    if (rva.getValue() == 0)
    {
      throw new IllegalArgumentException();
    }

    peFile_ = peFile;
    rva_ = rva;
    if (rva_.isValid(peFile))
    {
      hint_ = peFile_.readUInt16(rva_.getFilePosition());
      name_ = peFile_.readCString(rva_.getFilePosition() + 2);
      // If the name length is even, including the trailing null byte would have
      // this entry end on an odd boundary. In this case, an extra null pad byte
      // is added. Therefore, if the name length is even, we must add two bytes
      // to the length to indicate where the next HintName begins. If the name
      // length is odd, the trailing null byte ends on a HintName boundary, so
      // we only need to add a single byte to compensate for the trailing null.
      if (name_.length() % 2 == 0)
      {
        headerSize_ = 2 + name_.length() + 2;
      }
      else
      {
        headerSize_ = 2 + name_.length() + 1;
      }
    }
    else
    {
      hint_ = 0;
      name_ = null;
      headerSize_ = 0;
    }
  }

  public int getHint()
  {
    return hint_;
  }

  public String getName()
  {
    return name_;
  }

  @Override
  public int getHeaderSize()
  {
    return headerSize_;
  }

  @Override
  public long getStartOffset()
  {
    return rva_.getFilePosition();
  }

  @Override
  public long getEndOffset()
  {
    return getStartOffset() + getHeaderSize();
  }

  @Override
  public String toString()
  {
    if (name_ != null)
    {
      return name_;
    }
    else if (hint_ != 0)
    {
      return String.format("<hint:0x%x>", hint_);
    }

    return String.format("<invalid_rva:0x%x>", rva_.getValue());
  }
}
