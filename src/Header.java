//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           Header.java
//! @description    Interface used to provide functions for finding the start
//!                 and end offsets of any header-like data structures within a
//!                 PE file.
//!

import java.io.IOException;

public interface Header
{
  int getHeaderSize() throws IOException, EndOfStreamException;
  long getStartOffset() throws IOException, EndOfStreamException;
  long getEndOffset() throws IOException, EndOfStreamException;
}
