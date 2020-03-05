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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DOSHeader implements Header
{
  private final PortableExecutableFileChannel peFile_;

  enum Offsets
  {
    SIGNATURE                     (0x00),
    LAST_PAGE_SIZE                (0x02),
    TOTAL_PAGE_COUNT              (0x04),
    RELOCATION_ENTRY_COUNT        (0x06),
    HEADER_PARAGRAPH_SIZE         (0x08),
    MIN_ALLOCATED_PARAGRAPH_COUNT (0x0A),
    MAX_ALLOCATED_PARAGRAPH_COUNT (0x0C),
    INITIAL_SS_OFFSET             (0x0E),
    INITIAL_SP                    (0x10),
    CHECKSUM                      (0x12),
    CS_IP_OFFSET                  (0x14),
    RELOCATION_TABLE_OFFSET       (0x18),
    OVERLAY_NUMBER                (0x1A),
    OEM_ID                        (0x24),
    OEM_ID_INFO                   (0x26),
    PE_HEADER_OFFSET              (0x3C)
    ;

    public final int position;

    private Offsets(int offset)
    {
      position = offset;
    }
  }

  public DOSHeader(PortableExecutableFileChannel peFile)
  {
    if (peFile == null)
    {
      throw new NullPointerException();
    }

    peFile_ = peFile;
  }

  public PortableExecutableFileChannel getPEFile()
  {
    return peFile_;
  }

  // Two bytes: 'M' 'Z' (representing the initials of Mark Zbikowski, one of the
  // leading developers of MS-DOS)
  public byte[] getSignature()
    throws IOException, EndOfStreamException
  {
    return peFile_.read(Offsets.SIGNATURE.position, 2);
  }

  // Number of bytes in the last page of the file
  public int getLastPageSize()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.LAST_PAGE_SIZE.position);
  }

  // Number of pages in the file
  public int getTotalPageCount()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.TOTAL_PAGE_COUNT.position);
  }

  // Relocations
  public int getRelocationEntryCount()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.RELOCATION_ENTRY_COUNT.position);
  }

  // Size of the header in paragraphs
  public int getHeaderParagraphSize()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.HEADER_PARAGRAPH_SIZE.position);
  }

  // Minimum extra paragraphs needed
  public int getMinAllocatedParagraphCount()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.MIN_ALLOCATED_PARAGRAPH_COUNT.position);
  }

  // Maximum extra paragraphs needed
  public int getMaxAllocatedParagraphCount()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.MAX_ALLOCATED_PARAGRAPH_COUNT.position);
  }

  // Initial (relative) stack segment value
  public int getInitialSSOffset()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.INITIAL_SS_OFFSET.position);
  }

  // Initial stack pointer value
  public int getInitialSP()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.INITIAL_SP.position);
  }

  // Checksum
  public int getChecksum()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.CHECKSUM.position);
  }

  // Initial instruction pointer value and (relative) code segment value
  public int getCSIPOffset()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(Offsets.CS_IP_OFFSET.position);
  }

  // File address of the relocation table
  public int getRelocationTableOffset()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.RELOCATION_TABLE_OFFSET.position);
  }

  // Overlay number
  public short getOverlayNumber()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt16(Offsets.OVERLAY_NUMBER.position);
  }

  // OEM ID
  public int getOEMID()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.OEM_ID.position);
  }

  // OEM ID information
  public int getOEMIDInfo()
    throws IOException, EndOfStreamException
  {
    return peFile_.readUInt16(Offsets.OEM_ID_INFO.position);
  }

  // File address of the PE header
  public int getPEHeaderOffset()
    throws IOException, EndOfStreamException
  {
    return peFile_.readInt32(Offsets.PE_HEADER_OFFSET.position);
  }

  public boolean isValid()
    throws IOException
  {
    try
    {
      byte[] sig = getSignature();
      return (sig != null && sig.length == 2 &&
        sig[0] == 0x4d /*'M'*/ && sig[1] == 0x5a /*'Z'*/);
    }
    catch (EndOfStreamException eofEx)
    {
      return false;
    }
  }

  @Override
  public int getHeaderSize()
  {
    return 64;
  }

  @Override
  public long getStartOffset()
  {
    return peFile_.getStartPosition();
  }

  @Override
  public long getEndOffset()
  {
    return getStartOffset() + getHeaderSize();
  }

  @Override
  public String toString()
  {
    StringWriter sw = new StringWriter();
    PrintWriter w = new PrintWriter(sw);
    try
    {
      w.printf("Last page size:               %d\n", getLastPageSize());
      w.printf("Number of pages:              %d\n", getTotalPageCount());
      w.printf("Number of relocations:        %d\n", getRelocationEntryCount());
      w.printf("Header paragraph size:        %d\n", getHeaderParagraphSize());
      w.printf("Minimum number of paragraphs: %d\n",
        getMinAllocatedParagraphCount());
      w.printf("Maximum number of paragraphs: %d\n",
        getMaxAllocatedParagraphCount());
      w.printf("Initial SS offset:            %d\n", getInitialSSOffset());
      w.printf("Initial stack pointer:        0x%x\n", getInitialSP());
      w.printf("Checksum:                     0x%x\n", getChecksum());
      w.printf("CS/IP offset:                 %d\n", getCSIPOffset());
      w.printf("Relocation table offset:      %d\n",
        getRelocationTableOffset());
      w.printf("Overlay number:               %d (0x%x)\n",
        getOverlayNumber(), getOverlayNumber());
      w.printf("OEM ID:                       %d\n", getOEMID());
      w.printf("OEM ID info:                  %d\n", getOEMIDInfo());
      w.printf("PE header offset:             %d", getPEHeaderOffset());
    }
    catch (IOException e)
    {
      w.println("I/O exception while reading the DOS header:");
      e.printStackTrace(w);
    }
    catch (EndOfStreamException e)
    {
      w.println(
        "Reached the end of data before finishing reading the DOS header.");
      e.printStackTrace(w);
    }

    return sw.toString();
  }

  void validate()
    throws IOException, BadExecutableFormatException
  {
    if (!isValid())
    {
      throw new BadExecutableFormatException("Invalid or corrupt DOS header");
    }
  }
}