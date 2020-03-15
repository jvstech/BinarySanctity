//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           SectionEntropyScore.java
//! @description    Specialized version of EntropyScore for characterizing the
//!                 entropy value of a PE section.
//!

import java.io.IOException;

public class SectionEntropyScore extends EntropyScore
{
  public SectionEntropyScore(SectionHeader section)
    throws IOException, EndOfStreamException
  {
    super("Section entropy", section.getPEFile(),
      section.getRVA().getFilePosition(), section.getSizeOfRawData());
  }
}
