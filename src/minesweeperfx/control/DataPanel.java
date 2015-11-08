package minesweeperfx.control;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import minesweeperfx.MinesweeperFX;

import static minesweeperfx.Options.*;

public class DataPanel extends HBox {

	private final BorderedPane borderedPane = new BorderedPane(20, BORDER_SIZE, true);

	private final Label timer          = new Label(WATCH + " 00:00:00");
	private final Label countdownTimer = new Label(HOURGLASS + " 00:00:00");
	private final Label flags          = new Label(FLAG + " 0");

	public DataPanel() {
		setPadding(new Insets(2.5, 10, 0, 10));
		timer.setAlignment(Pos.BASELINE_RIGHT);
		countdownTimer.setAlignment(Pos.CENTER);
		flags.setPadding(new Insets(0));
		timer.setPadding(new Insets(0));
		countdownTimer.setPadding(new Insets(0));
		countdownTimer.setVisible(false);

		getChildren().addAll(flags, countdownTimer, timer);
		borderedPane.getChildren().add(this);

		getDifficultyProperty().addListener((a, b, c) -> updateSizes());
		getTileSizeProperty().addListener((a, b, c) -> updateSizes());
		flags.visibleProperty().bind(getShowFlagCountProperty());
		timer.visibleProperty().bind(getShowTimerProperty());
		MinesweeperFX.getGame().getFlagsUsedProperty().addListener((a, b, c) -> flags.setText(FLAG + c));
		timer.textProperty().bind(MinesweeperFX.getGame().timer.getFormattedTimeProperty());
		updateSizes();
	}

	private void updateSizes() {
		setWidth(getColumns()*getTileSize() + 2*BORDER_SIZE - 1);
		setHeight(getTileSize() + 2*BORDER_SIZE - 1);
		setPrefSize(getWidth(), getHeight());
		borderedPane.border.setWidth(getWidth());
		borderedPane.border.setHeight(getHeight());
		flags.setFont(Font.font("Calibri", FontWeight.NORMAL, getTileSize()*.5));
		timer.setFont(Font.font("Calibri", FontWeight.NORMAL, getTileSize()*.5));
		countdownTimer.setFont(Font.font("Calibri", FontWeight.NORMAL, getTileSize()*.5));
		flags.setPrefSize(getWidth()/getChildren().size(), getTileSize());
		timer.setPrefSize(getWidth()/getChildren().size(), getTileSize());
		countdownTimer.setPrefSize(getWidth()/getChildren().size(), getTileSize());
	}

	public BorderedPane getBorderedPane() {
		return borderedPane;
	}
}
