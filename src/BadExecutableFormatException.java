//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           BadExecutableFormatException.java
//! @description    A custom exception thrown when processing malformed
//!                 executable file data.
//!

public class BadExecutableFormatException extends Exception
{
  public BadExecutableFormatException()
  {
    super();
  }

  public BadExecutableFormatException(String message)
  {
    super(message);
  }
}