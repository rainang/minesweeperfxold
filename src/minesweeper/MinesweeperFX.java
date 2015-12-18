package minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import minesweeper.fx.*;
import minesweeper.game.Config;
import minesweeper.game.Difficulty;
import minesweeper.game.Game;
import minesweeper.game.GameState;
import minesweeper.io.IO;
import minesweeper.statistics.Profile;

public class MinesweeperFX extends Application {

	public static final String VERSION = "2.2.0";

	public static final String STYLE = "-fx-font-family: \"Calibri\"; -fx-alignment: CENTER";

	public static final Image ICON = new Image(MinesweeperFX.class.getResourceAsStream("bomb.png"));

	private static final int TITLE_BAR_HEIGHT = 23;
	private static final int STAGE_INSETS     = 3;

	private static boolean initialized;

	private static Window window;

	public static void main(String[] args) {
		Config.init();
		IO.init();
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		new Game();
		MenuBar menuBar = new MenuBar();
		DataPanel dataPanel = new DataPanel();
		TilePane tilePane = new TilePane();

		RootPane root = new RootPane(dataPanel, tilePane);

		Scene scene = new Scene(new VBox(menuBar, root));

		window = scene.getWindow();

		scene.setOnKeyPressed(tilePane::handleKeyInput);

		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("MinesweeperFX v" + VERSION);
		stage.getIcons().add(ICON);
		stage.focusedProperty().addListener((a, b, c) -> GameState.pause(true));
		stage.iconifiedProperty().addListener((a, b, c) -> GameState.pause(true));
		stage.setOnShowing(e -> initialize());
		stage.setOnCloseRequest(e -> close());

		Difficulty.listen((a, b, c) -> setStageSize(stage, menuBar, root));
		Config.numberListen("Resolution", (a, b, c) -> setStageSize(stage, menuBar, root));
		stage.show();
	}

	private void initialize() {
		if(!initialized) {
			Config.load();
			Profile.load();
			Formatter.init();
			initialized = true;
		}
	}

	private void close() {
		Config.save();
		Dialog.closeAll();
	}

	public static void saveBoard() {
		Game.get().board.write(IO.FILE_CHOOSER.showSaveDialog(window));
	}

	public static void openBoard() {
		Game.get().board.read(IO.FILE_CHOOSER.showOpenDialog(window));
	}

	private void setStageSize(Stage stage, MenuBar menuBar, RootPane root) {
		double width = root.getWidth();
		double height = root.getHeight() + menuBar.getHeight();
		stage.setWidth(width + STAGE_INSETS*2);
		stage.setHeight(height + STAGE_INSETS*2 + TITLE_BAR_HEIGHT);
		stage.centerOnScreen();

		GameState.newGame();
	}
}