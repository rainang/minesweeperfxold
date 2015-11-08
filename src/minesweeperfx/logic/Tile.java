package minesweeperfx.logic;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Tile {

	public final int x;
	public final int y;
	
	private final BooleanProperty peek = new SimpleBooleanProperty();
	private final BooleanProperty open = new SimpleBooleanProperty();
	private final BooleanProperty flag = new SimpleBooleanProperty();
	private final BooleanProperty mine = new SimpleBooleanProperty();

	private final BooleanProperty isLosingTile = new SimpleBooleanProperty();

	private final IntegerProperty minesNearby = new SimpleIntegerProperty();
	private final IntegerProperty flagsNearby = new SimpleIntegerProperty();

	private final BooleanBinding canPeek      = Bindings.and(flag.not(), open.not());
	private final BooleanBinding canMultiOpen = Bindings.and(open, Bindings.equal(minesNearby, flagsNearby));

	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setPeek(boolean b) {
		peek.set(b);
	}
	
	public void setOpen(boolean b) {
		open.set(b);
	}
	
	public void setFlag(boolean b) {
		flag.set(b);
	}
	
	public void setMine(boolean b) {
		mine.set(b);
	}

	public void setIsLosingTile(boolean b) {
		isLosingTile.set(b);
	}
	
	public boolean getOpen() {
		return open.get();
	}
	
	public boolean getFlag() {
		return flag.get();
	}
	
	public boolean getMine() {
		return mine.get();
	}

	public int getMinesNearby() {
		return minesNearby.get();
	}
	
	public boolean getCanPeek() {
		return canPeek.get();
	}
	
	public boolean getCanMultiOpen() {
		return canMultiOpen.get();
	}

	public BooleanProperty getOpenProperty() {
		return open;
	}

	public BooleanProperty getPeekProperty() {
		return peek;
	}

	public BooleanProperty getFlagProperty() {
		return flag;
	}

	public BooleanProperty getMineProperty() {
		return mine;
	}

	public BooleanProperty getIsLosingTileProperty() {
		return isLosingTile;
	}

	public void reset(boolean soft) {
		if(!soft)
			setMine(false);
		setOpen(false);
		setPeek(false);
		setFlag(false);
		setIsLosingTile(false);
	}

	public void updateFlagsNearby(boolean updown) {
		flagsNearby.set(flagsNearby.get() + (updown ? 1 : -1));
	}

	public void updateMinesNearby(boolean updown) {
		minesNearby.set(minesNearby.get() + (updown ? 1 : -1));
	}
}
