import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EntropyScoreTests
{
  @Test
  void sectionEntropy()
    throws Exception
  {
    PortableExecutableFileChannel peFile =
      PortableExecutableFileChannel.create(TestUtil.getHelloWorldExeBytes());
    HashMap<String, Double> entropyMap = new HashMap<>();
    for (SectionHeader section : peFile.getSections())
    {
      ShannonEntropy entropy = getSectionEntropy(peFile, section);
      System.out.println(new EntropyScore(section.getName() + " entropy",
        peFile, section.getRVA().getFilePosition(),
        section.getSizeOfRawData()));
      entropyMap.put(section.getName(), entropy.getValue());
    }

    /*
    .text: 6.42631889872101869087828163173981010913848876953125
    .rdata: 4.7089907972801565705367465852759778499603271484375
    .data: 1.6917977815019920040384704407188110053539276123046875
    .pdata: 4.871287257618465815767194726504385471343994140625
    _RDATA: 1.0437143519009595138413715176284313201904296875
    .reloc: 4.76245169194049555727588085574097931385040283203125
     */

    assertTrue(compareDouble(6.426319, entropyMap.get(".text")));
    assertTrue(compareDouble(4.708991, entropyMap.get(".rdata")));
    assertTrue(compareDouble(1.691798, entropyMap.get(".data")));
    assertTrue(compareDouble(4.871287, entropyMap.get(".pdata")));
    assertTrue(compareDouble(1.043714, entropyMap.get("_RDATA")));
    assertTrue(compareDouble(4.762452, entropyMap.get(".reloc")));
  }

  @Test
  void exeEntropy()
    throws Exception
  {
    PortableExecutableFileChannel peFile =
      PortableExecutableFileChannel.create(TestUtil.getHelloWorldExeBytes());
    PortableExecutableScore entropyScore =
      new PortableExecutableScore(peFile);
    System.out.println(entropyScore);
  }

  @Test
  void getOverallPEFileEntropyScore()
    throws Exception
  {
    PortableExecutableFileChannel peFile =
      PortableExecutableFileChannel.create(TestUtil.getHelloWorldExeBytes());
    System.out.println(new PortableExecutableScore(peFile));
  }

  @Test
  void moreCompressedExeEntropy()
    throws Exception
  {
    FileChannel compressedExe = TestUtil.getLessCompressedExeChannel();
    System.out.println(new EntropyScore("Compressed (LZMA) EXE entropy",
      compressedExe));
    ShannonEntropy entropy = ShannonEntropy.calculate(compressedExe);
    assertTrue(compareDouble(7.996559, entropy.getValue()));
  }

  @Test
  void lessCompressedExeEntropy()
    throws Exception
  {
    FileChannel compressedExe = TestUtil.getMoreCompressedExeChannel();
    System.out.println(new EntropyScore("Compressed (GZip) EXE entropy",
      compressedExe));
    ShannonEntropy entropy = ShannonEntropy.calculate(compressedExe);
    assertTrue(compareDouble(7.990545, entropy.getValue()));
  }

  @Test
  void encryptedEntropy()
    throws Exception
  {
    FileChannel encryptedChannel = TestUtil.getHighEntropyChannel();
    System.out.println(new EntropyScore("Cryptographic data entropy",
      encryptedChannel));
    ShannonEntropy entropy = ShannonEntropy.calculate(encryptedChannel);
    assertTrue(compareDouble(7.999517, entropy.getValue()));
  }

  @Test
  void encryptedNonZeroEntropy()
    throws Exception
  {
    FileChannel encryptedChannel = TestUtil.getHighEntropyNonZeroChannel();
    System.out.println(new EntropyScore("Non-zero crypytographic data entropy",
      encryptedChannel));
    ShannonEntropy entropy = ShannonEntropy.calculate(encryptedChannel);
    assertTrue(compareDouble(7.993934, entropy.getValue()));
  }

  @Test
  void entropyScores()
  {
    for (double v = 5.9; v <= 8.0; v += 0.1)
    {
      System.out.printf("%.2f\t%.2f\n", v,
        MathUtil.lerp(v, 5.9, 8.0, 0.0, 50.0));
    }
  }

  private static ShannonEntropy getSectionEntropy(
    PortableExecutableFileChannel peFile, SectionHeader section)
    throws IOException, EndOfStreamException
  {
    RelativeVirtualAddress rva = section.getRVA();
    long readPos = rva.getFilePosition();
    int readLen = section.getSizeOfRawData();
    ShannonEntropy entropy =
      ShannonEntropy.calculate(peFile, readPos, readLen);
    return entropy;
  }

  private static boolean compareDouble(double a, double b)
  {
    final double EPSILON = 0.000001;
    return (Math.abs(a - b) < EPSILON);
  }
}
