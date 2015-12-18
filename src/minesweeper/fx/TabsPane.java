package minesweeper.fx;

import java.util.Arrays;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabsPane extends TabPane {

	public static final String[][] strings = new String[][] {
			new String[] { "Statistics", "Games" }, new String[] { "Beginner", "Intermediate", "Expert", "All" },
			new String[] { "Flag", "No Flag", "Overall" }
	};

	public TabsPane(IntegerProperty difficultyIndex, IntegerProperty flagIndex, ReadOnlyDoubleProperty width) {
		this(0, difficultyIndex, flagIndex, width);
	}

	private TabsPane(
			int id, IntegerProperty difficultyIndex, IntegerProperty flagIndex, ReadOnlyDoubleProperty width) {
		super(Arrays.stream(strings[id]).map(Tab::new).peek(tab -> tab.setClosable(false)).toArray(Tab[]::new));
		if(id != 2)
			getTabs().forEach(tab -> tab.setContent(new TabsPane(id + 1, difficultyIndex, flagIndex, width)));
		IntegerProperty i1 = id == 0 ? difficultyIndex : id == 1 ? flagIndex : null;
		IntegerProperty i2 = id == 1 ? difficultyIndex : id == 2 ? flagIndex : null;
		if(i1 != null)
			i1.addListener((a, b, c) -> getTabs()
					.forEach(p -> ((TabPane)p.getContent()).getSelectionModel().select(c.intValue())));
		if(i2 != null)
			getSelectionModel().selectedIndexProperty().addListener((a, b, c) -> i2.set(c.intValue()));

		width.addListener((a, b, c) -> {
			double w = c.doubleValue() > 966 ? 966 : 450;
			double p = 2;
			double t = id == 0 ? 2 : id == 1 ? 4 : 3;
			w = w/t - t*p;
			w += id == 0 ? 1 : 0;
			setTabMinWidth(w);
			setTabMaxWidth(w);
		});

		if(id == 0) {
			setOnKeyPressed(event -> {
				switch(event.getCode()) {
				case F5:
					if(getSelectionModel().getSelectedIndex() == 0)
						Dialog.STATS.hide();
					else
						getSelectionModel().select(0);
					break;
				case F6:
					if(getSelectionModel().getSelectedIndex() == 1)
						Dialog.STATS.hide();
					else
						getSelectionModel().select(1);
					break;
				case B:
					difficultyIndex.set(0);
					break;
				case I:
					difficultyIndex.set(1);
					break;
				case E:
					difficultyIndex.set(2);
					break;
				case A:
					difficultyIndex.set(3);
					break;
				case F:
					flagIndex.set(0);
					break;
				case N:
					flagIndex.set(1);
					break;
				case O:
					flagIndex.set(2);
					break;
				}
			});
		}
	}
}