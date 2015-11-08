package minesweeperfx.statistics;

import java.util.Date;
import javafx.beans.property.*;

public class GameStats {
	
	private final IntegerProperty difficulty = new SimpleIntegerProperty();
	private final BooleanProperty nf         = new SimpleBooleanProperty();
	private final LongProperty    time       = new SimpleLongProperty();
	private final LongProperty    date       = new SimpleLongProperty();

	private final StringProperty       stringDifficulty = new SimpleStringProperty();
	private final StringProperty       stringTime       = new SimpleStringProperty();
	private final ObjectProperty<Date> stringDate       = new SimpleObjectProperty<>();

	public GameStats(int difficulty, boolean nf, long time, long date) {
		this.difficulty.set(difficulty);
		this.nf.set(nf);
		this.time.set(time);
		this.date.set(date);
		switch(difficulty) {
		case 0:
			stringDifficulty.set("Beginner");
			break;
		case 1:
			stringDifficulty.set("Intermediate");
			break;
		case 2:
			stringDifficulty.set("Expert");
			break;
		}
		stringTime.set(String.format("%02d:%02d:%02d", time/100/60, time/100%60, time%100));
		stringDate.set(new Date(date));
	}
	
	public int getDifficulty() {
		return difficulty.intValue();
	}
	
	public StringProperty getDifficultyProperty() {
		return stringDifficulty;
	}
	
	public boolean getNF() {
		return nf.get();
	}
	
	public BooleanProperty getNFProperty() {
		return nf;
	}
	
	public long getTime() {
		return time.get();
	}
	
	public StringProperty getTimeProperty() {
		return stringTime;
	}
	
	public long getDate() {
		return date.get();
	}
	
	public ObjectProperty<Date> getDateProperty() {
		return stringDate;
	}
	
	public String getFormattedTime() {
		return String.format("%02d:%02d:%02d", time.get()/100/60, time.get()/100%60, time.get()%100);
	}
}
