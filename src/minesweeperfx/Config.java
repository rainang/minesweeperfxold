package minesweeperfx;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static minesweeperfx.Options.*;

public class Config {

	public static final String DIRECTORY = System.getenv("APPDATA") + "\\MinesweeperFX\\";

	public static void saveSettings() {
		File dir = new File(DIRECTORY);
		if(!dir.exists())
			if(dir.mkdirs())
				System.out.println("Unable to create config file!");
		PrintWriter pw;
		try {
			pw = new PrintWriter(DIRECTORY + "minesweeperfx.cfg");
			pw.println("Difficulty:" + getDifficulty());
			pw.println("NF:" + getNF());
			pw.println("Show Flag:" + getShowFlagCount());
			pw.println("Show Timer:" + getShowTimer());
			pw.println("Tile Size:" + (int)getTileSize());
			pw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadSettings() {
		Map<String, String> map = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(DIRECTORY + "minesweeperfx.cfg"));
			br.lines().forEach(s -> map.put(s.substring(0, s.indexOf(":")), s.substring(s.indexOf(":") + 1)));
			br.close();
		} catch(IOException ignored) {}
		if(!map.values().isEmpty())
			initializeSettings(Integer.parseInt(map.get("Difficulty")), Boolean.parseBoolean(map.get("NF")),
							   Boolean.parseBoolean(map.get("Show Flag")), Boolean.parseBoolean(map.get("Show Timer")),
							   Integer.parseInt(map.get("Tile Size")));
		else
			initializeSettings(0, false, true, true, 30);
	}

	private static void initializeSettings(
			int difficulty, boolean nf, boolean showFlag, boolean showTimer, int tileSize) {
		Options.setDifficulty(difficulty);
		Options.getNFProperty().set(nf);
		Options.getShowFlagCountProperty().set(showFlag);
		Options.getShowTimerProperty().set(showTimer);
		Options.getTileSizeProperty().set(tileSize);
	}
}
