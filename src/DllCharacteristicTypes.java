//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           DllCharacteristicTypes.java
//! @description    Collection of constant values representing possible two-byte
//!                 DLL characteristics found in the PE optional header.
//!

import java.util.*;

public class DllCharacteristicTypes
{
  public static final int HIGH_ENTROPY_VIRTUAL_ADDRESS_SPACE = 0x0020;
  public static final int DYNAMIC_BASE = 0x0040;
  public static final int FORCE_INTEGRITY = 0x0080;
  public static final int NX_COMPATIBLE = 0x0100;
  public static final int NO_ISOLATION = 0x0200;
  public static final int NO_SEH = 0x0400;
  public static final int NO_BIND = 0x0800;
  public static final int APP_CONTAINER = 0x1000;
  public static final int WDM_DRIVER = 0x2000;
  public static final int CONTROL_FLOW_GUARD = 0x4000;
  public static final int TERMINAL_SERVER_AWARE = 0x8000;

  public static final int FIELD_MASK = 0xafc0;

  private static final Map<Integer, String> kTypeNames;

  static
  {
    kTypeNames = new HashMap<>();
    kTypeNames.put(HIGH_ENTROPY_VIRTUAL_ADDRESS_SPACE,
      "HIGH_ENTROPY_VIRTUAL_ADDRESS_SPACE");
    kTypeNames.put(DYNAMIC_BASE, "DYNAMIC_BASE");
    kTypeNames.put(FORCE_INTEGRITY, "FORCE_INTEGRITY");
    kTypeNames.put(NX_COMPATIBLE, "NX_COMPATIBLE");
    kTypeNames.put(NO_ISOLATION, "NO_ISOLATION");
    kTypeNames.put(NO_SEH, "NO_SEH");
    kTypeNames.put(NO_BIND, "NO_BIND");
    kTypeNames.put(APP_CONTAINER, "APP_CONTAINER");
    kTypeNames.put(WDM_DRIVER, "WDM_DRIVER");
    kTypeNames.put(CONTROL_FLOW_GUARD, "CONTROL_FLOW_GUARD");
    kTypeNames.put(TERMINAL_SERVER_AWARE, "TERMINAL_SERVER_AWARE");
  }

  // Converts DLL characteristics flags to a list of strings
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
