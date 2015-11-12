package minesweeperfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.util.Duration;

import static minesweeperfx.Options.getShowMinutesProperty;

public class Timer {

	private final LongProperty   longTime      = new SimpleLongProperty(-1);
	private final StringProperty formattedTime = new SimpleStringProperty();

	private Timeline timeline;

	private final BooleanProperty pause = new SimpleBooleanProperty();

	public Timer() {
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(10), e -> longTime.set(longTime.get() + 1)));
		longTime.addListener(e -> formattedTime.set(Options.WATCH + " " + toString()));
		longTime.set(0);
		pause.addListener((a, b, c) -> {
			if(c)
				timeline.pause();
			else
				play();
		});
	}

	public void reset() {
		pause.set(false);
		timeline.stop();
		longTime.set(0);
	}

	public void play() {
		timeline.play();
	}

	public void stop() {
		timeline.stop();
	}

	public void pause() {
		pause.set(!pause.get());
	}
	
	public long getLongTime() {
		return longTime.get();
	}
	
	public StringProperty getFormattedTimeProperty() {
		return formattedTime;
	}

	public BooleanProperty getPauseProperty() {
		return pause;
	}

	@Override
	public final String toString() {
		if(getShowMinutesProperty().get())
			return String.format("%02d:%02d:%02d", longTime.get()/100/60, longTime.get()/100%60, longTime.get()%100);
		return String.format("%03d:%02d", longTime.get()/100, longTime.get()%100);
	}
}
