//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           SectionNameScore.java
//! @description    Characterizes a section based on its name and known bad or
//!                 unusual section names.
//!

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SectionNameScore extends Score
{
  private static final HashMap<Pattern, String> PACKED_SECTION_PATTERNS;
  private final SectionHeader section_;
  private final String characterization_;

  static
  {
    PACKED_SECTION_PATTERNS = getPackedSectionPatterns();
  }

  public SectionNameScore(SectionHeader section)
    throws IOException, EndOfStreamException
  {
    section_ = section;
    characterization_ = characterize();
  }

  @Override
  public String getTitle()
  {
    return "Section name";
  }

  @Override
  public String getDescription()
  {
    return "Known bad section names";
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return (getValue() > 0);
  }

  @Override
  public String getCharacterization()
  {
    return characterization_;
  }

  private String characterize()
    throws IOException, EndOfStreamException
  {
    int score = 0;
    for (Map.Entry<Pattern, String> entry : PACKED_SECTION_PATTERNS.entrySet())
    {
      if (entry.getKey().matcher(section_.getName()).matches())
      {
        score = 50;
        // This section has a name set by a known packer. Check its entropy.
        EntropyScore entropyScore = new EntropyScore(section_.getPEFile(),
          section_.getRVA().getFilePosition(), section_.getSizeOfRawData());
        if (entropyScore.isSoftMalwareIndication())
        {
          score = 100;
          return entry.getValue() + " with high entropy";
        }
        else
        {
          return entry.getValue() + " packed";
        }
      }
    }

    return null;
  }

  private static HashMap<Pattern, String> getPackedSectionPatterns()
  {
    final HashMap<Pattern, String> packerPatterns = new HashMap<>();
    packerPatterns.put(Pattern.compile("^[_\\.]UPX\\d*$"), "UPX");
    packerPatterns.put(Pattern.compile("^SR$"), "Armadillo");
    return packerPatterns;
  }
}
