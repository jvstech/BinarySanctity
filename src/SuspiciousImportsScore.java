//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           SuspiciousImportsScore.java
//! @description    Characterization of function imports commonly found in
//!                 malware.
//!

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SuspiciousImportsScore extends Score
{
  // The layout of this list is as follows:
  //   new SuspiciousImports(
  //     "Description of imports",
  //     score value,
  //     new String[][]
  //     {
  //       {
  //         "Groups of regular expressions",
  //         "for sets of imports that *must* exist to be considered suspicious"
  //       },
  //       {
  //         "At least one of these groups must exist,"
  //       },
  //       {
  //         "but it is not required that ALL of them exist."
  //       }
  //     },
  //     amplified score value,
  //     new String[][]
  //     {
  //       {
  //         "optionally existing groups of regular expressions",
  //         "for imports that amplify the suspicion value"
  //       }
  //     }
  //   )

  private static final List<SuspiciousImports> SUSPICIOUS_IMPORTS =
    Arrays.asList(
      new SuspiciousImports(
        "Process injection",
        100,
        new String[][]
          {
            {
              "CreateRemoteThread|NtCreateThreadEx|RtlCreateUserThread",
              "WriteProcessMemory"
            }
          },
        175,
        new String[][]
          {
            {
              "Virtual(?:Alloc|Protect)Ex"
            }
          }
        ),
      new SuspiciousImports(
        "Process injection",
        100,
        new String[][]
          {
            {
              "QueueUserAPC",
              "WriteProcessMemory"
            }
          },
        175,
        new String[][]
          {
            {
              "OpenThread",
              "Virtual(?:Alloc|Protect)Ex",
            }
          }
        ),
      new SuspiciousImports(
        "Process injection",
        75,
        new String[][]
          {
            {
              "GetProcAddress",
              "SetWindowsHookEx",
              "PostThreadMessage[AW]"
            }
          },
        150,
        new String[][]
          {
            {
              "LoadLibrary[AW]",
              "GetCurrentThreadId"
            }
          }
        ),
      new SuspiciousImports(
        "Process hallowing",
        200,
        new String[][]
          {
            {
              "(?:Nt)?CreateProcess(?:Ex)?[AW]?|NtCreateUserProcess",
              "NtUmapViewOfSection",
              "VirtualAllocEx|NtAllocateVirtualMemory",
              "(?:Nt)?WriteProcessMemory",
              "(?:Nt)?SetThreadContext",
              "(?:Nt)?ResumeThread"
            }
          },
        225,
        new String[][]
          {
            {
              "(?:Nt)?GetThreadContext"
            }
          }
        ),
      new SuspiciousImports(
        "Thread injection",
        150,
        new String[][]
          {
            {
              "CreateToolhelp32Snapshot",
              "Thread32First",
              "OpenThread",
              "SuspendThread",
              "VirtualAllocEx|NtAllocateVirtualMemory",
              "(?:Nt)?WriteProcessMemory",
              "(?:Nt)?SetThreadContext",
              "(?:Nt)?ResumeThread"
            }
          },
        200,
        new String[][]
          {
            {
              "(?:Nt)?GetThreadContext"
            }
          }
        ),
      new SuspiciousImports(
        "Security control",
        150,
        new String[][]
          {
            {
              "EnableExecuteProtectionSupport"
            },
            {
              "SfcTerminateWatcherThread"
            }
          }
        ),
      new SuspiciousImports(
        "Alternative/stealth functions",
        80,
        new String[][]
          {
            {
              "LdrLoadDll"
            },
            {
              "SetFileTime"
            },
            {
              "WinExec"
            }
          }
        ),
      new SuspiciousImports(
        "Scheduled job creation",
        80,
        new String[][]
          {
            {
              "NetScheduleJobAdd"
            }
          }
        ),
      new SuspiciousImports(
        "Keylogging",
        50,
        new String[][]
          {
            {
              "Get(?:Async)?KeyState"
            },
            {
              "MapVirtualKey"
            }
          }
        ),
      new SuspiciousImports(
        "Dynamic imports",
        15,
        new String[][]
          {
            {
              "LoadLibrary[AW]",
              "GetProcAddress"
            }
          }
        ),
      new SuspiciousImports(
        "Screen capturing/logging",
        200,
        new String[][]
          {
            {
              "capCreateCaptureWindow[AW]",
              "capGetDriverDescription[AW]"
            }
          }
        ),
      new SuspiciousImports(
        "Bad import pointers",
        500,
        new String[][]
          {
            {
              "<invalid_rva:0x[0-9a-fA-F]+>"
            }
          }
        )
      );

  public static final String TITLE = "Suspicious imports";

  private final PortableExecutableFileChannel peFile_;

  public SuspiciousImportsScore(PortableExecutableFileChannel peFile)
    throws IOException, EndOfStreamException
  {
    peFile_ = peFile;
    characterize();
  }

  @Override
  public String getTitle()
  {
    return TITLE;
  }

  @Override
  public String getDescription()
  {
    return "Common maliciously used function imports";
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return (getValue() > 15);
  }

  private void characterize()
    throws IOException, EndOfStreamException
  {
    Set<String> importedNames =
      peFile_.getImportedNames(true).values().stream()
        .flatMap(Arrays::stream)
        .collect(Collectors.toSet());
    for (SuspiciousImports suspiciousImports : SUSPICIOUS_IMPORTS)
    {
      int currentScore = 0;
      for (String[] suspiciousNames : suspiciousImports.getNames())
      {
        if (hasImportMatch(importedNames, suspiciousNames))
        {
          currentScore = suspiciousImports.getScore();
          for (String[] optionalNames : suspiciousImports.getOptionalNames())
          {
            if (hasImportMatch(importedNames, optionalNames))
            {
              currentScore = suspiciousImports.getOptionalScore();
              break;
            }
          }

          addDetail(suspiciousImports.getDescription());
          setValue(getValue() + currentScore);
        }
      }
    }
  }

  private static boolean hasImportMatch(Set<String> importedNames,
    String[] suspiciousNames)
  {
    boolean isMatch = true;
    for (String suspiciousName : suspiciousNames)
    {
      boolean isCurrentMatch = false;
      for (String importedName : importedNames)
      {
        if (Pattern.matches("^" + suspiciousName + "$", importedName))
        {
          isCurrentMatch = true;
          break;
        }
      }

      isMatch = isCurrentMatch;
      if (!isCurrentMatch)
      {
        break;
      }
    }

    return isMatch;
  }
}
