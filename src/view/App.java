package view;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ChecksumFunction;

public class App extends Application {

  private static final ChecksumFunction[] CHECKSUM_TYPES =
      {ChecksumFunction.MD5, ChecksumFunction.SHA1, ChecksumFunction.SHA256, ChecksumFunction.SHA512};

  private MenuBar topMenu;
  private TextField fileNameTxtFld;
  private TextField checksumTxtFld;

  private ChecksumFunction selectedChecksumFunction;

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Checksum application");

    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(25, 25, 25, 25));

    Label fileNameLbl = new Label("File:");
    grid.add(fileNameLbl, 0, 1);

    fileNameTxtFld = new TextField();
    grid.add(fileNameTxtFld, 1, 1);

    Label checksumLbl = new Label("Checksum:");
    grid.add(checksumLbl, 0, 2);

    checksumTxtFld = new TextField();
    grid.add(checksumTxtFld, 1, 2);

    RadioBtnPanel radioBtnPanel = new RadioBtnPanel("Checksum type", CHECKSUM_TYPES);
    grid.add(radioBtnPanel, 1, 3);

    Button checkBtn = new Button();
    checkBtn.setText("Check");

    Button generateBtn = new Button();
    generateBtn.setText("Generate");

    BorderPane root = new BorderPane();
    VBox topContainer = new VBox();
    createMenu(primaryStage);
    topContainer.getChildren().add(topMenu);
    root.setTop(topContainer);

    VBox mainContainer = new VBox();
    mainContainer.getChildren().add(grid);

    HBox hbCheckBtn = new HBox(10);
    hbCheckBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbCheckBtn.getChildren().addAll(checkBtn, generateBtn);

    grid.add(hbCheckBtn, 1, 4);
    root.setCenter(mainContainer);

    Scene scene = new Scene(root, 500, 250);
    scene.getStylesheets().add("/style/main.css");

    radioBtnPanel.addListener(new ChangeListener<Toggle>() {
      @Override
      public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue,
                          Toggle newValue) {
        selectedChecksumFunction = ((ChecksumFunction) newValue.getUserData());
      }
    });

    checkBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if (fileNameTxtFld.getText().isEmpty()) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("File name is empty!");
          alert.setContentText("No file is chosen! Click on File -> Open file.");
          alert.show();
          return;
        }

        if (checksumTxtFld.getText().isEmpty()) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Checksum is empty!");
          alert.setContentText("Please enter the valid checksum!");
          alert.show();
          return;
        }

        if (selectedChecksumFunction == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("No checksum type selected!");
          alert.setContentText("Please select the type of the checksum!");
          alert.show();
          return;
        }

        File file = new File(fileNameTxtFld.getText().trim());

        if (!file.exists()) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("File does not exist!");
          alert.setContentText("The given file does not exist!");
          alert.show();
          return;
        }

        if (selectedChecksumFunction.generate(file)
            .equalsIgnoreCase(checksumTxtFld.getText().trim())) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("File is valid!");
          alert.setContentText("The file is valid! The checksums match each other!");
          alert.show();
          return;
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("File is not valid!");
          alert.setContentText("The file is not valid! The checksums do not match!");
          alert.show();
          return;
        }
      }
    });

    generateBtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if (fileNameTxtFld.getText().isEmpty()) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("File name is empty!");
          alert.setContentText("No file is chosen! Click on File -> Open file.");
          alert.show();
          return;
        }

        if (selectedChecksumFunction == null) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("No checksum type selected!");
          alert.setContentText("Please select the type of the checksum!");
          alert.show();
          return;
        }

        File file = new File(fileNameTxtFld.getText().trim());

        if (!file.exists()) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("File does not exist!");
          alert.setContentText("The given file does not exist!");
          alert.show();
          return;
        }

        checksumTxtFld.setText(selectedChecksumFunction.generate(file));
      }
    });

    primaryStage.setScene(scene);
    addDragNDrop(primaryStage);
    primaryStage.setResizable(false);
    primaryStage.show();
  }

  private void createMenu(final Stage stage) {
    topMenu = new MenuBar();

    Menu fileMenu = new Menu("File");

    MenuItem openFileMenu = createMenuItem("Open file", "Ctrl+o");
    openFileMenu.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
          fileNameTxtFld.setText(file.getAbsolutePath());
        }
      }
    });

    MenuItem exitMenu = createMenuItem("Exit", "Alt+x");
    exitMenu.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        Platform.exit();
      }
    });

    Menu helpMenu = new javafx.scene.control.Menu("Help");
    MenuItem about = createMenuItem("About", "F1");
    about.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(stage);

        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.getChildren().addAll(new Text("Developed by t0tec (t0tec.olmec@gmail.com)"));

        Scene myDialogScene = new Scene(box);

        dialog.setTitle("About");
        dialog.setScene(myDialogScene);
        dialog.show();
      }
    });

    fileMenu.getItems().addAll(openFileMenu, exitMenu);
    helpMenu.getItems().add(about);

    topMenu.getMenus().addAll(fileMenu, helpMenu);
  }

  private MenuItem createMenuItem(String text, String keyCombination) {
    MenuItem item = new MenuItem(text);
    item.setAccelerator(KeyCombination.keyCombination(keyCombination));
    return item;
  }

  private void addDragNDrop(final Stage stage) {
    stage.getScene().setOnDragOver(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
          event.acceptTransferModes(TransferMode.COPY);
        } else {
          event.consume();
        }
      }
    });

    // Dropping over surface
    stage.getScene().setOnDragDropped(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
          success = true;
          for (File file : db.getFiles()) {
            fileNameTxtFld.setText(file.getAbsolutePath());
          }
        }
        event.setDropCompleted(success);
        event.consume();
      }
    });
  }

  public static void main(String[] args) {
    launch(args);
  }
}
