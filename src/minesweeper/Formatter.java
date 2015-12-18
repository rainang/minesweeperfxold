package minesweeper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import minesweeper.game.Config;

public class Formatter {

	private static final DecimalFormat DF_MINUTES = new DecimalFormat("00");
	private static final DecimalFormat DF_MILLIS  = new DecimalFormat(".000");
	private static final DecimalFormat DF_RATIO   = new DecimalFormat("0.00");

	private static final List<FormatChangeListener> FORMAT_LISTENERS = new ArrayList<>();

	public static void init() {
		Config.boolListen("Minutes", (a, b, c) -> FORMAT_LISTENERS.forEach(FormatChangeListener::onFormatChanged));
		Config.boolListen("Milliseconds", (a, b, c) -> FORMAT_LISTENERS.forEach
				(FormatChangeListener::onFormatChanged));
	}

	public static String formatDouble(double d) {
		return DF_RATIO.format(d);
	}

	public static String formatScore(long l) {
		String s = "";
		if(Config.bool("Minutes"))
			s += DF_MINUTES.format(l/1000/60) + ":" + DF_MINUTES.format(l/1000%60);
		else
			s += (int)l/1000;
		if(Config.bool("Milliseconds"))
			s += DF_MILLIS.format(l%1000/1000d);
		return s;
	}

	public static void listenToScoreFormatChange(FormatChangeListener listener) {
		FORMAT_LISTENERS.add(listener);
	}

	public interface FormatChangeListener {

		void onFormatChanged();
	}
}
