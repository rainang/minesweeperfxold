package minesweeper.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import minesweeper.fx.Dialog;
import minesweeper.statistics.GameData;
import minesweeper.statistics.Profile;

import static minesweeper.game.GameState.*;
import static minesweeper.game.InputState.*;

public class Game {
	
	private static Game INSTANCE;
	
	public final Board board = new Board();
	public final Clock clock = new Clock();

	private final IntegerProperty minesRemaining = new SimpleIntegerProperty();

	private Board.Tile focus;

	private boolean isRestartedGame;
	private boolean nfGame;

	private int tilesOpen;
	private int actions;
	private int clicks;
	
	public Game() {
		INSTANCE = this;
		listenToInput((a, b, c) -> onInputEvent(c));
		listenToGame((a, b, c) -> onGameEvent(b, c));
	}
	
	private void onInputEvent(InputState input) {
		focus = board.getTile(inputX(), inputY());
		GameState state = getGameState();

		if(state == PAUSE)
			play();
		else if(input != IDLE) {
			if(isGameOver())
				Dialog.showGameOver();
			else {
				if(input == OPEN && !isGameStarted())
					play();
				if(input == FLAG)
					flag();
				else {
					if(input == OPEN)
						open();
					else if(input == CHORD)
						chord();
					if(tilesOpen == Difficulty.columns()*Difficulty.rows() - Difficulty.mines())
						win();
				}
			}
		}
		setIdle();
	}
	
	private void onGameEvent(GameState oldEvent, GameState event) {
		switch(event) {
		case NEW:
			clock.reset();
			newGame(false);
			break;
		case RESTART:
			clock.reset();
			newGame(true);
			break;
		case PLAY:
			if(oldEvent == PAUSE)
				board.listTiles().forEach(e -> e.setHide(false));
			else if(oldEvent == NEW)
				moveMines();
			clock.play();
			break;
		case PAUSE:
			clock.pause();
			if(oldEvent == PLAY)
				board.listTiles().forEach(e -> e.setHide(true));
			break;
		case WIN:
			clock.stop();
			gameOver(true);
			break;
		case LOSE:
			clock.stop();
			gameOver(false);
			break;
		}
	}
	
	private void newGame(boolean restart) {
		board.reset(restart);
		minesRemaining.set(Difficulty.mines());

		isRestartedGame = restart;
		nfGame = true;
		tilesOpen = 0;
		actions = 0;
		clicks = 0;
	}
	
	private void moveMines() {
		List<Board.Tile> toMove = board.getNeighborsOf(focus, null);
		int i = (int)toMove.stream().filter(Board.Tile::isMine).peek(Board.Tile::removeMine).count();
		if(i == 0)
			return;
		List<Board.Tile> list = new ArrayList<>();
		board.listTiles().stream().filter(Board.Tile::isNotMine).filter(t -> !toMove.contains(t)).forEach(list::add);
		Collections.shuffle(list);
		list.subList(0, i).forEach(Board.Tile::setMine);
	}
	
	private void gameOver(boolean win) {
		board.listTiles().stream().peek(Board.Tile::validate).filter(Board.Tile::canOpen).forEach(t -> t.setOpen
				(!win));
		if(!isRestartedGame)
			Profile.MAIN.addGameStats(new GameData(Profile.MAIN.name, Difficulty.get(), nfGame, clock.getTime(),
												   System.currentTimeMillis(), actions, clicks,
												   board.calculateBoardData(), board.toBytes()), win);
	}
	
	private void flag() {
		if(Config.bool("No Flagging"))
			return;
		nfGame = false;
		clicks++;
		if(!focus.isOpen()) {
			actions++;
			minesRemaining.set(minesRemaining.get() + (focus.setFlag() ? -1 : 1));
		}
	}
	
	private void open() {
		clicks++;
		if(openTile(focus))
			actions++;
	}
	
	private void chord() {
		clicks++;
		List<Board.Tile> list = board.getNeighborsOf(focus, Board.Tile::canOpen);
		if(focus.canChord()) {
			actions++;
			list.forEach(this::openTile);
		}
	}
	
	private boolean openTile(Board.Tile tile) {
		if(!tile.canOpen())
			return false;
		tile.setOpen(true);
		tilesOpen++;
		if(tile.isMine()) {
			lose();
			return true;
		} else if(tile.getMinesNearby() == 0)
			board.getNeighborsOf(tile, tile::isNotSelf).forEach(this::openTile);
		return true;
	}

	public void listenToRemainingMines(ChangeListener<? super Number> listener) {
		minesRemaining.addListener(listener);
	}
	
	public int getRemainingMines() {
		return minesRemaining.get();
	}
	
	public static Game get() {
		return INSTANCE;
	}
}
