package minesweeper.game;

import java.util.*;
import java.util.function.Predicate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import minesweeper.io.Readable;
import minesweeper.io.Writable;

public class Board implements Writable, Readable {

	/** The 2-dimensional array of all tiles on this board */
	private final Tile[][] tileset = new Tile[30][16];

	/** The constructor of {@link Board} */
	protected Board() {
		for(int y = 0; y < 16; y++)
			for(int x = 0; x < 30; x++)
				tileset[x][y] = new Tile(x, y);
	}

	/** Lists all tiles on this board */
	protected List<Tile> listTiles() {
		List<Tile> list = new ArrayList<>();

		for(int r = 0; r < Difficulty.rows(); r++)
			for(int c = 0; c < Difficulty.columns(); c++)
				list.add(getTile(c, r));
		return list;
	}

	/**
	 * Retrieves a tile on this board depending on the current {@link Difficulty}. If the difficulty is
	 * <code>null</code>, this will return a {@link Tile} regardless of the difficulty. If the given parameters
	 * are out of bounds, this will return <code>null</code>.
	 *
	 * @param x
	 * 		the x position of the tile
	 * @param y
	 * 		the y position of the tile
	 *
	 * @return The tile based on the current difficulty. If difficulty is <code>null</code>, returns the tile
	 * regardless of difficulty. If the parameters are out of bounds, returns null.
	 */
	public Tile getTile(int x, int y) {
		if(Difficulty.get() == null)
			return tileset[x][y];
		else if(x < 0 || x >= Difficulty.columns() || y < 0 || y >= Difficulty.rows())
			return null;
		return tileset[x][y];
	}

	/**
	 * Lists all tiles surrounding the {@link Tile} parameter with a given {@link Predicate} as filter
	 *
	 * @param tile
	 * 		the tile to search
	 * @param predicate
	 * 		the filter to use
	 *
	 * @return List of all tiles surrounding <code>tile</code> with the given <code>predicate</code>
	 */
	protected List<Tile> getNeighborsOf(Tile tile, Predicate<Tile> predicate) {
		List<Tile> tiles = new ArrayList<>();
		if(tile == null)
			return tiles;
		for(int y = tile.y - 1; y < tile.y + 2; y++)
			for(int x = tile.x - 1; x < tile.x + 2; x++) {
				Tile t = getTile(x, y);
				if(t != null)
					if(predicate == null)
						tiles.add(t);
					else if(predicate.test(t))
						tiles.add(t);
			}
		return tiles;
	}

	/**
	 * Resets all tiles on this board. Set the value of parameter <code>soft</code> to <code>true</code> for a soft
	 * reset. A soft reset will leave generated mines as is; otherwise, new mines are generated.
	 *
	 * @param soft
	 * 		<code>true</code> if mines should <code>NOT</code> be regenerated; <code>false</code> otherwise
	 *
	 * @see Tile#reset(boolean)
	 */
	protected void reset(boolean soft) {
		listTiles().forEach(t -> t.reset(soft));
		if(!soft) {
			List<Tile> list = listTiles();
			Collections.shuffle(list);
			list.subList(0, Difficulty.mines()).forEach(Tile::setMine);
		}
	}

	/**
	 * Calculates the current board's data: Bechtel's Board Benchmark Value (3BV), openings, and islands
	 *
	 * @return Integer array of 3BV, number of openings, and number of islands
	 */
	protected int[] calculateBoardData() {
		int[] data = new int[3];

		Set<Tile> passed = FXCollections.observableSet();
		for(int i = 1; i < 3; i++) {
			Predicate<Tile> predicate = i == 1 ? Tile::isOpening : Tile::isIsland;
			PriorityQueue<Tile> q = new PriorityQueue<>(
					FXCollections.observableArrayList(listTiles()).filtered(predicate));
			passed.clear();
			while(!q.isEmpty()) {
				Set<Board.Tile> cluster = FXCollections.observableSet();
				PriorityQueue<Board.Tile> search = new PriorityQueue<>();
				search.add(q.poll());
				while(!search.isEmpty()) {
					Board.Tile tile = search.poll();
					if(cluster.contains(tile))
						continue;
					cluster.add(tile);
					getNeighborsOf(tile, t -> tile.isNotSelf(t) && !cluster.contains(t)).forEach(t -> {
						if(predicate.test(t))
							search.add(t);
						else
							cluster.add(t);
					});
				}

				q.removeAll(cluster);
				passed.addAll(cluster);
				data[i]++;
			}
			if(i == 1)
				data[0] = Difficulty.columns()*Difficulty.rows() - Difficulty.mines() - passed.size() + data[1];
		}
		return data;
	}

	/** {@inheritDoc} */
	@Override
	public byte[] toBytes() {
		byte[] bytes = new byte[Difficulty.mines()];

		int step = 0;
		int current = 0;
		for(Tile t : listTiles()) {
			if(t.isMine()) {
				bytes[current] = (byte)step;
				current++;
				step = 0;
			} else
				step++;
		}
		return bytes;
	}

	/** {@inheritDoc} */
	@Override
	public void fromBytes(byte[] bytes) {
		int mines = bytes.length;
		Difficulty.set(mines == 99 ? Difficulty.EXPERT : mines == 40 ? Difficulty.INTERMEDIATE : Difficulty.BEGINNER);
		GameState.restart();
		listTiles().forEach(t -> t.reset(false));

		List<Board.Tile> list = listTiles();
		int current = 0;
		for(byte b : bytes) {
			int step = 0;
			while(b != step)
				step++;
			list.get(step + current).setMine();
			current += step + 1;
		}
	}

	public class Tile implements Comparable<Tile> {

		/** The tile's x position on the grid */
		public final int x;
		/** The tile's y position on the grid */
		public final int y;

		/** The tile's state property */
		private final ObjectProperty<TileState> state = new SimpleObjectProperty<>();

		/** True if the tile is revealed */
		private boolean open;
		/** True if the tile is flagged */
		private boolean flag;
		/** True if the tile has a mine */
		private boolean mine;
		/** True if the tile is hidden */
		private boolean hide;
		/** True if the tile is the losing tile or is an incorrectly flagged tile */
		private boolean lose;

		/** The number of mines near this tile. Max number is 8. */
		private int mineCounter;
		/** The number of flags near this tile. Max number is 8. */
		private int flagCounter;

		/**
		 * The constructor of {@link Tile}
		 *
		 * @param x
		 * 		grid x position
		 * @param y
		 * 		grid y position
		 */
		protected Tile(int x, int y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Reveals or covers this {@link Tile} depending on the value of parameter <code>b</code>.
		 * <p>
		 * If the value is changed, the {@link Tile#onStateChanged()} method is fired.
		 *
		 * @param b
		 * 		if <code>true</code>, reveals this tile; otherwise, covers this tile
		 */
		protected void setOpen(boolean b) {
			if(open == b)
				return;
			open = b;
			onStateChanged();
		}

		/**
		 * Sets a flag on this {@link Tile} if no flag is present; otherwise, removes flag.
		 * <p>
		 * If the value is changed, the {@link Tile#onStateChanged()} method is fired. This also updates all
		 * neighboring tiles' {@link Tile#flagCounter}, calling the {@link Tile#updateFlagCounter(int)} method.
		 *
		 * @return Returns the new flag state
		 */
		protected boolean setFlag() {
			flag = !flag;
			getNeighborsOf(this, this::isNotSelf).forEach(e -> e.updateFlagCounter(flag ? 1 : -1));
			onStateChanged();
			return flag;
		}

		/**
		 * Sets mine on this {@link Tile}.
		 * <p>
		 * If the value is changed, the {@link Tile#onStateChanged()} method is fired. This also updates all
		 * neighboring tiles' {@link Tile#mineCounter}, calling the {@link Tile#updateMineCounter(int)} method.
		 */
		protected void setMine() {
			mine = true;
			getNeighborsOf(this, this::isNotSelf).forEach(e -> e.updateMineCounter(1));
			onStateChanged();
		}

		/**
		 * Removes mine on this {@link Tile}.
		 * <p>
		 * If the value is changed, the {@link Tile#onStateChanged()} method is fired. This also updates all
		 * neighboring tiles' {@link Tile#mineCounter}, calling the {@link Tile#updateMineCounter(int)} method.
		 */
		protected void removeMine() {
			mine = false;
			getNeighborsOf(this, this::isNotSelf).forEach(e -> e.updateMineCounter(-1));
			onStateChanged();
		}

		/**
		 * Hides or shows this {@link Tile} depending on the value of parameter <code>b</code>.
		 * <p>
		 * If the value is changed, the {@link Tile#onStateChanged()} method is fired.
		 * <p>
		 *
		 * @param b
		 * 		if <code>true</code>, hides this tile; otherwise, shows this tile
		 */
		protected void setHide(boolean b) {
			if(hide == b)
				return;
			hide = b;
			onStateChanged();
		}

		/** @return <code>true</code> if the tile is revealed */
		public boolean isOpen() {
			return open;
		}

		/** @return <code>true</code> if the tile has a mine */
		protected boolean isMine() {
			return mine;
		}

		/** @return <code>true</code> if the tile has no mine */
		protected boolean isNotMine() {
			return !mine;
		}

		/** @return <code>true</code> if the tile is an opening tile */
		protected boolean isOpening() {
			return !mine && mineCounter == 0;
		}

		/** @return <code>true</code> if the tile is an island tile */
		protected boolean isIsland() {
			return !isOpening() && getNeighborsOf(this, t -> isNotSelf(t) && t.isOpening()).size() == 0;
		}

		/** @return True if this tile is not flagged nor opened */
		protected boolean canOpen() {
			return !flag && !open;
		}

		/**
		 * @return True if this tile is open, has at least 1 mine nearby, and has equal amount of mines and flags
		 * nearby
		 */
		protected boolean canChord() {
			return open && mineCounter > 0 && mineCounter == flagCounter;
		}

		/**
		 * Indicates whether some other tile is "equal to" this one.
		 *
		 * @param tile
		 * 		the reference tile with which to compare.
		 * 		The tile to compare
		 *
		 * @return {@code true} if this tile is the same as the tile argument; {@code false} otherwise.
		 *
		 * @see Tile#equals(Object)
		 */
		protected boolean isNotSelf(Tile tile) {
			return !this.equals(tile);
		}

		/** @return The number of mines near this tile. Max number is 8. */
		protected int getMinesNearby() {
			return mineCounter;
		}

		/** Adds a {@link ChangeListener} which will be notified whenever the tile's state changes */
		public void onStateChanged(ChangeListener<? super TileState> listener) {
			state.addListener(listener);
		}

		/**
		 * Validates this tile. Checks whether or not this {@link Tile} is a losing tile. A tile is considered a losing
		 * tile if
		 * <code>({@link Tile#open} && {@link Tile#mine}) || ({@link Tile#flag} && {@link Tile#mine !mine})</code>.
		 * <p>
		 * If the value is changed, the {@link Tile#onStateChanged()} method is fired.
		 * <p>
		 */
		protected void validate() {
			boolean b = (open && mine) || (flag && !mine);
			if(lose == b)
				return;
			lose = b;
			onStateChanged();
		}

		/** Sets the current state of the {@link Tile} */
		private void onStateChanged() {
			if(hide)
				state.set(TileState.EMPTY);
			else if(open) {
				if(mine)
					state.set(lose ? TileState.MINE_WRONG : TileState.MINE);
				else {
					switch(mineCounter) {
					default:
						state.set(TileState.EMPTY);
						break;
					case 1:
						state.set(TileState.ONE);
						break;
					case 2:
						state.set(TileState.TWO);
						break;
					case 3:
						state.set(TileState.THREE);
						break;
					case 4:
						state.set(TileState.FOUR);
						break;
					case 5:
						state.set(TileState.FIVE);
						break;
					case 6:
						state.set(TileState.SIX);
						break;
					case 7:
						state.set(TileState.SEVEN);
						break;
					case 8:
						state.set(TileState.EIGHT);
						break;
					}
				}
			} else if(flag)
				state.set(lose ? TileState.FLAG_WRONG : TileState.FLAG);
			else
				state.set(TileState.DEFAULT);
		}

		/**
		 * Increases or decreases the {@link Tile#flagCounter} by 1 depending on the value of parameter <code>i</code>.
		 * A value greater than 0 will increase the counter by 1; otherwise, decreases the counter by 1
		 *
		 * @param i
		 * 		if value > 0, increases the counter by 1; otherwise, decreases the counter by 1
		 */
		private void updateFlagCounter(int i) {
			flagCounter += i > 0 ? 1 : -1;
		}

		/**
		 * Increases or decreases the {@link Tile#mineCounter} by 1 depending on the value of parameter <code>i</code>.
		 * A value greater than 0 will increase the counter by 1; otherwise, decreases the counter by 1
		 *
		 * @param i
		 * 		if value > 0, increases the counter by 1; otherwise, decreases the counter by 1
		 */
		private void updateMineCounter(int i) {
			mineCounter += i > 0 ? 1 : -1;
		}

		/**
		 * Resets all fields of this {@link Tile}.
		 * <p>
		 * Set the value of parameter <code>b</code> to <code>true</code> for a soft reset. A soft reset will leave
		 * {@link Tile#mine} and {@link Tile#mineCounter} fields as is.
		 * <p>
		 * This fires the {@link Tile#onStateChanged()} method.
		 *
		 * @param soft
		 * 		if <code>true</code>, ignores {@link Tile#mine} and {@link Tile#mineCounter}; otherwise, resets all
		 * 		fields to default values
		 */
		protected void reset(boolean soft) {
			if(!soft) {
				mine = false;
				mineCounter = 0;
			}
			hide = false;
			setOpen(false);
			flag = false;
			flagCounter = 0;
			lose = false;
			onStateChanged();
		}

		@Override
		public int compareTo(Tile o) {
			return 0;
		}
	}
}
