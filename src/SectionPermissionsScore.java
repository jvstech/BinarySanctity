//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           SectionPermissionsScore.java
//! @description    Represents a malware characterization score of a PE section
//!                 based on its section characteristics/permissions.
//!

import java.io.IOException;
import java.util.TreeMap;

public class SectionPermissionsScore extends Score
{
  private static final TreeMap<String, Integer> EXPECTED_CHARACTERISTICS;
  private final SectionHeader section_;
  private final int characteristics_;
  private final String characterization_;

  static
  {
    EXPECTED_CHARACTERISTICS = getExpectedCharacteristics();
  }

  public SectionPermissionsScore(SectionHeader section)
    throws IOException, EndOfStreamException
  {
    section_ = section;
    characteristics_ = section_.getCharacteristics();
    characterization_ = characterize();
  }

  @Override
  public String getTitle()
  {
    return "Section permissions";
  }

  @Override
  public String getDescription()
  {
    return "Suspicious or abnormal section characteristics/permissions";
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    // #TODO: make sure this is the most logical way of doing this
    return (getValue() > 0);
  }

  private String characterize()
  {
    final int WX = SectionCharacteristicTypes.WRITE |
      SectionCharacteristicTypes.EXECUTE;
    boolean hasBadPermissions = ((characteristics_ & WX) == WX);
    if (hasBadPermissions)
    {
      setValue(50);
      return "write + execute permissions";
    }

    if (EXPECTED_CHARACTERISTICS.containsKey(section_.getName()))
    {
      int expected = EXPECTED_CHARACTERISTICS.get(section_.getName());
      if (expected != characteristics_)
      {
        if ((characteristics_ & expected) == 0)
        {
          // characteristics are completely different than expected
          setValue(25);
          return "exclusively differing characteristics for reserved section";
        }
        else
        {
          // at least some of the characteristics exist in the section
          setValue(10);
          return "unexpected characteristics for reserved section";
        }

      }
    }

    return null;
  }

  private static TreeMap<String, Integer> getExpectedCharacteristics()
  {
    final int R = SectionCharacteristicTypes.READ;
    final int W = SectionCharacteristicTypes.WRITE;
    final int X = SectionCharacteristicTypes.EXECUTE;
    final int RW = R | W;
    final int RX = R | X;
    final int I = SectionCharacteristicTypes.INITIALIZED_DATA;
    final int U = SectionCharacteristicTypes.UNINITIALIZED_DATA;
    final int L = SectionCharacteristicTypes.LINK_INFO;
    final int D = SectionCharacteristicTypes.DISCARDABLE;

    TreeMap<String, Integer> map = new TreeMap<>();
    // Uninitialized data (free format)
    map.put(".bss", U | RW);
    // CLR metadata that indicates that the object file contains managed code
    map.put(".cormeta", L);
    // Initialized data (free format)
    map.put(".data", I | RW);
    // Generated FPO debug information (object only, x86 architecture only, and
    // now obsolete)
    map.put(".debug$F", I | RW);
    // Precompiled debug types (object only)
    map.put(".debug$P", I | R | D);
    // Debug symbols (object only)
    map.put(".debug$S", I | R | D);
    // Debug types (object only)
    map.put(".debug$T", I | R | D);
    // Linker options
    map.put(".drective", L);
    // Export tables
    map.put(".edata", I | R);
    // Import tables
    map.put(".idata", I | RW);
    // Includes registered SEH (image only) to support IDL attributes
    map.put(".idlsym", L);
    // Exception information
    map.put(".pdata", I | R);
    // Read-only initialized data
    map.put(".rdata", I | R);
    // Image relocations
    map.put(".reloc", I | R | D);
    // Resource directory
    map.put(".rsrc", I | R);
    // GP-relative uninitialized data (free format)
    map.put(".sbss", U | RW);
    // GP-relative initialized data (free format)
    map.put(".sdata", I | RW);
    // GP-relative read-only data (free format)
    map.put(".srdata", I | R);
    // Registered exception handler data (free format and x86/object only)
    map.put(".sxdata", L);
    // Executable code (free format)
    map.put(".text", SectionCharacteristicTypes.CODE | RX);
    // Thread-local storage (object only)
    map.put(".tls", I | RW);
    // Thread-local storage (object only)
    map.put(".tls$", I | RW);
    // GP-relative initialized data (free format and for ARM, SH4, and Thumb
    // architectures only)
    map.put(".vsdata", I | RW);
    // Exception information (free format)
    map.put(".xdata", I | R);
    return map;
  }
}
