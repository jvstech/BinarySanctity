//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           AggregateScore.java
//! @description    Represents multiple scores aggregated together.
//!

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AggregateScore extends Score
{
  private List<Score> scores_ = new ArrayList<>();

  @Override
  public int hashCode()
  {
    HashCode hashCode = new HashCode(super.hashCode());
    for (Score score : getScores())
    {
      hashCode.add(score.hashCode());
    }

    return hashCode.getValue();
  }

  public String toPlainString()
  {
    return super.toString();
  }

  public String toReportString(boolean showAll)
  {
    StringWriter sw = new StringWriter();
    PrintWriter w = new PrintWriter(sw);
    sw.append(super.toString());
    for (Score score : scores_)
    {
      if (showAll || score.isSoftMalwareIndication())
      {
        w.print("\n" + score.toString());
      }
    }

    return sw.toString();
  }

  @Override
  public String toString()
  {
    return toReportString(false);
  }

  protected AggregateScore add(Score score)
  {
    score.setParent(this);
    scores_.add(score);
    return this;
  }

  protected int getAverageScore()
  {
    return (int)scores_.stream()
      .mapToDouble(Score::getValue)
      .average()
      .orElse(0.0);
  }

  protected int getExperpAverageScore(int normalLow, int normalHigh)
  {
    return (int)MathUtil.experp(getAverageScore(),
      getMinimumScore(), getMaximumScore(),
      normalLow, normalHigh);
  }

  // Returns a flattened array of every score used to generate the overall PE
  // score
  protected Score[] getFlattenedScores()
  {
    List<Score> allScores = new ArrayList<>();
    getFlattenedScores(allScores, this);
    return allScores.toArray(new Score[0]);
  }

  protected int getLerpAverageScore(int normalLow, int normalHigh)
  {
    return (int)MathUtil.lerp(getAverageScore(),
      getMinimumScore(), getMaximumScore(),
      normalLow, normalHigh);
  }

  // Returns all child scores that have made soft-positive malware indications
  protected Score[] getMalwareIndicatedScores()
  {
    return scores_.stream()
      .filter(Score::isSoftMalwareIndication)
      .toArray(Score[]::new);
  }

  protected int getMaximumScore()
  {
    return scores_.stream()
      .mapToInt(Score::getValue)
      .max()
      .orElse(0);
  }

  protected int getMinimumScore()
  {
    return scores_.stream()
      .mapToInt(Score::getValue)
      .min()
      .orElse(0);
  }

  // Returns all child scores that have a value > 0
  protected Score[] getNonZeroScores()
  {
    return scores_.stream()
      .filter(s -> (s.getValue() > 0))
      .toArray(Score[]::new);
  }

  protected List<Score> getScores()
  {
    return Collections.unmodifiableList(scores_);
  }

  protected int getTotalScore()
  {
    return scores_.stream()
      .mapToInt(Score::getValue)
      .sum();
  }

  // Walks the full list of contained scores using callbacks to indicate when
  // an AggregateScore is being traversed or a non-container score
  protected void visitScores(Consumer<AggregateScore> aggregateVisitor,
    BiConsumer<AggregateScore, Score> childVisitor)
  {
    aggregateVisitor.accept(this);
    for (Score childScore : getScores())
    {
      if (childScore instanceof AggregateScore)
      {
        ((AggregateScore)childScore).visitScores(aggregateVisitor,
          childVisitor);
      }
      else
      {
        childVisitor.accept(this, childScore);
      }
    }
  }

  // Recursive helper function for creating a flattened list of scores from an
  // AggregateScore object
  private static void getFlattenedScores(List<Score> scores,
    Score score)
  {
    scores.add(score);
    if (score instanceof AggregateScore)
    {
      AggregateScore aggregateScore = (AggregateScore)score;
      for (Score childScore : aggregateScore.getScores())
      {
        getFlattenedScores(scores, childScore);
      }
    }
  }
}
