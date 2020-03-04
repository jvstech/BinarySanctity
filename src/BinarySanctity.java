//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           BinarySanctity.java
//! @description    Entry point for a rudimentary malware scanner.
//!

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class BinarySanctity extends Application
{
  public static void main(String[] args)
  {
    launch(args);

    // #TODO:
    //    ArrayList<String> inputFilePaths = new ArrayList<>();
    //    inputFilePaths.addAll(Arrays.asList(args));
    //    if (inputFilePaths.isEmpty())
    //    {
    //      Scanner scanner = new Scanner(System.in);
    //      System.out.print("Path to executable file: ");
    //      inputFilePaths.add(scanner.nextLine());
    //    }
  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {
    Help.about();
  }
}
