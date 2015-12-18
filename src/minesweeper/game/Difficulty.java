package minesweeper.game;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

public enum Difficulty {
	BEGINNER("Beginner", 0, 9, 9, 10),
	INTERMEDIATE("Intermediate", 1, 16, 16, 40),
	EXPERT("Expert", 2, 30, 16, 99);

	private static final SimpleObjectProperty<Difficulty> INSTANCE = new SimpleObjectProperty<>();

	public final String name;
	public final int    id;
	public final int    columns;
	public final int    rows;
	public final int    mines;

	Difficulty(String n, int i, int c, int r, int m) {
		name = n;
		id = i;
		columns = c;
		rows = r;
		mines = m;
	}

	protected static void set(Difficulty difficulty) {
		INSTANCE.set(difficulty == null ? BEGINNER : difficulty);
	}

	public static Difficulty get() {
		return INSTANCE.get();
	}

	public static void bind(ObjectProperty<Difficulty> property) {
		INSTANCE.bindBidirectional(property);
	}

	public static void listen(ChangeListener<? super Difficulty> listener) {
		INSTANCE.addListener(listener);
	}

	public static int id() {
		return INSTANCE.get().id;
	}

	public static int columns() {
		return INSTANCE.get().columns;
	}

	public static int rows() {
		return INSTANCE.get().rows;
	}

	public static int mines() {
		return INSTANCE.get().mines;
	}
}
