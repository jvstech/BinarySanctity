//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           PortableExecutableScore.java
//! @description    Represents an average malware characterization score based
//!                 on a complete PE file.
//!

import java.io.IOException;

public class PortableExecutableScore extends AggregateScore
{
  private final double indicatorThreshold_;

  public PortableExecutableScore(PortableExecutableFileChannel peFile,
                                 double indicatorThreshold)
    throws IOException, EndOfStreamException
  {
    indicatorThreshold_ = indicatorThreshold;
    if (!peFile.hasValidImportDirectories())
    {
      // Something that goes out of its way to not have any imports despite
      // otherwise being a valid executable file can only be described as
      // malware.
      add(new GenericScore(3000,"Invalid/missing import directory"));
    }
    else
    {
      add(new MissingGUIImportsScore(peFile));
      add(new SuspiciousImportsScore(peFile));
    }

    // Section scores
    add(new ExecutableSectionCountScore(peFile));
    for (SectionHeader section : peFile.getSections())
    {
      add(new SectionScore(section));
    }

    add(new DuplicateSectionNameScore(peFile));

    // Strings-related scores
    add(new StringImportsScore(peFile));

    // #TODO:
    //    * valid entry point
    //    * DLL-indicated characteristic type without a DllMain export
  }

  public PortableExecutableScore(PortableExecutableFileChannel peFile)
    throws IOException, EndOfStreamException
  {
    this(peFile, 0.5);
  }

  @Override
  public int getValue()
  {
    return getTotalScore();
  }

  @Override
  public String getTitle()
  {
    return "Portable Executable Malware Indication";
  }

  @Override
  public String getDescription()
  {
    return "Malware characterization for a complete PE file";
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return ((getScores()
      .stream()
      .mapToDouble(s -> s.isSoftMalwareIndication() ? 1.0 : 0.0)
      .average()
      .orElse(0.0)) > indicatorThreshold_);
  }
}
