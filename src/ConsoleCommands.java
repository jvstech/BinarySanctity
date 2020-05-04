//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           ConsoleCommands.java
//! @description    Handles command-line arguments for running utility
//!                 sub-commands.
//!

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConsoleCommands
{
  @FunctionalInterface
  private interface PEFilePathHandler
  {
    void accept(String filePath)
      throws IOException, EndOfStreamException, BadExecutableFormatException;
  }

  private static int exitCode_ = 0;

  public static int getExitCode()
  {
    return exitCode_;
  }

  // This function returns true if an argument was handled by a console command
  // handler, or false if otherwise. A return of false means regular malware
  // scoring should occur.
  public static boolean handle(String[] cmdLineArgs)
  {
    if (cmdLineArgs == null || cmdLineArgs.length == 0)
    {
      return false;
    }

    // Strip out the "-console" argument if one was present.
    String[] args = Arrays.stream(cmdLineArgs)
      .filter(a -> !a.equals("-console"))
      .toArray(String[]::new);

    if (!args[0].startsWith("-"))
    {
      return false;
    }

    // Hyphen must be followed by at least one character
    if (args[0].length() < 2)
    {
      return false;
    }

    // The first argument is the "command" argument. Extract it and build a new
    // array containing the remaining arguments.
    String commandArg = args[0].substring(1);
    String[] nextArgs = Arrays.stream(args)
      .skip(1)
      .toArray(String[]::new);
    switch (commandArg)
    {
      case "sectionNames":
        exitCode_ = showSectionNames(nextArgs);
        return true;
      case "imports":
        exitCode_ = showImports(nextArgs);
        return true;
    }

    return false;
  }

  public static int showSectionNames(String[] args)
  {
    PEFilePathHandler handler = filePath ->
    {
      PortableExecutableFileChannel peFile =
        PortableExecutableFileChannel.create(filePath);
      System.out.println(filePath + ":");
      for (SectionHeader section : peFile.getSections())
      {
        System.out.printf("  \"%s\" %s\n",
          StringUtil.escapeFull(section.getName()),
          SectionCharacteristicTypes.getStrings(
            SectionCharacteristicTypes.getWithoutAlignments(
              section.getCharacteristics())));
      }
    };

    return forEachFile(args, handleDirWalkPEFilePath(handler),
      handlePEFilePath(handler));
  }

  public static int showImports(String[] args)
  {
    PEFilePathHandler handler = filePath ->
    {
      PortableExecutableFileChannel peFile =
        PortableExecutableFileChannel.create(filePath);
      System.out.println(filePath + ":");
      for (Map.Entry<String, String[]> imports :
        peFile.getImportedNames().entrySet())
      {
        for (String importFunc : imports.getValue())
        {
          System.out.printf("  %s!%s\n", imports.getKey(), importFunc);
        }
      }

      System.out.println();
    };

    return forEachFile(args, handleDirWalkPEFilePath(handler),
      handlePEFilePath(handler));
  }

  private static Consumer<? super String> handleDirWalkPEFilePath(
    PEFilePathHandler handler)
  {
    return filePath ->
    {
      try
      {
        handler.accept(filePath);
      }
      catch (IOException | EndOfStreamException |
        BadExecutableFormatException e)
      {
        // do nothing; move to the next file
      }
    };
  }

  private static Function<? super String, ? extends Boolean> handlePEFilePath(
    PEFilePathHandler handler)
  {
    return filePath ->
    {
      try
      {
        handler.accept(filePath);
      }
      catch (IOException e)
      {
        System.err.println(filePath + ": I/O error while reading: " +
          e.getMessage());
        return false;
      }
      catch (EndOfStreamException e)
      {
        System.err.println(filePath + ": reached premature end-of-file.");
        return false;
      }
      catch (BadExecutableFormatException e)
      {
        System.err.println(filePath +
          ": bad executable format/not an executable");
        return false;
      }

      return true;
    };
  }

  private static int forEachFile(String[] args,
    Consumer<? super String> dirWalkConsumer,
    Function<? super String, ? extends Boolean> argFileConsumer)
  {
    ArrayList<String> fileList = new ArrayList<>();
    for (String arg : args)
    {
      File f = new File(arg);
      if (!f.exists())
      {
        System.err.println(arg + ": file/directory not found.");
      }

      if (f.isDirectory())
      {
        try
        {
          Files.walk(Paths.get(arg))
            .filter(Files::isRegularFile)
            .filter(Files::isReadable)
            .map(Path::toAbsolutePath)
            .map(Path::toString)
            .forEach(dirWalkConsumer);
        }
        catch (IOException e)
        {
          System.err.println("I/O error reading " + arg + ": " +
            e.getMessage());
        }
      }
      else
      {
        fileList.add(arg);
      }
    }

    boolean success = fileList.stream()
      .filter(f -> Files.exists(Paths.get(f)))
      .map(argFileConsumer)
      .map(Boolean::booleanValue)
      .reduce((e1, e2) -> e1 & e2)
      .orElse(true);
    return (success ? 0 : 1);
  }

  private static int forEachFile(String[] args,
    Consumer<? super String> filePathConsumer)
  {
    return forEachFile(args, filePathConsumer,
      f -> { filePathConsumer.accept(f); return true; });
  }
}
