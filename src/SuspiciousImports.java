//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           ImportsScore.java
//! @description    Represents a grouping of function symbol imports that could
//!                 be an indicator of malicious capability.
//!

import java.util.Arrays;

public class SuspiciousImports
{
  private String[][] names_;
  private String[][] optionalNames_;
  private int score_;
  private int optionalScore_;
  private String description_;

  public SuspiciousImports(String description, int score, String[][] names,
    int optionalScore, String[][] optionalNames)
  {
    if (names == null || names.length == 0)
    {
      throw new NullPointerException();
    }

    if (Arrays.stream(names).anyMatch(n -> n.length == 0))
    {
      throw new NullPointerException();
    }

    names_ = names;
    if (optionalNames == null)
    {
      optionalNames_ = new String[0][0];
    }
    else
    {
      optionalNames_ = optionalNames;
    }

    score_ = score;
    optionalScore_ = optionalScore;
    description_ = description;
  }

  public SuspiciousImports(String description, int score, String[][] names)
  {
    this(description, score, names, 0, null);
  }

  public SuspiciousImports(String description, int score, String name)
  {
    this(description, score, new String[][] { { name } }, 0, null);
  }

  public String[][] getNames()
  {
    return names_;
  }

  public String[][] getOptionalNames()
  {
    return optionalNames_;
  }

  public int getScore()
  {
    return score_;
  }

  public int getOptionalScore()
  {
    return optionalScore_;
  }

  public String getDescription()
  {
    return description_;
  }
}
