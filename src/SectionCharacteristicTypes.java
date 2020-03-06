//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           SectionCharacteristicTypes.java
//! @description    Pseudo-enumeration of four-byte values that describe the
//!                 load characteristics of a portable executable file section.
//!

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionCharacteristicTypes
{
  public static final int NO_PAD = 0x00000008;
  public static final int CODE = 0x00000020;
  public static final int INITIALIZED_DATA = 0x00000040;
  public static final int UNINITIALIZED_DATA = 0x00000080;
  public static final int LINK_INFO = 0x00000200;
  public static final int LINK_REMOVE = 0x00000800;
  public static final int LINK_COMDAT = 0x00001000;
  public static final int GP_RELATIVE = 0x00008000;
  public static final int ALIGN_1_BYTES = 0x00100000;
  public static final int ALIGN_2_BYTES = 0x00200000;
  public static final int ALIGN_4_BYTES = 0x00300000;
  public static final int ALIGN_8_BYTES = 0x00400000;
  public static final int ALIGN_16_BYTES = 0x00500000;
  public static final int ALIGN_32_BYTES = 0x00600000;
  public static final int ALIGN_64_BYTES = 0x00700000;
  public static final int ALIGN_128_BYTES = 0x00800000;
  public static final int ALIGN_256_BYTES = 0x00900000;
  public static final int ALIGN_512_BYTES = 0x00A00000;
  public static final int ALIGN_1024_BYTES = 0x00B00000;
  public static final int ALIGN_2048_BYTES = 0x00C00000;
  public static final int ALIGN_4096_BYTES = 0x00D00000;
  public static final int ALIGN_8192_BYTES = 0x00E00000;
  public static final int LINK_RELOCATION_OVERFLOW = 0x01000000;
  public static final int DISCARDABLE = 0x02000000;
  public static final int NOT_CACHED = 0x04000000;
  public static final int NOT_PAGED = 0x08000000;
  public static final int SHARED = 0x10000000;
  public static final int EXECUTE = 0x20000000;
  public static final int READ = 0x40000000;
  public static final int WRITE = 0x80000000;

  private static final Map<Integer, String> kTypeNames;

  static
  {
    kTypeNames = new HashMap<>();
    kTypeNames.put(NO_PAD, "NO_PAD");
    kTypeNames.put(CODE, "CODE");
    kTypeNames.put(INITIALIZED_DATA, "INITIALIZED_DATA");
    kTypeNames.put(UNINITIALIZED_DATA, "UNINITIALIZED_DATA");
    kTypeNames.put(LINK_INFO, "LINK_INFO");
    kTypeNames.put(LINK_REMOVE, "LINK_REMOVE");
    kTypeNames.put(LINK_COMDAT, "LINK_COMDAT");
    kTypeNames.put(GP_RELATIVE, "GP_RELATIVE");
    kTypeNames.put(ALIGN_1_BYTES, "ALIGN_1_BYTES");
    kTypeNames.put(ALIGN_2_BYTES, "ALIGN_2_BYTES");
    kTypeNames.put(ALIGN_4_BYTES, "ALIGN_4_BYTES");
    kTypeNames.put(ALIGN_8_BYTES, "ALIGN_8_BYTES");
    kTypeNames.put(ALIGN_16_BYTES, "ALIGN_16_BYTES");
    kTypeNames.put(ALIGN_32_BYTES, "ALIGN_32_BYTES");
    kTypeNames.put(ALIGN_64_BYTES, "ALIGN_64_BYTES");
    kTypeNames.put(ALIGN_128_BYTES, "ALIGN_128_BYTES");
    kTypeNames.put(ALIGN_256_BYTES, "ALIGN_256_BYTES");
    kTypeNames.put(ALIGN_512_BYTES, "ALIGN_512_BYTES");
    kTypeNames.put(ALIGN_1024_BYTES, "ALIGN_1024_BYTES");
    kTypeNames.put(ALIGN_2048_BYTES, "ALIGN_2048_BYTES");
    kTypeNames.put(ALIGN_4096_BYTES, "ALIGN_4096_BYTES");
    kTypeNames.put(ALIGN_8192_BYTES, "ALIGN_8192_BYTES");
    kTypeNames.put(LINK_RELOCATION_OVERFLOW, "LINK_RELOCATION_OVERFLOW");
    kTypeNames.put(DISCARDABLE, "DISCARDABLE");
    kTypeNames.put(NOT_CACHED, "NOT_CACHED");
    kTypeNames.put(NOT_PAGED, "NOT_PAGED");
    kTypeNames.put(SHARED, "SHARED");
    kTypeNames.put(EXECUTE, "EXECUTE");
    kTypeNames.put(READ, "READ");
    kTypeNames.put(WRITE, "WRITE");
  }

  // Converts section characteristic flags to a list of strings
  public static List<String> getStrings(int c)
  {
    ArrayList<String> types = new ArrayList<>();
    for (int k : kTypeNames.keySet())
    {
      if ((c & k) != 0)
      {
        types.add(kTypeNames.get(k));
      }
    }

    return types;
  }
}
