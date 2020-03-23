import org.junit.jupiter.api.Test;


public class PEFileScoreTests
{
  @Test
  void missingGuiImports()
    throws Exception
  {
    PortableExecutableFileChannel peFile =
      PortableExecutableFileChannel.create(
        TestUtil.loadAndDecodeBase64("Backdoor.Win32.Banito.ps.b64"));
    PortableExecutableScore peFileScore = new PortableExecutableScore(peFile);
    System.out.println(peFileScore);
  }

  @Test
  void malwareTests()
    throws Exception
  {
    for (String malwareName : getMalwareFileNames())
    {
      PortableExecutableFileChannel peFile =
        PortableExecutableFileChannel.create(
          TestUtil.loadAndDecodeBase64(malwareName));
      PortableExecutableScore peFileScore = new PortableExecutableScore(peFile);
      System.out.println(malwareName + ":");
      System.out.println(peFileScore);
      System.out.println();
    }
  }

  private static String[] getMalwareFileNames()
  {
    return new String[]
      {
        "Backdoor.Win32.Banito.ps.b64",
        "UDS.DangerousObject.Multi.Generic.b64",
        "Backdoor.Win32.Hupigon.buwj.b64"
      };
  }
}
