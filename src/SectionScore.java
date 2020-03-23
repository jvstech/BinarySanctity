//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           SectionScore.java
//! @description    Collection of scores generated against a single PE section.
//!

import java.io.IOException;

public class SectionScore extends AggregateScore
{
  private final SectionHeader section_;

  public SectionScore(SectionHeader section)
    throws IOException, EndOfStreamException
  {
    section_ = section;
    add(new SectionNameScore(section_));
    add(new SectionPermissionsScore(section_));
    add(new SectionEntropyScore(section_));
  }

  @Override
  public String getTitle()
  {
    return String.format("Section \"%s\"",
      StringUtil.escapeFull(section_.getName()));
  }

  @Override
  public String getDescription()
  {
    return "Collection of scores generated against a single PE section";
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return (getMalwareIndicatedScores().length > 0);
  }

  @Override
  public int getValue()
  {
    return getTotalScore();
  }
}
