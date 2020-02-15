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
import java.nio.file.Files;
import java.nio.file.Paths;

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

  // Two bytes: 'M' 'Z' (representing the initials of Mark Zbikowski, one of the
  // leading developers of MS-DOS)
  public byte[] getSignature()
  {
    return signature_;
  }

  // Number of bytes in the last page of the file
  public int getLastPageSize()
  {
    return lastPageSize_;
  }

  // Number of pages in the file
  public int getTotalPageCount()
  {
    return totalPageCount_;
  }

  // Relocations
  public int getRelocationEntryCount()
  {
    return relocationEntryCount_;
  }

  // Size of the header in paragraphs
  public int getHeaderParagraphSize()
  {
    return headerParagraphSize_;
  }

  // Minimum extra paragraphs needed
  public int getMinAllocatedParagraphCount()
  {
    return minAllocatedParagraphCount_;
  }

  // Maximum extra paragraphs needed
  public int getMaxAllocatedParagraphCount()
  {
    return maxAllocatedParagraphCount_;
  }

  // Initial (relative) stack segment value
  public int getInitialSSOffset()
  {
    return initialSSOffset_;
  }

  // Initial stack pointer value
  public int getInitialSP()
  {
    return initialSP_;
  }

  // Checksum
  public int getChecksum()
  {
    return checksum_;
  }

  // Initial instruction pointer value and (relative) code segment value
  public int getCSIPOffset()
  {
    return csipOffset_;
  }

  // File address of the relocation table
  public int getRelocationTableOffset()
  {
    return relocationTableOffset_;
  }

  // Overlay number
  public short getOverlayNumber()
  {
    return overlayNumber_;
  }

  // File address of the PE header
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
      // skip reserved bytes
      stream.read(32);
      dosHeader.peHeaderOffset_ = stream.readInt32();
    }
    catch (BadExecutableFormatException exeEx)
    {
      // rethrow
      throw exeEx;
    }
    catch (Exception ex)
    {
      return new DOSHeader();
    }

    return dosHeader;
  }

  public static DOSHeader fromFile(String executableFilePath)
    throws BadExecutableFormatException, IOException
  {
    return fromStream(new ByteIOStream(Files.readAllBytes(Paths.get(
      executableFilePath))));
  }
}