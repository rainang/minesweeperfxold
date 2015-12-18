package minesweeper.statistics;

import java.nio.ByteBuffer;
import java.util.Arrays;
import minesweeper.game.Difficulty;
import minesweeper.io.Contractible;

public class GameData implements Contractible, Comparable<GameData> {

	public String player;

	public Difficulty difficulty;

	public boolean nf;

	public int actions;
	public int clicks;

	public int b3v;
	public int openings;
	public int islands;

	public long score;
	public long date;

	public byte[] board;

	public GameData(
			String player, Difficulty difficulty, boolean nf, long score, long date, int actions, int clicks,
			int[] boardData, byte[] board) {

		this.player = player;
		this.difficulty = difficulty;
		this.nf = nf;
		this.score = score;
		this.date = date;
		this.actions = actions;
		this.clicks = clicks;
		this.b3v = boardData[0];
		this.openings = boardData[1];
		this.islands = boardData[2];
		this.board = board == null ? new byte[0] : board;
	}

	public GameData(String player, byte[] bytes) {
		this.player = player;
		fromBytes(bytes);
	}

	public double getIOE() { return b3v == 0 ? 0 : (double)b3v/actions; }

	public double get3BVs() { return b3v == 0 ? 0 : (double)b3v/(score/1000d); }

	public double getRQP() { return b3v == 0 ? 0 : (score/1000d)/(get3BVs()); }

	public double getIOS() { return b3v == 0 ? 0 : Math.log(b3v)/Math.log((score/1000d)); }

	@Override
	public byte[] toBytes() {
		ByteBuffer bb = ByteBuffer.allocate(25 + board.length);

		Arrays.stream(new int[] { board.length, difficulty.id, nf ? 1 : 0 }).forEach(i -> bb.put((byte)i));
		Arrays.stream(new int[] { b3v, openings, islands, actions, clicks })
			  .mapToObj(i -> new byte[] { (byte)(i/128), (byte)(i%128) }).forEach(bb::put);
		bb.putInt((int)score).putLong(date).put(board);
		return bb.array();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		byte[] b = new byte[bb.get()];
		difficulty = Difficulty.values()[bb.get()];
		nf = bb.get() == 1;
		b3v = bb.get()*128 + bb.get();
		openings = bb.get()*128 + bb.get();
		islands = bb.get()*128 + bb.get();
		actions = bb.get()*128 + bb.get();
		clicks = bb.get()*128 + bb.get();
		score = bb.getInt();
		date = bb.getLong();
		bb.get(b, 0, b.length);
		board = b;
	}

	@Override
	public int compareTo(GameData o) {
		return score > o.score ? 11 : score < o.score ? -1 : 0;
	}
}