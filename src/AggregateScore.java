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

public abstract class AggregateScore extends Score
{
  private List<Score> scores_ = new ArrayList<>();

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

  protected int getMinimumScore()
  {
    return scores_.stream()
      .mapToInt(Score::getValue)
      .min()
      .orElse(0);
  }

  protected int getMaximumScore()
  {
    return scores_.stream()
      .mapToInt(Score::getValue)
      .max()
      .orElse(0);
  }

  protected int getTotalScore()
  {
    return scores_.stream()
      .mapToInt(Score::getValue)
      .sum();
  }

  protected int getLerpAverageScore(int normalLow, int normalHigh)
  {
    return (int)MathUtil.lerp(getAverageScore(),
      getMinimumScore(), getMaximumScore(),
      normalLow, normalHigh);
  }

  protected int getExperpAverageScore(int normalLow, int normalHigh)
  {
    return (int)MathUtil.experp(getAverageScore(),
      getMinimumScore(), getMaximumScore(),
      normalLow, normalHigh);
  }

  protected List<Score> getScores()
  {
    return Collections.unmodifiableList(scores_);
  }

  // Returns all child scores that have a value > 0
  protected Score[] getNonZeroScores()
  {
    return scores_.stream()
      .filter(s -> (s.getValue() > 0))
      .toArray(Score[]::new);
  }

  // Returns all child scores that have made soft-positive malware indications
  protected Score[] getMalwareIndicatedScores()
  {
    return scores_.stream()
      .filter(Score::isSoftMalwareIndication)
      .toArray(Score[]::new);
  }

  @Override
  public String toString()
  {
    return toReportString(false);
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
}
