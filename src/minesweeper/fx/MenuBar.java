package minesweeper.fx;

import java.util.Arrays;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import minesweeper.MinesweeperFX;
import minesweeper.game.Config;
import minesweeper.game.Difficulty;

import static minesweeper.game.GameState.*;

public class MenuBar extends javafx.scene.control.MenuBar {

	public MenuBar() {
		ObjectProperty<Difficulty> difficulty = new SimpleObjectProperty<>();
		Difficulty.bind(difficulty);
		RadioMenuItem[] difficulties = new RadioMenuItem[] {
				createRadioItem("Beginner", KeyCode.B, a -> difficulty.set(Difficulty.BEGINNER)),
				createRadioItem("Intermediate", KeyCode.I, a -> difficulty.set(Difficulty.INTERMEDIATE)),
				createRadioItem("Expert", KeyCode.E, a -> difficulty.set(Difficulty.EXPERT))
		};
		new ToggleGroup().getToggles().addAll(difficulties);
		Difficulty.listen((a, b, c) -> {
			difficulties[0].setSelected(c == Difficulty.BEGINNER);
			difficulties[1].setSelected(c == Difficulty.INTERMEDIATE);
			difficulties[2].setSelected(c == Difficulty.EXPERT);
		});

		DoubleProperty resolution = new SimpleDoubleProperty(this, "Resolution", 30);
		Config.numberBind("Resolution", resolution);
		RadioMenuItem[] resolutions = new RadioMenuItem[] {
				createRadioItem("Small", null, a -> resolution.set(24)),
				createRadioItem("Medium", null, a -> resolution.set(30)),
				createRadioItem("Large", null, a -> resolution.set(36))
		};
		new ToggleGroup().getToggles().addAll(resolutions);
		resolution.addListener((a, b, c) -> {
			resolutions[0].setSelected(resolution.get() == 24);
			resolutions[1].setSelected(resolution.get() == 30);
			resolutions[2].setSelected(resolution.get() == 36);
		});

		Menu file = new Menu("Game");
		Menu options = new Menu("Options");
		Menu view = new Menu("View");
		Menu timer = new Menu("Timer");
		Menu tileSize = new Menu("Tile Size");
		Menu about = new Menu("About");

		addToMenu(file, createMenuItem("New", KeyCode.F2, false, e -> newGame()));
		addToMenu(file, createMenuItem("Restart", KeyCode.F3, false, e -> restart()), separator());
		addToMenu(file, createMenuItem("Pause", KeyCode.SPACE, false, e -> pause()), separator());
		addToMenu(file, createMenuItem("Save", KeyCode.S, true, e -> MinesweeperFX.saveBoard()));
		addToMenu(file, createMenuItem("Open", KeyCode.O, true, e -> MinesweeperFX.openBoard()));

		addToMenu(options, difficulties);
		addToMenu(options, separator(), createCheckMenuItem("No Flagging", KeyCode.F, true));

		addToMenu(view, createMenuItem("Statistics", KeyCode.F5, false, e -> Dialog.showStats(0)));
		addToMenu(view, createMenuItem("Games", KeyCode.F6, false, e -> Dialog.showStats(1)), separator());
		addToMenu(view, createCheckMenuItem("Grid", KeyCode.G, false));
		addToMenu(view, createCheckMenuItem("Mines", KeyCode.M, false));
		addToMenu(view, createCheckMenuItem("Highlight", KeyCode.H, false));
		addToMenu(view, timer, tileSize);

		addToMenu(timer, createCheckMenuItem("Timer", KeyCode.T, false), separator());
		addToMenu(timer, createCheckMenuItem("Minutes", null, false));
		addToMenu(timer, createCheckMenuItem("Milliseconds", null, false));

		addToMenu(tileSize, resolutions);

		addToMenu(about, createMenuItem("Changelog", null, false, e -> Dialog.showChangeLog()));

		listenToGame((a, b, c) -> file.getItems().get(file.getItems().size() - 2)
									  .setDisable(!(c.equals(WIN) || c.equals(LOSE))));

		getMenus().addAll(file, options, view, about);
		setStyle("-fx-font-family: \"Calibri\";");
		setHeight(25);
		setMinHeight(25);
		setMaxHeight(25);
	}

	private void addToMenu(Menu menu, MenuItem... items) {
		menu.getItems().addAll(items);
	}

	private MenuItem createMenuItem(String name, KeyCode key, boolean ctrlDown, EventHandler<ActionEvent> onAction) {
		MenuItem menuItem = new MenuItem(name);
		menuItem.setAccelerator(createAccelerator(key, ctrlDown));
		menuItem.setOnAction(onAction);
		return menuItem;
	}

	private CheckMenuItem createCheckMenuItem(String name, KeyCode keyCode, boolean ctrlDown) {
		CheckMenuItem menuItem = checkMenuItem(name);
		menuItem.setAccelerator(createAccelerator(keyCode, ctrlDown));
		return menuItem;
	}

	private RadioMenuItem createRadioItem(String name, KeyCode key, EventHandler<ActionEvent> onAction) {
		RadioMenuItem menuItem = new RadioMenuItem(name);
		menuItem.setAccelerator(createAccelerator(key, false));
		menuItem.setOnAction(onAction);
		return menuItem;
	}

	private KeyCodeCombination createAccelerator(KeyCode key, boolean ctrlDown) {
		return key == null ? null : ctrlDown ? new KeyCodeCombination(key, KeyCombination.CONTROL_DOWN)
											 : new KeyCodeCombination(key);
	}

	private SeparatorMenuItem separator() {
		return new SeparatorMenuItem();
	}

	public static ContextMenu contextMenu(String... checks) {
		return new ContextMenu(
				Arrays.stream(checks).map(MenuBar::checkMenuItem).toArray(value -> new MenuItem[checks.length]));
	}

	public static CheckMenuItem checkMenuItem(String name) {
		CheckMenuItem item = new CheckMenuItem(name);
		Config.boolBind(name, item.selectedProperty());
		return item;
	}
}
