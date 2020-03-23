//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           GenericScore.java
//! @description    Generalized concrete score value for non-specific values.
//!

import java.util.Collection;

public class GenericScore extends Score
{
  private String title_;
  private String description_;
  private int indicatorThreshold_;
  private String characterization_;

  public GenericScore(int score, String title, String description,
    int softMalwareIndicationThreshold)
  {
    super.setValue(score);
    title_ = title;
    description_ = description;
    indicatorThreshold_ = softMalwareIndicationThreshold;
  }

  public GenericScore(int score, String title,
    int softMalwareIndicationThreshold)
  {
    this(score, title, title, softMalwareIndicationThreshold);
  }

  public GenericScore(int score, String title)
  {
    this(score, title, title, 0);
  }

  public GenericScore setCharacterization(String characterization)
  {
    characterization_ = characterization;
    return this;
  }

  public GenericScore addDetail(String detail)
  {
    super.addDetail(detail);
    return this;
  }

  public GenericScore addDetails(Collection<? extends String> details)
  {
    super.addDetails(details);
    return this;
  }

  public GenericScore setValue(int score)
  {
    super.setValue(score);
    return this;
  }

  @Override
  public String getTitle()
  {
    return title_;
  }

  @Override
  public String getDescription()
  {
    return description_;
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return (getValue() > indicatorThreshold_);
  }

  @Override
  public String getCharacterization()
  {
    return characterization_;
  }
}
