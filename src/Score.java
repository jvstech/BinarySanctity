//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           Score.java
//! @description    Provides the base class for malware characterization scores.
//!

import java.util.*;

public abstract class Score
{
  private Score parent_ = null;
  private int value_;
  private TreeMap<Integer, String> characterization_ = new TreeMap<>();
  private LinkedHashSet<String> details_ = new LinkedHashSet<>();

  public int getValue()
  {
    return value_;
  }

  protected Score setValue(int score)
  {
    value_ = score;
    return this;
  }

  protected int getDepth()
  {
    if (parent_ == null)
    {
      return 0;
    }

    return parent_.getDepth() + 1;
  }

  protected Score setParent(Score parentScore)
  {
    parent_ = parentScore;
    return this;
  }

  public String getCharacterization()
  {
    if (characterization_.isEmpty())
    {
      return null;
    }

    Map.Entry<Integer, String> entry = characterization_.entrySet().stream()
      .filter(e -> e.getKey() < value_)
      .reduce((a, b) -> b)
      .orElse(characterization_.firstEntry());
    return entry.getValue();
  }

  protected Score setCharacterization(int upperThreshold,
                                      String characterization)
  {
    characterization_.put(upperThreshold, characterization);
    return this;
  }

  protected Score addDetail(String detail)
  {
    details_.add(detail);
    return this;
  }

  protected Score addDetails(Collection<? extends String> details)
  {
    details_.addAll(details);
    return this;
  }

  protected Score setDetails(Collection<? extends String> details)
  {
    details_.clear();
    return addDetails(details);
  }

  public abstract String getTitle();
  public abstract String getDescription();
  public abstract boolean isSoftMalwareIndication();

  @Override
  public String toString()
  {
    String characterization = getCharacterization();
    String indent = String.join("", Collections.nCopies(getDepth() * 2, " "));
    String brief = null;
    if (characterization == null || characterization.isEmpty())
    {
      brief = String.format("%s%s - %d", indent, getTitle(), getValue());
    }
    else
    {
      brief = String.format("%s%s - %d (%s)", indent, getTitle(), getValue(),
        characterization);
    }

    if (details_.isEmpty())
    {
      return brief;
    }

    brief += "\n  " + indent + String.join("\n" + indent + "  ", details_);
    return brief;
  }
}
