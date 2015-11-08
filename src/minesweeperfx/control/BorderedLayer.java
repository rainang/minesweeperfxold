package minesweeperfx.control;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BorderedLayer extends Canvas {

	public final int borderSize;

	private boolean cover;
	private boolean invert;

	public BorderedLayer(double width, double height, int borderSize, boolean cover, boolean invert) {
		this.borderSize = borderSize;
		this.cover = cover;
		this.invert = invert;
		getGraphicsContext2D().setFill(Color.LIGHTGRAY);
		widthProperty().addListener((obs, o, n) -> setBorder(getGraphicsContext2D(), borderSize, this.cover, invert));
		heightProperty().addListener((obs, o, n) -> setBorder(getGraphicsContext2D(), borderSize, this.cover, invert));
		setWidth(width);
		setHeight(height);
	}

	public void setCover(boolean b) {
		cover = b;
		setBorder(getGraphicsContext2D(), borderSize, cover, invert);
	}

	private void strokeLine(GraphicsContext gc, double x1, double y1, double x2, double y2) {
		gc.strokeLine(((int)x1) + 0.5, ((int)y1) + 0.5, ((int)x2) + 0.5, ((int)y2) + 0.5);
	}

	private void setBorder(GraphicsContext gc, int borderSize, boolean cover, boolean invert) {
		double w = getWidth();
		double h = getHeight();
		gc.fillRect(0, 0, getWidth(), getHeight());
		if(cover) {
			for(int i = 0; i < borderSize; i++) {
				gc.setStroke(invert ? Color.GRAY : Color.WHITE);
				strokeLine(gc, 0, i, w - 1 - i, i);
				strokeLine(gc, i, 0, i, h - 1 - i);

				gc.setStroke(invert ? Color.WHITE : Color.GRAY);
				strokeLine(gc, w - i, 1 + i, w - i, h);
				strokeLine(gc, 1 + i, h - i, w, h - i);
			}
		} else {
			gc.setStroke(Color.GRAY);
			strokeLine(gc, 0, 0, w, 0);
			strokeLine(gc, 0, 0, 0, h);
		}
	}
}
