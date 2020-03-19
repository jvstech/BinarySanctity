//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           SectionNameScore.java
//! @description    Characterizes a section based on its name and known bad or
//!                 unusual section names.
//!

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class SectionNameScore extends Score
{
  private static final HashMap<Pattern, String> PACKED_SECTION_PATTERNS;
  private static final TreeSet<String> RESERVED_SECTION_NAMES;

  private final SectionHeader section_;
  private final String characterization_;

  static
  {
    PACKED_SECTION_PATTERNS = getPackedSectionPatterns();
    RESERVED_SECTION_NAMES = getReservedSectionNames();
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
    if (StringUtil.isWhiteSpace(section_.getName()))
    {
      setValue(100);
      return "Empty/whitespace-only section name";
    }

    for (Map.Entry<Pattern, String> entry : PACKED_SECTION_PATTERNS.entrySet())
    {
      if (entry.getKey().matcher(section_.getName()).matches())
      {
        // This section has a name set by a known packer. Check its entropy.
        EntropyScore entropyScore = new EntropyScore(section_.getPEFile(),
          section_.getRVA().getFilePosition(), section_.getSizeOfRawData());
        if (entropyScore.isSoftMalwareIndication())
        {
          setValue(150);
          addDetail(String.format("Entropy: %.2f (%d)",
            entropyScore.getEntropyValue(), entropyScore.getValue()));
          return entry.getValue() + " with high entropy";
        }
        else
        {
          setValue(100);
          return entry.getValue() + " packed";
        }
      }
    }

    if (RESERVED_SECTION_NAMES.stream()
      .noneMatch(s -> s.equals(section_.getName())))
    {
      setValue(10);
      return "Uncommon section name";
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

  private static TreeSet<String> getReservedSectionNames()
  {
    return new TreeSet<>(Arrays.asList(
      ".bss",
      ".cormeta",
      ".data",
      ".debug$F",
      ".debug$P",
      ".debug$S",
      ".debug$T",
      ".drective",
      ".edata",
      ".idata",
      ".idlsym",
      ".pdata",
      ".rdata",
      ".reloc",
      ".rsrc",
      ".sbss",
      ".sdata",
      ".srdata",
      ".sxdata",
      ".text",
      ".tls",
      ".tls$",
      ".vsdata",
      ".xdata"));
  }
}
