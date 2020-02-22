import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.*;

public class ReadOnlyBinaryFileChannel extends FileChannel
{
  private final FileChannel file_;

  public ReadOnlyBinaryFileChannel(FileInputStream fileInputStream)
  {
    file_ = fileInputStream.getChannel();
  }

  public ReadOnlyBinaryFileChannel(String filePath)
    throws FileNotFoundException
  {
    this(new FileInputStream(filePath));
  }

  public ReadOnlyBinaryFileChannel(File file)
    throws FileNotFoundException
  {
    this(new FileInputStream(file));
  }

  public ReadOnlyBinaryFileChannel(byte[] data)
  {
    if (data == null)
    {
      throw new NullPointerException();
    }

    file_ = new ReadOnlyByteArrayChannel(data);
  }

  protected FileChannel getFile()
  {
    return file_;
  }

  public int read(long position, byte[] buffer, int offset, int count)
    throws IOException
  {
    ByteBuffer byteBuffer = ByteBuffer.allocate(count);
    int bytesRead = read(byteBuffer, position);
    if (bytesRead == 0)
    {
      return 0;
    }

    byteBuffer.position(0);
    byteBuffer.get(buffer, offset, bytesRead);
    return bytesRead;
  }

  public int read(byte[] buffer, int offset, int count)
    throws IOException
  {
    ByteBuffer byteBuffer = ByteBuffer.allocate(count);
    int bytesRead = read(byteBuffer);
    if (bytesRead == 0)
    {
      return 0;
    }

    byteBuffer.position(0);
    byteBuffer.get(buffer, offset, bytesRead);
    return bytesRead;
  }

  public byte[] read(long position, int count)
    throws IOException, EndOfStreamException
  {
    ByteBuffer byteBuffer = ByteBuffer.allocate(count);
    int bytesRead = read(byteBuffer, position);
    if (bytesRead == 0)
    {
      throw new EndOfStreamException();
    }

    byte[] result = new byte[bytesRead];
    byteBuffer.position(0);
    byteBuffer.get(result, 0, bytesRead);
    return result;
  }

  public byte[] read(int count)
    throws IOException, EndOfStreamException
  {
    ByteBuffer byteBuffer = ByteBuffer.allocate(count);
    int bytesRead = read(byteBuffer);
    if (bytesRead == 0)
    {
      throw new EndOfStreamException();
    }

    byte[] result = new byte[bytesRead];
    byteBuffer.position(0);
    byteBuffer.get(result, 0, bytesRead);
    return result;
  }

  public byte readInt8(long position)
    throws IOException, EndOfStreamException
  {
    byte[] result = new byte[1];
    if (read(position, result, 0, 1) == 0)
    {
      throw new EndOfStreamException();
    }
    
    return result[0];
  }
  
  public byte readInt8() 
    throws IOException, EndOfStreamException
  {
    byte[] result = new byte[1];
    if (read(result, 0, 1) == 0)
    {
      throw new EndOfStreamException();
    }

    return result[0];
  }
  
  public short readUInt8(long position)
    throws IOException, EndOfStreamException
  {
    byte[] result = new byte[1];
    if (read(position, result, 0, 1) == 0)
    {
      throw new EndOfStreamException();
    }
    
    return (short)(result[0] & 0xff);
  }

  public short readUInt8()
    throws IOException, EndOfStreamException
  {
    byte[] result = new byte[1];
    if (read(result, 0, 1) == 0)
    {
      throw new EndOfStreamException();
    }

    return (short)(result[0] & 0xff);
  }
  
  public short readInt16(long position)
    throws IOException, EndOfStreamException
  {
    byte[] result = read(position, 2);
    if (result.length < 2)
    {
      throw new EndOfStreamException();
    }

    return (short)((result[1] << 8) |
      (result[0]));
  }

  public short readInt16()
    throws IOException, EndOfStreamException
  {
    byte[] result = read(2);
    if (result.length < 2)
    {
      throw new EndOfStreamException();
    }

    return (short)(((result[1] & 0xff) << 8) |
      ((result[0] & 0xff)));
  }

  public int readUInt16(long position)
    throws IOException, EndOfStreamException
  {
    byte[] result = read(position, 2);
    if (result.length < 2)
    {
      throw new EndOfStreamException();
    }

    return (((result[1] & 0xff) << 8) |
      ((result[0] & 0xff)));
  }

  public int readUInt16()
    throws IOException, EndOfStreamException
  {
    byte[] result = read(2);
    if (result.length < 2)
    {
      throw new EndOfStreamException();
    }

    return (((result[1] & 0xff) << 8) |
      ((result[0] & 0xff)));
  }

  public int readInt32(long position)
    throws IOException, EndOfStreamException
  {
    byte[] result = read(position, 4);
    if (result.length < 4)
    {
      throw new EndOfStreamException();
    }

    return (((result[3] & 0xff) << 24) |
      ((result[2] & 0xff) << 16) |
      ((result[1] & 0xff) << 8) |
      ((result[0] & 0xff)));
  }

  public int readInt32()
    throws IOException, EndOfStreamException
  {
    byte[] result = read(4);
    if (result.length < 4)
    {
      throw new EndOfStreamException();
    }

    return (((result[3] & 0xff) << 24) |
      ((result[2] & 0xff) << 16) |
      ((result[1] & 0xff) << 8) |
      ((result[0] & 0xff)));
  }

  public long readUInt32(long position)
    throws IOException, EndOfStreamException
  {
    byte[] result = read(position, 4);
    if (result.length < 4)
    {
      throw new EndOfStreamException();
    }

    return (long)(((result[3] & 0xff) << 24) |
      ((result[2] & 0xff) << 16) |
      ((result[1] & 0xff) << 8) |
      ((result[0] & 0xff)));
  }

  public long readUInt32()
    throws IOException, EndOfStreamException
  {
    byte[] result = read(4);
    if (result.length < 4)
    {
      throw new EndOfStreamException();
    }

    return (long)(((result[3] & 0xff) << 24) |
      ((result[2] & 0xff) << 16) |
      ((result[1] & 0xff) << 8) |
      ((result[0] & 0xff)));
  }

  public long readInt64(long position)
    throws IOException, EndOfStreamException
  {
    byte[] result = read(position, 8);
    return extractLEInt64(result);
  }

  public long readInt64()
    throws IOException, EndOfStreamException
  {
    byte[] result = read(8);
    return extractLEInt64(result);
  }

  private long extractLEInt64(byte[] result)
    throws EndOfStreamException
  {
    if (result == null)
    {
      throw new NullPointerException();
    }

    if (result.length < 8)
    {
      throw new EndOfStreamException();
    }

    return (((result[7] & 0xffL) << 56L) |
      ((result[6] & 0xffL) << 48L) |
      ((result[5] & 0xffL) << 40L) |
      ((result[4] & 0xffL) << 32L) |
      ((result[3] & 0xffL) << 24L) |
      ((result[2] & 0xffL) << 16L) |
      ((result[1] & 0xffL) << 8L) |
      ((result[0] & 0xffL)));
  }

  public String readCString(long position, int maxLength)
    throws IOException
  {
    StringBuilder sb = new StringBuilder();
    try
    {
      int charVal = readUInt8(position);
      while (charVal > 0 && position < size() && maxLength > 0)
      {
        sb.append((char)charVal);
        ++position;
        --maxLength;
        charVal = readUInt8(position);
      }
    }
    catch (EndOfStreamException eofEx)
    {
    }

    return sb.toString();
  }

  public String readCString(long position)
    throws IOException
  {
    StringBuilder sb = new StringBuilder();
    try
    {
      int charVal = readUInt8(position);
      while (charVal > 0 && position < size())
      {
        sb.append((char)charVal);
        ++position;
        charVal = readUInt8(position);
      }
    }
    catch (EndOfStreamException eofEx)
    {
    }

    return sb.toString();
  }

  public String readCString()
    throws IOException
  {
    StringBuilder sb = new StringBuilder();
    try
    {
      int charVal = readUInt8();
      while (charVal > 0)
      {
        sb.append((char)charVal);
        charVal = readUInt8();
      }
    }
    catch (EndOfStreamException eofEx)
    {
    }

    return sb.toString();
  }

  public String readString(long position, int length)
    throws IOException
  {
    try
    {
      byte[] charBytes = read(position, length);
      return new String(charBytes);
    }
    catch (EndOfStreamException eofEx)
    {
      return null;
    }
  }

  /**
   * Reads a sequence of bytes from this channel into the given buffer.
   *
   * <p> Bytes are read starting at this channel's current file position, and
   * then the file position is updated with the number of bytes actually
   * read.  Otherwise this method behaves exactly as specified in the {@link
   * ReadableByteChannel} interface. </p>
   *
   * @param dst
   */
  @Override
  public int read(ByteBuffer dst) throws IOException
  {
    return file_.read(dst);
  }

  /**
   * Reads a sequence of bytes from this channel into a subsequence of the
   * given buffers.
   *
   * <p> Bytes are read starting at this channel's current file position, and
   * then the file position is updated with the number of bytes actually
   * read.  Otherwise this method behaves exactly as specified in the {@link
   * ScatteringByteChannel} interface.  </p>
   *
   * @param dsts
   * @param offset
   * @param length
   */
  @Override
  public long read(ByteBuffer[] dsts, int offset, int length) throws IOException
  {
    return file_.read(dsts, offset, length);
  }

  @Override
  public int write(ByteBuffer src) throws IOException
  {
    throw new UnsupportedOperationException(
      "Writing to a read-only file channel is not supported.");
  }

  @Override
  public long write(ByteBuffer[] srcs, int offset, int length) throws IOException
  {
    throw new UnsupportedOperationException(
      "Writing to a read-only file channel is not supported.");
  }

  /**
   * Returns this channel's file position.
   *
   * @return This channel's file position,
   * a non-negative integer counting the number of bytes
   * from the beginning of the file to the current position
   * @throws ClosedChannelException If this channel is closed
   * @throws IOException            If some other I/O error occurs
   */
  @Override
  public long position() throws IOException
  {
    return file_.position();
  }

  /**
   * Sets this channel's file position.
   *
   * <p> Setting the position to a value that is greater than the file's
   * current size is legal but does not change the size of the file.  A later
   * attempt to read bytes at such a position will immediately return an
   * end-of-file indication.  A later attempt to write bytes at such a
   * position will cause the file to be grown to accommodate the new bytes;
   * the values of any bytes between the previous end-of-file and the
   * newly-written bytes are unspecified.  </p>
   *
   * @param newPosition The new position, a non-negative integer counting
   *                    the number of bytes from the beginning of the file
   * @return This file channel
   * @throws ClosedChannelException   If this channel is closed
   * @throws IllegalArgumentException If the new position is negative
   * @throws IOException              If some other I/O error occurs
   */
  @Override
  public FileChannel position(long newPosition) throws IOException
  {
    file_.position(newPosition);
    return this;
  }

  /**
   * Returns the current size of this channel's file.
   *
   * @return The current size of this channel's file,
   * measured in bytes
   * @throws ClosedChannelException If this channel is closed
   * @throws IOException            If some other I/O error occurs
   */
  @Override
  public long size() throws IOException
  {
    return file_.size();
  }

  /**
   * Truncates this channel's file to the given size.
   *
   * <p> If the given size is less than the file's current size then the file
   * is truncated, discarding any bytes beyond the new end of the file.  If
   * the given size is greater than or equal to the file's current size then
   * the file is not modified.  In either case, if this channel's file
   * position is greater than the given size then it is set to that size.
   * </p>
   *
   * @param size The new size, a non-negative byte count
   * @return This file channel
   * @throws NonWritableChannelException If this channel was not opened for writing
   * @throws ClosedChannelException      If this channel is closed
   * @throws IllegalArgumentException    If the new size is negative
   * @throws IOException                 If some other I/O error occurs
   */
  @Override
  public FileChannel truncate(long size) throws IOException
  {
    throw new UnsupportedOperationException(
      "Truncating a read-only file channel is not supported.");
  }

  /**
   * Forces any updates to this channel's file to be written to the storage
   * device that contains it.
   *
   * <p> If this channel's file resides on a local storage device then when
   * this method returns it is guaranteed that all changes made to the file
   * since this channel was created, or since this method was last invoked,
   * will have been written to that device.  This is useful for ensuring that
   * critical information is not lost in the event of a system crash.
   *
   * <p> If the file does not reside on a local device then no such guarantee
   * is made.
   *
   * <p> The <tt>metaData</tt> parameter can be used to limit the number of
   * I/O operations that this method is required to perform.  Passing
   * <tt>false</tt> for this parameter indicates that only updates to the
   * file's content need be written to storage; passing <tt>true</tt>
   * indicates that updates to both the file's content and metadata must be
   * written, which generally requires at least one more I/O operation.
   * Whether this parameter actually has any effect is dependent upon the
   * underlying operating system and is therefore unspecified.
   *
   * <p> Invoking this method may cause an I/O operation to occur even if the
   * channel was only opened for reading.  Some operating systems, for
   * example, maintain a last-access time as part of a file's metadata, and
   * this time is updated whenever the file is read.  Whether or not this is
   * actually done is system-dependent and is therefore unspecified.
   *
   * <p> This method is only guaranteed to force changes that were made to
   * this channel's file via the methods defined in this class.  It may or
   * may not force changes that were made by modifying the content of a
   * {@link MappedByteBuffer <i>mapped byte buffer</i>} obtained by
   * invoking the {@link #map map} method.  Invoking the {@link
   * MappedByteBuffer#force force} method of the mapped byte buffer will
   * force changes made to the buffer's content to be written.  </p>
   *
   * @param metaData If <tt>true</tt> then this method is required to force changes
   *                 to both the file's content and metadata to be written to
   *                 storage; otherwise, it need only force content changes to be
   *                 written
   * @throws ClosedChannelException If this channel is closed
   * @throws IOException            If some other I/O error occurs
   */
  @Override
  public void force(boolean metaData) throws IOException
  {
    // literally do nothing since this is unsupported
  }

  /**
   * Transfers bytes from this channel's file to the given writable byte
   * channel.
   *
   * <p> An attempt is made to read up to <tt>count</tt> bytes starting at
   * the given <tt>position</tt> in this channel's file and write them to the
   * target channel.  An invocation of this method may or may not transfer
   * all of the requested bytes; whether or not it does so depends upon the
   * natures and states of the channels.  Fewer than the requested number of
   * bytes are transferred if this channel's file contains fewer than
   * <tt>count</tt> bytes starting at the given <tt>position</tt>, or if the
   * target channel is non-blocking and it has fewer than <tt>count</tt>
   * bytes free in its output buffer.
   *
   * <p> This method does not modify this channel's position.  If the given
   * position is greater than the file's current size then no bytes are
   * transferred.  If the target channel has a position then bytes are
   * written starting at that position and then the position is incremented
   * by the number of bytes written.
   *
   * <p> This method is potentially much more efficient than a simple loop
   * that reads from this channel and writes to the target channel.  Many
   * operating systems can transfer bytes directly from the filesystem cache
   * to the target channel without actually copying them.  </p>
   *
   * @param position The position within the file at which the transfer is to begin;
   *                 must be non-negative
   * @param count    The maximum number of bytes to be transferred; must be
   *                 non-negative
   * @param target   The target channel
   * @return The number of bytes, possibly zero,
   * that were actually transferred
   * @throws IllegalArgumentException    If the preconditions on the parameters do not hold
   * @throws NonReadableChannelException If this channel was not opened for reading
   * @throws NonWritableChannelException If the target channel was not opened for writing
   * @throws ClosedChannelException      If either this channel or the target channel is closed
   * @throws AsynchronousCloseException  If another thread closes either channel
   *                                     while the transfer is in progress
   * @throws ClosedByInterruptException  If another thread interrupts the current thread while the
   *                                     transfer is in progress, thereby closing both channels and
   *                                     setting the current thread's interrupt status
   * @throws IOException                 If some other I/O error occurs
   */
  @Override
  public long transferTo(long position, long count, WritableByteChannel target)
    throws IOException
  {
    return file_.transferTo(position, count, target);
  }

  /**
   * Transfers bytes into this channel's file from the given readable byte
   * channel.
   *
   * <p> An attempt is made to read up to <tt>count</tt> bytes from the
   * source channel and write them to this channel's file starting at the
   * given <tt>position</tt>.  An invocation of this method may or may not
   * transfer all of the requested bytes; whether or not it does so depends
   * upon the natures and states of the channels.  Fewer than the requested
   * number of bytes will be transferred if the source channel has fewer than
   * <tt>count</tt> bytes remaining, or if the source channel is non-blocking
   * and has fewer than <tt>count</tt> bytes immediately available in its
   * input buffer.
   *
   * <p> This method does not modify this channel's position.  If the given
   * position is greater than the file's current size then no bytes are
   * transferred.  If the source channel has a position then bytes are read
   * starting at that position and then the position is incremented by the
   * number of bytes read.
   *
   * <p> This method is potentially much more efficient than a simple loop
   * that reads from the source channel and writes to this channel.  Many
   * operating systems can transfer bytes directly from the source channel
   * into the filesystem cache without actually copying them.  </p>
   *
   * @param src      The source channel
   * @param position The position within the file at which the transfer is to begin;
   *                 must be non-negative
   * @param count    The maximum number of bytes to be transferred; must be
   *                 non-negative
   * @return The number of bytes, possibly zero,
   * that were actually transferred
   * @throws IllegalArgumentException    If the preconditions on the parameters do not hold
   * @throws NonReadableChannelException If the source channel was not opened for reading
   * @throws NonWritableChannelException If this channel was not opened for writing
   * @throws ClosedChannelException      If either this channel or the source channel is closed
   * @throws AsynchronousCloseException  If another thread closes either channel
   *                                     while the transfer is in progress
   * @throws ClosedByInterruptException  If another thread interrupts the current thread while the
   *                                     transfer is in progress, thereby closing both channels and
   *                                     setting the current thread's interrupt status
   * @throws IOException                 If some other I/O error occurs
   */
  @Override
  public long transferFrom(ReadableByteChannel src, long position, long count)
    throws IOException
  {
    throw new UnsupportedOperationException(
      "Writing to a read-only file channel is not supported.");
  }

  /**
   * Reads a sequence of bytes from this channel into the given buffer,
   * starting at the given file position.
   *
   * <p> This method works in the same manner as the {@link
   * #read(ByteBuffer)} method, except that bytes are read starting at the
   * given file position rather than at the channel's current position.  This
   * method does not modify this channel's position.  If the given position
   * is greater than the file's current size then no bytes are read.  </p>
   *
   * @param dst      The buffer into which bytes are to be transferred
   * @param position The file position at which the transfer is to begin;
   *                 must be non-negative
   * @return The number of bytes read, possibly zero, or <tt>-1</tt> if the
   * given position is greater than or equal to the file's current
   * size
   * @throws IllegalArgumentException    If the position is negative
   * @throws NonReadableChannelException If this channel was not opened for reading
   * @throws ClosedChannelException      If this channel is closed
   * @throws AsynchronousCloseException  If another thread closes this channel
   *                                     while the read operation is in progress
   * @throws ClosedByInterruptException  If another thread interrupts the current thread
   *                                     while the read operation is in progress, thereby
   *                                     closing the channel and setting the current thread's
   *                                     interrupt status
   * @throws IOException                 If some other I/O error occurs
   */
  @Override
  public int read(ByteBuffer dst, long position) throws IOException
  {
    return file_.read(dst, position);
  }

  /**
   * Writes a sequence of bytes to this channel from the given buffer,
   * starting at the given file position.
   *
   * <p> This method works in the same manner as the {@link
   * #write(ByteBuffer)} method, except that bytes are written starting at
   * the given file position rather than at the channel's current position.
   * This method does not modify this channel's position.  If the given
   * position is greater than the file's current size then the file will be
   * grown to accommodate the new bytes; the values of any bytes between the
   * previous end-of-file and the newly-written bytes are unspecified.  </p>
   *
   * @param src      The buffer from which bytes are to be transferred
   * @param position The file position at which the transfer is to begin;
   *                 must be non-negative
   * @return The number of bytes written, possibly zero
   * @throws IllegalArgumentException    If the position is negative
   * @throws NonWritableChannelException If this channel was not opened for writing
   * @throws ClosedChannelException      If this channel is closed
   * @throws AsynchronousCloseException  If another thread closes this channel
   *                                     while the write operation is in progress
   * @throws ClosedByInterruptException  If another thread interrupts the current thread
   *                                     while the write operation is in progress, thereby
   *                                     closing the channel and setting the current thread's
   *                                     interrupt status
   * @throws IOException                 If some other I/O error occurs
   */
  @Override
  public int write(ByteBuffer src, long position) throws IOException
  {
    throw new UnsupportedOperationException(
      "Writing to a read-only file channel is not supported.");
  }

  /**
   * Maps a region of this channel's file directly into memory.
   *
   * <p> A region of a file may be mapped into memory in one of three modes:
   * </p>
   *
   * <ul>
   *
   * <li><p> <i>Read-only:</i> Any attempt to modify the resulting buffer
   * will cause a {@link ReadOnlyBufferException} to be thrown.
   * ({@link MapMode#READ_ONLY MapMode.READ_ONLY}) </p></li>
   *
   * <li><p> <i>Read/write:</i> Changes made to the resulting buffer will
   * eventually be propagated to the file; they may or may not be made
   * visible to other programs that have mapped the same file.  ({@link
   * MapMode#READ_WRITE MapMode.READ_WRITE}) </p></li>
   *
   * <li><p> <i>Private:</i> Changes made to the resulting buffer will not
   * be propagated to the file and will not be visible to other programs
   * that have mapped the same file; instead, they will cause private
   * copies of the modified portions of the buffer to be created.  ({@link
   * MapMode#PRIVATE MapMode.PRIVATE}) </p></li>
   *
   * </ul>
   *
   * <p> For a read-only mapping, this channel must have been opened for
   * reading; for a read/write or private mapping, this channel must have
   * been opened for both reading and writing.
   *
   * <p> The {@link MappedByteBuffer <i>mapped byte buffer</i>}
   * returned by this method will have a position of zero and a limit and
   * capacity of <tt>size</tt>; its mark will be undefined.  The buffer and
   * the mapping that it represents will remain valid until the buffer itself
   * is garbage-collected.
   *
   * <p> A mapping, once established, is not dependent upon the file channel
   * that was used to create it.  Closing the channel, in particular, has no
   * effect upon the validity of the mapping.
   *
   * <p> Many of the details of memory-mapped files are inherently dependent
   * upon the underlying operating system and are therefore unspecified.  The
   * behavior of this method when the requested region is not completely
   * contained within this channel's file is unspecified.  Whether changes
   * made to the content or size of the underlying file, by this program or
   * another, are propagated to the buffer is unspecified.  The rate at which
   * changes to the buffer are propagated to the file is unspecified.
   *
   * <p> For most operating systems, mapping a file into memory is more
   * expensive than reading or writing a few tens of kilobytes of data via
   * the usual {@link #read read} and {@link #write write} methods.  From the
   * standpoint of performance it is generally only worth mapping relatively
   * large files into memory.  </p>
   *
   * @param mode     One of the constants {@link MapMode#READ_ONLY READ_ONLY}, {@link
   *                 MapMode#READ_WRITE READ_WRITE}, or {@link MapMode#PRIVATE
   *                 PRIVATE} defined in the {@link MapMode} class, according to
   *                 whether the file is to be mapped read-only, read/write, or
   *                 privately (copy-on-write), respectively
   * @param position The position within the file at which the mapped region
   *                 is to start; must be non-negative
   * @param size     The size of the region to be mapped; must be non-negative and
   *                 no greater than {@link Integer#MAX_VALUE}
   * @return The mapped byte buffer
   * @throws NonReadableChannelException If the <tt>mode</tt> is {@link MapMode#READ_ONLY READ_ONLY} but
   *                                     this channel was not opened for reading
   * @throws NonWritableChannelException If the <tt>mode</tt> is {@link MapMode#READ_WRITE READ_WRITE} or
   *                                     {@link MapMode#PRIVATE PRIVATE} but this channel was not opened
   *                                     for both reading and writing
   * @throws IllegalArgumentException    If the preconditions on the parameters do not hold
   * @throws IOException                 If some other I/O error occurs
   * @see MapMode
   * @see MappedByteBuffer
   */
  @Override
  public MappedByteBuffer map(MapMode mode, long position, long size)
    throws IOException
  {
    return file_.map(mode, position, size);
  }

  /**
   * Acquires a lock on the given region of this channel's file.
   *
   * <p> An invocation of this method will block until the region can be
   * locked, this channel is closed, or the invoking thread is interrupted,
   * whichever comes first.
   *
   * <p> If this channel is closed by another thread during an invocation of
   * this method then an {@link AsynchronousCloseException} will be thrown.
   *
   * <p> If the invoking thread is interrupted while waiting to acquire the
   * lock then its interrupt status will be set and a {@link
   * FileLockInterruptionException} will be thrown.  If the invoker's
   * interrupt status is set when this method is invoked then that exception
   * will be thrown immediately; the thread's interrupt status will not be
   * changed.
   *
   * <p> The region specified by the <tt>position</tt> and <tt>size</tt>
   * parameters need not be contained within, or even overlap, the actual
   * underlying file.  Lock regions are fixed in size; if a locked region
   * initially contains the end of the file and the file grows beyond the
   * region then the new portion of the file will not be covered by the lock.
   * If a file is expected to grow in size and a lock on the entire file is
   * required then a region starting at zero, and no smaller than the
   * expected maximum size of the file, should be locked.  The zero-argument
   * {@link #lock()} method simply locks a region of size {@link
   * Long#MAX_VALUE}.
   *
   * <p> Some operating systems do not support shared locks, in which case a
   * request for a shared lock is automatically converted into a request for
   * an exclusive lock.  Whether the newly-acquired lock is shared or
   * exclusive may be tested by invoking the resulting lock object's {@link
   * FileLock#isShared() isShared} method.
   *
   * <p> File locks are held on behalf of the entire Java virtual machine.
   * They are not suitable for controlling access to a file by multiple
   * threads within the same virtual machine.  </p>
   *
   * @param position The position at which the locked region is to start; must be
   *                 non-negative
   * @param size     The size of the locked region; must be non-negative, and the sum
   *                 <tt>position</tt>&nbsp;+&nbsp;<tt>size</tt> must be non-negative
   * @param shared   <tt>true</tt> to request a shared lock, in which case this
   *                 channel must be open for reading (and possibly writing);
   *                 <tt>false</tt> to request an exclusive lock, in which case this
   *                 channel must be open for writing (and possibly reading)
   * @return A lock object representing the newly-acquired lock
   * @throws IllegalArgumentException      If the preconditions on the parameters do not hold
   * @throws ClosedChannelException        If this channel is closed
   * @throws AsynchronousCloseException    If another thread closes this channel while the invoking
   *                                       thread is blocked in this method
   * @throws FileLockInterruptionException If the invoking thread is interrupted while blocked in this
   *                                       method
   * @throws OverlappingFileLockException  If a lock that overlaps the requested region is already held by
   *                                       this Java virtual machine, or if another thread is already
   *                                       blocked in this method and is attempting to lock an overlapping
   *                                       region
   * @throws NonReadableChannelException   If <tt>shared</tt> is <tt>true</tt> this channel was not
   *                                       opened for reading
   * @throws NonWritableChannelException   If <tt>shared</tt> is <tt>false</tt> but this channel was not
   *                                       opened for writing
   * @throws IOException                   If some other I/O error occurs
   * @see #lock()
   * @see #tryLock()
   * @see #tryLock(long, long, boolean)
   */
  @Override
  public FileLock lock(long position, long size, boolean shared)
    throws IOException
  {
    return file_.lock(position, size, shared);
  }

  /**
   * Attempts to acquire a lock on the given region of this channel's file.
   *
   * <p> This method does not block.  An invocation always returns
   * immediately, either having acquired a lock on the requested region or
   * having failed to do so.  If it fails to acquire a lock because an
   * overlapping lock is held by another program then it returns
   * <tt>null</tt>.  If it fails to acquire a lock for any other reason then
   * an appropriate exception is thrown.
   *
   * <p> The region specified by the <tt>position</tt> and <tt>size</tt>
   * parameters need not be contained within, or even overlap, the actual
   * underlying file.  Lock regions are fixed in size; if a locked region
   * initially contains the end of the file and the file grows beyond the
   * region then the new portion of the file will not be covered by the lock.
   * If a file is expected to grow in size and a lock on the entire file is
   * required then a region starting at zero, and no smaller than the
   * expected maximum size of the file, should be locked.  The zero-argument
   * {@link #tryLock()} method simply locks a region of size {@link
   * Long#MAX_VALUE}.
   *
   * <p> Some operating systems do not support shared locks, in which case a
   * request for a shared lock is automatically converted into a request for
   * an exclusive lock.  Whether the newly-acquired lock is shared or
   * exclusive may be tested by invoking the resulting lock object's {@link
   * FileLock#isShared() isShared} method.
   *
   * <p> File locks are held on behalf of the entire Java virtual machine.
   * They are not suitable for controlling access to a file by multiple
   * threads within the same virtual machine.  </p>
   *
   * @param position The position at which the locked region is to start; must be
   *                 non-negative
   * @param size     The size of the locked region; must be non-negative, and the sum
   *                 <tt>position</tt>&nbsp;+&nbsp;<tt>size</tt> must be non-negative
   * @param shared   <tt>true</tt> to request a shared lock,
   *                 <tt>false</tt> to request an exclusive lock
   * @return A lock object representing the newly-acquired lock,
   * or <tt>null</tt> if the lock could not be acquired
   * because another program holds an overlapping lock
   * @throws IllegalArgumentException     If the preconditions on the parameters do not hold
   * @throws ClosedChannelException       If this channel is closed
   * @throws OverlappingFileLockException If a lock that overlaps the requested region is already held by
   *                                      this Java virtual machine, or if another thread is already
   *                                      blocked in this method and is attempting to lock an overlapping
   *                                      region of the same file
   * @throws IOException                  If some other I/O error occurs
   * @see #lock()
   * @see #lock(long, long, boolean)
   * @see #tryLock()
   */
  @Override
  public FileLock tryLock(long position, long size, boolean shared)
    throws IOException
  {
    return file_.tryLock(position, size, shared);
  }

  /**
   * Closes this channel.
   *
   * <p> This method is invoked by the {@link #close close} method in order
   * to perform the actual work of closing the channel.  This method is only
   * invoked if the channel has not yet been closed, and it is never invoked
   * more than once.
   *
   * <p> An implementation of this method must arrange for any other thread
   * that is blocked in an I/O operation upon this channel to return
   * immediately, either by throwing an exception or by returning normally.
   * </p>
   *
   * @throws IOException If an I/O error occurs while closing the channel
   */
  @Override
  protected void implCloseChannel() throws IOException
  {
    file_.close();
  }
}
