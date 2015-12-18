package minesweeper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import minesweeper.game.Config;

public class Formatter {

	private static final DecimalFormat df      = new DecimalFormat("00");
	private static final DecimalFormat df2     = new DecimalFormat(".000");
	private static final DecimalFormat RATIO   = new DecimalFormat("0.00");
	private static final DecimalFormat PERCENT = new DecimalFormat("0.00%");

	public static final Formatter FORMAT = new Formatter();

	private static final List<Consumer<Formatter>> SCORE_FORMAT_LISTENERS = new ArrayList<>();

	public static void init() {
		Config.boolListen("Minutes", (a, b, c) -> SCORE_FORMAT_LISTENERS.forEach(e -> e.accept(FORMAT)));
		Config.boolListen("Milliseconds", (a, b, c) -> SCORE_FORMAT_LISTENERS.forEach(e -> e.accept(FORMAT)));
	}

	public static void addScoreFormatListener(Consumer<Formatter> listener) {
		SCORE_FORMAT_LISTENERS.add(listener);
	}

	public static String formatToRatio(double d) {
		return RATIO.format(d);
	}

	public static String formatToPercent(double d) {
		return PERCENT.format(d);
	}

	public static String formatToScore(long l) {
		boolean b1 = Config.bool("Minutes");
		boolean b2 = Config.bool("Milliseconds");
		String s = "";
		if(b1)
			s += df.format(l/1000/60) + ":" + df.format(l/1000%60);
		else
			s += (int)l/1000;
		if(b2)
			s += df2.format(l%1000/1000d);
		return s;
	}

	public static StringProperty getFormattedScoreProperty(long time) {
		StringProperty property = new SimpleStringProperty(formatToScore(time));
		addScoreFormatListener(f -> property.set(f.format(time)));
		return property;
	}

	public static void listenToScoreFormatChange(Consumer<Formatter> listener) {
		SCORE_FORMAT_LISTENERS.add(listener);
	}

	public String format(long l) {
		return formatToScore(l);
	}
}
