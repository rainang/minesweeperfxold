package minesweeper.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.util.Duration;

public class Clock {

	private Timeline timeline;

	private final LongProperty longTime = new SimpleLongProperty();

	private final BooleanProperty pause = new SimpleBooleanProperty();

	public Clock() {
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1), e -> longTime.set(longTime.get() + 1)));
		longTime.set(0);
		pause.addListener((a, b, c) -> {
			if(c)
				timeline.pause();
			else
				play();
		});
	}

	protected void reset() {
		pause.set(false);
		timeline.stop();
		longTime.set(0);
	}

	protected void play() {
		pause.set(false);
		timeline.play();
	}

	protected void stop() {
		timeline.stop();
	}

	protected void pause() {
		pause.set(true);
	}

	public long getTime() {
		return longTime.get();
	}

	public void listenToTime(ChangeListener<? super Number> listener) {
		longTime.addListener(listener);
	}
}
