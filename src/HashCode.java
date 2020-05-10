//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           HashCode.java
//! @description    Generates and stores a 32-bit hash code using the FNV1a
//!                 algorithm.
//!

public class HashCode
{
  // FNV1a 32-bit offset basis (FNV-0 value of the string "chon")
  private static final int FNV_OFFSET_BASIS_32 = 0x811c9dc5;
  // FNV1a 32-bit prime (2^24 + 2^8 + 0x93)
  private static final int FNV_PRIME_32 = 0x1000193;

  private int value_ = FNV_OFFSET_BASIS_32;

  public HashCode()
  {
  }

  public HashCode(int v)
  {
    add(v);
  }

  public HashCode add(int v)
  {
    value_ ^= v;
    value_ *= FNV_PRIME_32;
    return this;
  }

  public HashCode add(String s)
  {
    if (StringUtil.isNullOrEmpty(s))
    {
      return this;
    }

    for (char c : s.toCharArray())
    {
      add(c);
    }

    return this;
  }

  public HashCode add(boolean b)
  {
    return add(b ? 1 : 0);
  }

  public HashCode add(String[] strings)
  {
    if (strings == null)
    {
      return this;
    }

    for (String s : strings)
    {
      add(s);
    }

    return this;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof HashCode))
    {
      return false;
    }

    if (obj == this)
    {
      return true;
    }

    return (((HashCode)obj).value_ == value_);
  }

  public int getValue()
  {
    return value_;
  }

  @Override
  public int hashCode()
  {
    return value_;
  }

  @Override
  public String toString()
  {
    return Integer.toString(value_);
  }
}
