//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           UIView.java
//! @description    Contains logic and properties used to create the primary
//!                 window and controls for display and use.
//!

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UIView extends Stage
{
  // Nested class for combining an import name or ordinal with its associated
  // library
  public class ImportItem
  {
    private final SimpleStringProperty name_;
    private final SimpleStringProperty library_;

    public ImportItem(String name, String library)
    {
      name_ = new SimpleStringProperty(name);
      library_ = new SimpleStringProperty(library);
    }

    public String getLibrary()
    {
      return library_.get();
    }

    public String getName()
    {
      return name_.get();
    }

    public StringProperty libraryProperty()
    {
      return library_;
    }

    public StringProperty nameProperty()
    {
      return name_;
    }

    @Override
    public String toString()
    {
      return String.format("%s!%s", getLibrary(), getName());
    }
  }

  // Nested class for display formatting of section permissions
  public class SectionPermissions implements Comparable<SectionPermissions>
  {
    private final int value_;

    public SectionPermissions()
    {
      value_ = 0;
    }

    public SectionPermissions(int value)
    {
      value_ = value;
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o)
      {
        return true;
      }

      if (!(o instanceof SectionPermissions))
      {
        return false;
      }

      SectionPermissions rhs = (SectionPermissions)o;
      return (value_ == rhs.value_);
    }

    @Override
    public int hashCode()
    {
      return value_;
    }

    @Override
    public int compareTo(SectionPermissions rhs)
    {
      return Integer.compare(value_, rhs.value_);
    }

    @Override
    public String toString()
    {
      return String.format("0x%x (%s)", value_,
        String.join(" ",
          SectionCharacteristicTypes.getSimpleStrings(value_)));
    }
  }

  // Nested class for hex display formatting of integer/long values
  public class HexValue implements Comparable<HexValue>
  {
    private final long value_;

    public HexValue(long value)
    {
      value_ = value;
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o)
      {
        return true;
      }

      if (!(o instanceof HexValue))
      {
        return false;
      }

      HexValue rhs = (HexValue)o;
      return (value_ == rhs.value_);
    }

    @Override
    public int hashCode()
    {
      return Long.hashCode(value_);
    }

    @Override
    public int compareTo(HexValue rhs)
    {
      return Long.compare(value_, rhs.value_);
    }

    @Override
    public String toString()
    {
      return String.format("0x%x", value_);
    }
  }

  // Nested class holding displayable information about a PE section
  public class SectionItem
  {
    private final SimpleStringProperty name_;
    private final ObservableValue<SectionPermissions> permissions_;
    private final SimpleIntegerProperty virtualSize_;
    private final SimpleIntegerProperty rawSize_;
    private final ObservableValue<HexValue> virtualOffset_;
    private final ObservableValue<HexValue> fileOffset_;

    public SectionItem(SectionHeader section)
    {
      name_ = new SimpleStringProperty(
        String.format("\"%s\"", StringUtil.escapeFull(section.getName())));

      SectionPermissions permissions;
      try
      {
        permissions = new SectionPermissions(section.getCharacteristics());
      }
      catch (IOException | EndOfStreamException e)
      {
        permissions = new SectionPermissions();
      }

      int virtualSize = 0;
      try
      {
        virtualSize = section.getVirtualSize();
      }
      catch (IOException | EndOfStreamException e)
      {
      }

      int rawSize = 0;
      try
      {
        rawSize = section.getSizeOfRawData();
      }
      catch (IOException | EndOfStreamException e)
      {
      }

      HexValue virtualOffset;
      try
      {
        virtualOffset = new HexValue(section.getVirtualAddress());
      }
      catch (IOException | EndOfStreamException e)
      {
        virtualOffset = new HexValue(0);
      }

      HexValue fileOffset;
      try
      {
        fileOffset = new HexValue(section.getPointerToRawData());
      }
      catch (IOException | EndOfStreamException e)
      {
        fileOffset = new HexValue(0);
      }

      permissions_ = new ReadOnlyObjectWrapper<>(permissions);
      virtualSize_ = new SimpleIntegerProperty(virtualSize);
      rawSize_ = new SimpleIntegerProperty(rawSize);
      virtualOffset_ = new ReadOnlyObjectWrapper<>(virtualOffset);
      fileOffset_ = new ReadOnlyObjectWrapper<>(fileOffset);
    }

    public StringProperty nameProperty()
    {
      return name_;
    }

    public ObservableValue<SectionPermissions> permissionsProperty()
    {
      return permissions_;
    }

    public IntegerProperty virtualSizeProperty()
    {
      return virtualSize_;
    }

    public IntegerProperty rawSizeProperty()
    {
      return rawSize_;
    }

    public ObservableValue<HexValue> virtualOffsetProperty()
    {
      return virtualOffset_;
    }

    public ObservableValue<HexValue> fileOffsetProperty()
    {
      return fileOffset_;
    }
  }

  // Nested class for combining a PortableExecutableScore with an executable
  // file name as a ListView item
  public class ScoreItem
  {
    private final ObservableList<ImportItem> importItems_;
    private final ObservableList<SectionItem> sectionItems_;
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

      // Compose the import items
      List<ImportItem> importItems = new ArrayList<>();
      try
      {
        for (Map.Entry<String, String[]> imports :
          score_.getPEFile().getImportedNames().entrySet())
        {
          for (String importName : imports.getValue())
          {
            importItems.add(new ImportItem(importName, imports.getKey()));
          }
        }
      }
      catch (EndOfStreamException | IOException e)
      {
        e.printStackTrace();
      }

      importItems_ = FXCollections.observableArrayList(importItems);

      // Compose the section items
      List<SectionItem> sectionItems = new ArrayList<>();
      for (SectionHeader section : score_.getPEFile().getSections())
      {
        sectionItems.add(new SectionItem(section));
      }

      sectionItems_ = FXCollections.observableArrayList(sectionItems);
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
      importItems_ = FXCollections.observableArrayList();
      sectionItems_ = FXCollections.observableArrayList();
    }

    public String getErrorMessage()
    {
      return errorMessage_;
    }

    public String getErrorTitle()
    {
      return errorTitle_;
    }

    public Exception getException()
    {
      return exception_;
    }

    public String getFilePath()
    {
      return filePath_;
    }

    public ObservableList<ImportItem> getImportItems()
    {
      return importItems_;
    }

    public ObservableList<SectionItem> getSectionItems()
    {
      return sectionItems_;
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

    public boolean hasException()
    {
      return (exception_ != null);
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
      textLayout_.setAlignment(Pos.CENTER_LEFT);
      content_ = new HBox(scoreText_, textLayout_);
      content_.setAlignment(Pos.CENTER_LEFT);
      HBox.setHgrow(scoreText_, Priority.NEVER);
      HBox.setHgrow(textLayout_, Priority.ALWAYS);
    }

    @Override
    protected void updateItem(ScoreItem item, boolean empty)
    {
      super.updateItem(item, empty);
      nameText_.getStyleClass().clear();
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
  private Button cancelButton_;

  // "Analyze text" checkbox
  private CheckBox analyzeTextCheckBox_;

  // File ListView
  private ListView<ScoreItem> fileListView_;

  // Malware score results text box
  private TextArea malwareScoreText_;

  // Imports TableView
  private TableView<ImportItem> importsTableView_;

  // Sections TableView
  private TableView<SectionItem> sectionsTableView_;

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

  public Button getCancelButton()
  {
    return cancelButton_;
  }

  public Button getClearButton()
  {
    return clearButton_;
  }

  public ListView<ScoreItem> getFileListView()
  {
    return fileListView_;
  }

  public TableView<ImportItem> getImportsTableView()
  {
    return importsTableView_;
  }

  public TableView<SectionItem> getSectionsTableView()
  {
    return sectionsTableView_;
  }

  public TextArea getMalwareScoreText()
  {
    return malwareScoreText_;
  }

  public ProgressBar getProgressBar()
  {
    return progressBar_;
  }

  public Button getRemoveButton()
  {
    return removeButton_;
  }

  public DoubleProperty progressProperty()
  {
    return progressBar_.progressProperty();
  }

  public StringProperty statusProperty()
  {
    return statusText_.textProperty();
  }

  public static void showError(String errorMessage, String errorTitle)
  {
    Alert errorAlert = new Alert(Alert.AlertType.WARNING, errorMessage);
    errorAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    errorAlert.setTitle(errorTitle);
    errorAlert.showAndWait();
  }

  public static void stylize(Scene scene)
  {
    scene.getStylesheets().add(
      UIView.class.getResource("/UIStyle.css").toExternalForm());
  }

  private void createButtonBar()
  {
    buttonBar_ = new FlowPane(Orientation.HORIZONTAL);
    buttonBar_.setId("button-bar");
    layout_.getChildren().add(buttonBar_);

    // "Add File" button
    addFileButton_ = new Button("Add File");
    addFileButton_.setOnAction(event ->
      UICommands.promptAndLoadFiles(this, analyzeTextCheckBox_.isSelected()));
    buttonBar_.getChildren().add(addFileButton_);

    // "Add Folder" button
    addFolderButton_ = new Button("Add Folder");
    addFolderButton_.setOnAction(event ->
      UICommands.promptAndLoadFolder(this, analyzeTextCheckBox_.isSelected()));
    buttonBar_.getChildren().add(addFolderButton_);

    // "Remove" button
    removeButton_ = new Button("Remove");
    removeButton_.setOnAction(event ->
      UICommands.removeSelectedScoreItem(this));
    buttonBar_.getChildren().add(removeButton_);

    // "Clear" button
    clearButton_ = new Button("Clear");
    clearButton_.setOnAction(event -> UICommands.clearScoreItems(this));
    buttonBar_.getChildren().add(clearButton_);

    // "Cancel" button
    cancelButton_ = new Button("Cancel");
    cancelButton_.setOnAction(event -> UICommands.stopAllTasks());
    buttonBar_.getChildren().add(cancelButton_);

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
    fileListView_.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    fileListView_.getSelectionModel().selectedItemProperty().addListener(
      (observable, oldValue, newValue) ->
        UICommands.selectScoreItem(this, newValue));
    splitPane_.getItems().add(0, fileListView_);
  }

  private void createImportsTableView(Tab tab)
  {
    importsTableView_ = new TableView<ImportItem>();
    importsTableView_.setEditable(false);
    importsTableView_.setColumnResizePolicy(
      TableView.CONSTRAINED_RESIZE_POLICY);
    importsTableView_.setPlaceholder(new VBox());
    TableColumn<ImportItem, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
    TableColumn<ImportItem, String> libraryColumn =
      new TableColumn<>("Library");
    libraryColumn.setCellValueFactory(param ->
      param.getValue().libraryProperty());
    importsTableView_.getColumns().addAll(nameColumn, libraryColumn);
    GridPane pane = createExpandingGridPane();
    pane.add(importsTableView_, 0, 0);
    tab.setContent(pane);
  }

  private void createMalwareScoreText(Tab tab)
  {
    malwareScoreText_ = new TextArea();
    malwareScoreText_.getStyleClass().addAll("mono-text", "analysis-text");
    malwareScoreText_.setEditable(false);
    malwareScoreText_.setWrapText(false);
    GridPane pane = createExpandingGridPane();
    pane.add(malwareScoreText_, 0,0);
    tab.setContent(pane);
  }

  private void createSectionsTableView(Tab tab)
  {
    sectionsTableView_ = new TableView<SectionItem>();
    sectionsTableView_.setEditable(false);
    sectionsTableView_.setColumnResizePolicy(
      TableView.CONSTRAINED_RESIZE_POLICY);
    sectionsTableView_.setPlaceholder(new VBox());
    TableColumn<SectionItem, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
    TableColumn<SectionItem, SectionPermissions> permsColumn =
      new TableColumn<>("Permissions");
    permsColumn.setCellValueFactory(
      param -> param.getValue().permissionsProperty());
    TableColumn<SectionItem, Integer> virtSizeColumn =
      new TableColumn<>("Virtual Size");
    virtSizeColumn.getStyleClass().add("section-number");
    virtSizeColumn.setCellValueFactory(
      param -> param.getValue().virtualSizeProperty().asObject());
    TableColumn<SectionItem, Integer> rawSizeColumn =
      new TableColumn<>("Raw Size");
    rawSizeColumn.getStyleClass().add("section-number");
    rawSizeColumn.setCellValueFactory(
      param -> param.getValue().rawSizeProperty().asObject());
    TableColumn<SectionItem, HexValue> virtOffsetColumn =
      new TableColumn<>("Virtual Offset");
    virtOffsetColumn.setCellValueFactory(
      param -> param.getValue().virtualOffsetProperty());
    virtOffsetColumn.getStyleClass().add("section-number");
    TableColumn<SectionItem, HexValue> fileOffsetColumn =
      new TableColumn<>("File Offset");
    fileOffsetColumn.getStyleClass().add("section-number");
    fileOffsetColumn.setCellValueFactory(
      param -> param.getValue().fileOffsetProperty());
    sectionsTableView_.getColumns().addAll(nameColumn, permsColumn,
      virtSizeColumn, rawSizeColumn, virtOffsetColumn, fileOffsetColumn);
    GridPane pane = createExpandingGridPane();
    pane.add(sectionsTableView_, 0, 0);
    tab.setContent(pane);
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
    progressBar_.setMinWidth(Control.USE_PREF_SIZE);
    statusText_ = new Label("Ready");
    statusText_.getStyleClass().add("status-label");
    statusBox_ = new HBox(progressBar_, statusText_);
    statusBox_.getStyleClass().add("status-bar");
    statusBox_.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(statusText_, Priority.SOMETIMES);
    layout_.getChildren().add(statusBox_);
  }

  private void createTabPane()
  {
    tabPane_ = new TabPane();
    tabPane_.getStyleClass().add("floating");

    Tab scoreTab = new Tab("Malware Score");
    scoreTab.setClosable(false);
    createMalwareScoreText(scoreTab);
    tabPane_.getTabs().add(scoreTab);

    Tab importsTab = new Tab("Imports");
    importsTab.setClosable(false);
    createImportsTableView(importsTab);
    tabPane_.getTabs().add(importsTab);

    Tab sectionNamesTab = new Tab("Sections");
    sectionNamesTab.setClosable(false);
    createSectionsTableView(sectionNamesTab);
    tabPane_.getTabs().add(sectionNamesTab);

    splitPane_.getItems().add(1, tabPane_);
  }

  private void initComponents()
  {
    // Set up the vertically-based layout
    layout_ = new VBox(5);
    layout_.getStyleClass().add("root-container");

    createButtonBar();
    createSplitPane();
    createStatusBar();

    VBox.setVgrow(splitPane_, Priority.ALWAYS);
    scene_ = new Scene(layout_, WIDTH, HEIGHT);
    stylize(scene_);
    setTitle("Binary Sanctity");
    setScene(scene_);

    scene_.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST,
      UICommands::closeUI);
  }

  private static GridPane createExpandingGridPane()
  {
    GridPane pane = new GridPane();
    RowConstraints rowConstraints = new RowConstraints();
    rowConstraints.setFillHeight(true);
    rowConstraints.setVgrow(Priority.ALWAYS);
    pane.getRowConstraints().add(rowConstraints);
    ColumnConstraints columnConstraints = new ColumnConstraints();
    columnConstraints.setFillWidth(true);
    columnConstraints.setHgrow(Priority.ALWAYS);
    pane.getColumnConstraints().add(columnConstraints);
    return pane;
  }
}
