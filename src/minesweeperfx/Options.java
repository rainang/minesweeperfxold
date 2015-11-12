package minesweeperfx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Options {

	public static final String HOURGLASS = "\u231B";
	public static final String WATCH     = "\u231A";
	public static final String FLAG      = "\u2690";
	public static final String MINE      = "\u2738";
	
	private static final IntegerProperty difficulty = new SimpleIntegerProperty(-1);
	
	private static final IntegerProperty tileSize = new SimpleIntegerProperty(30);
	
	private static final BooleanProperty nf = new SimpleBooleanProperty();

	private static final BooleanProperty showFlagCount = new SimpleBooleanProperty();
	private static final BooleanProperty showTimer     = new SimpleBooleanProperty();
	private static final BooleanProperty showMinutes   = new SimpleBooleanProperty();
	
	public static final int BORDER_SIZE    = 4;
	public static final int BORDER_PADDING = 5;
	
	public static IntegerProperty getDifficultyProperty() {
		return difficulty;
	}
	
	public static IntegerProperty getTileSizeProperty() {
		return tileSize;
	}
	
	public static BooleanProperty getNFProperty() {
		return nf;
	}
	
	public static BooleanProperty getShowFlagCountProperty() {
		return showFlagCount;
	}
	
	public static BooleanProperty getShowTimerProperty() {
		return showTimer;
	}

	public static BooleanProperty getShowMinutesProperty() {
		return showMinutes;
	}
	
	public static int getColumns() {
		switch(difficulty.get()) {
		case 0:
			return 9;
		case 1:
			return 16;
		default:
			return 30;
		}
	}
	
	public static int getRows() {
		switch(difficulty.get()) {
		case 0:
			return 9;
		default:
			return 16;
		}
	}
	
	public static int getMines() {
		switch(difficulty.get()) {
		case 0:
			return 10;
		case 1:
			return 40;
		default:
			return 99;
		}
	}

	public static boolean getNF() {
		return nf.get();
	}
	
	public static int getDifficulty() {
		return difficulty.intValue();
	}
	
	public static double getTileSize() {
		return tileSize.doubleValue();
	}

	public static boolean getShowFlagCount() {
		return showFlagCount.get();
	}

	public static boolean getShowTimer() {
		return showTimer.get();
	}
	
	public static void setDifficulty(int i) {
		difficulty.set(i);
	}
}
