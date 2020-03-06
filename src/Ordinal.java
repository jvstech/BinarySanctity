//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           Ordinal.java
//! @description    Represents an import or export table entry index.
//!

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

  @Override
  public String toString()
  {
    return "#" + getBiasedValue();
  }
}
