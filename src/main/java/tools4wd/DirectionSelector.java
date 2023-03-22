package tools4wd;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class DirectionSelector extends GridPane implements ChangeListener<Boolean> {
	private final Button bForward = new Button("forward");
	private final Button bBackward = new Button("backward");
	private final Button bLeft = new Button("left");
	private final Button bRight = new Button("right");
	private final StringProperty directionProperty = new SimpleStringProperty();

	public StringProperty getDirectionProperty() {
		return directionProperty;
	}

	public DirectionSelector() {
		add(bForward, 2, 1);
		add(bBackward, 2, 3);
		add(bLeft, 1, 2);
		add(bRight, 3, 2);
		directionProperty.set("stop");

		bForward.armedProperty().addListener(this);
		bBackward.armedProperty().addListener(this);
		bLeft.armedProperty().addListener(this);
		bRight.armedProperty().addListener(this);
	}


	@Override
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (bForward.isArmed()) {
			directionProperty.set("forward");
		} else if (bBackward.isArmed()) {
			directionProperty.set("backward");
		} else if (bLeft.isArmed()) {
			directionProperty.set("left");
		} else if (bRight.isArmed()) {
			directionProperty.set("right");
		} else {
			directionProperty.set("stop");
		}
	}
}
