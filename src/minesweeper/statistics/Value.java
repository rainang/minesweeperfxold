package minesweeper.statistics;

import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import minesweeper.io.Contractible;

public abstract class Value<V> implements ObservableValue<V>, Contractible {

	protected V value;
	private ExpressionHelper<V> helper = null;

	public Value() {}

	public Value(V value) {
		this.value = value;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		helper = ExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		helper = ExpressionHelper.removeListener(helper, listener);
	}

	@Override
	public void addListener(javafx.beans.value.ChangeListener<? super V> listener) {
		helper = ExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void removeListener(javafx.beans.value.ChangeListener<? super V> listener) {
		helper = ExpressionHelper.removeListener(helper, listener);
	}
}
