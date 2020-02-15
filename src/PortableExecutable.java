//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           PortableExecutable.java
//! @description    Represents a complete Windows portable executable image.
//!

public class PortableExecutable
{
  private DOSHeader dosHeader_;
  private PEHeader peHeader_;
  private OptionalHeader optionalHeader_;

  public DOSHeader getDOSHeader()
  {
    return dosHeader_;
  }

  public PEHeader getPEHeader()
  {
    return peHeader_;
  }

  public OptionalHeader getOptionalHeader()
  {
    return optionalHeader_;
  }

  public static PortableExecutable fromStream(ByteIOStream stream)
    throws BadExecutableFormatException
  {
    PortableExecutable pe = new PortableExecutable();
    pe.dosHeader_ = DOSHeader.fromStream(stream);
    if (pe.dosHeader_.isValid())
    {
      stream.setPosition(pe.dosHeader_.getPEHeaderOffset());
      pe.peHeader_ = PEHeader.fromStream(stream, pe.dosHeader_);
      if (pe.peHeader_.isValid())
      {
        pe.optionalHeader_ = OptionalHeader.fromStream(stream, pe.peHeader_);
        // #TODO
      }
    }

    return pe;
  }
}
