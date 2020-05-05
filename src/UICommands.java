//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           UICommands.java
//! @description    Handles events and execution logic generated from the main
//!                 window and controls.
//!

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UICommands
{
  // Nested class for asynchronously loading and analyzing executable files
  private static class AnalyzeFilesTask extends Task<Void>
  {
    private List<File> files_;
    private UIView view_;
    private boolean allowSlowAnalysis_;
    private boolean ignoreErrors_;
    private File currentFile_;

    public AnalyzeFilesTask(UIView view, Collection<File> files,
      boolean allowSlowAnalysis, boolean ignoreErrors)
    {
      view_= view;
      files_ = new ArrayList<>(files);
      allowSlowAnalysis_ = allowSlowAnalysis;
      ignoreErrors_ = ignoreErrors;
      currentFile_ = null;
    }

    public boolean getAllowSlowAnalysis()
    {
      return allowSlowAnalysis_;
    }

    public File getCurrentFile()
    {
      return currentFile_;
    }

    public boolean getIgnoreErrors()
    {
      return ignoreErrors_;
    }

    public UIView getView()
    {
      return view_;
    }

    @Override
    protected Void call() throws Exception
    {
      int processedCount = 0;
      int fileCount = files_.size();

      for (File f : files_)
      {
        currentFile_ = f;
        updateMessage(String.format("Analyzing %s (%s)",
          f.getName(), f.getParent()));
        if (fileCount > 1)
        {
          updateProgress(processedCount, fileCount);
          ++processedCount;
        }

        final UIView.ScoreItem scoreItem =
          readAndScoreExecutableFile(view_, f, allowSlowAnalysis_,
            s -> updateMessage(String.format("Analyzing %s (%s)", f.getName(),
              s)));
        if (scoreItem != null)
        {
          if (!scoreItem.hasException())
          {
            Platform.runLater(() ->
              view_.getFileListView().getItems().add(scoreItem));
          }
          else
          {
            if (!ignoreErrors_)
            {
              Platform.runLater(() ->
                UIView.showError(scoreItem.getErrorMessage(),
                  scoreItem.getErrorTitle()));
            }
          }
        }
      }

      currentFile_ = null;
      updateMessage("Complete");
      updateProgress(0, 0);
      return null;
    }
  }

  // Nested task class that forwards progress and message updates from a child
  // task
  private static abstract class TransparentTask<T> extends Task<T>
  {
    protected void forwardBind(Task<?> task)
    {
      task.progressProperty().addListener(this::progressHandler);
      task.messageProperty().addListener(this::messageHandler);
    }

    protected void forwardUnbind(Task<?> task)
    {
      task.progressProperty().removeListener(this::progressHandler);
      task.messageProperty().removeListener(this::messageHandler);
    }

    protected void messageHandler(ObservableValue<? extends String> observable,
      String oldValue, String newValue)
    {
      updateMessage(newValue);
    }

    protected void progressHandler(ObservableValue<? extends Number> observable,
      Number oldValue, Number newValue)
    {
      updateProgress(newValue.doubleValue(), 1.0);
    }
  }

  // Keeps track of currently running tasks
  private static final Map<Runnable, Thread> RunningTaskMap =
    new ConcurrentHashMap<>();
  // Custom thread pool that automatically adds and removes executing threads
  // to the list of running tasks as they start and stop
  private static final ExecutorService TaskPool =
    new ThreadPoolExecutor(10, Integer.MAX_VALUE, 0L,
      TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>())
  {
    @Override
    protected void afterExecute(Runnable r, Throwable t)
    {
      RunningTaskMap.remove(r);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r)
    {
      RunningTaskMap.put(r, t);
    }
  };

  // Flag to keep track of whether or not start-up arguments were parsed
  private static boolean startupHandled = false;

  // Clears analysis information from display
  public static void clearScoreItemSelection(UIView view)
  {
    // If the analysis controls are ever changed, this function will have to be
    // changed, too.
    view.getMalwareScoreText().clear();
    view.getImportsTableView().setItems(null);
    view.getSectionsTableView().setItems(null);
  }

  // Removes all scores from the list and clears analysis information from
  // display
  public static void clearScoreItems(UIView view)
  {
    view.getFileListView().getItems().clear();
    clearScoreItemSelection(view);
  }

  // Terminates all running tasks and prevents any new ones from starting
  public static void closeUI(WindowEvent event)
  {
    stopAllTasks();
    TaskPool.shutdownNow();
  }

  // Analyzes any files or folders specified as command-line arguments
  public static void handle(UIView view, Application.Parameters cmdLineArgs)
  {
    if (startupHandled || cmdLineArgs == null ||
      cmdLineArgs.getRaw().isEmpty())
    {
      return;
    }

    startupHandled = true;
    // Strip out the "--graphical" argument if one was present
    List<String> args = cmdLineArgs.getUnnamed().stream()
      .filter(a -> !a.equals("--graphical"))
      .collect(Collectors.toList());

    Map.Entry<Map<String, String>, List<String>> parsedArgMap =
      parseFlagArgs(args,
        "analyze-text",
        "no-analyze-text");
    Map<String, String> parsedArgs = parsedArgMap.getKey();
    args = parsedArgMap.getValue();

    final boolean noAnalyzeText = parsedArgs.containsKey("no-analyze-text");
    // "--no-analyze-text" takes precedence over "--analyze-text" if both are
    // present on the command line
    final boolean analyzeText = (parsedArgs.containsKey("analyze-text") &&
      !noAnalyzeText);
    final List<String> finalArgs = args;

    // Update the "Analyze text" checkbox if "--no-analyze-text" is explicitly
    // passed on the command line
    runOnUI(() -> view.getAnalyzeTextCheckBox().setSelected(!noAnalyzeText));

    // If there are no files or folders listed, we're done.
    if (args.isEmpty())
    {
      return;
    }

    Task<Set<String>> wrapperTask = new TransparentTask<Set<String>>()
    {
      @Override
      protected Set<String> call() throws Exception
      {
        Set<String> notFoundPaths = new TreeSet<>();
        Set<String> filePaths = new LinkedHashSet<>();
        Set<String> folderPaths = new LinkedHashSet<>();
        for (String path : finalArgs)
        {
          File f = new File(path);
          if (!f.exists())
          {
            notFoundPaths.add(path);
            continue;
          }

          if (f.isDirectory())
          {
            folderPaths.add(f.getAbsolutePath());
          } else
          {
            filePaths.add(f.getAbsolutePath());
          }
        }

        // Handle folders first followed by files
        for (String folderPath : folderPaths)
        {
          if (!Thread.currentThread().isInterrupted())
          {
            Map.Entry<Future<?>, Task<?>> futureFolderTask =
              scoreFolderAsync(view, new File(folderPath), analyzeText);
            Task<?> folderTask = futureFolderTask.getValue();
            forwardBind(folderTask);
            futureFolderTask.getKey().get();
            forwardUnbind(folderTask);
          }
        }

        if (!filePaths.isEmpty())
        {
          List<File> files = filePaths.stream()
            .map(File::new)
            .collect(Collectors.toList());
          Map.Entry<Future<?>, AnalyzeFilesTask> futureFilesTask =
            scoreFilesAsync(view, files, analyzeText, false);
          AnalyzeFilesTask filesTask = futureFilesTask.getValue();
          forwardBind(filesTask);
          futureFilesTask.getKey().get();
          forwardUnbind(filesTask);
        }

        return notFoundPaths;
      }
    };

    EventHandler<WorkerStateEvent> taskEndedEvent = event ->
    {
      unbindProperty(view.statusProperty());
      unbindProperty(view.progressProperty());
      runOnUI(() ->
      {
        view.getProgressBar().setProgress(0.0);
        showPathsNotFound(wrapperTask.getValue());
      });
    };

    runOnUI(() ->
    {
      wrapperTask.setOnSucceeded(taskEndedEvent);
      wrapperTask.setOnFailed(taskEndedEvent);
      bindTaskUpdates(view, wrapperTask);
      TaskPool.execute(wrapperTask);
    });

  }

  public static void promptAndLoadFiles(UIView view, boolean allowSlowAnalysis)
  {
    FileChooser fileDialog = new FileChooser();
    fileDialog.setTitle("Add Executable File");
    fileDialog.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter(
        "Portable executable files",
        "*.exe", "*.dll", "*.ocx", "*.cpl", "*.drv", "*.acm", "*.ax",
        "*.efi", "*.mui", "*.scr", "*.sys", "*.tsp"),
      new FileChooser.ExtensionFilter("Base64-encoded files",
        "*.b64"),
      new FileChooser.ExtensionFilter("All files",
        "*.*", "*"));
    List<File> files = fileDialog.showOpenMultipleDialog(view);
    if (files != null)
    {
      // Unbind properties before use
      unbindProperty(view.progressProperty());
      unbindProperty(view.statusProperty());

      if (files.size() == 1)
      {
        view.getProgressBar().setProgress(ProgressBar.INDETERMINATE_PROGRESS);
      }

      scoreFilesAsync(view, files, allowSlowAnalysis, false);
    }
  }

  public static void promptAndLoadFolder(UIView view, boolean allowSlowAnalysis)
  {
    DirectoryChooser folderDialog = new DirectoryChooser();
    folderDialog.setTitle("Add Executable Files in Folder");
    File folder = folderDialog.showDialog(view);
    if (folder != null)
    {
      scoreFolderAsync(view, folder, allowSlowAnalysis);
    }
  }

  public static void removeSelectedScoreItem(UIView view)
  {
    MultipleSelectionModel<UIView.ScoreItem> selectionModel =
      view.getFileListView().getSelectionModel();
    if (selectionModel.getSelectedIndex() != -1)
    {
      view.getFileListView().getItems().remove(
        selectionModel.getSelectedIndex());
      // If a new item isn't selected, be sure to clear out the displayed
      // analysis information.
      if (selectionModel.getSelectedIndex() == -1)
      {
        clearScoreItemSelection(view);
      }
    }
  }

  // Runs the given Runnable object on the UI thread if safe to do so or places
  // it in the UI execution queue to be run on the UI thread with an option to
  // wait for completion
  public static void runOnUI(Runnable action, long timeout, TimeUnit timeUnit)
  {
    if (action == null)
    {
      throw new NullPointerException("action");
    }

    if (Platform.isFxApplicationThread())
    {
      // We're executing in the UI thread, so run synchronously.
      action.run();
    }
    else
    {
      // We're executing on a different thread.
      FutureTask<?> future = new FutureTask<>(action, null);
      Platform.runLater(future);
      try
      {
        if (timeout > 0)
        {
          future.get(timeout, timeUnit);
        }
        else
        {
          future.get();
        }
      }
      catch (InterruptedException | ExecutionException | TimeoutException e)
      {
        // Ignore the error
      }
      finally
      {
        future.cancel(true);
      }
    }
  }

  public static void runOnUI(Runnable action, long timeoutNanoSeconds)
  {
    runOnUI(action, timeoutNanoSeconds, TimeUnit.NANOSECONDS);
  }

  public static void runOnUI(Runnable action)
  {
    runOnUI(action, 0L, TimeUnit.NANOSECONDS);
  }

  // Loads the analysis results from the given UIView.ScoreItem object into
  // the appropriate display controls.
  public static void selectScoreItem(UIView view, UIView.ScoreItem scoreItem)
  {
    if (scoreItem != null)
    {
      Path path = Paths.get(scoreItem.getFilePath());
      view.getMalwareScoreText().setText(
        String.format("File: %s\nDirectory: %s\n\n%s",
          path.getFileName(), path.getParent(),
          scoreItem.getScore().toReportString(false)));
      view.getImportsTableView().setItems(scoreItem.getImportItems());
      view.getSectionsTableView().setItems(scoreItem.getSectionItems());
    }
  }

  // Terminates all running tasks
  public static void stopAllTasks()
  {
    if (!RunningTaskMap.isEmpty())
    {
      for (Thread thread : RunningTaskMap.values())
      {
        if (thread.isAlive() && !thread.isInterrupted())
        {
          thread.interrupt();
        }
      }
    }
  }

  // Utility function for converting an argument string into a compatible
  // regular expression in the form of "--arg-name" or "-arg-name"
  private static String asArg(String s)
  {
    if (s == null)
    {
      return "";
    }

    return String.format("^\\-{1,2}%s$", Pattern.quote(s));
  }

  // Utility function for binding a property whether or not it is already bound
  private static <T> void bindProperty(Property<T> property,
    ObservableValue<T> value)
  {
    runOnUI(() ->
    {
      unbindProperty(property);
      property.bind(value);
    });
  }

  // Utility for binding the progress and message properties of a task to the
  // UI's progress and status bars
  private static void bindTaskUpdates(UIView view, Task<?> task)
  {
    bindProperty(view.progressProperty(), task.progressProperty());
    bindProperty(view.statusProperty(), task.messageProperty());
  }

  // Utility for binding the progress and message properties of one task to the
  // progress and message properties of another task
  private static void bindTaskUpdates(Task<?> sourceTask, Task<?> destTask)
  {
  }

  private static PortableExecutableFileChannel loadPortableExecutable(File file)
    throws IOException, EndOfStreamException, BadExecutableFormatException
  {
    PortableExecutableFileChannel peFile = null;
    if (StringUtil.isMatch(file.getName(), ".*\\.[bB]64$"))
    {
      // Try to load this as base64 data.
      try
      {
        Path filePath = Paths.get(file.getAbsoluteFile().toURI());
        String encodedData = new String(Files.readAllBytes(filePath),
          StandardCharsets.UTF_8);
        peFile = PortableExecutableFileChannel.fromBase64(encodedData);
        return peFile;
      }
      catch (IllegalArgumentException e)
      {
        // Not base64 encoded; continue on to try to load it the normal way.
      }
    }

    peFile = PortableExecutableFileChannel.create(file);
    return peFile;
  }

  private static <T1, T2> Map.Entry<T1, T2> makePair(T1 item1, T2 item2)
  {
    return new AbstractMap.SimpleEntry<>(item1, item2);
  }

  private static Map.Entry<Map<String, String>, List<String>> parseFlagArgs(
    List<String> unnamedArgs, String... argNames)
  {
    List<String> resultArgs = new ArrayList<>(unnamedArgs);
    Map<String, String> parsed = new TreeMap<>();
    for (String argName : argNames)
    {
      String argMatch = resultArgs.stream()
        .filter(a -> Pattern.matches(asArg(argName), a))
        .findFirst()
        .orElse(null);
      if (argMatch != null)
      {
        resultArgs = resultArgs.stream()
          .filter(a -> !a.equals(argMatch))
          .collect(Collectors.toList());
        parsed.put(argName, argMatch);
      }
    }

    return new AbstractMap.SimpleEntry<>(parsed, resultArgs);
  }

  private static UIView.ScoreItem readAndScoreExecutableFile(UIView view,
    File file, boolean allowSlowAnalysis,
    Consumer<? super String> statusCallback)
  {
    String errorTitle = null;
    String errorMessage = null;
    UIView.ScoreItem scoreItem = null;
    PortableExecutableFileChannel peFile = null;

    try
    {
      peFile = loadPortableExecutable(file);
      PortableExecutableScore score =
        new PortableExecutableScore(peFile, allowSlowAnalysis, statusCallback);
      scoreItem = view.new ScoreItem(score, file.getAbsolutePath());
    }
    catch (ClosedByInterruptException e)
    {
      // Processing was stopped; not an error so don't do anything special.
      scoreItem = null;
    }
    catch (IOException e)
    {
      errorMessage = String.format("Couldn't load file \"%s\": %s",
        file.getAbsolutePath(), e.getMessage());
      errorTitle = "File Read Error";
      scoreItem = view.new ScoreItem(e, errorTitle, errorMessage, file);
    }
    catch (EndOfStreamException e)
    {
      errorMessage = String.format(
        "The file \"%s\" is smaller than expected and cannot be read.",
        file.getAbsolutePath());
      errorTitle = "File Too Small";
      scoreItem = view.new ScoreItem(e, errorTitle, errorMessage, file);
    }
    catch (BadExecutableFormatException e)
    {
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

  private static Map.Entry<Future<?>, AnalyzeFilesTask> scoreFilesAsync(
    UIView view, List<File> files, boolean allowSlowAnalysis,
    boolean ignoreErrors)
  {
    AnalyzeFilesTask task =
      new AnalyzeFilesTask(view, files, allowSlowAnalysis, ignoreErrors);

    bindProperty(view.progressProperty(), task.progressProperty());
    bindProperty(view.statusProperty(), task.messageProperty());

    task.setOnFailed(event ->
      runOnUI(() ->
      {
        File failedFile =
          ((AnalyzeFilesTask) event.getSource()).getCurrentFile();
        unbindProperty(view.progressProperty());
        unbindProperty(view.statusProperty());
        Throwable e = event.getSource().getException();
        String errorMessage = "File analysis failed.";
        if (failedFile != null)
        {
          errorMessage = "File analysis failed while processing \"" +
            failedFile.getAbsolutePath() + "\".";
        }

        if (e != null)
        {
          if (!StringUtil.isNullOrWhiteSpace(e.getMessage()))
          {
            errorMessage += " " + e.getMessage();
          } else
          {
            errorMessage += " Encountered an exception of type " +
              e.getClass().getSimpleName() + '.';
            e.printStackTrace();
          }
        }

        UIView.showError(errorMessage, "Analysis Failure");
        view.statusProperty().setValue(errorMessage);
        view.getProgressBar().setProgress(0.0);
      }));

    Future<?> future = TaskPool.submit(task);
    return makePair(future, task);
  }

  private static Map.Entry<Future<?>, Task<?>> scoreFolderAsync(UIView view,
    File folder, boolean allowSlowAnalysis)
  {
    // Unbind properties before use
    unbindProperty(view.getProgressBar().progressProperty());
    unbindProperty(view.statusProperty());

    runOnUI(() ->
      view.getProgressBar().setProgress(ProgressBar.INDETERMINATE_PROGRESS));

    Task<List<File>> walkFilesTask = new Task<List<File>>()
    {
      @Override
      protected List<File> call() throws Exception
      {
        List<File> fileList = new ArrayList<>();
        Files.walkFileTree(Paths.get(folder.toURI()),
          new SimpleFileVisitor<Path>()
          {
            @Override
            public FileVisitResult visitFile(Path file,
              BasicFileAttributes attrs)
            {
              if (attrs.isRegularFile() && Files.isReadable(file))
              {
                fileList.add(new File(file.toUri()));
                updateMessage(String.format("Gathering files (%s) ...",
                  file.toAbsolutePath()));
              }

              return FileVisitResult.CONTINUE;
            }
          });

        return fileList;
      }
    };

    walkFilesTask.setOnFailed(event ->
    {
      unbindProperty(view.statusProperty());
      unbindProperty(view.progressProperty());

      UIView.showError(
        String.format("Failed to read all of \"%s\".",
          folder.getAbsolutePath()), "Directory Read Failed");
    });

    walkFilesTask.setOnSucceeded(event ->
    {
      unbindProperty(view.progressProperty());
      List<File> files = walkFilesTask.getValue();
      view.getProgressBar().setProgress(0.0);
      scoreFilesAsync(view, files, allowSlowAnalysis, true);
    });

    bindProperty(view.statusProperty(), walkFilesTask.messageProperty());
    Future<?> future = TaskPool.submit(walkFilesTask);
    return makePair(future, walkFilesTask);
  }

  // Utility function for showing a warning alert for missing file or folder
  // paths
  private static void showPathsNotFound(Collection<String> notFoundPaths)
  {
    if (notFoundPaths != null && !notFoundPaths.isEmpty())
    {
      UIView.showError("The following paths were not found:\n\n" +
        String.join("\n", notFoundPaths), "Paths Not Found");
    }
  }

  // Utility method for unbinding a property only if it's already bound
  private static void unbindProperty(Property<?> property)
  {
    runOnUI(() ->
    {
      if (property.isBound())
      {
        property.unbind();
      }
    });
  }
}
