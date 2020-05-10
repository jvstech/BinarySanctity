//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           Score.java
//! @description    Provides the base class for malware characterization scores.
//!

import java.util.*;

public abstract class Score implements Comparable<Score>
{
  private Score parent_ = null;
  private int value_;
  private TreeMap<Integer, String> characterization_ = new TreeMap<>();
  private LinkedHashSet<String> details_ = new LinkedHashSet<>();

  @Override
  public int compareTo(Score o)
  {
    if (getValue() != o.getValue())
    {
      return Integer.compare(getValue(), o.getValue());
    }

    if (!getTitle().equals(o.getTitle()))
    {
      return getTitle().compareTo(o.getTitle());
    }

    if (!getDescription().equals(o.getDescription()))
    {
      return getDescription().compareTo(o.getDescription());
    }

    if (isSoftMalwareIndication() != o.isSoftMalwareIndication())
    {
      return Boolean.compare(isSoftMalwareIndication(),
        o.isSoftMalwareIndication());
    }

    if (!StringUtil.equals(getCharacterization(), o.getCharacterization()))
    {
      return StringUtil.compare(getCharacterization(), o.getCharacterization());
    }

    String[] detailArray = getDetails();
    String[] rhsDetailArray = o.getDetails();

    if (detailArray.length != rhsDetailArray.length)
    {
      return Integer.compare(detailArray.length, rhsDetailArray.length);
    }

    for (int i = 0; i < detailArray.length; ++i)
    {
      if (detailArray[i].equals(rhsDetailArray[i]))
      {
        return detailArray[i].compareTo(rhsDetailArray[i]);
      }
    }

    return 0;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof Score))
    {
      return false;
    }

    if (obj == this)
    {
      return true;
    }

    Score rhs = (Score)obj;
    return (getValue() == rhs.getValue() &&
      getTitle().equals(rhs.getTitle()) &&
      StringUtil.equals(getDescription(), rhs.getDescription()) &&
      isSoftMalwareIndication() == rhs.isSoftMalwareIndication() &&
      Arrays.equals(getDetails(), rhs.getDetails()) &&
      StringUtil.equals(getCharacterization(), rhs.getCharacterization()));
  }

  // Returns a string describing how this score analyzer has characterized the
  // given executable file (or a relevant portion thereof). By default, this is
  // based on the score value and mapped to a series of characterizations, so
  // different scores could indicate different types or levels of
  // characterizations.
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

  public abstract String getDescription();

  public String[] getDetails()
  {
    return details_.toArray(new String[0]);
  }

  protected Score setDetails(Collection<? extends String> details)
  {
    details_.clear();
    return addDetails(details);
  }

  public abstract String getTitle();

  public int getValue()
  {
    return value_;
  }

  protected Score setValue(int score)
  {
    value_ = score;
    return this;
  }

  @Override
  public int hashCode()
  {
    HashCode hashCode = new HashCode();
    hashCode.add(getValue())
      .add(getTitle())
      .add(getDescription())
      .add(isSoftMalwareIndication())
      .add(getDetails())
      .add(getCharacterization());
    return hashCode.getValue();
  }

  // Returns a true/false value indicating whether or not the given executable
  // file could be (but is not definitively) indicated to be malware
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

  // Returns the instance-level of this score (for scores which are sub-scores
  // of other scores)
  protected int getDepth()
  {
    if (parent_ == null)
    {
      return 0;
    }

    return parent_.getDepth() + 1;
  }

  // Sets the analysis characterization description up to the given score value.
  protected Score setCharacterization(int upperThreshold,
                                      String characterization)
  {
    characterization_.put(upperThreshold, characterization);
    return this;
  }

  protected Score setParent(Score parentScore)
  {
    parent_ = parentScore;
    return this;
  }
}
