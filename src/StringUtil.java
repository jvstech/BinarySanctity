//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           StringUtil.java
//! @description    Collection of utility functions for working with strings.
//!

public class StringUtil
{
  // Adds newlines to strings to wrap long text without breaking words
  public static String wordWrap(String s, int columns, int startPosition)
  {
    StringBuffer sb = new StringBuffer(s);
    int i = startPosition;
    while (i + columns < sb.length() &&
      (i = sb.lastIndexOf(" ", i + columns)) != -1)
    {
      sb.replace(i, i + 1, "\n");
    }

    return sb.toString();
  }

  // Adds newlines to strings to wrap long text without breaking words
  public static String wordWrap(String s, int columns)
  {
    return wordWrap(s, columns, 0);
  }

  // Adds newlines to strings to wrap long text without breaking words
  public static String wordWrap(String s)
  {
    return wordWrap(s, 80);
  }

  public static String escape(String s)
  {
    if (s == null || s.isEmpty())
    {
      return s;
    }

    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < s.length(); ++i)
    {
      char c = s.charAt(i);
      int code = (int)c;

      if (c == '\\')
      {
        sb.append("\\\\");
      }
      else if (code == 0xa)
      {
        sb.append("\\n");
      }
      else if (code == 0xd)
      {
        sb.append("\\r");
      }
      else if (code == 0x9)
      {
        sb.append("\\t");
      }
      else
      {
        sb.append(c);
      }
    }

    return sb.toString();
  }
}
