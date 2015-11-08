package minesweeperfx.control;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import minesweeperfx.MinesweeperFX;
import minesweeperfx.Options;
import minesweeperfx.logic.Tile;

import static minesweeperfx.Options.*;

public class GridTile extends StackPane {

	private final BorderedLayer border = new BorderedLayer(getTileSize(), getTileSize(), 3, true, false);
	private final Label         label  = new Label();

	private Tile tile;

	private final int x, y;

	public GridTile(int x, int y) {
		this.x = x;
		this.y = y;
		label.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, getTileSize()*0.5f));
		getTileSizeProperty().addListener((a, b, c) -> {
			label.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, c.intValue()*0.5f));
			border.setWidth(c.doubleValue());
			border.setHeight(c.doubleValue());
		});
		getChildren().addAll(border, label);
		update();
	}

	public void update() {
		tile = MinesweeperFX.getGame().board.getTile(x, y);
		if(tile == null)
			return;
		tile.getOpenProperty().addListener((obs, o, n) -> setOpen(n));
		tile.getPeekProperty().addListener((obs, o, n) -> border.setCover(!n));
		tile.getFlagProperty().addListener((obs, o, n) -> setFlag(n));
		tile.getIsLosingTileProperty().addListener((obs, o, n) -> label.setTextFill(n ? Color.RED : Color.BLACK));
		setOpen(false);
		setFlag(false);
	}

	private void setOpen(boolean b) {
		if(b) {
			int i = tile.getMine() ? 0 : tile.getMinesNearby();
			if(tile.getMine())
				label.setText(Options.MINE);
			else if(tile.getMinesNearby() > 0)
				label.setText(tile.getMinesNearby() + "");
			else
				label.setText("");
			label.setTextFill(getFontColor(i));
		} else {
			label.setText("");
			label.setTextFill(getFontColor(0));
		}
		border.setCover(!b);
		label.setVisible(b);
	}

	private void setFlag(boolean b) {
		label.setVisible(b);
		label.setText(b ? FLAG : "");
	}

	private Color getFontColor(int i) {
		switch(i) {
		default:
			return Color.BLACK;
		case 1:
			return Color.BLUE;
		case 2:
			return Color.GREEN;
		case 3:
			return Color.RED;
		case 4:
			return Color.DARKBLUE;
		case 5:
			return Color.DARKRED;
		case 6:
			return Color.TEAL;
		case 7:
			return Color.GRAY;
		case 8:
			return Color.BLACK;
		}
	}
}
