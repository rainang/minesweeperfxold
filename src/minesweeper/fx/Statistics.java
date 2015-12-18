package minesweeper.fx;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import minesweeper.Formatter;
import minesweeper.MinesweeperFX;
import minesweeper.statistics.DifficultyStats;
import minesweeper.statistics.Profile;

public class Statistics extends HBox implements InvalidationListener {

	private final HBox play    = newHBox("Games Played");
	private final HBox won     = newHBox("Games Won");
	private final HBox lost    = newHBox("Games Lost");
	private final HBox winRate = newHBox("Win Rate");
	private final HBox best    = newHBox("Best Time");
	private final HBox worst   = newHBox("Worst Time");

	private final HBox aveTime = newHBox("Average Time");
	private final HBox ave3BV  = newHBox("Average 3BV");
	private final HBox ave3BVs = newHBox("Average 3BV/s");
	private final HBox aveIOE  = newHBox("Average IOE");
	private final HBox aveRQP  = newHBox("Average RQP");
	private final HBox aveIOS  = newHBox("Average IOS");

	private final HBox cws = newHBox("Current Winning Streak");
	private final HBox lws = newHBox("Longest Winning Streak");
	private final HBox cls = newHBox("Current Losing Streak");
	private final HBox lls = newHBox("Longest Losing Streak");

	private final ObjectProperty<DifficultyStats> currentPage = new SimpleObjectProperty<>();

	public Statistics(IntegerProperty i1, IntegerProperty i2) {
		currentPage.addListener((a, b, c) -> {
			if(b != null)
				b.removeListener(this);
			c.addListener(this);
			invalidated(c);
		});
		Formatter.addScoreFormatListener(f -> {
			setText(best, f.format(currentPage.get().getBestTime()));
			setText(worst, f.format(currentPage.get().getWorstTime()));
			setText(aveTime, f.format(currentPage.get().getAverageTime()));
		});
		currentPage.set(Profile.MAIN.getStats(i1.get(), i2.get()));
		i1.addListener(o -> {
			currentPage.set(Profile.MAIN.getStats(i1.get(), i2.get()));
		});
		i2.addListener(o -> {
			currentPage.set(Profile.MAIN.getStats(i1.get(), i2.get()));
		});

		setMinSize(490, 290);
		setSpacing(50);
		setPadding(new Insets(20));

		VBox left = new VBox(play, won, lost, winRate, best, worst, emptyHBox(), cws, lws);
		VBox right = new VBox(aveTime, ave3BV, ave3BVs, aveIOE, aveRQP, aveIOS, emptyHBox(), cls, lls);
		while(left.getChildren().size() != 10)
			left.getChildren().add(emptyHBox());
		getChildren().addAll(left, right);
	}

	public HBox newHBox(String label) {
		Label l = new Label(label);
		Text t = new Text();
		HBox h = emptyHBox();

		l.setContextMenu(new ContextMenu(MenuBar.checkMenuItem("Minutes"), MenuBar.checkMenuItem("Milliseconds")));
		h.getChildren().addAll(l, t);
		t.setWrappingWidth(50);
		l.setMinSize(150, h.getMinHeight());
		l.setMaxSize(150, h.getMinHeight());

		t.setTextAlignment(TextAlignment.RIGHT);
		h.setOnMouseEntered(e -> h.setBackground(Painter.TILE_HIGHLIGHT));
		h.setOnMouseExited(e -> h.setBackground(null));
		return h;
	}

	public HBox emptyHBox() {
		HBox box = new HBox();
		box.setMinSize(200, 20);
		box.setMaxSize(200, 20);
		box.setStyle(MinesweeperFX.STYLE);
		return box;
	}

	private void setText(HBox box, String text) {
		((Text)box.getChildren().get(1)).setText(text);
	}

	@Override
	public void invalidated(Observable observable) {
		DifficultyStats ds = currentPage.get();
		setText(won, ds.getGamesWon() + "");
		setText(lost, ds.getGamesLost() + "");
		setText(play, ds.getGamesPlayed() + "");
		setText(winRate, Formatter.formatToPercent(ds.getWinRate()));
		setText(best, Formatter.formatToScore(ds.getBestTime()));
		setText(worst, Formatter.formatToScore(ds.getWorstTime()));

		setText(aveTime, Formatter.formatToScore(ds.getAverageTime()));
		setText(ave3BV, Formatter.formatToRatio(ds.getAverage3BV()));
		setText(ave3BVs, Formatter.formatToRatio(ds.getAverage3BVs()));
		setText(aveIOE, Formatter.formatToRatio(ds.getAverageIOE()));
		setText(aveRQP, Formatter.formatToRatio(ds.getAverageRQP()));
		setText(aveIOS, Formatter.formatToRatio(ds.getAverageIOS()));

		if(ds instanceof DifficultyStats.Merged) {
			cws.setVisible(false);
			cls.setVisible(false);
			lws.setVisible(false);
			lls.setVisible(false);
		} else {
			cws.setVisible(true);
			cls.setVisible(true);
			lws.setVisible(true);
			lls.setVisible(true);
			setText(cws, ds.getCurrentWinningStreak() + "");
			setText(lws, ds.getLongestWinningStreak() + "");
			setText(cls, ds.getCurrentLosingStreak() + "");
			setText(lls, ds.getLongestLosingStreak() + "");
		}
	}
}
