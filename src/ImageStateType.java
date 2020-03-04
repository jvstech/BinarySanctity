//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           ImageStateType.java
//! @description    Enumeration type describing the executable image type. This
//!                 maps directly to the bytes found in the Optional header.
//!

import java.util.HashMap;

public enum ImageStateType
{
  UNKNOWN(0),
  PE32(0x10b),
  PE64(0x20b),
  ROM(0x107);

  public static final int PE32_PLUS = 0x20b;

  public final int value;

  private ImageStateType(int v)
  {
    value = v;
  }

  private static final HashMap<Integer, ImageStateType> valueMapping =
    new HashMap<>();

  static
  {
    for (ImageStateType ist : ImageStateType.values())
    {
      valueMapping.put(ist.value, ist);
    }
  }

  public static ImageStateType fromInt(int v)
  {
    ImageStateType result = valueMapping.get(Integer.valueOf(v));
    if (result == null)
    {
      return ImageStateType.UNKNOWN;
    }

    return result;
  }
}
