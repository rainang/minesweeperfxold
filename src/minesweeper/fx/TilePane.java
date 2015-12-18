package minesweeper.fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import minesweeper.game.*;

import static minesweeper.fx.Painter.*;
import static minesweeper.game.GameState.*;
import static minesweeper.game.InputState.*;

public class TilePane extends javafx.scene.layout.TilePane {

	private Tile[][] tiles;

	private final ObjectProperty<Tile> focusedTile = new SimpleObjectProperty<>();

	private boolean comboFlag;

	public TilePane() {
		tiles = new Tile[30][16];
		for(int y = 0; y < 16; y++)
			for(int x = 0; x < 30; x++) {
				tiles[x][y] = new Tile(Game.get().board.getTile(x, y));
				getChildren().add(tiles[x][y]);
			}

		setBorder(MAIN_BORDER);
		setOnMouseMoved(this::setMouseoverTile);
		setOnMouseDragged(this::setMouseoverTile);
		setOnMousePressed(this::handleMouseInput);
		setOnMouseReleased(this::handleMouseInput);
		setOnKeyPressed(this::handleKeyInput);
		listenToGame((a, b, c) -> {
			if(c != PAUSE && c != PLAY)
				for(int y = 0; y < 16; y++)
					for(int x = 0; x < 30; x++)
						tiles[x][y].setFocus(c == WIN || c == LOSE);
		});
		focusedTile.addListener((a, b, c) -> transferFocus(b, c));
		Config.numberListen("Resolution", (a, b, c) -> setResolution());
		Difficulty.listen((a, b, c) -> setDifficulty());
	}

	private void transferFocus(Tile from, Tile to) {
		if(isGameOver())
			return;
		if(from != null)
			from.setFocus(false);
		if(to != null)
			to.setFocus(true);
	}

	private void setMouseoverTile(MouseEvent e) {
		setCursor(Cursor.DEFAULT);

		int d = Config.getInt("Resolution");
		int x = (int)((e.getX() - MAIN_BORDER_WIDTH)/d);
		int y = (int)((e.getY() - MAIN_BORDER_WIDTH)/d);
		if(x >= 0 && y >= 0 && x < Difficulty.columns() && y < Difficulty.rows())
			focusedTile.set(tiles[x][y]);
		else
			focusedTile.set(null);
	}

	private int focusX() {
		return focusedTile.get() == null ? 0 : focusedTile.get().x;
	}

	private int focusY() {
		return focusedTile.get() == null ? 0 : focusedTile.get().y;
	}

	public void handleKeyInput(KeyEvent event) {
		setCursor(Cursor.NONE);
		if(event.getCode() == KeyCode.ENTER) {
			if(Game.get().board.getTile(focusX(), focusY()).isOpen())
				setInput(focusX(), focusY(), CHORD);
			else
				setInput(focusX(), focusY(), OPEN);
		} else if(event.getCode() == KeyCode.LEFT) {
			int x = focusX() - 1;
			if(x < 0)
				x = Difficulty.columns() - 1;
			focusedTile.set(tiles[x][focusY()]);
		} else if(event.getCode() == KeyCode.RIGHT) {
			int x = focusX() + 1;
			if(x >= Difficulty.columns())
				x = 0;
			focusedTile.set(tiles[x][focusY()]);
		} else if(event.getCode() == KeyCode.UP) {
			int y = focusY() - 1;
			if(y < 0)
				y = Difficulty.rows() - 1;
			focusedTile.set(tiles[focusX()][y]);
		} else if(event.getCode() == KeyCode.DOWN) {
			int y = focusY() + 1;
			if(y >= Difficulty.rows())
				y = 0;
			focusedTile.set(tiles[focusX()][y]);
		} else if(event.getCode() == KeyCode.ADD) {
			setInput(focusX(), focusY(), FLAG);
		}
	}

	private void handleMouseInput(MouseEvent event) {
		setCursor(Cursor.DEFAULT);
		if(focusedTile.get() == null)
			return;
		boolean a = event.isPrimaryButtonDown();
		boolean b = event.isSecondaryButtonDown();

		if(event.getButton() != MouseButton.PRIMARY && event.getButton() != MouseButton.SECONDARY)
			return;

		if(event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			if(a && b) {
				comboFlag = true;
			} else if(event.getButton() == MouseButton.SECONDARY) {
				setInput(focusX(), focusY(), FLAG);
			}
		} else if(event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			if((a && !b) || (!a && b)) {
				setInput(focusX(), focusY(), CHORD);
			} else {
				if(comboFlag)
					comboFlag = false;
				else if(event.getButton() == MouseButton.PRIMARY)
					setInput(focusX(), focusY(), OPEN);
			}
		}
	}

	private void setDifficulty() {
		setPrefColumns(Difficulty.columns());
		setPrefRows(Difficulty.rows());
		getChildren().clear();
		for(int y = 0; y < Difficulty.rows(); y++)
			for(int x = 0; x < Difficulty.columns(); x++)
				getChildren().add(tiles[x][y]);
		setResolution();
	}

	private void setResolution() {
		double resolution = Config.number("Resolution").doubleValue();
		Insets insets = getBorder().getInsets();
		setWidth(Difficulty.columns()*resolution + insets.getLeft() + insets.getRight());
		setHeight(Difficulty.rows()*resolution + insets.getTop() + insets.getBottom());
		setPrefSize(getWidth(), getHeight());
		setMinSize(getWidth(), getHeight());
		setMaxSize(getWidth(), getHeight());
		setPrefTileWidth(resolution);
		setPrefTileHeight(resolution);
	}

	private class Tile extends Label {

		public final int x;
		public final int y;

		public Tile(Board.Tile tile) {
			this.x = tile.x;
			this.y = tile.y;

			setBorder(TILE_BORDER);
			setAlignment(Pos.CENTER);
			tile.onStateChanged(this::setTileState);
			Config.numberListen("Resolution", (a, b, c) -> setResolution());
			Config.boolListen("Grid", (a, b, c) -> {
				if(c && getBorder() == null)
					setBorder(TILE_GRID_LINES);
				else if(!c && getBorder() == TILE_GRID_LINES)
					setBorder(null);
			});

			setResolution();
		}

		private void setFocus(boolean b) {
			setBackground(b && (isGameOver() || Config.bool("Highlight")) ? TILE_HIGHLIGHT : MAIN_BACKGROUND);
		}

		private void setTileState(ObservableValue<? extends TileState> o, TileState oldState, TileState newState) {
			setBorder(newState.hasBorder ? TILE_BORDER : Config.bool("Grid") ? TILE_GRID_LINES : null);
			setText(newState.text);
			setTextFill(newState.color);
		}

		private void setResolution() {
			int resolution = Config.getInt("Resolution");
			setFont(getTileFont(resolution));
			setHeight(resolution);
			setWidth(resolution);
			setPrefSize(resolution, resolution);
		}
	}
}