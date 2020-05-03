//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           ImportLookup.java
//! @description    Maps an index from an import directory to an imported
//!                 function name or import hint and virtual address.
//!

import java.io.IOException;

public class ImportLookup implements Header
{
  private final PortableExecutableFileChannel peFile_;
  private final ImportDirectory importDirectory_;
  private final ImageStateType imageStateType_;

  private RelativeVirtualAddress hintNameRVA_;
  private HintName hintName_;
  private int index_;

  public ImportLookup(PortableExecutableFileChannel peFile,
    ImportDirectory importDirectory, int index)
    throws IOException, EndOfStreamException
  {
    if (peFile == null || importDirectory == null)
    {
      throw new NullPointerException();
    }

    if (index < 0 || !importDirectory.isValid())
    {
      throw new IllegalArgumentException();
    }

    peFile_ = peFile;
    importDirectory_ = importDirectory;
    imageStateType_ = peFile_.getOptionalHeader().getImageState();
    index_ = index;
    if (getBits() == 0)
    {
      index_ = -1;
      hintNameRVA_ = null;
      hintName_ = null;
    }
    else
    {
      if (isImportByOrdinal())
      {
        hintNameRVA_ = null;
        hintName_ = null;
      }
      else
      {
        hintNameRVA_ =
          new RelativeVirtualAddress((int)(getBits() & ((1 << 30) - 1)),
            peFile_.getSections());
        hintName_ = new HintName(peFile_, hintNameRVA_);
      }
    }
  }

  public long getBits()
    throws IOException, EndOfStreamException
  {
    if (imageStateType_ == ImageStateType.PE64)
    {
      return peFile_.readInt64(getStartOffset());
    }

    return peFile_.readUInt32(getStartOffset());
  }

  public boolean isImportByOrdinal()
    throws IOException, EndOfStreamException
  {
    if (imageStateType_ == ImageStateType.PE64)
    {
      return ((getBits() >>> 63) != 0);
    }

    return ((getBits() >>> 31) != 0);
  }

  public short getOrdinalNumber()
    throws IOException, EndOfStreamException
  {
    return (short)(getBits() & ((1 << 15) - 1));
  }

  public int getHint()
  {
    return hintName_.getHint();
  }

  public String getName()
  {
    return hintName_.getName();
  }

  public int getIndex()
  {
    return index_;
  }

  public boolean isValid()
  {
    return (index_ >= 0 && hintNameRVA_ != null &&
      hintNameRVA_.getValue() != 0);
  }

  @Override
  public String toString()
  {
    try
    {
      if (isImportByOrdinal())
      {
        return String.format("<ordinal:%d>", getOrdinalNumber());
      }
    }
    catch (IOException | EndOfStreamException e)
    {
      return null;
    }

    return hintName_.toString();
  }

  @Override
  public int getHeaderSize()
    throws IOException, EndOfStreamException
  {
    if (peFile_.getOptionalHeader().getImageState() == ImageStateType.PE64)
    {
      return 8;
    }

    return 4;
  }

  @Override
  public long getStartOffset()
    throws IOException, EndOfStreamException
  {
    return importDirectory_.getImportLookupTableRVA().getFilePosition() +
      (getHeaderSize() * index_);
  }

  @Override
  public long getEndOffset()
    throws IOException, EndOfStreamException
  {
    return getStartOffset() + getHeaderSize();
  }
}
