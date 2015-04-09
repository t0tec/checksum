package view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class TitlePanel extends StackPane {

  public TitlePanel(String title) {
    Label titleLabel = new Label();
    titleLabel.setText(title);
    titleLabel.getStyleClass().add("bordered-titled-title");
    StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
    getChildren().add(titleLabel);

    getStyleClass().add("bordered-titled-border");
  }

  public void setContent(Node panel) {
    panel.getStyleClass().add("bordered-titled-content");
    getChildren().add(panel);
  }
}
