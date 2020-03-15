//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           ShannonEntropy.java
//! @description    Calculates Shannon entropy for binary data in an attempt to
//!                 locate compressed or encrypted data.
//!

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ShannonEntropy
{
  private final double value_;

  private ShannonEntropy(double entropyValue)
  {
    value_ = entropyValue;
  }

  public double getValue()
  {
    return value_;
  }

  @Override
  public String toString()
  {
    return (new BigDecimal(value_).toPlainString());
  }

  // Calculates the byte-wise entropy value of the given data; return value is
  // between 0.0 (no entropy) and 8.0 (maximum entropy).
  public static ShannonEntropy calculate(FileChannel data, long startPosition,
    int dataLength)
    throws IOException
  {
    if (data == null || data.size() == 0)
    {
      return new ShannonEntropy(0.0);
    }

    ByteBuffer buf = ByteBuffer.allocate(4096);
    long oldPos = data.position();
    data.position(startPosition);
    int totalBytesRead = 0;
    int bytesRead = 0;
    // I was originally using a HashMap<Byte, Integer> for storing the counts,
    // but it was 10x slower than just using an array (which makes sense, since
    // contiguous random access data is literally just a pointer index
    // dereference instead of a hash calculation).
    int[] byteCounts = new int[256];
    do
    {
      bytesRead = data.read(buf);
      if (bytesRead <= 0)
      {
        break;
      }

      for (int i = 0; i < bytesRead && totalBytesRead < dataLength; ++i)
      {
        byte b = buf.get(i);
        ++byteCounts[128 + b];
        ++totalBytesRead;
      }

      buf.clear();
    } while (totalBytesRead < dataLength);

    data.position(oldPos);

    double entropy = 0.0;
    for (int i = 0; i < byteCounts.length; ++i)
    {
      int count = byteCounts[i];
      if (count > 0)
      {
        double probability = 1.0 * count / totalBytesRead;
        entropy -= probability * MathUtil.log2(probability);
      }
    }

    return new ShannonEntropy(entropy);
  }

  public static ShannonEntropy calculate(FileChannel fileChannel)
    throws IOException
  {
    return calculate(fileChannel, 0, (int)fileChannel.size());
  }
}
