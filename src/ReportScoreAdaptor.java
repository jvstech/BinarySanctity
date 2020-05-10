//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           ReportScoreAdaptor.java
//! @description    Wraps a score to make it a serializable object
//!

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportScoreAdaptor implements Comparable<ReportScoreAdaptor>
{
  private String title_;
  private String description_;
  private boolean softMalwareIndication_;
  private int score_;
  private String characterization_;
  private String[] details_;
  private String toStringValue_;
  private ReportScoreAdaptor[] childScores_;

  // Serializable classes are required to have a default constructor, even if it
  // isn't explicitly used.
  public ReportScoreAdaptor()
  {
  }

  public ReportScoreAdaptor(Score score)
  {
    title_ = score.getTitle();
    description_ = score.getDescription();
    softMalwareIndication_ = score.isSoftMalwareIndication();
    score_ = score.getValue();
    characterization_ = score.getCharacterization();
    details_ = score.getDetails();
    if (score instanceof AggregateScore)
    {
      // AggregateScore objects have slightly different
      toStringValue_ = ((AggregateScore) score).toReportString(false);
      List<ReportScoreAdaptor> childScores = new ArrayList<>();
      childScores_ = ((AggregateScore)score).getScores().stream()
        .map(ReportScoreAdaptor::new)
        .toArray(ReportScoreAdaptor[]::new);
    }
    else
    {
      toStringValue_ = score.toString();
    }
  }

  @Override
  public int compareTo(ReportScoreAdaptor o)
  {
    if (this.equals(o))
    {
      return 0;
    }

    if (!StringUtil.equals(title_, o.title_))
    {
      return StringUtil.compare(title_, o.title_);
    }

    if (!StringUtil.equals(description_, o.description_))
    {
      return StringUtil.compare(description_, o.description_);
    }

    if (softMalwareIndication_ != o.softMalwareIndication_)
    {
      return (Boolean.compare(softMalwareIndication_,
        o.softMalwareIndication_));
    }

    if (score_ != o.score_)
    {
      return Integer.compare(score_, o.score_);
    }

    if (!StringUtil.equals(characterization_, o.characterization_))
    {
      return StringUtil.compare(characterization_, o.characterization_);
    }

    if (Arrays.equals(details_, o.details_))
    {
      if (details_.length != o.details_.length)
      {
        return (Integer.compare(details_.length, o.details_.length));
      }

      for (int i = 0; i < details_.length; ++i)
      {
        if (!StringUtil.equals(details_[i], o.details_[i]))
        {
          return StringUtil.compare(details_[i], o.details_[i]);
        }
      }
    }

    // There is already a check for equality at the top so this line should
    // never be reached, but just in case, we return 0 to indicate equality.
    return 0;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof ReportScoreAdaptor))
    {
      return false;
    }

    if (obj == this)
    {
      return true;
    }

    ReportScoreAdaptor rhs = (ReportScoreAdaptor)obj;
    return (title_.equals(rhs.title_) &&
      description_.equals(rhs.description_) &&
      softMalwareIndication_ == rhs.softMalwareIndication_ &&
      score_ == rhs.score_ &&
      characterization_.equals(rhs.characterization_) &&
      Arrays.equals(details_, rhs.details_));
  }

  public String getCharacterization()
  {
    return characterization_;
  }

  public void setCharacterization(String characterization)
  {
    characterization_ = characterization;
  }

  public ReportScoreAdaptor[] getChildScores()
  {
    return childScores_;
  }

  public void setChildScores(ReportScoreAdaptor[] scores)
  {
    childScores_ = scores;
  }

  public String getDescription()
  {
    return description_;
  }

  public void setDescription(String description)
  {
    description_ = description;
  }

  public String[] getDetails()
  {
    return details_;
  }

  public void setDetails(String[] details)
  {
    details_ = details;
  }

  public boolean getSoftMalwareIndication()
  {
    return softMalwareIndication_;
  }

  public void setSoftMalwareIndication(boolean indication)
  {
    softMalwareIndication_ = indication;
  }

  public String getTitle()
  {
    return title_;
  }

  public void setTitle(String title)
  {
    title_ = title;
  }

  public int getValue()
  {
    return score_;
  }

  public void setValue(int score)
  {
    score_ = score;
  }

  @Override
  public int hashCode()
  {
    HashCode hashCode = new HashCode();
    hashCode
      .add(title_)
      .add(description_)
      .add(softMalwareIndication_)
      .add(score_)
      .add(characterization_)
      .add(details_)
      ;
    return hashCode.getValue();
  }

  @Override
  public String toString()
  {
    if (toStringValue_ == null)
    {
      String brief = null;
      if (characterization_ == null || characterization_.isEmpty())
      {
        brief = String.format("%s - %d", getTitle(), getValue());
      }
      else
      {
        brief = String.format("%s - %d (%s)", getTitle(), getValue(),
          characterization_);
      }

      if (details_ == null || details_.length == 0)
      {
        return brief;
      }

      brief += "\n  " + String.join("\n  ", details_);
      return brief;
    }
    else
    {
      return toStringValue_;
    }
  }

  // Serializes this report score to an XML string
  public String toXml()
  {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    XMLEncoder encoder = new XMLEncoder(os);
    encoder.setExceptionListener(
      e -> System.err.println("Exception while encoding report score: " +
        e.toString()));
    encoder.writeObject(this);
    encoder.close();
    return os.toString();
  }

  // Creates a new ReportScoreAdaptor object from the given XML string
  public static ReportScoreAdaptor fromXml(String xml)
  {
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
    XMLDecoder decoder = new XMLDecoder(is);
    decoder.setExceptionListener(
      e -> System.err.println("Exception while decoding report score: " +
        e.toString()));
    ReportScoreAdaptor result = (ReportScoreAdaptor)decoder.readObject();
    decoder.close();
    return result;
  }
}
