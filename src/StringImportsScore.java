//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           StringImportsScore.java
//! @description    Looks for suspicious imports stored as strings not found in
//!                 the import lookup table.
//!

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class StringImportsScore extends Score
{
  public StringImportsScore(PortableExecutableFileChannel peFile)
    throws IOException, EndOfStreamException
  {
    int score = 0;
    Long[][] stringOffsets = Strings.findOffsets(peFile, 4);
    if (stringOffsets.length > 0)
    {
      Set<String> importedLibs = peFile.getImportedNames().keySet();
      ArrayList<Pattern> libPatterns = new ArrayList<>(
        Arrays.asList(SuspiciousImports.DEFAULT_MODULE_PATTERNS));
      // remove any pattern matches for already imported libraries
      for (int i = 0; i < libPatterns.size(); /* no action */)
      {
        int idx = i;
        if (importedLibs.stream()
          .anyMatch(lib -> libPatterns.get(idx).matcher(lib).matches()))
        {
          libPatterns.remove(i);
          continue;
        }

        ++i;
      }

      Set<String> importedFuncs = new TreeSet<>();
      peFile.getImportedNames().values().stream()
        .flatMap(Arrays::stream)
        .forEach(importedFuncs::add);
      ArrayList<Pattern> funcPatterns = new ArrayList<>(
        Arrays.asList(SuspiciousImports.DEFAULT_FUNCTION_PATTERNS));
      // remove any patterns for already imported functions
      for (int i = 0; i < funcPatterns.size(); /* no action */)
      {
        int idx = i;
        if (importedFuncs.stream()
          .anyMatch(f -> funcPatterns.get(idx).matcher(f).matches()))
        {
          funcPatterns.remove(i);
          continue;
        }

        ++i;
      }

      for (Long[] offsetLengthPair : stringOffsets)
      {
        // offsetLengthPair is always 2 elements (offset and length)
        String s = peFile.readString(offsetLengthPair[0],
          Math.toIntExact(offsetLengthPair[1]));
        if (libPatterns.stream().anyMatch(p -> p.matcher(s).matches()))
        {
          score += 15;
          addDetail("Library: " + s);
        }

        if (funcPatterns.stream().anyMatch(p -> p.matcher(s).matches()))
        {
          score += 20;
          addDetail("Function: " + s);
        }
      }
    }

    setValue(score);
  }

  @Override
  public String getTitle()
  {
    return "String imports";
  }

  @Override
  public String getDescription()
  {
    return "Fuzzy check for suspicious import strings not listed in the " +
      "import lookup table";
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return (getValue() > 0);
  }
}
