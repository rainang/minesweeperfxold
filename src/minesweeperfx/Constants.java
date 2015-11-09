package minesweeperfx;

import javafx.scene.image.Image;

public class Constants {

	/** The current game version */
	public static final String VERSION = "v1.2.0";

	/** The game icon */
	public static final Image ICON = new Image(MinesweeperFX.class.getResourceAsStream("bomb.png"));

	/** The main user profile name */
	public static final String PROFILE = System.getProperty("user.name");

	/** The main game directory */
	public static final String DIRECTORY = System.getenv("APPDATA") + "\\MinesweeperFX\\";

	/** The config file name */
	public static final String FILE_CONFIG  = "minesweeperfx.ini";

	/** The main profile file name */
	public static final String FILE_PROFILE = PROFILE + ".dat";
}
