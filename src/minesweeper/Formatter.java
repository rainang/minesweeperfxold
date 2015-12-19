package minesweeper;

import com.sun.javafx.binding.ExpressionHelper;
import java.text.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import minesweeper.game.Config;

public class Formatter {

	public static final DecimalFormat DF_MINUTES = new DecimalFormat("00");
	public static final DecimalFormat DF_MILLIS  = new DecimalFormat(".000");
	public static final DecimalFormat DF_RATIO   = new DecimalFormat("0.00");

	public static final String                 DATE_FORMAT_PATTERN  = "EEE ddMMMyy hh:mm:ss a";
	public static final Format                 DATE_FORMAT          = new SimpleDateFormat(DATE_FORMAT_PATTERN);
	public static final ObjectProperty<Format> DATE_FORMAT_PROPERTY = new SimpleObjectProperty<>(DATE_FORMAT);

	public static final Format SCORE_FORMAT = getScoreFormat();

	public static final ObjectProperty<Format> SCORE_FORMAT_PROPERTY = new SimpleObjectProperty<>(SCORE_FORMAT);

	public static void init() {
		Config.boolListen("Minutes", (a, b, c) -> SCORE_FORMAT_PROPERTY.set(getScoreFormat()));
		Config.boolListen("Milliseconds", (a, b, c) -> SCORE_FORMAT_PROPERTY.set(getScoreFormat()));
	}

	public static Format getScoreFormat() {
		return new Format() {
			@Override
			public StringBuffer format(Object obj, StringBuffer sb, FieldPosition pos) {
				if(obj instanceof Long) {
					long l = (long)obj;
					if(Config.bool("Minutes"))
						sb.append(DF_MINUTES.format(l/1000/60)).append(":").append(DF_MINUTES.format(l/1000%60));
					else
						sb.append((int)l/1000);
					if(Config.bool("Milliseconds"))
						sb.append(DF_MILLIS.format(l%1000/1000d));
				}
				return sb;
			}

			@Override
			public Object parseObject(String source, ParsePosition pos) {
				return null;
			}
		};
	}

	public static class Property<V> implements ObservableValue<Value<V>> {

		private Value<V> value;
		private ExpressionHelper<Value<V>> helper = null;

		public Property(V value, Format format) {
			this.value = new Value<>(value, format);
		}

		public Property(V value, ObjectProperty<Format> formatProperty) {
			this(value, formatProperty.get());
			formatProperty.addListener(e -> ExpressionHelper.fireValueChangedEvent(helper));
		}

		@Override
		public void addListener(ChangeListener<? super Value<V>> listener) {
			helper = ExpressionHelper.addListener(helper, this, listener);
		}

		@Override
		public void removeListener(ChangeListener<? super Value<V>> listener) {
			helper = ExpressionHelper.removeListener(helper, listener);
		}

		@Override
		public Value<V> getValue() {
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
	}

	public static class Value<V> implements Comparable<Value<V>> {

		final V      value;
		final Format format;

		Value(V initialValue, Format format) {
			this.value = initialValue;
			this.format = format;
		}

		public String toString() {
			return format.format(value);
		}

		@Override
		public int compareTo(Value<V> o) {
			return value instanceof Comparable ? ((Comparable)value).compareTo(o.value) : 0;
		}
	}
}
