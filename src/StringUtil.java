//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           StringUtil.java
//! @description    Collection of utility functions for working with strings.
//!

import java.util.HashMap;
import java.util.regex.Pattern;

public class StringUtil
{
  private static HashMap<String, Pattern> compiledPatterns = new HashMap<>();

  // Compares two strings, even if either of them are null
  public static int compare(String a, String b)
  {
    if (equals(a, b))
    {
      return 0;
    }

    if (a == null)
    {
      return Integer.MAX_VALUE;
    }
    else if (b == null)
    {
      return Integer.MIN_VALUE;
    }

    return a.compareTo(b);
  }

  // Compares two strings for equality, even if either of them are null
  public static boolean equals(String a, String b)
  {
    if (a == null && b == null)
    {
      return true;
    }
    else if ((a == null) != (b == null))
    {
      // One of the strings is null.
      return false;
    }

    return a.equals(b);
  }

  public static String escape(String s, boolean full)
  {
    if (s == null || s.isEmpty())
    {
      return s;
    }

    StringBuilder sb = new StringBuilder();
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
        if (full && (code < 0x20 || code > 0x7e))
        {
          sb.append(String.format("\\x%02x", code));
        }
        else
        {
          sb.append(c);
        }
      }
    }

    return sb.toString();
  }

  public static String escape(String s)
  {
    return escape(s, false);
  }

  public static String escapeFull(String s)
  {
    return escape(s, true);
  }

  // Returns true if a string matches the given regex pattern
  public static boolean isMatch(String s, String pattern, boolean compiled)
  {
    if (s == null)
    {
      return false;
    }

    if (compiled)
    {
      Pattern compiledPattern = null;
      if (!compiledPatterns.containsKey(pattern))
      {
        compiledPattern = Pattern.compile(pattern);
        compiledPatterns.put(pattern, compiledPattern);
      }
      else
      {
        compiledPattern = compiledPatterns.get(pattern);
      }

      return compiledPattern.matcher(s).matches();
    }

    return (Pattern.matches(pattern, s));
  }

  public static boolean isMatch(String s, String pattern)
  {
    return isMatch(s, pattern, false);
  }

  // Returns true of a string is null or empty
  public static boolean isNullOrEmpty(String s)
  {
    return (s == null || s.isEmpty());
  }

  // Returns true if a string is null, empty, or only whitespace
  public static boolean isNullOrWhiteSpace(String s)
  {
    return (s == null || isWhiteSpace(s));
  }

  // Returns true if a string is empty or consists of nothing but whitespace
  public static boolean isWhiteSpace(String s)
  {
    if (s == null)
    {
      return false;
    }

    if (s.isEmpty())
    {
      return true;
    }

    for (int i = 0; i < s.length(); ++i)
    {
      if (!Character.isWhitespace(s.charAt(i)))
      {
        return false;
      }
    }

    return true;
  }

  // Adds newlines to strings to wrap long text without breaking words
  public static String wordWrap(String s, int columns, int startPosition)
  {
    StringBuilder sb = new StringBuilder(s);
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
}
