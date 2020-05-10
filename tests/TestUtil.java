import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class TestUtil
{
  public static byte[] loadAndDecodeBase64(String name)
    throws IOException, URISyntaxException
  {
    URI testDataUri =
      PEFileReadTest.class.getResource(name).toURI();
    Path testDataPath = Paths.get(testDataUri);

    String encodedData =
      new String(Files.readAllBytes(testDataPath), StandardCharsets.UTF_8);
    byte[] decodedData = Base64.getDecoder().decode(encodedData);
    return decodedData;
  }

  public static byte[] getHelloWorldExeBytes()
    throws IOException, URISyntaxException
  {
    return loadAndDecodeBase64("hello-world.b64");
  }

  public static byte[] getLessCompressedExeBytes()
    throws IOException, URISyntaxException
  {
    return loadAndDecodeBase64("hello-world.xz.b64");
  }

  public static FileChannel getLessCompressedExeChannel()
    throws IOException, URISyntaxException
  {
    return new ReadOnlyByteArrayChannel(getLessCompressedExeBytes());
  }

  public static byte[] getMoreCompressedExeBytes()
    throws IOException, URISyntaxException
  {
    return loadAndDecodeBase64("hello-world.gzip.b64");
  }

  public static FileChannel getMoreCompressedExeChannel()
    throws IOException, URISyntaxException
  {
    return new ReadOnlyByteArrayChannel(getMoreCompressedExeBytes());
  }

  public static byte[] getHighEntropyBytes()
    throws IOException, URISyntaxException
  {
    return loadAndDecodeBase64("random-data1.b64");
  }

  public static FileChannel getHighEntropyChannel()
    throws IOException, URISyntaxException
  {
    return new ReadOnlyByteArrayChannel(getHighEntropyBytes());
  }

  public static byte[] getHighEntropyNonZeroBytes()
    throws IOException, URISyntaxException
  {
    return loadAndDecodeBase64("random-data2.b64");
  }

  public static FileChannel getHighEntropyNonZeroChannel()
    throws IOException, URISyntaxException
  {
    return new ReadOnlyByteArrayChannel(getHighEntropyNonZeroBytes());
  }

  public static String[] getMalwareFileNames()
  {
    return new String[]
      {
        "Backdoor.Win32.Banito.ps.b64",
        "UDS.DangerousObject.Multi.Generic.b64",
        "Backdoor.Win32.Hupigon.buwj.b64"
      };
  }

  public static byte[] getBanitoMalwareBytes()
    throws IOException, URISyntaxException
  {
    return loadAndDecodeBase64(getMalwareFileNames()[0]);
  }

  public static FileChannel getBanitoMalwareChannel()
    throws IOException, URISyntaxException
  {
    return new ReadOnlyByteArrayChannel(getBanitoMalwareBytes());
  }

  public static byte[] getDangerousObjectMalwareBytes()
    throws IOException, URISyntaxException
  {
    return loadAndDecodeBase64(getMalwareFileNames()[1]);
  }

  public static FileChannel getDangerousObjectMalwareChannel()
    throws IOException, URISyntaxException
  {
    return new ReadOnlyByteArrayChannel(getDangerousObjectMalwareBytes());
  }

  public static byte[] getHupigonMalwareBytes()
    throws IOException, URISyntaxException
  {
    return loadAndDecodeBase64(getMalwareFileNames()[2]);
  }

  public static FileChannel getHupigonMalwareChannel()
    throws IOException, URISyntaxException
  {
    return new ReadOnlyByteArrayChannel(getHupigonMalwareBytes());
  }
}
