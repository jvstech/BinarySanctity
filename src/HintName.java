public class HintName
{
  private int hint_;
  private String name_;

  public int getHint()
  {
    return hint_;
  }

  public String getName()
  {
    return name_;
  }

  @Override
  public String toString()
  {
    return name_;
  }

  public static HintName fromStream(ByteIOStream stream)
    throws BadExecutableFormatException
  {
    if (stream == null)
    {
      throw new IllegalArgumentException("Stream is null.");
    }

    HintName hintName = new HintName();
    try
    {
      hintName.hint_ = stream.readUInt16();
      hintName.name_ = stream.readCString();
      if ((hintName.name_.length() % 2) == 0)
      {
        stream.read(1);
      }
    }
    catch (EndOfStreamException eofEx)
    {
      throw new BadExecutableFormatException("Invalid hint/name data.");
    }

    return hintName;
  }
}
