package minesweeper.fx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import minesweeper.game.Config;
import minesweeper.game.Difficulty;
import minesweeper.io.IO;
import minesweeper.statistics.GameData;

import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static minesweeper.MinesweeperFX.STYLE;

public class DialogStats extends Dialog<byte[]> {
	
	private final IntegerProperty difficultyIndex = new SimpleIntegerProperty();
	private final IntegerProperty flagIndex       = new SimpleIntegerProperty();

	protected TabsPane tabsPane = new TabsPane(difficultyIndex, flagIndex, widthProperty());
	
	protected DialogStats() {
		Label label = new Label();
		Statistics stats = new Statistics(difficultyIndex, flagIndex);
		Table table = new Table(difficultyIndex, flagIndex, label.textProperty());
		VBox tile = new VBox(tabsPane, stats, label);
		
		Difficulty.listen((a, b, c) -> difficultyIndex.set(c.id));
		Config.boolListen("No Flagging", (a, b, c) -> flagIndex.set(c ? 1 : 0));
		
		setTitle("Profile : " + IO.MAIN_PROFILE);
		setOnHidden(e -> tabsPane.getSelectionModel().select(-1));
		widthProperty().addListener((a, b, c) -> {
			if(getDialogPane().getScene() != null)
				getDialogPane().getScene().getWindow().centerOnScreen();
		});
		initModality(Modality.WINDOW_MODAL);
		getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CLOSE);
		getDialogPane().setStyle(STYLE);
		getDialogPane().setOnKeyPressed(tabsPane.getOnKeyPressed());
		getDialogPane().setContent(tile);
		getDialogPane().getScene().getWindow().setOnShowing(e -> {
			difficultyIndex.set(Difficulty.id());
			flagIndex.set(Config.bool("No Flagging") ? 1 : 0);
		});
		setResultConverter(
				c -> c.getButtonData() == CANCEL_CLOSE ? null : table.getSelectionModel().getSelectedItem().board);
		
		label.setTextFill(Color.GRAY);
		label.setPadding(new Insets(5));
		label.setMinHeight(60);
		label.setMaxHeight(60);
		
		Button button = (Button)getDialogPane().lookupButton(ButtonType.APPLY);
		button.setText("Load Board");
		button.setVisible(false);
		
		table.getSelectionModel().selectedItemProperty()
			 .addListener((a, b, c) -> button.setDisable(a.getValue() == null || a.getValue().board.length == 0));
		tabsPane.getSelectionModel().selectedIndexProperty().addListener((a, b, c) -> {
			if(c.intValue() == 0) {
				tile.getChildren().remove(table);
				tile.getChildren().add(1, stats);
				button.setVisible(false);
				setWidth(516);
			} else if(c.intValue() == 1) {
				tile.getChildren().remove(stats);
				tile.getChildren().add(1, table);
				GameData gd = table.getSelectionModel().getSelectedItem();
				button.setVisible(true);
				button.setDisable(gd == null || gd.board.length == 0);
				setWidth(1026);
			}
		});
	}
}
