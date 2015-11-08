package minesweeperfx.control;

import javafx.scene.Group;

public class BorderedPane extends Group {

	public final BorderedLayer border;

	public BorderedPane(int size, int borderSize, boolean invert) {
		this(size, size, borderSize, invert);
	}

	public BorderedPane(int width, int height, int borderSize, boolean invert) {
		border = new BorderedLayer(width, height, borderSize, true, invert);
		getChildren().add(border);
	}
}