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
    add(new SuspiciousImportsScore(peFile));
    for (SectionHeader section : peFile.getSections())
    {
      add(new SectionScore(section));
    }
  }

  public PortableExecutableScore(PortableExecutableFileChannel peFile)
    throws IOException, EndOfStreamException
  {
    this(peFile, 0.5);
  }

  @Override
  public int getValue()
  {
    return getAverageScore();
  }

  @Override
  public String getTitle()
  {
    return "Portable Executable Malware Indication";
  }

  @Override
  public String getDescription()
  {
    return "Average malware characterization for a complete PE file";
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
