public class Ordinal
{
  private int unbiasedValue_;
  private int biasedValue_;

  public Ordinal(int value, int ordinalBase)
  {
    unbiasedValue_ = value;
    biasedValue_ = value + ordinalBase;
  }

  public int getBiasedValue()
  {
    return biasedValue_;
  }

  public int getUnbiasedValue()
  {
    return unbiasedValue_;
  }
}
