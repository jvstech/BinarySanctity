//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           EntropyScore.java
//! @description    Evaluates the entropy value of given data and returns a
//!                 characterization score.
//!

import java.io.IOException;
import java.nio.channels.FileChannel;

public class EntropyScore extends Score
{
  private static final double MIN_SCORE = Math.nextUp(6.0);
  private final double entropyValue_;
  private String title_ = "Entropy";

  public EntropyScore(String title, FileChannel fileChannel, long startPosition,
    int dataLength)
    throws IOException
  {
    if (title != null && !title.isEmpty())
    {
      title_ = title;
    }

    ShannonEntropy entropy = ShannonEntropy.calculate(fileChannel,
      startPosition, dataLength);
    entropyValue_ = entropy.getValue();
    // Linearly interpolate the entropy value and normalize the score to a
    // max value of 50, tossing out any entropy values under 5.9.
    if (entropyValue_ < MIN_SCORE)
    {
      setValue(0);
    }
    else
    {
      setValue((int)MathUtil.experp(entropyValue_, MIN_SCORE, 8.0, 0.0, 50.0));
    }
  }

  public EntropyScore(String title, FileChannel fileChannel)
    throws IOException
  {
    this(title, fileChannel, 0, (int)fileChannel.size());
  }

  public EntropyScore(FileChannel fileChannel, long startPosition,
    int dataLength)
    throws IOException
  {
    this(null, fileChannel, startPosition, dataLength);
  }

  public EntropyScore(FileChannel fileChannel)
    throws IOException
  {
    this(null, fileChannel);
  }

  public double getEntropyValue()
  {
    return entropyValue_;
  }

  @Override
  public String getTitle()
  {
    return title_;
  }

  @Override
  public String getDescription()
  {
    return
      "Value representing the likeliness data is compressed or encrypted";
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return (entropyValue_ >= MIN_SCORE);
  }

  @Override
  public String getCharacterization()
  {
    // #TODO: This could be made much more robust with Monte Carlo pi
    //  calculation (and another calculation I can't remember the name of)

    if (entropyValue_ >= MIN_SCORE && entropyValue_ < 7.0)
    {
      return "possibly compressed";
    }
    else if (entropyValue_ >= 7.0 && entropyValue_ < 8.0)
    {
      return "possibly encrypted";
    }
    else if (entropyValue_ >= 8.0)
    {
      return "maximum entropy, encrypted";
    }

    return null;
  }
}
