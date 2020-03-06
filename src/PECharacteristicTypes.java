//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           PECharacteristicTypes.java
//! @description    Group of two-byte flag values describing the characteristics
//!                 of the portable executable file.
//!

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PECharacteristicTypes
{
  public static final int RELOCS_STRIPPED = 0x0001;
  public static final int EXECUTABLE_IMAGE = 0x0002;
  public static final int LINE_NUMBERS_STRIPPED = 0x0004;
  public static final int LOCAL_SYMBOLS_STRIPPED = 0x0008;
  public static final int AGGRESSIVE_WORKING_SET_TRIM = 0x0010;
  public static final int LARGE_ADDRESS_AWARE = 0x0020;
  public static final int BYTES_REVERSED_LOW = 0x0080;
  public static final int MACHINE_32_BIT = 0x0100;
  public static final int DEBUG_STRIPPED = 0x0200;
  public static final int REMOVABLE_RUN_FROM_SWAP = 0x0400;
  public static final int NETWORK_RUN_FROM_SWAP = 0x0800;
  public static final int SYSTEM = 0x1000;
  public static final int DLL = 0x2000;
  public static final int UNIPROCESSOR_ONLY = 0x4000;
  public static final int BYTES_REVERSED_HIGH = 0x8000;

  private static final Map<Integer, String> kTypeNames;

  static
  {
    kTypeNames = new HashMap<>();
    kTypeNames.put(RELOCS_STRIPPED, "RELOCS_STRIPPED");
    kTypeNames.put(EXECUTABLE_IMAGE, "EXECUTABLE_IMAGE");
    kTypeNames.put(LINE_NUMBERS_STRIPPED, "LINE_NUMBERS_STRIPPED");
    kTypeNames.put(LOCAL_SYMBOLS_STRIPPED, "LOCAL_SYMBOLS_STRIPPED");
    kTypeNames.put(AGGRESSIVE_WORKING_SET_TRIM, "AGGRESSIVE_WORKING_SET_TRIM");
    kTypeNames.put(LARGE_ADDRESS_AWARE, "LARGE_ADDRESS_AWARE");
    kTypeNames.put(BYTES_REVERSED_LOW, "BYTES_REVERSED_LOW");
    kTypeNames.put(MACHINE_32_BIT, "MACHINE_32_BIT");
    kTypeNames.put(DEBUG_STRIPPED, "DEBUG_STRIPPED");
    kTypeNames.put(REMOVABLE_RUN_FROM_SWAP, "REMOVABLE_RUN_FROM_SWAP");
    kTypeNames.put(NETWORK_RUN_FROM_SWAP, "NETWORK_RUN_FROM_SWAP");
    kTypeNames.put(SYSTEM, "SYSTEM");
    kTypeNames.put(DLL, "DLL");
    kTypeNames.put(UNIPROCESSOR_ONLY, "UNIPROCESSOR_ONLY");
    kTypeNames.put(BYTES_REVERSED_HIGH, "BYTES_REVERSED_HIGH");
  }

  // Converts PE characteristic flags to a list of strings
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
