//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           BinarySanctity.java
//! @description    Entry point for a rudimentary malware scanner.
//!

import javafx.application.Application;
import javafx.stage.Stage;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class BinarySanctity extends Application
{
  public static void main(String[] args)
  {
    // Check to see if we're running in headless mode (no UI available) *or* if
    // the user specified wanting to run in console mode via the "-console"
    // command-line argument.
    if (GraphicsEnvironment.isHeadless() || (args != null && args.length > 0 &&
      Arrays.asList(args).contains("-console")))
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

    if (ConsoleCommands.handle(args))
    {
      return ConsoleCommands.getExitCode();
    }

    ArrayList<String> inputFilePaths = new ArrayList<>(Arrays.asList(args));
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
      catch (EndOfStreamException | BadExecutableFormatException e)
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
    UIView view;

    Help.about();
    view = new UIView();
    view.setOnShown(event -> UICommands.handle(view, getParameters()));
    view.show();
  }
}
