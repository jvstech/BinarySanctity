import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;

public class ReadOnlyByteArrayChannel extends FileChannel
{
  private final ByteIOStream buffer_;
  private long position_;

  public ReadOnlyByteArrayChannel(ByteIOStream data)
  {
    buffer_ = data;
    position_ = 0;
  }

  public ReadOnlyByteArrayChannel(byte[] data)
  {
    this(new ByteIOStream(data));
  }

  @Override
  public int read(ByteBuffer dst)
    throws IOException
  {
    if (!isOpen())
    {
      throw new ClosedChannelException();
    }

    long len = Math.min(buffer_.getLength() - position_, dst.remaining());
    if (len < 1)
    {
      return -1;
    }

    dst.put(buffer_.getBuffer(), (int)position_, (int)len);
    position_ += len;

    return (int)len;
  }

  @Override
  public long read(ByteBuffer[] dsts, int offset, int length)
  {
    return 0;
  }

  @Override
  public int write(ByteBuffer src)
  {
    throw new UnsupportedOperationException(
      "Cannot write to a read-only byte array stream.");
  }

  @Override
  public long write(ByteBuffer[] srcs, int offset, int length)
  {
    throw new UnsupportedOperationException(
      "Cannot write to a read-only byte array stream.");
  }

  @Override
  public long position()
    throws IOException
  {
    if (!isOpen())
    {
      throw new ClosedChannelException();
    }

    return position_;
  }

  @Override
  public FileChannel position(long newPosition)
    throws IOException
  {
    if (!isOpen())
    {
      throw new ClosedChannelException();
    }

    if (newPosition < 0)
    {
      throw new IllegalArgumentException("position_ must be non negative");
    }

    position_ = newPosition;
    return this;
  }

  @Override
  public long size()
    throws IOException
  {
    if (!isOpen())
    {
      throw new ClosedChannelException();
    }

    return buffer_.getLength();
  }

  @Override
  public FileChannel truncate(long size)
  {
    throw new UnsupportedOperationException(
      "Cannot truncate a read-only byte array stream.");
  }

  @Override
  public void force(boolean metaData)
  {
    // nothing to do
  }

  @Override
  public long transferTo(long pos, long count, WritableByteChannel target)
    throws IOException
  {
    if (!isOpen())
    {
      throw new ClosedChannelException();
    }

    ByteBuffer bb = ByteBuffer.allocate((int)count);
    long oldPos = position_;
    position(pos);
    int readCount = read(bb);
    bb.flip();
    target.write(bb);
    position(oldPos);

    return readCount;
  }

  @Override
  public long transferFrom(ReadableByteChannel src, long pos, long count)
    throws IOException
  {
    throw new UnsupportedOperationException(
      "Cannot write to a read-only byte array stream.");
  }

  @Override
  public int read(ByteBuffer dst, long position)
    throws IOException
  {
    long oldPos = position_;
    this.position(position);
    int readCount = read(dst);
    this.position(oldPos);
    return readCount;
  }

  @Override
  public int write(ByteBuffer src, long position)
  {
    throw new UnsupportedOperationException(
      "Cannot write to a read-only byte array stream.");
  }

  @Override
  public MappedByteBuffer map(MapMode mode, long position, long size)
  {
    throw new UnsupportedOperationException("Mapping is not implemented.");
  }

  @Override
  public FileLock lock(long position, long size, boolean shared)
  {
    throw new UnsupportedOperationException("Locking is not implemented.");
  }

  @Override
  public FileLock tryLock(long position, long size, boolean shared)
  {
    throw new UnsupportedOperationException("Locking is not implemented.");
  }

  @Override
  protected void implCloseChannel()
  {
    // literally don't have to do anything
  }
}
