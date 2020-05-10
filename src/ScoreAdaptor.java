//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           ScoreAdaptor.java
//! @description    Wraps any Score object as a read-only instance.
//!

public class ScoreAdaptor extends Score
{
  private final Score score_;

  public ScoreAdaptor(Score score)
  {
    score_ = score;
  }

  @Override
  public int compareTo(Score o)
  {
    return score_.compareTo(o);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof ScoreAdaptor)
    {
      return getAdaptedScore().equals(((ScoreAdaptor)obj).getAdaptedScore());
    }
    else if (obj instanceof Score)
    {
      return getAdaptedScore().equals(obj);
    }

    return false;
  }

  public final Score getAdaptedScore()
  {
    return score_;
  }

  @Override
  public String getCharacterization()
  {
    return score_.getCharacterization();
  }

  @Override
  public String getDescription()
  {
    return score_.getDescription();
  }

  @Override
  public String[] getDetails()
  {
    return score_.getDetails();
  }

  @Override
  public String getTitle()
  {
    return score_.getTitle();
  }

  @Override
  public int getValue()
  {
    return score_.getValue();
  }

  @Override
  public int hashCode()
  {
    return score_.hashCode();
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return score_.isSoftMalwareIndication();
  }

  @Override
  public String toString()
  {
    return score_.toString();
  }

  @Override
  protected int getDepth()
  {
    return score_.getDepth();
  }
}
