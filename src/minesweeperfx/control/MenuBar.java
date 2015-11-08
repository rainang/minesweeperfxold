package minesweeperfx.control;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MenuBar extends javafx.scene.control.MenuBar {

	public final Menu file    = new Menu("Game");
	public final Menu options = new Menu("Options");
	public final Menu profile = new Menu("Profile");

	public final MenuItem newGame     = new MenuItem("New");
	public final MenuItem restartGame = new MenuItem("Restart");
	public final MenuItem pauseGame   = new MenuItem("Pause Game");

	public final RadioMenuItem[] difficulty = new RadioMenuItem[] {
			new RadioMenuItem("Beginner"), new RadioMenuItem("Intermediate"), new RadioMenuItem("Expert")
	};

	public final CheckMenuItem nf        = new CheckMenuItem("No Flagging");
	public final CheckMenuItem showFlags = new CheckMenuItem("Show Flag Count");
	public final CheckMenuItem showTimer = new CheckMenuItem("Show Timer");

	public final Label          label          = new Label("Tile Size");
	public final Slider         slider         = new Slider(24, 36, 30);
	public final CustomMenuItem tileSizeSlider = new CustomMenuItem(new VBox(label, slider));

	public final MenuItem viewProfile = new MenuItem("View Profile");

	public MenuBar() {
		slider.setSnapToTicks(true);
		slider.setMinorTickCount(4);
		slider.setMajorTickUnit(5);
		slider.setBlockIncrement(1);

		newGame.setAccelerator(new KeyCodeCombination(KeyCode.F2));
		restartGame.setAccelerator(new KeyCodeCombination(KeyCode.F3));
		pauseGame.setAccelerator(new KeyCodeCombination(KeyCode.SPACE));
		difficulty[0].setAccelerator(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN));
		difficulty[1].setAccelerator(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.CONTROL_DOWN));
		difficulty[2].setAccelerator(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.CONTROL_DOWN));
		viewProfile.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));

		tileSizeSlider.setHideOnClick(false);
		label.setTextFill(Color.gray(0.25));
		label.setPadding(new Insets(0, 0, 0, 15));

		file.getItems().addAll(newGame, restartGame, new SeparatorMenuItem(), pauseGame);
		options.getItems()
			   .addAll(difficulty[0], difficulty[1], difficulty[2], new SeparatorMenuItem(), nf, showFlags, showTimer,
					   new SeparatorMenuItem(), tileSizeSlider);
		profile.getItems().add(viewProfile);
		getMenus().addAll(file, options, profile);
	}
}
