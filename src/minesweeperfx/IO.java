package minesweeperfx;

import java.io.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class IO {

	public static final String DIRECTORY = System.getenv("APPDATA") + "\\MinesweeperFX\\";

	public static void write(String filename, ObservableList<String> lines) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(DIRECTORY + filename);
			lines.forEach(pw::println);
			pw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static ObservableList<String> read(String filename) {
		ObservableList<String> lines = FXCollections.observableArrayList();
		try {
			BufferedReader br = new BufferedReader(new FileReader(DIRECTORY + filename));
			br.lines().forEach(lines::add);
			br.close();
		} catch(IOException ignored) {}
		return lines;
	}

	public static void initMSFXDirectory() {
		File dir = new File(DIRECTORY);
		if(!dir.exists())
			if(dir.mkdirs())
				System.out.println("ERROR: Unable to create directory!");
	}
}
