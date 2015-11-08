package minesweeperfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import minesweeperfx.control.DataPanel;
import minesweeperfx.control.Grid;
import minesweeperfx.control.MenuBar;
import minesweeperfx.logic.Game;

import static minesweeperfx.Options.*;
import static minesweeperfx.logic.FSM.*;

public class MinesweeperFX extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	public static MinesweeperFX INSTANCE;

	public final Game GAME = new Game();

	public MenuBar MENU_BAR;

	public GridPane  TABLE;
	public DataPanel DATA_PANEL;
	public Grid      GRID;

	public Scene SCENE;
	
	@Override
	public void start(Stage stage) throws Exception {
		INSTANCE = this;
		MENU_BAR = new MenuBar();
		TABLE = new GridPane();
		DATA_PANEL = new DataPanel();
		GRID = new Grid();
		SCENE = new Scene(new VBox(MENU_BAR, TABLE));
		initMenu();

		stage.setOnShowing(e -> Config.loadSettings());
		stage.setOnCloseRequest(e -> Config.saveSettings());
		getTileSizeProperty().addListener((a, b, c) -> adjustStageSize(stage));
		getDifficultyProperty().addListener((a, b, c) -> adjustStageSize(stage));
		
		TABLE.add(DATA_PANEL.getBorderedPane(), 0, 0);
		TABLE.add(GRID.get(), 0, 1);
		TABLE.setVgap(BORDER_PADDING);
		TABLE.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
		TABLE.setPadding(new Insets(BORDER_PADDING));
		
		stage.setScene(SCENE);
		stage.setResizable(false);
		stage.setTitle("MinesweeperFX v1.0.1");
		stage.show();
	}
	
	private void adjustStageSize(Stage stage) {
		double width = 2*(BORDER_SIZE - 1) + 1;
		double height = 2*(BORDER_SIZE - 1) + 1;
		switch(getDifficulty()) {
		default:
			width += 9*getTileSize();
			height += 9*getTileSize();
			break;
		case 1:
			width += 16*getTileSize();
			height += 16*getTileSize();
			break;
		case 2:
			width += 30*getTileSize();
			height += 16*getTileSize();
			break;
		}
		stage.setWidth(BORDER_PADDING*2 + width + 6);
		stage.setHeight(BORDER_PADDING*3 + height + 29 + 25 +
								DATA_PANEL.getBorderedPane().border.getHeight());
		stage.centerOnScreen();
	}

	public void initMenu() {
		MENU_BAR.slider.valueProperty().bindBidirectional(getTileSizeProperty());

		MENU_BAR.newGame.setOnAction(a -> GAME_STATE.doIt(GAME_NEW));
		MENU_BAR.restartGame.setOnAction(a -> GAME_STATE.doIt(GAME_RESTART));
		MENU_BAR.pauseGame.setOnAction((a) -> GAME_STATE.doIt(PAUSE_GAME));
		GAME.getPauseProperty().addListener((q,b,c)->GRID.setVisible(!c));

		MENU_BAR.nf.selectedProperty().addListener((a, b, c) -> {
			MENU_BAR.showFlags.setDisable(MENU_BAR.nf.isSelected());
			MENU_BAR.showFlags.setSelected(!MENU_BAR.nf.isSelected());
		});
		MENU_BAR.nf.selectedProperty().bindBidirectional(getNFProperty());
		MENU_BAR.showFlags.selectedProperty().bindBidirectional(getShowFlagCountProperty());
		MENU_BAR.showTimer.selectedProperty().bindBidirectional(getShowTimerProperty());

		MENU_BAR.difficulty[0].setOnAction(a -> setDifficulty(0));
		MENU_BAR.difficulty[1].setOnAction(a -> setDifficulty(1));
		MENU_BAR.difficulty[2].setOnAction(a -> setDifficulty(2));

		for(RadioMenuItem item : MENU_BAR.difficulty)
			item.selectedProperty().addListener((a, b, c) -> {
				if(!c && MENU_BAR.difficulty[getDifficulty()] == item)
					item.setSelected(true);
			});

		Options.getDifficultyProperty().addListener((a, b, c) -> {
			GAME.board.update();
			GAME_STATE.doIt(GAME_NEW);
			if(b.intValue() != -1)
				MENU_BAR.difficulty[b.intValue()].setSelected(false);
			MENU_BAR.difficulty[c.intValue()].setSelected(true);
			GRID.update();
		});
		Grid.MOUSE_POS.addListener((a, b, c) -> GAME.setFocus(GAME.board.getTile(c.tileX, c.tileY)));
	}

	public static Game getGame() {
		return INSTANCE.GAME;
	}
}