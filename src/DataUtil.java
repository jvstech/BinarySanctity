//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           DataUtil.java
//! @description    Utility class for simple operations and checks involving
//!                 bytes/binary data.
//!

import java.io.IOException;
import java.nio.channels.FileChannel;

public class DataUtil
{
  public static boolean isInBounds(long pos, long referenceSize)
  {
    if (referenceSize < 0)
    {
      throw
        new IllegalArgumentException("Reference size must be 0 or positive.");
    }

    if (pos < 0)
    {
      return false;
    }

    return (pos < referenceSize);
  }

  public static boolean isInBounds(long pos, FileChannel fileChannel)
    throws IOException
  {
    if (fileChannel == null)
    {
      return false;
    }

    return isInBounds(pos, fileChannel.size());
  }
}
