//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           ImportsScore.java
//! @description    Represents a grouping of function symbol imports that could
//!                 be an indicator of malicious capability.
//!

import java.util.Arrays;
import java.util.regex.Pattern;

public class SuspiciousImports
{
  private String[][] names_;
  private String[][] optionalNames_;
  private int score_;
  private int optionalScore_;
  private String description_;

  public static final Pattern[] DEFAULT_FUNCTION_PATTERNS;
  public static final Pattern[] DEFAULT_MODULE_PATTERNS;

  static
  {
    DEFAULT_FUNCTION_PATTERNS = getDefaultSuspiciousFunctionsList();
    DEFAULT_MODULE_PATTERNS = getDefaultSuspiciousModulesList();
  }

  private static Pattern[] getDefaultSuspiciousModulesList()
  {
    return Arrays.stream(new String[]
      {
        "kernel32",
        "ntdll",
        "psapi",
        "shell32",
        "urlmon",
        "user32",
        "wininet",
        "winsock|wsock32|mswsock|wshelp|ws2_32|wshtcpip"
      })
      .map(s -> Pattern.compile("^" + s + "(?:\\.dll)?$",
        Pattern.CASE_INSENSITIVE))
      .toArray(Pattern[]::new);

  }

  private static Pattern[] getDefaultSuspiciousFunctionsList()
  {
    return Arrays.stream(new String[]
      {
        "(?:Nt)?AdjustTokenPrivileges",
        "NtAllocateVirtualMemory",
        "AttachThreadInput",
        //"Bind",
        "BitBlt",
        "CertOpenSystemStore",
        //"Connect",
        "ConnectNamedPipe",
        "ControlService",
        "(?:Nt)?CreateFile[AW]",
        "CreateFileMapping[AW]",
        "CreateMutex[AW]",
        "(?:Nt)?CreateProcess(?:Ex)?[AW]?",
        "CreateRemoteThread",
        "(?:Nt)?CreateProcess(?:Ex)?[AW]?",
        "CreateService[AW]",
        "CreateToolhelp32Snapshot",
        "CryptAcquireContext",
        "DeviceIoControl",
        "EnableExecuteProtectionSupport",
        "EnumProcesses",
        "EnumProcessModules",
        "Find(?:First|Next)File",
        "FindResource(?:Ex)?[AW]",
        "FindWindow(?:Ex)?[AW]",
        "FtpPutFile[AW]",
        "GetAdaptersInfo",
        "GetAsyncKeyState",
        "GetDC",
        "GetForegroundWindow",
        "gethostbyname",
        "gethostname",
        "GetKeyState",
        "GetModuleFilename[AW]",
        "GetModuleHandle",
        "GetProcAddress",
        "GetStartupInfo",
        "GetSystemDefaultLangId",
        "GetTempPath[AW]",
        "GetThreadContext",
        "GetVersionEx",
        "GetWindowsDirectory[AW]",
        "inet_addr",
        "InternetOpen",
        "InternetOpenUrl[AW]",
        "InternetReadFile",
        "InternetWriteFile",
        "IsNTAdmin",
        "IsWoW64Process",
        "LdrLoadDll",
        "LoadLibrary[AW]",
        "LoadResource",
        "LsaEnumerateLogonSessions",
        "MapViewOfFile",
        "(?:Nt)?MapViewOfSection(?:Ex)?[AW]?",
        "MapVirtualKey",
        "Module32(?:First|Next)",
        "NetScheduleJobAdd",
        "NetShareEnum",
        "NtQueryDirectoryFile",
        "NtQueryInformation[a-zA-Z]+",
        "NtQuerySystemInformation(?:Ex)?",
        "NtQueueApcThread(?:Ex)?",
        "NtSetInformation[a-zA-Z]+",
        "OpenMutex",
        "(?:Nt)?Open(?:Process|Thread)",
        "OpenProcess(?:Ex)?",
        "OutputDebugString[AW]",
        "PeekNamedPipe",
        "Process32(?:First|Next)",
        "QueueUserAPC",
        "ReadProcessMemory",
        //"Recv",
        "RegisterHotKey",
        "RegOpenKey",
        "(?:Nt)?ResumeThread",
        "RtlCreateRegistryKey",
        "RtlCreateUser(?:Process(?:Ex)?|Thread)",
        "RtlWriteRegistryValue",
        "SamIConnect",
        "SamIGetPrivateData",
        "SamQueryInformationUse",
        //"Send",
        "SetFileTime",
        "SetThreadContext",
        "SetWindowsHookEx",
        "SfcTerminateWatcherThread",
        "ShellExecute(?:Ex)?[AW]",
        "StartServiceCtrlDispatcher",
        "(?:Nt)?Suspend(?:Process|Thread)",
        //"System",
        "Thread32(?:First|Next)",
        "Toolhelp32ReadProcessMemory",
        "(?:Nt)?UnmapViewOfSection(?:Ex)?",
        "URLDownloadToFile[AW]",
        "Virtual(?:Alloc|Protect)Ex",
        "WideCharToMultiByte",
        "WinExec",
        "WriteProcessMemory",
        "WSAStartup"
      })
    .map(s -> Pattern.compile("^" + s + "$"))
    .toArray(Pattern[]::new);
  }

  public SuspiciousImports(String description, int score, String[][] names,
    int optionalScore, String[][] optionalNames)
  {
    if (names == null || names.length == 0)
    {
      throw new NullPointerException();
    }

    if (Arrays.stream(names).anyMatch(n -> n.length == 0))
    {
      throw new NullPointerException();
    }

    names_ = names;
    if (optionalNames == null)
    {
      optionalNames_ = new String[0][0];
    }
    else
    {
      optionalNames_ = optionalNames;
    }

    score_ = score;
    optionalScore_ = optionalScore;
    description_ = description;
  }

  public SuspiciousImports(String description, int score, String[][] names)
  {
    this(description, score, names, 0, null);
  }

  public SuspiciousImports(String description, int score, String name)
  {
    this(description, score, new String[][] { { name } }, 0, null);
  }

  public String[][] getNames()
  {
    return names_;
  }

  public String[][] getOptionalNames()
  {
    return optionalNames_;
  }

  public int getScore()
  {
    return score_;
  }

  public int getOptionalScore()
  {
    return optionalScore_;
  }

  public String getDescription()
  {
    return description_;
  }
}
