package minesweeper.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.FileChooser;
import minesweeper.game.Config;

public class IO {

	public static final String MAIN_PROFILE = System.getProperty("user.name");
	
	public static final String DIRECTORY  = System.getenv("APPDATA") + "\\MinesweeperFX\\";
	public static final String DIR_BOARDS = DIRECTORY + "boards\\";
	public static final String DIR_USER   = DIRECTORY + "profiles\\";

	public static final String PATH_CFG   = DIRECTORY + "minesweeperfx.dat";
	public static final String PATH_STATS = DIR_USER + MAIN_PROFILE + ".dat";

	public static final File   FILE_CONFIG = new File(DIRECTORY + "minesweeperfx.ini");
	public static final String FILE_STATS  = DIR_USER + MAIN_PROFILE + "\\stats.dat";
	public static final String FILE_HIGHS  = DIR_USER + MAIN_PROFILE + "\\highscores.dat";

	public static final FileChooser FILE_CHOOSER = new FileChooser();

	public static void init() {
		for(String path : new String[] { DIR_BOARDS, DIR_USER }) {
			File dir = new File(path);
			if(!dir.isDirectory())
				dir.mkdirs();
		}

		File file = new File(PATH_STATS);
		if(!file.exists()) {
			byte[] bytes = new byte[84];
			bytes[14 + 1] = 1;
			bytes[14*2] = 1;
			bytes[14*3] = 1;
			bytes[14*3 + 1] = 1;
			bytes[14*4] = 2;
			bytes[14*5] = 2;
			bytes[14*5 + 1] = 1;
			writeBytes(bytes, PATH_STATS);
		}
		file = new File(PATH_CFG);
		if(!file.exists())
			writeBytes(new Config().toBytes(), PATH_CFG);

		FILE_CHOOSER.setInitialDirectory(new File(DIR_BOARDS));
		FILE_CHOOSER.getExtensionFilters().add(new FileChooser.ExtensionFilter("Minesweeper Board File", "*.msb"));
	}
	
	public static List<String> read(String filename) {
		List<String> lines = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			br.lines().forEach(lines::add);
			br.close();
		} catch(IOException ignored) {}
		return lines;
	}

	public static void writeBytes(byte[] bytes, String path) {
		try {
			OutputStream output = new BufferedOutputStream(new FileOutputStream(path));
			output.write(bytes);
			output.close();
		} catch(FileNotFoundException ex) {
			System.out.println("File not found.");
		} catch(IOException ignore) {}
	}

	public static byte[] readBytes(File file) {
		byte[] result = new byte[(int)file.length()];
		try {
			int totalBytesRead = 0;
			InputStream input = new BufferedInputStream(new FileInputStream(file));
			while(totalBytesRead < result.length) {
				int bytesRemaining = result.length - totalBytesRead;
				//input.read() returns -1, 0, or more :
				int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
				if(bytesRead > 0) {
					totalBytesRead = totalBytesRead + bytesRead;
				}
			}
		} catch(FileNotFoundException ex) {
			System.out.println("File not found.");
		} catch(IOException ignore) {}
		return result;
	}
}
