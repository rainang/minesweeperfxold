package minesweeper.fx;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import minesweeper.game.Config;
import minesweeper.game.Difficulty;

public class RootPane extends VBox {

	private final DataPanel dataPanel;
	private final TilePane  tilePane;

	public RootPane(DataPanel dataPanel, TilePane tilePane) {
		this.dataPanel = dataPanel;
		this.tilePane = tilePane;

		setBackground(Painter.MAIN_BACKGROUND);
		setPadding(new Insets(5));
		setSpacing(5);
		getChildren().addAll(dataPanel, tilePane);

		Config.numberListen("Resolution", (a, b, c) -> setDifficulty());
		Difficulty.listen((a, b, c) -> setDifficulty());
	}

	public void setDifficulty() {
		Insets insets = Painter.MAIN_BORDER.getInsets();
		setWidth(tilePane.getWidth() + insets.getLeft() + insets.getRight());
		setHeight(dataPanel.getHeight() + tilePane.getHeight() + insets.getTop() + insets.getBottom() + getSpacing());
		setPrefSize(getWidth(), getHeight());
	}
}
