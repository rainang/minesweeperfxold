package minesweeperfx;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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
			pw.println("Difficulty:" + Options.getDifficulty());
			pw.println("NF:" + Options.getNF());
			pw.println("Show Flag:" + Options.getShowFlagCount());
			pw.println("Show Timer:" + Options.getShowTimer());
			pw.println("Tile Size:" + Options.getTileSize());
			pw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadSettings() {
		BufferedReader br;
		Map<String, Object> map = new HashMap<>();
		try {
			br = new BufferedReader(new FileReader(DIRECTORY + "minesweeperfx.cfg"));
			br.lines().forEach(s -> map.put(s.substring(0, s.indexOf(":")), s.substring(s.indexOf(":") + 1)));
			br.close();
		} catch(IOException e) {
			saveSettings();
		}
		Options.setDifficulty(Integer.parseInt((String)map.get("Difficulty")));
		Options.getNFProperty().set(Boolean.parseBoolean((String)map.get("NF")));
		Options.getShowFlagCountProperty().set(Boolean.parseBoolean((String)map.get("Show Flag")));
		Options.getShowTimerProperty().set(Boolean.parseBoolean((String)map.get("Show Timer")));
		Options.getTileSizeProperty().set((int)Double.parseDouble((String)map.get("Tile Size")));
	}
}
