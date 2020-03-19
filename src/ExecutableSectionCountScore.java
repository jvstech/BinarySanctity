//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           ExecutableSectionCountScore.java
//! @description    Calculates a score based on the number of executable
//!                 sections (greater than 1) present in a PE file.
//!

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExecutableSectionCountScore extends Score
{
  List<SectionHeader> executableSections_ = new ArrayList<>();

  public ExecutableSectionCountScore(PortableExecutableFileChannel peFile)
    throws IOException, EndOfStreamException
  {
    for (SectionHeader section : peFile.getSections())
    {
      if ((section.getCharacteristics()
        & SectionCharacteristicTypes.EXECUTE) != 0)
      {
        executableSections_.add(section);
      }
    }

    if (executableSections_.size() > 1)
    {
      setValue(45 * executableSections_.size());
      executableSections_.stream()
        .map(SectionHeader::getName)
        .forEach(s -> addDetail("Executable section: " + s));
    }
  }

  @Override
  public String getCharacterization()
  {
    if (executableSections_.size() > 1)
    {
      return "multiple executable sections";
    }

    return null;
  }

  @Override
  public String getTitle()
  {
    return "Executable section score";
  }

  @Override
  public String getDescription()
  {
    return "Score amplifier based on the number of executable sections";
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return (executableSections_.size() > 1);
  }
}
