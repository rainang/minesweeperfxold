package minesweeper.game;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

public enum GameState {

	INIT, NEW, RESTART, PLAY, PAUSE, WIN, LOSE;

	private static final Property STATE = new Property();

	public static GameState getGameState() { return STATE.get(); }

	public static void listenToGame(ChangeListener<? super GameState> listener) {
		STATE.addListener(listener);
	}

	public static void newGame() { STATE.set(INIT); }

	public static void restart() { STATE.set(RESTART); }

	public static void play() { STATE.set(PLAY); }

	public static void pause() {
		pause(STATE.get() != PAUSE);
	}

	public static void pause(boolean b) {
		if(b)
			STATE.set(PAUSE);
		else
			STATE.set(PLAY);
	}

	public static boolean isGameOver() { return STATE.get() == WIN || STATE.get() == LOSE; }

	public static boolean isGameStarted() { return STATE.get() != NEW && STATE.get() != RESTART; }

	protected static void win() {
		STATE.set(WIN);
	}

	protected static void lose() {
		STATE.set(LOSE);
	}

	public static class Property extends SimpleObjectProperty<GameState> {

		@Override
		public void set(GameState value) {
			switch(value) {
			case INIT:
				super.set(value);
				super.set(NEW);
				break;
			case NEW:
			case RESTART:
				super.set(value);
				break;
			case PAUSE:
				if(PLAY.equals(get()))
					super.set(value);
				break;
			case PLAY:
				if(!WIN.equals(get()) && !LOSE.equals(get()))
					super.set(value);
				break;
			case WIN:
			case LOSE:
				if(PLAY.equals(get()))
					super.set(value);
				break;
			}
		}
	}
}
