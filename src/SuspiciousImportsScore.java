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
import java.util.stream.Collectors;

public class SuspiciousImportsScore extends Score
{
  private static final List<SuspiciousImports> SUSPICIOUS_IMPORTS =
    Arrays.asList(
      new SuspiciousImports(
        "Process injection",
        100,
        new String[][]
          {
            {
              "CreateRemoteThread",
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
              "CreateProcess(?:Ex)?[AW]|NtCreateUserProcess",
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
        )
      );

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
    return "Suspicious Imports";
  }

  @Override
  public String getDescription()
  {
    return "Common maliciously used function imports";
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return (getValue() > 80);
  }

  private void characterize()
    throws IOException, EndOfStreamException
  {
    Set<String> importedNames = peFile_.getImportedNames().values().stream()
      .flatMap(Arrays::stream)
      .collect(Collectors.toSet());
    for (SuspiciousImports suspiciousImports : SUSPICIOUS_IMPORTS)
    {
      int currentScore = 0;
      boolean hasRequiredMatch =
        Arrays.stream(suspiciousImports.getNames())
          .anyMatch(names -> Arrays.stream(names)
            .allMatch(importedNames::contains));
      if (hasRequiredMatch)
      {
        currentScore = suspiciousImports.getScore();
        boolean hasOptionalMatch =
          Arrays.stream(suspiciousImports.getOptionalNames())
          .anyMatch(names -> Arrays.stream(names)
          .allMatch(importedNames::contains));
        if (hasOptionalMatch)
        {
          currentScore = suspiciousImports.getOptionalScore();
        }

        addDetail(suspiciousImports.getDescription());
        setValue(getValue() + currentScore);
      }
    }
  }
}
