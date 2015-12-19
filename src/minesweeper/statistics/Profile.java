package minesweeper.statistics;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import minesweeper.io.Contractible;
import minesweeper.io.IO;
import minesweeper.io.Readable;
import minesweeper.io.Writable;

import static minesweeper.game.Difficulty.*;

public class Profile implements Readable, Writable, Contractible {

	public static final Map<String, Profile>     PROFILES    = new HashMap<>();
	public static final Profile                  MAIN        = new Profile(System.getProperty("user.name"));
	public static final ObservableList<GameData> HIGH_SCORES = FXCollections.observableArrayList();

	public final DifficultyStats[][]      stats = new DifficultyStats[4][3];
	public final ObservableList<GameData> highs = FXCollections.observableArrayList();
	public final String name;
	public final File   file;

	public Profile(String name) {
		this.name = name;
		this.file = new File(IO.DIR_USER + name + ".dat");
		PROFILES.put(name, this);
	}

	private void init() {
		read(file);
	}

	public void addGameStats(GameData gameData, boolean win) {
		if(win)
			addGameStats(gameData);
		stats[gameData.difficulty.id][gameData.nf ? 1 : 0].addGamePlayed(win);
		write(file);
	}

	public DifficultyStats getStats(int id, int flagType) {
		return stats[id][flagType];
	}

	private void addGameStats(GameData gameData) {
		HIGH_SCORES.add(gameData);
		highs.add(gameData);
		HIGH_SCORES.sort(GameData::compareTo);
		highs.sort(GameData::compareTo);
	}

	@Override
	public byte[] toBytes() {
		List<Byte> dsBytes = new ArrayList<>();
		List<Byte> gsBytes = new ArrayList<>();
		for(int dif = 0, pass = 0; dif < 3; dif++)
			for(int flag = 0; flag < 2; flag++, pass++)
				for(byte b : stats[dif][flag].toBytes())
					dsBytes.add(b);
		for(GameData gs : highs)
			for(byte b : gs.toBytes())
				gsBytes.add(b);

		dsBytes.addAll(gsBytes);
		byte[] ss = new byte[dsBytes.size()];
		for(int i = 0; i < dsBytes.size(); i++)
			ss[i] = dsBytes.get(i);
		return ss;
	}

	@Override
	public void fromBytes(byte[] bytes) {
		if(bytes.length == 0)
			System.out.println("Profile data for " + name + " not found.");
		int aLength = 14;
		for(int dif = 0, pass = 0; dif < 3; dif++)
			for(int flag = 0; flag < 2; flag++, pass++) {
				byte[] bs = new byte[aLength];
				if(bytes.length == 0) {
					bs[0] = (byte)dif;
					bs[1] = (byte)flag;
				} else
					System.arraycopy(bytes, pass*aLength, bs, 0, aLength);
				stats[dif][flag] = new DifficultyStats(bs);
			}
		stats[0][2] = new DifficultyStats.Merged(stats[BEGINNER.id][0], stats[BEGINNER.id][1]);
		stats[1][2] = new DifficultyStats.Merged(stats[INTERMEDIATE.id][0], stats[INTERMEDIATE.id][1]);
		stats[2][2] = new DifficultyStats.Merged(stats[EXPERT.id][0], stats[EXPERT.id][1]);

		stats[3][0] = new DifficultyStats.Merged(stats[BEGINNER.id][0], stats[INTERMEDIATE.id][0], stats[EXPERT
				.id][0]);
		stats[3][1] = new DifficultyStats.Merged(stats[BEGINNER.id][1], stats[INTERMEDIATE.id][1], stats[EXPERT
				.id][1]);
		stats[3][2] = new DifficultyStats.Merged(stats[BEGINNER.id][2], stats[INTERMEDIATE.id][2], stats[EXPERT
				.id][2]);

		if(bytes.length <= 84)
			return;

		ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(bytes, aLength*6, bytes.length));
		byte[] b;
		while(bb.hasRemaining()) {
			bb.mark();
			int i = bb.get() + 25;
			bb.reset();
			b = new byte[i];
			bb.get(b, 0, i);
			addGameStats(new GameData(name, b));
		}
	}

	public static void load() {
		MAIN.init();
		File[] files = new File(IO.DIR_USER).listFiles(file -> {
			int i = file.getName().length();
			return file.getName().substring(i - 4).equals(".dat") &&
					!file.getName().substring(0, i - 4).equals(System.getProperty("user.name"));
		});
		for(File f : files)
			new Profile(f.getName().substring(0, f.getName().length() - 4)).init();
	}
}
