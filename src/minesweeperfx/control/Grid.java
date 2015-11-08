package minesweeperfx.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import static minesweeperfx.Options.*;
import static minesweeperfx.logic.FSM.*;

public class Grid extends GridPane {
	
	private final BorderedPane pane = new BorderedPane(0, BORDER_SIZE, true);
	private final GridTile[][] tileset;
	
	public static final ObjectProperty<MousePos> MOUSE_POS = new SimpleObjectProperty<>();
	
	public Grid() {
		pane.getChildren().add(this);
		tileset = new GridTile[30][16];
		for(int j = 0; j < 16; j++)
			for(int i = 0; i < 30; i++) {
				tileset[i][j] = new GridTile(i, j);
				add(tileset[i][j], i, j);
			}
		setLayoutX(BORDER_SIZE - 1);
		setLayoutY(BORDER_SIZE - 1);
		
		setOnMousePressed(e -> {
			if(e.isPrimaryButtonDown() && e.isSecondaryButtonDown())
				INPUT_STATE.doIt(COMBO_CLICK);
			else if(e.isPrimaryButtonDown())
				INPUT_STATE.doIt(LEFT_CLICK);
			else if(e.isSecondaryButtonDown())
				INPUT_STATE.doIt(RIGHT_CLICK);
		});
		setOnMouseReleased(e -> {
			if(e.isPrimaryButtonDown() || e.isSecondaryButtonDown())
				INPUT_STATE.doIt(COMBO_RELEASE);
			else if(e.getButton() == MouseButton.PRIMARY || e.getButton() == MouseButton.SECONDARY)
				INPUT_STATE.doIt(RELEASE);
		});
		setOnMouseMoved(e -> MOUSE_POS.set(new MousePos(e)));
		setOnMouseDragged(e -> MOUSE_POS.set(new MousePos(e)));

		getTileSizeProperty().addListener((a, b, c) -> {
			for(int j = 0; j < 3; j++) {
				setWidth(getColumns()*getTileSize());
				setHeight(getRows()*getTileSize());
			}
			pane.border.setWidth(getWidth() + 2*(BORDER_SIZE - 1) + 1);
			pane.border.setHeight(getHeight() + 2*(BORDER_SIZE - 1) + 1);
		});
	}
	
	public void update() {
		pane.border.setWidth(getColumns()*getTileSize() + 2*(BORDER_SIZE - 1) + 1);
		pane.border.setHeight(getColumns()*getTileSize() + 2*(BORDER_SIZE - 1) + 1);
		for(int j = 0; j < getRows(); j++)
			for(int i = 0; i < getColumns(); i++)
				tileset[i][j].update();
		ObservableList<GridTile> allTiles = FXCollections.observableArrayList();
		ObservableList<GridTile> showing = FXCollections.observableArrayList();
		for(int j = 0; j < 16; j++)
			for(int i = 0; i < 30; i++)
				allTiles.add(tileset[i][j]);
		for(int j = 0; j < getRows(); j++)
			for(int i = 0; i < getColumns(); i++)
				showing.add(tileset[i][j]);
		allTiles.filtered(t -> !showing.contains(t)).forEach(t -> t.setVisible(false));
		showing.forEach(t -> t.setVisible(true));
	}
	
	public BorderedPane get() {
		return pane;
	}
	
	public class MousePos {
		
		public final int x;
		public final int y;
		public final int tileX;
		public final int tileY;
		
		public MousePos(MouseEvent event) {
			this.x = (int)event.getX();
			this.y = (int)event.getY();
			this.tileX = (int)(event.getX()/getTileSize());
			this.tileY = (int)(event.getY()/getTileSize());
		}
	}
}
