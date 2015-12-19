package minesweeper.io;

import java.io.*;
import javafx.stage.FileChooser;
import minesweeper.game.Config;

public class IO {

	public static final String MAIN_PROFILE = System.getProperty("user.name");

	public static final String DIRECTORY  = System.getenv("APPDATA") + "\\MinesweeperFX\\";
	public static final String DIR_BOARDS = DIRECTORY + "boards\\";
	public static final String DIR_USER   = DIRECTORY + "profiles\\";

	public static final File FILE_CFG   = new File(DIRECTORY + "minesweeperfx.dat");
	public static final File FILE_STATS = new File(DIR_USER + MAIN_PROFILE + ".dat");

	public static final FileChooser FILE_CHOOSER = new FileChooser();

	public static void init() {
		for(String path : new String[] { DIR_BOARDS, DIR_USER }) {
			File dir = new File(path);
			if(!dir.isDirectory())
				dir.mkdirs();
		}

		if(!FILE_STATS.exists())
			writeBytes(new byte[0], FILE_STATS.getAbsolutePath());
		if(!FILE_CFG.exists())
			Config.save();

		FILE_CHOOSER.setInitialDirectory(new File(DIR_BOARDS));
		FILE_CHOOSER.getExtensionFilters().add(new FileChooser.ExtensionFilter("Minesweeper Board File", "*.msb"));
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
