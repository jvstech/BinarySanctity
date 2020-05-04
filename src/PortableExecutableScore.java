//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           PortableExecutableScore.java
//! @description    Represents an average malware characterization score based
//!                 on a complete PE file.
//!

import java.io.IOException;
import java.util.function.Consumer;

public class PortableExecutableScore extends AggregateScore
{
  private final double indicatorThreshold_;
  private final PortableExecutableFileChannel peFile_;

  public PortableExecutableScore(PortableExecutableFileChannel peFile,
    double indicatorThreshold, boolean allowSlowAnalysis,
    Consumer<? super String> statusCallback)
    throws IOException, EndOfStreamException
  {
    indicatorThreshold_ = indicatorThreshold;
    if (statusCallback == null)
    {
      // Ensure a callable, but empty callback consumer exists.
      statusCallback = s -> {};
    }

    peFile_ = peFile;
    if (!peFile.hasValidImportDirectories())
    {
      // Something that goes out of its way to not have any imports despite
      // otherwise being a valid executable file can only be described as
      // malware.
      statusCallback.accept("Invalid/missing imports");
      add(new GenericScore(3000,"Invalid/missing import directory"));
    }
    else
    {
      statusCallback.accept(MissingGUIImportsScore.TITLE);
      add(new MissingGUIImportsScore(peFile));
      statusCallback.accept(SuspiciousImportsScore.TITLE);
      add(new SuspiciousImportsScore(peFile));
    }

    // Section scores
    statusCallback.accept(ExecutableSectionCountScore.TITLE);
    add(new ExecutableSectionCountScore(peFile));
    int sectionScoreCount = 0;
    for (SectionHeader section : peFile.getSections())
    {
      statusCallback.accept(
        String.format("Section score %d", ++sectionScoreCount));
      add(new SectionScore(section));
    }

    statusCallback.accept(DuplicateSectionNameScore.TITLE);
    add(new DuplicateSectionNameScore(peFile));

    if (allowSlowAnalysis)
    {
      // Strings-related scores
      statusCallback.accept(StringImportsScore.TITLE);
      Consumer<? super String> finalStatusCallback = statusCallback;
      add(new StringImportsScore(peFile,
        p -> finalStatusCallback.accept(String.format("%s - %d%%",
          StringImportsScore.TITLE, p))));
    }

    // #TODO (if I have time):
    //    * valid entry point
    //    * DLL-indicated characteristic type without a DllMain export
  }

  public PortableExecutableScore(PortableExecutableFileChannel peFile)
    throws IOException, EndOfStreamException
  {
    this(peFile, 0.5, true, null);
  }

  public PortableExecutableScore(PortableExecutableFileChannel peFile,
    double indicatorThreshold)
    throws IOException, EndOfStreamException
  {
    this(peFile, indicatorThreshold, true, null);
  }

  public PortableExecutableScore(PortableExecutableFileChannel peFile,
    Consumer<? super String> statusCallback)
    throws IOException, EndOfStreamException
  {
    this(peFile, 0.5, true, statusCallback);
  }

  public PortableExecutableScore(PortableExecutableFileChannel peFile,
    boolean allowSlowAnalysis)
    throws IOException, EndOfStreamException
  {
    this(peFile, 0.5, allowSlowAnalysis, null);
  }

  public PortableExecutableScore(PortableExecutableFileChannel peFile,
    double indicatorThreshold, boolean allowSlowAnalysis)
    throws IOException, EndOfStreamException
  {
    this(peFile, indicatorThreshold, allowSlowAnalysis, null);
  }

  public PortableExecutableScore(PortableExecutableFileChannel peFile,
    boolean allowSlowAnalysis, Consumer<? super String> statusCallback)
    throws IOException, EndOfStreamException
  {
    this(peFile, 0.5, allowSlowAnalysis, statusCallback);
  }

  public PortableExecutableFileChannel getPEFile()
  {
    return peFile_;
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
