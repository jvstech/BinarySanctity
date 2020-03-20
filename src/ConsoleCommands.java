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

public class ConsoleCommands
{
  private static int exitCode_ = 0;

  public static int getExitCode()
  {
    return exitCode_;
  }

  public static boolean handle(String[] args)
  {
    if (args == null || args.length == 0)
    {
      return false;
    }

    if (!args[0].startsWith("-"))
    {
      return false;
    }

    // Hyphen must be followed by at least one character
    if (args[0].length() < 2)
    {
      return false;
    }

    String commandArg = args[0].substring(1);
    String[] nextArgs = Arrays.stream(args)
      .skip(1)
      .toArray(String[]::new);
    switch (commandArg)
    {
      case "sectionNames":
        exitCode_ = showSectionNames(nextArgs);
        return true;
    }

    return false;
  }

  public static int showSectionNames(String[] args)
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
            .forEach(filePath ->
            {
              try
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
              }
              catch (IOException | EndOfStreamException |
                BadExecutableFormatException e)
              {
                // do nothing; move to the next file
                //e.printStackTrace();
              }
            });
        }
        catch (IOException e)
        {
          // #TODO: maybe emit a application-wide signal noting this file
          //    couldn't be read
        }
      }
      else
      {
        fileList.add(arg);
      }
    }

    boolean hadErrors = false;
    for (String filePath : fileList)
    {
      if (Files.exists(Paths.get(filePath)))
      {
        try
        {
          PortableExecutableFileChannel peFile =
            PortableExecutableFileChannel.create(filePath);
          System.out.println(filePath + ":");
          for (SectionHeader section : peFile.getSections())
          {
            System.out.printf("  %s %s\n", section,
              SectionCharacteristicTypes.getStrings(
                SectionCharacteristicTypes.getWithoutAlignments(
                  section.getCharacteristics())));
          }
        }
        catch (IOException e)
        {
          hadErrors = true;
          System.err.println(filePath + ": I/O error while reading: " +
            e.getMessage());
        }
        catch (EndOfStreamException e)
        {
          hadErrors = true;
          System.err.println(filePath + ": reached premature end-of-file.");
        }
        catch (BadExecutableFormatException e)
        {
          hadErrors = true;
          System.err.println(filePath +
            ": bad executable format/not an executable");
        }
      }
    }

    return (hadErrors ? 1 : 0);
  }
}
