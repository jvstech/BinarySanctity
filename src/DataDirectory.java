import java.io.IOException;

public class DataDirectory implements Header
{
  public static final int HEADER_SIZE = 8;
  private final PortableExecutableFileChannel peFile_;
  private final DataDirectoryIndex index_;

  private int virtualAddress_;
  private int size_;
  private RelativeVirtualAddress rva_;

  public DataDirectory(PortableExecutableFileChannel peFile, int directoryIndex)
    throws IOException, BadExecutableFormatException, EndOfStreamException
  {
    peFile_ = peFile;
    index_ = DataDirectoryIndex.values()[directoryIndex];
    try
    {
      virtualAddress_ = peFile_.readInt32(relpos(0L));
      size_ = peFile_.readInt32(relpos(4L));
    }
    catch (EndOfStreamException eofEx)
    {
      throw new BadExecutableFormatException(
        "Number of data directories too small or invalid data." +
          String.format("Expected %d directories but only read %d.",
            peFile_.getOptionalHeader().getNumberOfRvaAndSizes(), index_));
    }
  }

  public OptionalHeader getOptionalHeader()
  {
    return peFile_.getOptionalHeader();
  }

  public DataDirectoryIndex getIndex()
  {
    return index_;
  }

  public int getVirtualAddress()
  {
    return virtualAddress_;
  }

  public int getSize()
  {
    return size_;
  }

  public RelativeVirtualAddress getRVA()
  {
    return rva_;
  }

  @Override
  public int getHeaderSize()
    throws IOException, EndOfStreamException
  {
    return HEADER_SIZE;
  }

  @Override
  public long getStartOffset()
    throws IOException, EndOfStreamException
  {
    return peFile_.getOptionalHeader().getEndOffset() +
      (index_.ordinal() * HEADER_SIZE);
  }

  @Override
  public long getEndOffset()
    throws IOException, EndOfStreamException
  {
    return getStartOffset() + getHeaderSize();
  }

  private long relpos(long pos)
    throws IOException, EndOfStreamException
  {
    return getStartOffset() + pos;
  }
}
