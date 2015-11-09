package minesweeperfx.logic;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import minesweeperfx.MinesweeperFX;
import minesweeperfx.Options;
import minesweeperfx.Timer;
import minesweeperfx.statistics.GameStats;
import minesweeperfx.statistics.Profile;

import static minesweeperfx.Options.*;
import static minesweeperfx.logic.FSM.*;

public class Game {

	public final Board board = new Board();

	public final Timer timer = new Timer();

	private final ObjectProperty<Tile> focus = new SimpleObjectProperty<>();

	private final IntegerProperty tilesOpen = new SimpleIntegerProperty();
	private final IntegerProperty flagsUsed = new SimpleIntegerProperty();

	private boolean restart;

	public Game() {
		board.update();
		focus.addListener((obs, o, n) -> {
			if(o == n)
				return;
			if(INPUT_STATE.getState() == PRIMARY) {
				if(o != null && o.getCanPeek())
					o.setPeek(false);
				if(n != null && n.getCanPeek())
					n.setPeek(true);
			} else if(INPUT_STATE.getState() == COMBO_START) {
				if(n != null && o != null) {
					ObservableList<Tile> tnO = board.getNeighborsOf(o, Tile::getCanPeek);
					ObservableList<Tile> tnN = board.getNeighborsOf(n, Tile::getCanPeek);
					tnO.stream().filter(t -> !tnN.contains(t)).forEach(e -> e.setPeek(false));
					tnN.stream().filter(t -> !tnO.contains(t)).forEach(e -> e.setPeek(true));
				} else if(n == null)
					board.getNeighborsOf(o, Tile::getCanPeek).forEach(e -> e.setPeek(false));
				else
					board.getNeighborsOf(n, Tile::getCanPeek).forEach(e -> e.setPeek(true));
			}
		});
		tilesOpen.addListener((obs, o, n) -> {
			if(tilesOpen.get() == getColumns()*getRows() - getMines())
				GAME_STATE.doIt(WIN_GAME);
		});
	}

	public void startGame(boolean moveMines) {
		timer.play();
		if(moveMines) {
			if(focus.get() == null)
				return;
			ObservableList<Tile> tilesStart = board.getNeighborsOf(focus.get(), t -> true);
			int i = tilesStart.filtered(Tile::getMine).size();
			if(i == 0)
				return;

			tilesStart.forEach(t -> t.setMine(false));
			ObservableList<Tile> list = FXCollections.observableArrayList();
			board.listTiles().stream().filter(t -> !t.getMine()).filter(t -> !tilesStart.contains(t))
				 .forEach(list::add);
			FXCollections.shuffle(list);
			list.subList(0, i).forEach(t -> t.setMine(true));
		}
	}

	public void newGame(boolean restart) {
		timer.reset();
		MinesweeperFX.INSTANCE.GRID.setGameOver(false);
		this.restart = restart;
		tilesOpen.set(0);
		flagsUsed.set(0);
		board.newGame(restart);
	}

	public void gameOver(boolean win) {
		timer.stop();
		MinesweeperFX.INSTANCE.GRID.setGameOver(true);
		board.gameOver(win);
		if(win && !restart)
			Profile.writeStats(new GameStats(Options.getDifficulty(), Options.getNF(), timer.getLongTime(),
											 System.currentTimeMillis()));
	}

	public void flag() {
		if(Options.getNF())
			return;
		if(!focus.get().getOpen()) {
			focus.get().setFlag(!focus.get().getFlag());
			flagsUsed.set(flagsUsed.get() + (focus.get().getFlag() ? 1 : -1));
		}
	}

	public void peek() {
		if(focus.get().getCanPeek())
			focus.get().setPeek(true);
	}

	public void peekCombo() {
		board.getNeighborsOf(focus.get(), null).filtered(Tile::getCanPeek).forEach(t -> t.setPeek(true));
	}

	public void open() {
		openTile(focus.get());
	}

	public void openCombo() {
		if(focus.get() == null)
			return;
		ObservableList<Tile> list = board.getNeighborsOf(focus.get(), t -> t != null && t.getCanPeek());
		list.forEach(t -> t.setPeek(false));
		if(focus.get().getCanMultiOpen())
			list.forEach(this::openTile);
	}

	public void openTile(Tile tile) {
		if(GAME_STATE.getState() == START || GAME_STATE.getState() == RESTART)
			GAME_STATE.doIt(BEGIN_GAME);
		if(GAME_STATE.getState() != PLAYING || !tile.getCanPeek())
			return;
		tile.setPeek(false);
		tile.setOpen(true);
		if(tile.getMine()) {
			GAME_STATE.doIt(LOSE_GAME);
			tile.setIsLosingTile(true);
			return;
		} else if(tile.getMinesNearby() == 0)
			board.getNeighborsOf(tile, t -> t != tile).forEach(this::openTile);
		tilesOpen.set(tilesOpen.get() + 1);
	}

	public void pause() {
		timer.pause();
	}

	public void setFocus(Tile tile) {
		focus.set(tile);
	}
	
	public BooleanProperty getPauseProperty() {
		return timer.getPauseProperty();
	}
	
	public IntegerProperty getFlagsUsedProperty() {
		return flagsUsed;
	}
}
