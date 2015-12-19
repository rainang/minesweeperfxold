package minesweeper.fx;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import minesweeper.MinesweeperFX;
import minesweeper.game.Game;

import static minesweeper.game.GameState.*;

public class Dialog {

	private static final Changelog CHANGELOG = new Changelog();
	private static final GameOver  GAME_OVER = new GameOver();

	protected static final DialogStats STATS = new DialogStats();

	public static void showStats(int i) {
		STATS.tabsPane.getSelectionModel().select(i);
		if(STATS.isShowing())
			STATS.getDialogPane().getScene().getWindow().requestFocus();
		else
			STATS.showAndWait().ifPresent(c -> Game.get().board.fromBytes(c));
	}

	public static void closeAll() {
		STATS.close();
		GAME_OVER.close();
		CHANGELOG.close();
	}

	public static void showChangeLog() {
		CHANGELOG.showAndWait();
	}

	public static void showGameOver() {
		GAME_OVER.setTitle(getGameState() == WIN ? "Game Won" : "Game Lost");
		GAME_OVER.showAndWait().ifPresent(c -> {
			switch(c.getText()) {
			case "New":
				newGame();
				break;
			case "Restart":
				restart();
				break;
			case "Statistics":
				showStats(0);
				break;
			case "Games":
				showStats(1);
				break;
			case "Save Board":
				MinesweeperFX.saveBoard();
				break;
			}
		});
	}

	private static class GameOver extends javafx.scene.control.Dialog<ButtonType> {

		public GameOver() {
			((Stage)getDialogPane().getScene().getWindow()).getIcons().add(MinesweeperFX.ICON);
			getDialogPane().setStyle(MinesweeperFX.STYLE);
			initModality(Modality.APPLICATION_MODAL);
			getDialogPane().getChildren().removeIf(e -> e instanceof Label);
			getDialogPane().getButtonTypes().addAll(new ButtonType("New", ButtonBar.ButtonData.FINISH),
													new ButtonType("Restart", ButtonBar.ButtonData.FINISH),
													new ButtonType("Statistics", ButtonBar.ButtonData.LEFT),
													new ButtonType("Games", ButtonBar.ButtonData.LEFT),
													new ButtonType("Save Board", ButtonBar.ButtonData.RIGHT));
		}
	}

	private static class Changelog extends javafx.scene.control.Dialog {

		public Changelog() {
			getDialogPane().setStyle(MinesweeperFX.STYLE);
			getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
			setTitle("MinesweeperFX Changelog");
			setContentText("v2.4.0\n" +
								   "changes:\n" +
								   "\tadd about menu\n" +
								   "\tadd changelog\n" +
								   "\n" +
								   "v2.3.1\n" +
								   "bug fixes:\n" +
								   "\tfix table dates not updating on format change\n" +
								   "\n" +
								   "v2.3.0\n" +
								   "changes:\n" +
								   "\tremove old load methods\n" +
								   "\n" +
								   "bug fixes:\n" +
								   "\tfix time format not showing on startup\n" +
								   "\tfix date not sorting correctly");
		}
	}
}
