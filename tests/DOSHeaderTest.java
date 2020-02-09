import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class DOSHeaderTest
{
  @Test
  void fromStream()
    throws IOException, BadExecutableFormatException, URISyntaxException
  {
    URI testDataUri =
      DOSHeaderTest.class.getResource("hello-world.b64").toURI();
    Path testDataPath = Paths.get(testDataUri);

    String encodedData =
      new String(Files.readAllBytes(testDataPath), StandardCharsets.UTF_8);
    byte[] decodedData = Base64.getDecoder().decode(encodedData);
    DOSHeader dosHeader = DOSHeader.fromStream(new ByteIOStream(decodedData));
    assertTrue(dosHeader.isValid());
    assertEquals(144, dosHeader.getLastPageSize());
    assertEquals(3, dosHeader.getTotalPageCount());
    assertEquals(65535, dosHeader.getMaxAllocatedParagraphCount());
    assertEquals(184, dosHeader.getInitialSP());
    assertEquals(64, dosHeader.getRelocationTableOffset());
    assertEquals(256, dosHeader.getPEHeaderOffset());
  }
}