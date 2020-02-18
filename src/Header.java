import java.io.IOException;

public interface Header
{
  int getHeaderSize() throws IOException, EndOfStreamException;
  long getStartOffset() throws IOException, EndOfStreamException;
  long getEndOffset() throws IOException, EndOfStreamException;
}
