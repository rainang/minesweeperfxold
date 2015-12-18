package minesweeper.fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import minesweeper.Formatter;
import minesweeper.game.Config;
import minesweeper.game.Difficulty;
import minesweeper.game.Game;

import static minesweeper.game.TileState.MINE;

public class DataPanel extends HBox {

	private final ObjectProperty<Font> font;

	private final Label mines = new Label(MINE.text + " 0");
	private final Label timer = new Label("0 \u231A");

	public DataPanel() {
		font = new SimpleObjectProperty<>(Painter.getDataPanelFont(Config.getInt("Resolution")));

		setBorder(Painter.MAIN_BORDER);
		setPadding(new Insets(5));

		mines.setPadding(new Insets(2));
		timer.setPadding(new Insets(2));

		mines.fontProperty().bind(font);
		timer.fontProperty().bind(font);

		Config.boolBind("Mines", mines.visibleProperty());
		Config.boolBind("Timer", timer.visibleProperty());
		Config.boolListen("No Flagging", (a, b, c) -> mines.setText(Game.get().getRemainingMines() + ""));

		Game.get().listenToRemainingMines((a, b, c) -> mines.setText(c.intValue() + ""));
		Game.get().clock.listenToTime((a, b, c) -> timer.setText(Formatter.formatToScore(c.longValue()) + " \u231A"));
		Formatter.listenToScoreFormatChange(f -> timer.setText(f.format(Game.get().clock.getTime())));
		mines.textProperty().addListener((a, b, c) -> {
			if(Config.bool("No Flagging"))
				mines.setText(MINE.text + " NF");
			else if(!c.contains(MINE.text))
				mines.setText(MINE.text + " " + c);
		});
		mines.setAlignment(Pos.CENTER_LEFT);
		timer.setAlignment(Pos.CENTER_RIGHT);

		getChildren().addAll(mines, timer);

		Config.numberListen("Resolution", (a, b, c) -> setDifficulty());
		Difficulty.listen((a, b, c) -> setDifficulty());
	}

	private void setDifficulty() {
		int resolution = Config.getInt("Resolution");
		Insets insets = getBorder().getInsets();
		setWidth(Difficulty.columns()*resolution + +insets.getLeft() + insets.getRight());
		setHeight(resolution + insets.getTop() + insets.getBottom() + mines.getPadding().getTop()*2);
		setPrefSize(getWidth(), getHeight());
		setMinSize(getWidth(), getHeight());
		setMaxSize(getWidth(), getHeight());
		mines.setMinWidth(getWidth()/2 - 9);
		timer.setMinWidth(getWidth()/2 - 9);

		font.set(Painter.getDataPanelFont(resolution));
	}
}
