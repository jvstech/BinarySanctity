//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           GUIImportsScore.java
//! @description    Scores a PE file based on missing imports it should have
//!                 if considered to be a GUI subsystem application.
//!

import java.io.IOException;
import java.util.Arrays;

public class MissingGUIImportsScore extends Score
{
  public MissingGUIImportsScore(PortableExecutableFileChannel peFile)
    throws IOException, EndOfStreamException
  {
    if (peFile.getOptionalHeader().getSubsystem() ==
      WindowsSubsystemType.GRAPHICAL)
    {
      boolean hasCreateWindowImport = false;
      for (String[] importNames : peFile.getImportedNames().values())
      {
        if (Arrays.stream(importNames)
          .anyMatch(name -> StringUtil.isMatch(name,
            "CreateWindow(?:Ex)?[AW]", true)))
        {
          hasCreateWindowImport = true;
          break;
        }
      }

      if (!hasCreateWindowImport)
      {
        setValue(50);
      }
    }
  }

  @Override
  public String getTitle()
  {
    return "Missing GUI imports";
  }

  @Override
  public String getDescription()
  {
    return "Checks for missing imports for GUI applications";
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return (getValue() > 0);
  }
}
