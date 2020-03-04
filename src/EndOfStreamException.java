//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           EndOfStreamException.java
//! @description    A custom exception thrown when trying to read data beyond
//!                 the end of a byte buffer or stream.
//!

public class EndOfStreamException extends Exception
{
  public EndOfStreamException()
  {
    super();
  }

  public EndOfStreamException(String message)
  {
    super(message);
  }
}
