//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           UICommands.java
//! @description    Handles events and execution logic generated from the main
//!                 window and controls.
//!

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UICommands
{
  private static class PromptAndLoadFilesTask
    extends Task<List<UIView.ScoreItem>>
  {
    private List<File> files_;
    private UIView view_;
    private List<UIView.ScoreItem> scoreItems_;

    public PromptAndLoadFilesTask(UIView view, List<File> files)
    {
      view_= view;
      files_ = files;
    }

    public UIView getView()
    {
      return view_;
    }

    public List<UIView.ScoreItem> getScoreItems()
    {
      return scoreItems_;
    }

    @Override
    protected List<UIView.ScoreItem> call() throws Exception
    {
      List<UIView.ScoreItem> results = new ArrayList<>();
      int processedCount = 0;
      int fileCount = files_.size();

      for (File f : files_)
      {
        updateMessage(String.format("Analyzing %s (%s)",
          f.getName(), f.getParent()));
        if (fileCount > 1)
        {
          updateProgress(processedCount, fileCount);
          ++processedCount;
        }

        results.add(readAndScoreExecutableFile(view_, f,
          s -> updateMessage(String.format("Analyzing %s (%s)", f.getName(),
            s))));
      }

      updateMessage("Complete");
      updateProgress(0, 0);
      return results;
    }
  }

  public static void promptAndLoadFiles(UIView view)
  {
    FileChooser fileDialog = new FileChooser();
    fileDialog.setTitle("Add Executable File");
    fileDialog.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter(
        "Portable executable files",
        "*.exe", "*.dll", "*.ocx", "*.cpl", "*.drv", "*.acm", "*.ax",
        "*.efi", "*.mui", "*.scr", "*.sys", "*.tsp"),
      new FileChooser.ExtensionFilter("All files",
        "*.*", "*"));
    List<File> files = fileDialog.showOpenMultipleDialog(view);
    if (files != null)
    {
      // Unbind properties before use
      if (view.getProgressBar().progressProperty().isBound())
      {
        view.getProgressBar().progressProperty().unbind();
      }

      if (view.statusProperty().isBound())
      {
        view.statusProperty().unbind();
      }

      if (files.size() == 1)
      {
        view.getProgressBar().setProgress(ProgressBar.INDETERMINATE_PROGRESS);
      }

      PromptAndLoadFilesTask task = new PromptAndLoadFilesTask(view, files);
      task.setOnSucceeded(event ->
      {
        PromptAndLoadFilesTask loadFilesTask =
          ((PromptAndLoadFilesTask)event.getSource());
        List<UIView.ScoreItem> scoreItems = loadFilesTask.getValue();
        for (UIView.ScoreItem scoreItem : scoreItems)
        {
          if (!scoreItem.hasException())
          {
            loadFilesTask.getView().getFileListView().getItems().add(scoreItem);
          }
          else
          {
            UIView.showError(scoreItem.getErrorMessage(),
              scoreItem.getErrorTitle());
          }
        }
      });

      view.getProgressBar().progressProperty().bind(task.progressProperty());
      view.statusProperty().bind(task.messageProperty());
      new Thread(task).start();
    }
  }

  public static UIView.ScoreItem readAndScoreExecutableFile(UIView view,
    File file, Consumer<? super String> statusCallback)
  {
    String errorTitle = null;
    String errorMessage = null;
    UIView.ScoreItem scoreItem = null;
    try
    {
      PortableExecutableFileChannel peFile =
        PortableExecutableFileChannel.create(file);
      PortableExecutableScore score =
        new PortableExecutableScore(peFile, statusCallback);
      scoreItem = view.new ScoreItem(score, file.getAbsolutePath());
    }
    catch (IOException e)
    {
      e.printStackTrace();
      errorMessage = String.format("Couldn't load file \"%s\": %s",
        file.getAbsolutePath(), e.getMessage());
      errorTitle = "File Read Error";
      scoreItem = view.new ScoreItem(e, errorTitle, errorMessage, file);
    }
    catch (EndOfStreamException e)
    {
      e.printStackTrace();
      errorMessage = String.format(
        "The file \"%s\" is smaller than expected and cannot be read.",
        file.getAbsolutePath());
      errorTitle = "File Too Small";
      scoreItem = view.new ScoreItem(e, errorTitle, errorMessage, file);
    }
    catch (BadExecutableFormatException e)
    {
      e.printStackTrace();
      errorMessage = String.format(
        "The file \"%s\" does not appear to be a valid executable file.",
        file.getAbsolutePath());
      if (!StringUtil.isNullOrWhiteSpace(e.getMessage()))
      {
        errorMessage =
          String.format("%s\nReason: %s", errorMessage, e.getMessage());
      }

      errorTitle = "Bad Executable File";
      scoreItem = view.new ScoreItem(e, errorTitle, errorMessage, file);
    }

    return scoreItem;
  }
}
