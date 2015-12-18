package minesweeper.fx;

import java.util.stream.IntStream;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static javafx.scene.layout.BorderStrokeStyle.SOLID;
import static javafx.scene.paint.Color.GRAY;
import static javafx.scene.paint.Color.WHITE;

public class Painter {

	public static final int MAIN_BORDER_WIDTH = 4;
	public static final int TILE_BORDER_WIDTH = 3;

	public static final Background MAIN_BACKGROUND = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));
	public static final Background TILE_HIGHLIGHT  = new Background(
			new BackgroundFill(Color.color(0, 0, 0, 0.1), null, null));

	public static final Border MAIN_BORDER     = createBevelBorderLowered(MAIN_BORDER_WIDTH);
	public static final Border TILE_BORDER     = createBevelBorderRaised(TILE_BORDER_WIDTH);
	public static final Border TILE_GRID_LINES = createGridLines();

	public static Font getTileFont(int resolution) {
		return Font.font("Consolas", FontWeight.EXTRA_BOLD, resolution*0.5f);
	}

	public static Font getDataPanelFont(int resolution) {
		return Font.font("Calibri", FontWeight.NORMAL, resolution*0.5f);
	}

	public static Border createBevelBorderRaised(int size) {
		return new Border(IntStream.range(0, size).mapToObj(Painter::strokeRaised).toArray(BorderStroke[]::new));
	}

	public static Border createBevelBorderLowered(int size) {
		return new Border(IntStream.range(0, size).mapToObj(Painter::strokeLowered).toArray(BorderStroke[]::new));
	}

	public static Border createGridLines() {
		return new Border(
				new BorderStroke(GRAY, WHITE, WHITE, GRAY, SOLID, SOLID, SOLID, SOLID, null, BorderWidths.DEFAULT,
								 new Insets(0)));
	}

	public static BorderStroke strokeRaised(int inset) {
		return new BorderStroke(WHITE, GRAY, GRAY, WHITE, SOLID, SOLID, SOLID, SOLID, null, BorderWidths.DEFAULT,
								new Insets(inset));
	}

	public static BorderStroke strokeLowered(int inset) {
		return new BorderStroke(GRAY, WHITE, WHITE, GRAY, SOLID, SOLID, SOLID, SOLID, null, BorderWidths.DEFAULT,
								new Insets(inset));
	}
}
