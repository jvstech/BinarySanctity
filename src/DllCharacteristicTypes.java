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
}
