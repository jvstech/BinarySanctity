//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           Strings.java
//! @description    Utility class for locating strings/text within binary data.
//!

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.*;

public class Strings
{
  // Reads printable ASCII strings from a file buffer.
  // This function has the potential to use up a LOT of memory as it generates
  // copies of all the strings found in the data. None of the strings are
  // duplicated, but if all the strings are unique, it doesn't matter.
  public static String[] find(FileChannel file, int minSize)
  {
    Set<String> strings = new LinkedHashSet<>();
    ReadOnlyBinaryFileChannel data = new ReadOnlyBinaryFileChannel(file);
    Long[][] offsets = findOffsets(file, minSize);
    for (Long[] offset : offsets)
    {
      try
      {
        strings.add(data.readString(offset[0], Math.toIntExact(offset[1])));
      }
      catch (IOException e)
      {
        // #TODO
        e.printStackTrace();
      }
    }

    return strings.toArray(new String[0]);
  }

  public static String[] find(FileChannel file)
  {
    return find(file, 5);
  }

  // Finds the offsets and lengths of every printable string in a file. Returns
  // an array of two-element long values (0 = offset, 1 = length).
  public static Long[][] findOffsets(FileChannel file, int minSize)
  {
    List<Long[]> offsets = new ArrayList<>();
    ByteBuffer bytes = ByteBuffer.allocate(8192).order(ByteOrder.LITTLE_ENDIAN);
    ReadOnlyBinaryFileChannel data = new ReadOnlyBinaryFileChannel(file);

    try
    {
      long pos = file.position();
      int bytesRead;
      long startIdx = -1;
      int length = 0;

      while ((bytesRead = file.read(bytes, pos)) > 0)
      {
        // Read 8K into the buffer, but only process 4K at a time for overlapped
        // reading.
        for (int idx = 0; idx < (bytesRead < 4096 ? bytesRead : 4096); ++idx)
        {
          int curByte = data.readUInt8(pos + idx);
          if (isPrintableASCII(curByte))
          {
            if (startIdx < 0)
            {
              startIdx = pos + idx;
            }

            ++length;
          }
          else if (startIdx >= 0)
          {
            if (length >= minSize)
            {
              offsets.add(new Long[]{startIdx, (long)length});
            }

            length = 0;
            startIdx = -1;
          }
        }

        pos += 4096;
        bytes.clear();
      }
    }
    catch (IOException e)
    {
      // #TODO
      e.printStackTrace();
    }
    catch (EndOfStreamException e)
    {
      // This is fine; it just means there's no more data to read.
    }

    return offsets.toArray(new Long[0][0]);
  }

  public static Long[][] findOffsets(FileChannel file)
  {
    return findOffsets(file, 5);
  }

  public static boolean isPrintableASCII(int b)
  {
    return
      // Visible, printable characters
      (((b >= 0x20) && (b < 0x7E)) ||
      // Space characters
      (b == 0x9 || b == 0xA || b == 0xD));
  }
}
