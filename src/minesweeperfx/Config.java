package minesweeperfx;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static minesweeperfx.Constants.FILE_CONFIG;
import static minesweeperfx.Options.*;

public class Config {
	
	public static void saveSettings() {
		IO.write(FILE_CONFIG, FXCollections.observableArrayList("Difficulty:" + getDifficulty(), "NF:" + getNF(),
																"Show Flag:" + getShowFlagCount(),
																"Show Timer:" + getShowTimer(),
																"Tile Size:" + (int)getTileSize()));
	}
	
	public static void loadSettings() {
		ObservableList<String> list = IO.read(FILE_CONFIG);
		Map<String, String> map = new HashMap<>();
		list.forEach(s -> map.put(s.substring(0, s.indexOf(":")), s.substring(s.indexOf(":") + 1)));
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
