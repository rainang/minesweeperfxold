package minesweeperfx.statistics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import minesweeperfx.IO;

public class Profile {

	private static final String                    PROFILE      = System.getProperty("user.name");
	private static final ObservableList<GameStats> STATS        = FXCollections.observableArrayList();
	private static final ProfileView               PROFILE_VIEW = new ProfileView(STATS);

	public static void writeStats(GameStats gameStats) {
		STATS.add(gameStats);
		ObservableList<String> list = FXCollections.observableArrayList();
		STATS.forEach(gs -> list
				.add(gs.getDifficulty() + ":" + (gs.getNF() ? 1 : 0) + ":" + gs.getTime() + ":" + gs.getDate()));
		IO.write(PROFILE + ".dat", list);
	}

	public static void readStats() {
		ObservableList<String> list = IO.read(PROFILE + ".dat");
		list.forEach(s -> {

			int difficulty = Integer.parseInt(s.substring(0, s.indexOf(":")));
			s = s.substring(s.indexOf(":") + 1);
			boolean nf = s.substring(0, s.indexOf(":")).equals("1");
			s = s.substring(s.indexOf(":") + 1);
			long time = Long.parseLong(s.substring(0, s.indexOf(":")));
			s = s.substring(s.indexOf(":") + 1);
			long date = Long.parseLong(s);
			STATS.add(new GameStats(difficulty, nf, time, date));
		});
	}

	public static void viewProfile() {
		PROFILE_VIEW.showProfile();
	}
}
