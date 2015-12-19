package minesweeper.fx;

import com.sun.javafx.binding.ExpressionHelper;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.util.Callback;
import minesweeper.game.Config;
import minesweeper.statistics.GameData;
import minesweeper.statistics.Profile;

import static minesweeper.Formatter.*;
import static minesweeper.MinesweeperFX.STYLE;

public class Table extends TableView<GameData> {

	private static final String tipName  = "Right click to set name filter.";
	private static final String tipDate  =
			"Right click to set date format.\nDefault long format [EEE MMMM dd, yyyy hh:mm:ss a]" +
					" - short format [E dMMMyy HH:mm]\n" +
					"E=day of week,  M=month, d=date y=year, h=hour, H=hour military, m=minute, s=second, " +
					"S=millisecond, a=am/pm";
	private static final String tipScore = "Right click to set score format.";
	private static final String tip3BV   = "Bechtel's Board Benchmark Value (3BV)\n" +
			" - the minimum number of clicks to win";
	private static final String tipOps   = "The number of openings on the board";
	private static final String tipIsl   = "The number of islands on the board";
	private static final String tip3BVs  = "3BV per second [ 3BV / Score ]";
	private static final String tipIOE   = "Index of Efficiency [ 3BV / Action ]";
	private static final String tipRQP   = "Rapport Qualité Prix (Quality Price Ration) [ Score / (3BV/s) ]";
	private static final String tipIOS   = "Index of Speed [ log(3BV) / log(Score) ]";
	private static final String tipAct   = "The total number of successful actions made. " +
			"An action is considered successful if a tile has been opened (via click or chord) or flagged.";
	private static final String tipClk   = "The total number of actions made including inconclusive actions. " +
			"This includes missed tile clicks and false chords.";
	private static final String tipNF    = "No Flagging\nWhether or not the game is a flagging or no flagging game";

	private final Predicate<GameData> defaultfilter;

	private final ObjectProperty<Predicate<GameData>> filter;

	private final StringProperty details;

	public Table(IntegerProperty tabIndex1, IntegerProperty tabIndex2, StringProperty details) {
		FilteredList<GameData> filteredData = new FilteredList<>(Profile.HIGH_SCORES);
		setItems(filteredData.sorted());
		((SortedList<GameData>)getItems()).comparatorProperty().bind(comparatorProperty());

		defaultfilter = data -> (tabIndex2.get() == 2 || tabIndex2.get() == (data.nf ? 1 : 0)) &&
				(tabIndex1.get() == 3 || data.difficulty.id == tabIndex1.get());
		filter = new SimpleObjectProperty<>(defaultfilter);
		filter.addListener((a, b, c) -> filteredData.setPredicate(c));
		tabIndex1.addListener(a -> filter());
		tabIndex2.addListener(a -> filter());
		filter();

		this.details = details;

		ColumnName colName = new ColumnName();
		ColumnDate colDate = new ColumnDate();

		Column<String> colScore = new Column<>("Score", tipScore, 60, p -> {
			StringProperty property = new SimpleStringProperty(formatScore(p.getValue().score));
			listenToScoreFormatChange(() -> property.set(formatScore(p.getValue().score)));
			return property;
		});
		colScore.setContextMenu(MenuBar.contextMenu("Minutes", "Milliseconds"));
		colScore.setMaxWidth(Integer.MAX_VALUE);
		colScore.setResizable(true);

		Column<String> colDif = new Column<>("Difficulty", "", 80,
											 p -> new SimpleStringProperty(p.getValue().difficulty.name));
		Column<String> colNF = new Column<>("NF", tipNF, 25,
											p -> new SimpleStringProperty(p.getValue().nf ? "Y" : "N"));
		colDif.setVisible(false);
		colNF.setVisible(false);
		tabIndex1.addListener((a, b, c) -> colDif.setVisible(c.intValue() == 3));
		tabIndex1.addListener((a, b, c) -> colNF.setVisible(c.intValue() == 3 && tabIndex2.get() == 2));
		tabIndex2.addListener((a, b, c) -> colNF.setVisible(c.intValue() == 2 && tabIndex1.get() == 3));

		Column<Number> c3bv = new Column<>("3BV", tip3BV, 60, p -> new SimpleIntegerProperty(p.getValue().b3v));
		Column<Number> cOps = new Column<>("Openings", tipOps, 60,
										   p -> new SimpleIntegerProperty(p.getValue().openings));
		Column<Number> cIsl = new Column<>("Islands", tipIsl, 60, p -> new SimpleIntegerProperty(p.getValue()
																										 .islands));
		Column<Number> cAct = new Column<>("Actions", tipAct, 60, p -> new SimpleIntegerProperty(p.getValue()
																										 .actions));
		Column<Number> cClk = new Column<>("Clicks", tipClk, 60, p -> new SimpleIntegerProperty(p.getValue().clicks));
		Column<String> cIOE = new Column<>("IOE", tipIOE, 60,
										   p -> new SimpleStringProperty(formatDouble(p.getValue().getIOE())));
		Column<String> c3BVs = new Column<>("3BV/s", tip3BVs, 60,
											p -> new SimpleStringProperty(formatDouble(p.getValue().get3BVs())));
		Column<String> cRQP = new Column<>("RQP", tipRQP, 60,
										   p -> new SimpleStringProperty(formatDouble(p.getValue().getRQP())));
		Column<String> cIOS = new Column<>("IOS", tipIOS, 60,
										   p -> new SimpleStringProperty(formatDouble(p.getValue().getIOS())));

		NestedColumn colBoard = new NestedColumn("Board", c3bv, cOps, cIsl);
		NestedColumn colEfficiency = new NestedColumn("Efficiency", cAct, cClk, cIOE);
		NestedColumn colSpeed = new NestedColumn("Speed", c3BVs, cRQP, cIOS);

		getColumns().add(colName);
		getColumns().add(colDate);
		getColumns().add(colDif);
		getColumns().add(colNF);
		getColumns().add(colScore);
		getColumns().add(colBoard);
		getColumns().add(colEfficiency);
		getColumns().add(colSpeed);

		setContextMenu(MenuBar.contextMenu("Efficiency", "Speed"));
		Config.boolBind("Efficiency", colEfficiency.visibleProperty());
		Config.boolBind("Speed", colSpeed.visibleProperty());
		focusedProperty().addListener(a -> setFocused(false));
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		setStyle(STYLE);

		widthProperty().addListener((a, b, c) -> {
			setMinWidth(getWidth());
			setMaxWidth(getWidth());
			setPrefWidth(getWidth());
		});
		setWidth(1000);
		setHeight(290);
		setMaxHeight(getHeight());
		setMinHeight(getHeight());
	}

	public void filter() {
		filter.set(filter.get().and(p -> true));
	}

	public class ColumnName extends Column<String> {

		public ColumnName() {
			super("Name", tipName, 80, p -> new SimpleStringProperty(p.getValue().player));
			Label label = (Label)getGraphic();
			TextField field = new TextField("");
			setContextMenu(new ContextMenu());
			label.setContextMenu(new ContextMenu());
			field.setStyle(STYLE);
			field.setOnAction(a -> setGraphic(label));
			label.setOnMouseClicked(e -> {
				setGraphic(field);
				field.requestFocus();
				field.selectAll();
			});
			field.textProperty().addListener((a, b, c) -> {
				if(Profile.PROFILES.keySet().contains(c))
					filter.set(defaultfilter.and(s -> s.player.equals(c)));
				else
					filter.set(defaultfilter);
			});
			field.focusedProperty().addListener((a, b, c) -> {
				if(!c)
					setGraphic(label);
				if(!Profile.PROFILES.keySet().contains(field.getText()))
					field.setText("");
			});
			Config.stringBind("Name Filter", field.textProperty());
		}
	}

	public class ColumnDate extends Column<Date> {

		private final String formatLong    = "EEE ddMMMyy hh:mm:ss a";
		private final Format defaultFormat = new SimpleDateFormat(formatLong);

		public ColumnDate() {
			super("Date", tipDate);
			Label label = (Label)getGraphic();
			TextField field = new TextField();
			ObjectProperty<Format> format = new SimpleObjectProperty<>(defaultFormat);
			setContextMenu(new ContextMenu());
			label.setContextMenu(new ContextMenu());

			setCellValueFactory(p -> new FormattedProperty(p.getValue().date, format));
			setMinWidth(200);
			setMaxWidth(200);

			field.setStyle(STYLE);
			field.setOnMouseEntered(e -> details.set(tipDate));
			label.setOnMouseClicked(e -> {
				setGraphic(field);
				field.requestFocus();
				field.selectAll();
			});

			field.textProperty().addListener((a, b, c) -> {
				Format f = null;
				try {
					f = c.isEmpty() ? defaultFormat : new SimpleDateFormat(c);
				} catch(IllegalArgumentException e) {
					f = defaultFormat;
				} finally {
					if(f != null)
						format.set(f);
				}
			});
			field.setOnAction(a -> setGraphic(label));
			field.focusedProperty().addListener((a, b, c) -> {
				if(!c)
					setGraphic(label);
				if(field.getText().isEmpty())
					field.setText(formatLong);
			});

			Config.stringBind("Date Format", field.textProperty());
		}
	}

	public class NestedColumn extends Column<GameData> {

		public NestedColumn(String name, Column... columns) {
			super(name, null);
			getColumns().addAll(columns);
		}
	}

	public class Column<T> extends TableColumn<GameData, T> {

		public Column(String name) {
			setId(name);
			setStyle(STYLE);
			setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
			setResizable(false);
		}

		public Column(String name, String tip) {
			this(name);
			setGraphic(new Label(name));
			getGraphic().setOnMouseEntered(e -> details.set(tip));
			getGraphic().setOnMouseExited(e -> details.set(""));
		}

		public Column(
				String name, String tip, double size, Callback<CellDataFeatures<GameData, T>, ObservableValue<T>>
				cvf) {
			this(name, tip);
			setMinWidth(size);
			setMaxWidth(size);
			setCellValueFactory(cvf);
		}
	}

	public class FormattedProperty implements ObservableValue<Date> {

		private Date value;
		private ExpressionHelper<Date> helper = null;

		public FormattedProperty(long time, ObjectProperty<Format> format) {
			value = new Date() {
				public String toString() {
					return format.get().format(time);
				}
			};
			value.setTime(time);
			format.addListener(e -> ExpressionHelper.fireValueChangedEvent(helper));
		}

		@Override
		public void addListener(ChangeListener<? super Date> listener) {
			ExpressionHelper.addListener(helper, this, listener);
		}

		@Override
		public void removeListener(ChangeListener<? super Date> listener) {
			ExpressionHelper.removeListener(helper, listener);
		}

		@Override
		public Date getValue() {
			return value;
		}

		@Override
		public void addListener(InvalidationListener listener) {
			ExpressionHelper.addListener(helper, this, listener);
		}

		@Override
		public void removeListener(InvalidationListener listener) {
			ExpressionHelper.removeListener(helper, listener);
		}
	}
}
