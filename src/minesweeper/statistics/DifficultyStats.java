package minesweeper.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import minesweeper.game.Difficulty;
import minesweeper.io.Contractible;

import static minesweeper.io.Contractible.*;

public class DifficultyStats implements Contractible, Observable {
	
	private Difficulty difficulty;
	
	private int flagType;

	private int gamesWon;
	private int gamesLost;
	private int currentWinningStreak;
	private int longestWinningStreak;
	private int currentLosingStreak;
	private int longestLosingStreak;

	private final List<InvalidationListener> listeners = new ArrayList<>();

	private DifficultyStats() {}
	
	public DifficultyStats(int[] stats) {
		this.difficulty = Difficulty.values()[stats[0]];
		this.flagType = stats[1];
		
		gamesWon = stats[2];
		gamesLost = stats[3];
		currentWinningStreak = stats[4];
		longestWinningStreak = stats[5];
		currentLosingStreak = stats[6];
		longestLosingStreak = stats[7];
	}
	
	public DifficultyStats(byte[] bytes) {
		fromBytes(bytes);
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public int getFlagType() {
		return flagType;
	}

	public int getGamesWon() {
		return gamesWon;
	}

	public int getGamesLost() {
		return gamesLost;
	}

	public int getCurrentWinningStreak() {
		return currentWinningStreak;
	}

	public int getLongestWinningStreak() {
		return longestWinningStreak;
	}

	public int getCurrentLosingStreak() {
		return currentLosingStreak;
	}

	public int getLongestLosingStreak() {
		return longestLosingStreak;
	}

	public int getGamesPlayed() {
		return getGamesWon() + getGamesLost();
	}

	public double getWinRate() {
		return (double)getGamesWon()/getGamesPlayed();
	}

	public long getBestTime() {
		List<GameData> list = highScores();
		long l = list.isEmpty() ? 0 : list.get(0).score;
		for(GameData gs : list)
			l = Math.min(l, gs.score);
		return l;
	}

	public long getWorstTime() {
		List<GameData> list = highScores();
		long l = 0;
		for(GameData gs : list)
			l = Math.max(l, gs.score);
		return l;
	}

	public long getAverageTime() {
		List<GameData> list = filtered(gs -> gs.score != 0);
		return list.isEmpty() ? 0 : (long)list.stream().mapToLong(gs -> gs.score).average().getAsDouble();
	}

	public double getAverage3BV() {
		List<GameData> list = filtered(gs -> gs.b3v != 0);
		return list.isEmpty() ? 0 : list.stream().mapToInt(gs -> gs.b3v).average().getAsDouble();
	}

	public double getAverage3BVs() {
		List<GameData> list = filtered(gs -> gs.get3BVs() != 0);
		return list.isEmpty() ? 0 : list.stream().mapToDouble(GameData::get3BVs).average().getAsDouble();
	}

	public double getAverageIOE() {
		List<GameData> list = filtered(gs -> gs.getIOE() != 0);
		return list.isEmpty() ? 0 : list.stream().mapToDouble(GameData::getIOE).average().getAsDouble();
	}

	public double getAverageRQP() {
		List<GameData> list = filtered(gs -> gs.getRQP() != 0);
		return list.isEmpty() ? 0 : list.stream().mapToDouble(GameData::getRQP).average().getAsDouble();
	}

	public double getAverageIOS() {
		List<GameData> list = filtered(gs -> gs.getIOS() != 0);
		return list.isEmpty() ? 0 : list.stream().mapToDouble(GameData::getIOS).average().getAsDouble();
	}

	public List<GameData> filtered(Predicate<GameData> predicate) {
		return highScores().stream().filter(predicate).collect(Collectors.toList());
	}

	public List<GameData> highScores() {
		return Profile.MAIN.highs.stream().filter(gs -> gs.difficulty == difficulty && (gs.nf ? 1 : 0) == flagType)
								 .collect(Collectors.toList());
	}
	
	public void addGamePlayed(boolean win) {
		if(win) {
			gamesWon++;
			currentWinningStreak++;
			currentLosingStreak = 0;
			if(currentWinningStreak > longestWinningStreak)
				longestWinningStreak = currentWinningStreak;
		} else {
			gamesLost++;
			currentLosingStreak++;
			currentWinningStreak = 0;
			if(currentLosingStreak > longestLosingStreak)
				longestLosingStreak = currentLosingStreak;
		}
		listeners.forEach(l -> l.invalidated(this));
	}
	
	@Override
	public byte[] toBytes() {
		return merge(new byte[] { (byte)difficulty.id, (byte)flagType, }, contract(gamesWon), contract(gamesLost),
					 contract(currentWinningStreak), contract(longestWinningStreak), contract(currentLosingStreak),
					 contract(longestLosingStreak));
	}
	
	@Override
	public void fromBytes(byte[] bytes) {
		difficulty = Difficulty.values()[bytes[0]];
		flagType = bytes[1];
		gamesWon = expand(new byte[] { bytes[2], bytes[3] });
		gamesLost = expand(new byte[] { bytes[4], bytes[5] });
		currentWinningStreak = expand(new byte[] { bytes[6], bytes[7] });
		longestWinningStreak = expand(new byte[] { bytes[8], bytes[9] });
		currentLosingStreak = expand(new byte[] { bytes[10], bytes[1] });
		longestLosingStreak = expand(new byte[] { bytes[12], bytes[13] });
	}

	@Override
	public void addListener(InvalidationListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		listeners.remove(listener);
	}

	public static class Merged extends DifficultyStats {

		private final DifficultyStats[] stats;

		private int id = 0;

		public Merged(DifficultyStats... stats) {
			this.stats = stats;
			this.id = stats.length == 2 ? 0 : 1;
		}

		@Override
		public Difficulty getDifficulty() {
			return stats[0].difficulty;
		}

		@Override
		public int getFlagType() {
			return id == 0 ? 2 : stats[0].flagType;
		}

		@Override
		public int getGamesWon() {
			return Arrays.stream(stats).mapToInt(DifficultyStats::getGamesWon).sum();
		}

		@Override
		public int getGamesLost() {
			return Arrays.stream(stats).mapToInt(DifficultyStats::getGamesLost).sum();
		}

		@Override
		public int getLongestLosingStreak() {
			return Arrays.stream(stats).mapToInt(DifficultyStats::getLongestLosingStreak).sum();
		}

		@Override
		public List<GameData> highScores() {
			List<GameData> list = new ArrayList<>();
			Arrays.stream(stats).map(DifficultyStats::highScores).forEach(list::addAll);
			return list;
		}
	}
}