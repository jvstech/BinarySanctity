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

  private static final Map<Integer, String> TYPE_NAMES;
  private static final Map<Integer, String> SIMPLE_TYPE_NAMES;

  static
  {
    TYPE_NAMES = new HashMap<>();
    SIMPLE_TYPE_NAMES = new HashMap<>();

    TYPE_NAMES.put(NO_PAD, "NO_PAD");
    TYPE_NAMES.put(CODE, "CODE");
    TYPE_NAMES.put(INITIALIZED_DATA, "INITIALIZED_DATA");
    TYPE_NAMES.put(UNINITIALIZED_DATA, "UNINITIALIZED_DATA");
    TYPE_NAMES.put(LINK_INFO, "LINK_INFO");
    TYPE_NAMES.put(LINK_REMOVE, "LINK_REMOVE");
    TYPE_NAMES.put(LINK_COMDAT, "LINK_COMDAT");
    TYPE_NAMES.put(GP_RELATIVE, "GP_RELATIVE");
    TYPE_NAMES.put(ALIGN_1_BYTES, "ALIGN_1_BYTES");
    TYPE_NAMES.put(ALIGN_2_BYTES, "ALIGN_2_BYTES");
    TYPE_NAMES.put(ALIGN_4_BYTES, "ALIGN_4_BYTES");
    TYPE_NAMES.put(ALIGN_8_BYTES, "ALIGN_8_BYTES");
    TYPE_NAMES.put(ALIGN_16_BYTES, "ALIGN_16_BYTES");
    TYPE_NAMES.put(ALIGN_32_BYTES, "ALIGN_32_BYTES");
    TYPE_NAMES.put(ALIGN_64_BYTES, "ALIGN_64_BYTES");
    TYPE_NAMES.put(ALIGN_128_BYTES, "ALIGN_128_BYTES");
    TYPE_NAMES.put(ALIGN_256_BYTES, "ALIGN_256_BYTES");
    TYPE_NAMES.put(ALIGN_512_BYTES, "ALIGN_512_BYTES");
    TYPE_NAMES.put(ALIGN_1024_BYTES, "ALIGN_1024_BYTES");
    TYPE_NAMES.put(ALIGN_2048_BYTES, "ALIGN_2048_BYTES");
    TYPE_NAMES.put(ALIGN_4096_BYTES, "ALIGN_4096_BYTES");
    TYPE_NAMES.put(ALIGN_8192_BYTES, "ALIGN_8192_BYTES");
    TYPE_NAMES.put(LINK_RELOCATION_OVERFLOW, "LINK_RELOCATION_OVERFLOW");
    TYPE_NAMES.put(DISCARDABLE, "DISCARDABLE");
    TYPE_NAMES.put(NOT_CACHED, "NOT_CACHED");
    TYPE_NAMES.put(NOT_PAGED, "NOT_PAGED");
    TYPE_NAMES.put(SHARED, "SHARED");
    TYPE_NAMES.put(EXECUTE, "EXECUTE");
    TYPE_NAMES.put(READ, "READ");
    TYPE_NAMES.put(WRITE, "WRITE");

    SIMPLE_TYPE_NAMES.put(NO_PAD, "np");
    SIMPLE_TYPE_NAMES.put(CODE, "c");
    SIMPLE_TYPE_NAMES.put(INITIALIZED_DATA, "i");
    SIMPLE_TYPE_NAMES.put(UNINITIALIZED_DATA, "u");
    SIMPLE_TYPE_NAMES.put(LINK_INFO, "li");
    SIMPLE_TYPE_NAMES.put(LINK_REMOVE, "lr");
    SIMPLE_TYPE_NAMES.put(LINK_COMDAT, "lc");
    SIMPLE_TYPE_NAMES.put(GP_RELATIVE, "gprel");
    SIMPLE_TYPE_NAMES.put(ALIGN_1_BYTES, "<<0");
    SIMPLE_TYPE_NAMES.put(ALIGN_2_BYTES, "<<1");
    SIMPLE_TYPE_NAMES.put(ALIGN_4_BYTES, "<<2");
    SIMPLE_TYPE_NAMES.put(ALIGN_8_BYTES, "<<3");
    SIMPLE_TYPE_NAMES.put(ALIGN_16_BYTES, "<<4");
    SIMPLE_TYPE_NAMES.put(ALIGN_32_BYTES, "<<5");
    SIMPLE_TYPE_NAMES.put(ALIGN_64_BYTES, "<<6");
    SIMPLE_TYPE_NAMES.put(ALIGN_128_BYTES, "<<7");
    SIMPLE_TYPE_NAMES.put(ALIGN_256_BYTES, "<<8");
    SIMPLE_TYPE_NAMES.put(ALIGN_512_BYTES, "<<9");
    SIMPLE_TYPE_NAMES.put(ALIGN_1024_BYTES, "<<10");
    SIMPLE_TYPE_NAMES.put(ALIGN_2048_BYTES, "<<11");
    SIMPLE_TYPE_NAMES.put(ALIGN_4096_BYTES, "<<12");
    SIMPLE_TYPE_NAMES.put(ALIGN_8192_BYTES, "<<13");
    SIMPLE_TYPE_NAMES.put(LINK_RELOCATION_OVERFLOW, "o");
    SIMPLE_TYPE_NAMES.put(DISCARDABLE, "d");
    SIMPLE_TYPE_NAMES.put(NOT_CACHED, "!c");
    SIMPLE_TYPE_NAMES.put(NOT_PAGED, "!p");
    SIMPLE_TYPE_NAMES.put(SHARED, "s");
    SIMPLE_TYPE_NAMES.put(EXECUTE, "x");
    SIMPLE_TYPE_NAMES.put(READ, "r");
    SIMPLE_TYPE_NAMES.put(WRITE, "w");
  }

  // Converts section characteristic flags to a list of strings
  public static List<String> getStrings(int c)
  {
    ArrayList<String> types = new ArrayList<>();
    for (int k : TYPE_NAMES.keySet())
    {
      if ((c & k) != 0)
      {
        types.add(TYPE_NAMES.get(k));
      }
    }

    return types;
  }

  // Converts section characteristic flags to a list of simple strings
  public static List<String> getSimpleStrings(int c)
  {
    ArrayList<String> types = new ArrayList<>();
    for (int k : SIMPLE_TYPE_NAMES.keySet())
    {
      if ((c & k) != 0)
      {
        types.add(SIMPLE_TYPE_NAMES.get(k));
      }
    }

    return types;
  }

  // Returns section characteristics without any of the ALIGN flags
  public static int getWithoutAlignments(int c)
  {
    // The ALIGN_*_BYTES characteristics aren't all single-bit flags. Only a few
    // have to be removed to end up removing all of them.
    return (c & ~(ALIGN_1_BYTES | ALIGN_2_BYTES | ALIGN_8_BYTES |
      ALIGN_128_BYTES));
  }
}
