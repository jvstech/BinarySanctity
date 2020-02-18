import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class ImportLookup
{
  private long bits_;
  private boolean importByOrdinal_;
  private short ordinalNumber_;
  private RelativeVirtualAddress hintNameRVA_;
  private HintName hintName_;
  private int index_;

  /* default access */ ImportLookup(int index)
  {
    index_ = index;
  }

  @Override
  public String toString()
  {
    if (importByOrdinal_)
    {
      return String.format("<ordinal:%d>", ordinalNumber_);
    }

    return hintName_.toString();
  }

  public static ImportLookup[] fromStream(ByteIOStream stream,
    OptionalHeader optionalHeader, Iterable<SectionHeader> sections)
    throws BadExecutableFormatException, IOException, EndOfStreamException
  {
    if (stream == null)
    {
      throw new IllegalArgumentException("Stream is null.");
    }

    if (optionalHeader == null)
    {
      throw new IllegalArgumentException("Optional header is null.");
    }

    if (sections == null)
    {
      throw new IllegalArgumentException("Sections list is null.");
    }

    byte[] buffer;
    if (optionalHeader.getImageState() == ImageStateType.PE64)
    {
      buffer = new byte[8];
    }
    else
    {
      buffer = new byte[4];
    }

    ArrayList<ImportLookup> importLookups = new ArrayList<>();
    for (int i = 0; /* no condition */ ; i++)
    {
      int bytesRead = stream.read(buffer, 0, buffer.length);
      if (bytesRead < buffer.length)
      {
        throw new BadExecutableFormatException("Invalid import lookup entry.");
      }

      ImportLookup il = new ImportLookup(i);
      long bits;
      if (optionalHeader.getImageState() == ImageStateType.PE64)
      {
        bits = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getLong();
        if (bits == 0)
        {
          break;
        }

        il.bits_ = bits;
        il.importByOrdinal_ = ((bits >>> 63) != 0);
      }
      else
      {
        bits = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
        if (bits == 0)
        {
          break;
        }

        il.bits_ = bits;
        il.importByOrdinal_ = ((bits >>> 31) != 0);
      }

      if (il.importByOrdinal_)
      {
        il.ordinalNumber_ = (short)(bits & ((1 << 15) - 1));
      }
      else
      {
        il.hintNameRVA_ =
          new RelativeVirtualAddress((int)(bits & ((1 << 30) - 1)), sections);
        int rewindPos = stream.getPosition();
        il.hintName_ = HintName.fromStream(stream);
        stream.setPosition(rewindPos);
      }

      importLookups.add(il);
    }

    return importLookups.toArray(new ImportLookup[0]);
  }

  public static ImportLookup[] fromStream(ByteIOStream stream,
    OptionalHeader optionalHeader, SectionHeader[] sections)
    throws BadExecutableFormatException, IOException, EndOfStreamException
  {
    return fromStream(stream, optionalHeader,
      java.util.Arrays.asList(sections));
  }
}
