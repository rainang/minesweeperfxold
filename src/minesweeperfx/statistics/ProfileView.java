package minesweeperfx.statistics;

import java.util.Date;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import static minesweeperfx.Constants.ICON;
import static minesweeperfx.Constants.PROFILE;

public class ProfileView extends TableView<GameStats> {

	public final TableColumn<GameStats, String>  colDifficulty = new TableColumn<>("Difficulty");
	public final TableColumn<GameStats, String>  colTime       = new TableColumn<>("Time");
	public final TableColumn<GameStats, Date>    colDate       = new TableColumn<>("Date");
	public final TableColumn<GameStats, Boolean> colNF         = new TableColumn<>("NF");

	private final Dialog dialog = new Dialog();

	public ProfileView(ObservableList<GameStats> stats) {
		super(stats);
		Stage stage = ((Stage)dialog.getDialogPane().getScene().getWindow());
		stage.getIcons().add(ICON);
		stage.setTitle("High Scores : " + PROFILE);

		colDifficulty.setCellValueFactory(new PropertyValueFactory("difficulty"));
		colTime.setCellValueFactory(new PropertyValueFactory("time"));
		colDate.setCellValueFactory(new PropertyValueFactory("date"));
		colNF.setCellValueFactory(new PropertyValueFactory("nf"));

		int i = 100;
		colDifficulty.setMinWidth(i);
		colDifficulty.setMaxWidth(i);
		colDifficulty.setPrefWidth(i);
		i = 60;
		colTime.setMinWidth(i);
		colTime.setMaxWidth(i);
		colTime.setPrefWidth(i);
		i = 200;
		colDate.setMinWidth(i);
		colDate.setMaxWidth(i);
		colDate.setPrefWidth(i);
		i = 60;
		colNF.setMinWidth(i);
		colNF.setMaxWidth(i);
		colNF.setPrefWidth(i);

		colDifficulty.setResizable(false);

		getColumns().add(colDifficulty);
		getColumns().add(colTime);
		getColumns().add(colDate);
		getColumns().add(colNF);

		colDifficulty.setCellValueFactory(p -> p.getValue().getDifficultyProperty());
		colTime.setCellValueFactory(p -> p.getValue().getTimeProperty());
		colDate.setCellValueFactory(p -> p.getValue().getDateProperty());
		colNF.setCellValueFactory(p -> p.getValue().getNFProperty());

		setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		dialog.getDialogPane().getButtonTypes().add(new ButtonType("Done", ButtonBar.ButtonData.FINISH));
	}

	public void showProfile() {
		dialog.getDialogPane().setContent(this);
		dialog.showAndWait();
	}
}
