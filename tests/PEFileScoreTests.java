import org.junit.jupiter.api.Test;

// Problematic files:
//    Backdoor.Win32.Hupigon.buwj.665ddac0f5f90697c27bb4a1ce4ea294


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
        "UDS.DangerousObject.Multi.Generic.b64"
      };
  }
}
