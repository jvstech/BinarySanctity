//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           MathUtil.java
//! @description    Collection of math-related utility functions.
//!

public class MathUtil
{
  // linear interpolation
  public static double lerp(double value, double low1, double high1,
                            double low2, double high2)
  {
    double normRange1 = high1 - low1;
    double normRange2 = high2 - low2;
    double normValue = value - low1;
    return (((normValue / normRange1) * normRange2) + low2);
  }

  // exponential interpolation
  public static double experp(double value, double low1, double high1,
                              double low2, double high2)
  {
    double normRange1 = high1 - low1;
    double normRange2 = high2 - low2;
    double normValue = value - low1;
    return ((Math.exp((normValue / normRange1) *
      Math.log(1 + normRange2)) - 1) + low2);
  }

  // #TODO: logarithmic interpolation

  public static double log2(double n)
  {
    return Math.log(n) / Math.log(2);
  }
}
