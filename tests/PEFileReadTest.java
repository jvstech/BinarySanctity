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

class PEFileReadTest
{
  @Test
  void dosHeaderFromStream()
    throws Exception
  {
    PortableExecutableFileChannel peFile =
      new PortableExecutableFileChannel(getDecodedData());
    DOSHeader dosHeader = peFile.getDOSHeader();
    assertTrue(dosHeader.isValid());
    assertEquals(144, dosHeader.getLastPageSize());
    assertEquals(3, dosHeader.getTotalPageCount());
    assertEquals(65535, dosHeader.getMaxAllocatedParagraphCount());
    assertEquals(184, dosHeader.getInitialSP());
    assertEquals(64, dosHeader.getRelocationTableOffset());
    assertEquals(256, dosHeader.getPEHeaderOffset());
  }

  @Test
  void peHeaderFromStream()
    throws Exception
  {
    PortableExecutableFileChannel peFile =
      new PortableExecutableFileChannel(getDecodedData());
    PEHeader peHeader = peFile.getPEHeader();
    assertTrue(peHeader.isValid());
    assertEquals(MachineType.AMD64, peHeader.getMachine());
    assertEquals(6, peHeader.getNumberOfSections());
    assertEquals(1581221088, peHeader.getTimeDateStampValue());
    assertEquals(0, peHeader.getPointerToSymbolTable());
    assertEquals(0, peHeader.getNumberOfSymbols());
    assertEquals(240, peHeader.getSizeOfOptionalHeader());
    assertEquals((PECharacteristicTypes.EXECUTABLE_IMAGE |
      PECharacteristicTypes.LARGE_ADDRESS_AWARE),
      peHeader.getCharacteristics());
  }

  @Test
  void optionalHeaderFromStream()
    throws Exception
  {
    byte[] decodedData = getDecodedData();
    PortableExecutableFileChannel peFile =
      new PortableExecutableFileChannel(decodedData);
    OptionalHeader optHeader = peFile.getOptionalHeader();

    assertTrue(optHeader.isValid());
    assertEquals(ImageStateType.PE64, optHeader.getImageState());
    assertEquals(14, optHeader.getMajorLinkerVersion());
    assertEquals(24, optHeader.getMinorLinkerVersion());
    assertEquals(50688, optHeader.getSizeOfCode());
    assertEquals(50176, optHeader.getSizeOfInitializedData());
    assertEquals(0, optHeader.getSizeofUninitializedData());
    assertEquals(0x1280, optHeader.getAddressOfEntryPoint());
    assertEquals(0x1000, optHeader.getBaseOfCode());
    assertEquals(0x140000000L, optHeader.getImageBase());
    assertEquals(4096, optHeader.getSectionAlignment());
    assertEquals(512, optHeader.getFileAlignment());
    assertEquals(6, optHeader.getMajorOperatingSystemVersion());
    assertEquals(0, optHeader.getMinorOperatingSystemVersion());
    assertEquals(0, optHeader.getMajorImageVersion());
    assertEquals(0, optHeader.getMinorImageVersion());
    assertEquals(6, optHeader.getMajorSubsystemVersion());
    assertEquals(0, optHeader.getMinorSubsystemVersion());
    assertEquals(0, optHeader.getWin32VersionValue());
    assertEquals(0x1c000, optHeader.getSizeOfImage());
    assertEquals(1024, optHeader.getSizeOfHeaders());
    assertEquals(0, optHeader.getChecksum());
    assertEquals(WindowsSubsystemType.CONSOLE, optHeader.getSubsystem());
    assertEquals((DllCharacteristicTypes.DYNAMIC_BASE |
        DllCharacteristicTypes.NX_COMPATIBLE |
        DllCharacteristicTypes.TERMINAL_SERVER_AWARE),
      optHeader.getDllCharacteristics());
    assertEquals(0x100000, optHeader.getSizeOfStackReserve());
    assertEquals(0x1000, optHeader.getSizeOfStackCommit());
    assertEquals(0x100000, optHeader.getSizeOfHeapReserve());
    assertEquals(0x1000, optHeader.getSizeOfHeapCommit());
    assertEquals(0, optHeader.getLoaderFlags());
    assertEquals(16, optHeader.getNumberOfRvaAndSizes());
  }

  private static byte[] getDecodedData()
    throws IOException, URISyntaxException
  {
    URI testDataUri =
      PEFileReadTest.class.getResource("hello-world.b64").toURI();
    Path testDataPath = Paths.get(testDataUri);

    String encodedData =
      new String(Files.readAllBytes(testDataPath), StandardCharsets.UTF_8);
    byte[] decodedData = Base64.getDecoder().decode(encodedData);
    return decodedData;
  }
}