package minesweeper.game;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

public enum InputState {
	IDLE, OPEN, FLAG, CHORD;

	private static final ObjectProperty<InputState> EVENT = new SimpleObjectProperty<>();

	private static int x;
	private static int y;

	public static InputState get() {
		return EVENT.get();
	}

	public static void setInput(int x, int y, InputState event) {
		InputState.x = x;
		InputState.y = y;
		EVENT.set(event);
	}

	public static void setIdle() {
		EVENT.set(IDLE);
	}

	public static int getX() {
		return x;
	}

	public static int getY() {
		return y;
	}

	public static void listenToInput(ChangeListener<? super InputState> listener) {
		EVENT.addListener(listener);
	}
}
