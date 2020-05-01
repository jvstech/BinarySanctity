//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           UIView.java
//! @description    Contains logic and properties used to create the primary
//!                 window and controls for display and use.
//!

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;

public class UIView extends Stage
{
  // Nested class for combining a PortableExecutableScore with an executable
  // file name as a ListView item
  public class ScoreItem
  {
    private PortableExecutableScore score_;
    private String filePath_;
    private String fileName_;
    private Exception exception_;
    private String errorTitle_;
    private String errorMessage_;

    public ScoreItem(PortableExecutableScore score, String filePath)
    {
      score_ = score;
      filePath_ = filePath;
      File file = new File(filePath_);
      fileName_ = file.getName();
      exception_ = null;
      errorTitle_ = null;
      errorMessage_ = null;
    }

    public ScoreItem(Exception ex, String errorTitle, String errorMessage,
      File file)
    {
      score_ = null;
      filePath_ = file.getAbsolutePath();
      fileName_ = file.getName();
      exception_ = ex;
      errorTitle_ = errorTitle;
      errorMessage_ = errorMessage;
    }

    public PortableExecutableScore getScore()
    {
      return score_;
    }

    public String getScoreText()
    {
      if (score_ != null && exception_ == null)
      {
        return String.format("%d", score_.getValue());
      }

      return "!";
    }

    public String getFilePath()
    {
      return filePath_;
    }

    public boolean hasException()
    {
      return (exception_ != null);
    }

    public Exception getException()
    {
      return exception_;
    }

    public String getErrorTitle()
    {
      return errorTitle_;
    }

    public String getErrorMessage()
    {
      return errorMessage_;
    }

    @Override
    public String toString()
    {
      return fileName_;
    }
  }

  // Nested class for formatting the display of a ScoreItem in a ListView
  private class ScoreItemListCell extends ListCell<ScoreItem>
  {
    private HBox content_;
    private Label scoreText_;
    private VBox textLayout_;
    private Label nameText_;
    private Label pathText_;

    public ScoreItemListCell()
    {
      super();
      scoreText_ = new Label();
      scoreText_.getStyleClass().add("score-item-value");
      nameText_ = new Label();
      pathText_ = new Label();
      pathText_.getStyleClass().add("score-item-path");
      textLayout_ = new VBox(nameText_, pathText_);
      content_ = new HBox(scoreText_, textLayout_);
      content_.setAlignment(Pos.CENTER_LEFT);
      HBox.setHgrow(textLayout_, Priority.ALWAYS);
    }

    @Override
    protected void updateItem(ScoreItem item, boolean empty)
    {
      super.updateItem(item, empty);
      if (item != null && !empty)
      {
        scoreText_.setText(String.format("%d", item.getScore().getValue()));
        nameText_.setText(item.toString());
        pathText_.setText(item.getFilePath());
        if (item.getScore().isSoftMalwareIndication())
        {
          nameText_.getStyleClass().add("score-item-malware");
        }

        setGraphic(content_);
      }
      else
      {
        setGraphic(null);
      }
    }
  }

  public static final int WIDTH = 800;
  public static final int HEIGHT = 600;

  // Containers
  private Scene scene_;
  private VBox layout_;
  private FlowPane buttonBar_;
  private SplitPane splitPane_;
  private TabPane tabPane_;

  // Buttons
  private Button addFileButton_;
  private Button addFolderButton_;
  private Button removeButton_;
  private Button clearButton_;

  // "Analyze text" checkbox
  private CheckBox analyzeTextCheckBox_;

  // File ListView
  private ListView<ScoreItem> fileListView_;

  // Status bar
  private HBox statusBox_;
  private ProgressBar progressBar_;
  private Label statusText_;


  public UIView()
  {
    super();
    initComponents();
  }

  public Button getAddFileButton()
  {
    return addFileButton_;
  }

  public Button getAddFolderButton()
  {
    return addFolderButton_;
  }

  public Button getRemoveButton()
  {
    return removeButton_;
  }

  public Button getClearButton()
  {
    return clearButton_;
  }

  public ListView<ScoreItem> getFileListView()
  {
    return fileListView_;
  }

  public ProgressBar getProgressBar()
  {
    return progressBar_;
  }

  public String getStatusText()
  {
    return statusText_.getText();
  }

  public UIView setStatusText(String statusText)
  {
    statusText_.setText(statusText);
    return this;
  }

  public StringProperty statusProperty()
  {
    return statusText_.textProperty();
  }

  private void initComponents()
  {
    // Set up the vertically-based layout
    layout_ = new VBox(5);
    layout_.setPadding(new Insets(5));
    layout_.setAlignment(Pos.CENTER);

    createButtonBar();
    createSplitPane();
    createStatusBar();

    VBox.setVgrow(splitPane_, Priority.ALWAYS);
    scene_ = new Scene(layout_, WIDTH, HEIGHT);
    //scene_.getStylesheets().add(
    //  getClass().getResource("/UIStyle.css").toExternalForm());
    stylize(scene_);
    setTitle("Binary Sanctity");
    setScene(scene_);
  }

  private void createButtonBar()
  {
    buttonBar_ = new FlowPane(Orientation.HORIZONTAL);
    buttonBar_.setPadding(new Insets(5));
    buttonBar_.setHgap(5.0);
    layout_.getChildren().add(buttonBar_);

    // "Add File" button
    addFileButton_ = new Button("Add File");
    addFileButton_.setOnAction(event -> UICommands.promptAndLoadFiles(this));
    buttonBar_.getChildren().add(addFileButton_);

    // "Add Folder" button
    addFolderButton_ = new Button("Add Folder");
    buttonBar_.getChildren().add(addFolderButton_);

    // "Remove" button
    removeButton_ = new Button("Remove");
    buttonBar_.getChildren().add(removeButton_);

    // "Clear" button
    clearButton_ = new Button("Clear");
    buttonBar_.getChildren().add(clearButton_);

    // "Analyze text" checkbox (might as well put it here since it's part of the
    // button bar)
    analyzeTextCheckBox_ = new CheckBox("Analyze text");
    analyzeTextCheckBox_.setId("analyze-text-checkbox");
    analyzeTextCheckBox_.setSelected(true);
    buttonBar_.getChildren().add(analyzeTextCheckBox_);
  }

  private void createFileView()
  {
    fileListView_ = new ListView<>();
    fileListView_.setCellFactory(param -> new ScoreItemListCell());
    splitPane_.getItems().add(0, fileListView_);
  }

  private void createSplitPane()
  {
    splitPane_ = new SplitPane();
    createFileView();
    createTabPane();
    layout_.getChildren().add(splitPane_);
  }

  private void createStatusBar()
  {
    progressBar_ = new ProgressBar(0.0);
    statusText_ = new Label("Ready");
    statusText_.getStyleClass().add("status-label");
    statusBox_ = new HBox(progressBar_, statusText_);
    statusBox_.getStyleClass().add("status-bar");
    statusBox_.setSpacing(5.0);
    statusBox_.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(statusText_, Priority.ALWAYS);
    layout_.getChildren().add(statusBox_);
  }

  private void createTabPane()
  {
    tabPane_ = new TabPane();
    tabPane_.getStyleClass().add("floating");

    Tab scoreTab = new Tab("Malware Score");
    scoreTab.setClosable(false);
    tabPane_.getTabs().add(scoreTab);

    Tab importsTab = new Tab("Imports");
    importsTab.setClosable(false);
    tabPane_.getTabs().add(importsTab);

    Tab sectionNamesTab = new Tab("Section Names");
    sectionNamesTab.setClosable(false);
    tabPane_.getTabs().add(sectionNamesTab);

    splitPane_.getItems().add(1, tabPane_);
  }

  public static void stylize(Scene scene)
  {
    scene.getStylesheets().add(
      UIView.class.getResource("/UIStyle.css").toExternalForm());
  }

  public static void showError(String errorMessage, String errorTitle)
  {
    Alert errorAlert = new Alert(Alert.AlertType.WARNING);
    errorAlert.setTitle(errorTitle);
    errorAlert.setContentText(errorMessage);
    errorAlert.showAndWait();
  }
}
