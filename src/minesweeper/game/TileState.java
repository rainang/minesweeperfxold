package minesweeper.game;

import javafx.scene.paint.Color;

public enum TileState {
	EMPTY("", Color.BLACK, false),
	ONE("1", Color.BLUE, false),
	TWO("2", Color.GREEN, false),
	THREE("3", Color.RED, false),
	FOUR("4", Color.DARKBLUE, false),
	FIVE("5", Color.DARKRED, false),
	SIX("6", Color.TEAL, false),
	SEVEN("7", Color.GRAY, false),
	EIGHT("8", Color.BLACK, false),
	DEFAULT("", Color.BLACK, true),

	FLAG("\u2690", Color.BLACK, true),
	FLAG_WRONG("\u2690", Color.RED, true),

	MINE("\u2738", Color.BLACK, false),
	MINE_WRONG("\u2738", Color.RED, false);

	public final String  text;
	public final Color   color;
	public final boolean hasBorder;

	TileState(String text, Color color, boolean hasBorder) {
		this.text = text;
		this.color = color;
		this.hasBorder = hasBorder;
	}
}