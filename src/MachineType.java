//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           MachineType.java
//! @description    Enumeration describing the type of architecture or
//!                 environment on which the executable file runs.
//!

import java.util.HashMap;

public enum MachineType
{
  UNKNOWN(0),
  MATSUSHITA_AM33(0x1d3),
  AMD64(0x8664),
  ARM(0x1c0),
  AARCH64(0xaa64),
  THUMB(0x1c2),
  THUMB2(0x1c4),
  EFI_BYTECODE(0xebc),
  I386(0x14c),
  X86(0x14c),
  IA64(0x200),
  MITSUBISHI_M32R(0x9041),
  MIPS(0x166),
  MIPS16(0x266),
  MIPSFPU(0x366),
  MIPS16FPU(0x466),
  POWERPC(0x1f0),
  POWERPCFP(0x1f1),
  RISCV32(0x5032),
  RISCV64(0x5064),
  RISCV128(0x5128),
  HITACHI_SH3(0x1a2),
  HITACHI_SH3DSP(0x1a3),
  HITACHI_SH4(0x1a6),
  HITACHI_SH5(0x1a8),
  MIPSWCEV2(0x169);

  public final int value;

  private MachineType(int machineType)
  {
    value = machineType;
  }

  private static final HashMap<Integer, MachineType> valueMapping =
    new HashMap<>();

  static
  {
    for (MachineType mt : MachineType.values())
    {
      valueMapping.put(mt.value, mt);
    }
  }

  public static MachineType fromInt(int v)
  {
    MachineType result = valueMapping.get(Integer.valueOf(v));
    if (result == null)
    {
      return MachineType.UNKNOWN;
    }

    return result;
  }
}
