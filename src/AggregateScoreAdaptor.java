//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           AggregateScoreAdaptor.java
//! @description    Wraps any Score object as an AggregateScore to allow for the
//!                 creation of tree structures of scores.
//!

import java.util.ArrayList;
import java.util.List;

public class AggregateScoreAdaptor extends AggregateScore
{
  private final Score rootScore_;

  public AggregateScoreAdaptor(Score rootScore)
  {
    if (!(rootScore instanceof AggregateScoreAdaptor))
    {
      rootScore_ = adapt(rootScore, this);
    }
    else
    {
      rootScore_ = rootScore;
    }
  }

  @Override
  public String getCharacterization()
  {
    return rootScore_.getCharacterization();
  }

  @Override
  public String getDescription()
  {
    return rootScore_.getDescription();
  }

  @Override
  public String[] getDetails()
  {
    return rootScore_.getDetails();
  }

  @Override
  public String getTitle()
  {
    return rootScore_.getTitle();
  }

  @Override
  public int getValue()
  {
    return rootScore_.getValue();
  }

  @Override
  public int hashCode()
  {
    HashCode hashCode = new HashCode(rootScore_.hashCode());
    for (Score score : getScores())
    {
      hashCode.add(score.hashCode());
    }

    return hashCode.getValue();
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return rootScore_.isSoftMalwareIndication();
  }

  @Override
  protected AggregateScore add(Score score)
  {
    if (!(score instanceof AggregateScoreAdaptor))
    {
      return super.add(new AggregateScoreAdaptor(score));
    }

    return super.add(score);
  }

  private static Score adapt(Score rootScore, AggregateScoreAdaptor adaptor)
  {
    if (rootScore instanceof AggregateScore)
    {
      // We need to recursively replace all the child scores with
      // AggregateScoreAdapter objects
      List<Score> adaptedScores = new ArrayList<>();
      for (Score childScore : ((AggregateScore)rootScore).getScores())
      {
        adaptedScores.add(new AggregateScoreAdaptor(childScore));
      }

      ScoreAdaptor result = new ScoreAdaptor(rootScore);
      adaptedScores.forEach(adaptor::add);
      return result;
    }
    else
    {
      return rootScore;
    }
  }
}
