//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           MathUtil.java
//! @description    Collection of math-related utility functions.
//!

import java.math.BigDecimal;

public class MathUtil
{
  public static final double IEEE754_DBL_EPSILON;

  static
  {
    // Epsilon value of IEEE 754 double-precision floating point types
    IEEE754_DBL_EPSILON = Math.pow(2.0, -52.0);
  }

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

  public static double log2(double n)
  {
    return Math.log(n) / Math.log(2);
  }

  // Floating-point comparison functions that ignore rounding errors using
  // the IEEE 754 minimum error value as a comparison

  public static boolean floatEQ(double a, double b)
  {
    return (Math.abs(a - b) <= IEEE754_DBL_EPSILON);
  }

  public static boolean floatLT(double a, double b)
  {
    return (a < b && !floatEQ(a, b));
  }

  public static boolean floatLE(double a, double b)
  {
    return (a < b || floatEQ(a, b));
  }

  public static boolean floatGT(double a, double b)
  {
    return (a > b && !floatEQ(a, b));
  }

  public static boolean floatGE(double a, double b)
  {
    return (a > b || floatEQ(a, b));
  }
}
