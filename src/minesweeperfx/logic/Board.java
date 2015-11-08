package minesweeperfx.logic;

import java.util.function.Predicate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static minesweeperfx.Options.*;

public class Board {

	private final ObjectProperty<Tile[][]> tileset = new SimpleObjectProperty<>();

	public void update() {
		newTileset(getColumns(), getRows());
		listTiles().forEach(t -> {
			t.getFlagProperty()
			 .addListener((obs, o, n) -> getNeighborsOf(t, nt -> nt != t).forEach(tn -> tn.updateFlagsNearby(n)));
			t.getMineProperty()
			 .addListener((obs, o, n) -> getNeighborsOf(t, nt -> nt != t).forEach(tn -> tn.updateMinesNearby(n)));
		});
	}

	private void newTileset(int cols, int rows) {
		Tile[][] set = new Tile[cols][rows];
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < cols; c++)
				set[c][r] = new Tile(c, r);
		tileset.set(set);
	}

	public ObservableList<Tile> listTiles() {
		ObservableList<Tile> list = FXCollections.observableArrayList();
		for(int r = 0; r < getRows(); r++)
			for(int c = 0; c < getColumns(); c++)
				list.add(getTile(c, r));
		return list;
	}

	public Tile getTile(int x, int y) {
		if(x < 0 || x >= getColumns() || y < 0 || y >= getRows())
			return null;
		return tileset.get()[x][y];
	}

	public ObservableList<Tile> getNeighborsOf(Tile tile, Predicate<Tile> predicate) {
		ObservableList<Tile> tiles = FXCollections.observableArrayList();
		for(int j = tile.y - 1; j < tile.y + 2; j++)
			for(int i = tile.x - 1; i < tile.x + 2; i++) {
				Tile n = getTile(i, j);
				if(n != null)
					if(predicate == null)
						tiles.add(n);
					else if(predicate.test(n))
						tiles.add(n);
			}
		return tiles;
	}

	public void newGame(boolean restart) {
		listTiles().forEach(t -> t.reset(restart));
		if(!restart) {
			ObservableList<Tile> list = listTiles();
			FXCollections.shuffle(list);
			list.subList(0, getMines() < list.size() ? getMines() : list.size()).forEach(tile -> tile.setMine(true));
		}
	}

	public void gameOver(boolean win) {
		if(!win) {
			listTiles().filtered(Tile::getFlag).filtered(t -> !t.getMine()).forEach(t -> t.setIsLosingTile(true));
			listTiles().filtered(Tile::getCanPeek).forEach(t -> t.setOpen(true));
		}
	}
}
