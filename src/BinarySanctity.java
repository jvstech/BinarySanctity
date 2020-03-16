//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           BinarySanctity.java
//! @description    Entry point for a rudimentary malware scanner.
//!

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class BinarySanctity extends Application
{
  public static void main(String[] args)
  {
    // #TODO: switch these if/else clauses eventually.
    if (args == null || args.length == 0)
    {
      int exitCode = runConsoleMode(args);
      System.exit(exitCode);
    }
    else
    {
      launch(args);
    }
  }

  private static int runConsoleMode(String[] args)
  {
    System.out.println(StringUtil.wordWrap(Help.ABOUT_TEXT, 80, 132));
    ArrayList<String> inputFilePaths = new ArrayList<>();
    inputFilePaths.addAll(Arrays.asList(args));
    if (inputFilePaths.isEmpty())
    {
      Scanner scanner = new Scanner(System.in);
      System.out.print("Path to executable file: ");
      inputFilePaths.add(scanner.nextLine());
    }

    for (String peFilePath : inputFilePaths)
    {
      System.out.print(peFilePath + ": ");
      try
      {
        PortableExecutableFileChannel peFile =
          PortableExecutableFileChannel.create(peFilePath);
        PortableExecutableScore score = new PortableExecutableScore(peFile);
        System.out.println("\n" + score);
      }
      catch (FileNotFoundException e)
      {
        System.out.println("file not found.");
      }
      catch (IOException e)
      {
        System.out.println("I/O exception. " + e.getMessage());
        e.printStackTrace();
      }
      catch (EndOfStreamException e)
      {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
      catch (BadExecutableFormatException e)
      {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }

      System.out.println();
    }

    return 0;
  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    Help.about();
  }
}
