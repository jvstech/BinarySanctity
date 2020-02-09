//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           DOSHeader.java
//! @description    Represents the first part of a portable executable file -
//!                 the DOS header (sometimes called the MZ header) - which
//!                 contains the DOS stub program and the offset to the PE
//!                 header for further processing.
//!

import java.io.IOException;

public class DOSHeader
{
  private byte[] signature_;
  private int lastPageSize_;
  private int totalPageCount_;
  private int relocationEntryCount_;
  private int headerParagraphSize_;
  private int minAllocatedParagraphCount_;
  private int maxAllocatedParagraphCount_;
  private int initialSSOffset_;
  private int initialSP_;
  private int checksum_;
  private int csipOffset_;
  private int relocationTableOffset_;
  private short overlayNumber_;
  private int peHeaderOffset_;

  public byte[] getSignature()
  {
    return signature_;
  }

  public int getLastPageSize()
  {
    return lastPageSize_;
  }

  public int getTotalPageCount()
  {
    return totalPageCount_;
  }

  public int getRelocationEntryCount()
  {
    return relocationEntryCount_;
  }

  public int getHeaderParagraphSize()
  {
    return headerParagraphSize_;
  }

  public int getMinAllocatedParagraphCount()
  {
    return minAllocatedParagraphCount_;
  }

  public int getMaxAllocatedParagraphCount()
  {
    return maxAllocatedParagraphCount_;
  }

  public int getInitialSSOffset()
  {
    return initialSSOffset_;
  }

  public int getInitialSP()
  {
    return initialSP_;
  }

  public int getChecksum()
  {
    return checksum_;
  }

  public int getCSIPOffset()
  {
    return csipOffset_;
  }

  public int getRelocationTableOffset()
  {
    return relocationTableOffset_;
  }

  public int getPEHeaderOffset()
  {
    return peHeaderOffset_;
  }

  public boolean isValid()
  {
    return (signature_ != null && signature_.length == 2 &&
      signature_[0] == 0x4d /*'M'*/ && signature_[1] == 0x5a /*'Z'*/);
  }

  public static DOSHeader fromStream(ByteIOStream stream)
    throws BadExecutableFormatException
  {
    DOSHeader dosHeader = new DOSHeader();
    try
    {
      dosHeader.signature_ = stream.read(2);
      if (!dosHeader.isValid())
      {
        throw new BadExecutableFormatException("Invalid DOS header.");
      }

      dosHeader.lastPageSize_ = stream.readUInt16();
      dosHeader.totalPageCount_ = stream.readUInt16();
      dosHeader.relocationEntryCount_ = stream.readUInt16();
      dosHeader.headerParagraphSize_ = stream.readUInt16();
      dosHeader.minAllocatedParagraphCount_ = stream.readUInt16();
      dosHeader.maxAllocatedParagraphCount_ = stream.readUInt16();
      dosHeader.initialSSOffset_ = stream.readUInt16();
      dosHeader.initialSP_ = stream.readUInt16();
      dosHeader.checksum_ = stream.readUInt16();
      dosHeader.csipOffset_ = stream.readInt32();
      dosHeader.relocationTableOffset_ = stream.readUInt16();
      dosHeader.overlayNumber_ = stream.readInt16();
      // skip bytes
      stream.read(32);
      dosHeader.peHeaderOffset_ = stream.readInt32();
    }
    catch (BadExecutableFormatException exeEx)
    {
      throw exeEx;
    }
    catch (Exception ex)
    {
      return new DOSHeader();
    }

    return dosHeader;
  }
}