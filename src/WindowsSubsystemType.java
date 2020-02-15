import java.util.HashMap;

public enum WindowsSubsystemType
{
  UNKNOWN(0),
  NATIVE(1),
  GRAPHICAL(2),
  CONSOLE(3),
  OS2_CONSOLE(5),
  POSIX_CONSOLE(7),
  NATIVE_9X(8),
  CE_GRAPHICAL(9),
  EFI_APPLICATION(10),
  EFI_BOOT_SERVICE_DRIVER(11),
  EFI_RUNTIME_DRIVER(12),
  EFI_ROM(13),
  XBOX(14),
  BOOT_APPLICATION(16);

  public final int value;

  private WindowsSubsystemType(int v)
  {
    value = v;
  }

  private static final HashMap<Integer, WindowsSubsystemType> valueMapping =
    new HashMap<>();

  static
  {
    for (WindowsSubsystemType wst : WindowsSubsystemType.values())
    {
      valueMapping.put(wst.value, wst);
    }
  }

  public static WindowsSubsystemType fromInt(int v)
  {
    WindowsSubsystemType result = valueMapping.get(Integer.valueOf(v));
    if (result == null)
    {
      return WindowsSubsystemType.UNKNOWN;
    }

    return result;
  }
}
