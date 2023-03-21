package tools4wd;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class MainframeLoader extends GridPane {

	private class HScroller extends Slider {

		private HScroller(final String jsonTag) {
			setMin(0.0);
			setMax(100);
			setShowTickMarks(true);
			setShowTickLabels(true);
			setMajorTickUnit(10.0);
			setOrientation(Orientation.HORIZONTAL);
		}
	}
	public class SLogger extends ListView<String> {
		
		public SLogger() {
			setEditable(false);
		}
	}

	public MainframeLoader(final MainframeControllerValues mainframeControllerValues) {
		// setGridLinesVisible(true);
		final ChoiceBox<Duration> transmitSpeed = new ChoiceBox<Duration>(General.SPEEDS);
		transmitSpeed.setValue(General.DEFAULT_SPEED);

		final DirectionSelector directionSelector = new DirectionSelector();
		final ToggleButton lightSwitch = new ToggleButton("Lights");
		final Button startButton = new Button("Start");
		final Button stopButton = new Button("Stop");
		final HScroller acceleration = new HScroller("A");
		final HScroller illumination = new HScroller("H");
		final Label speed = new Label("XXXXXXXX");
		final Label mileage = new Label("WWWWWW");

		final SLogger transmittLogger = new SLogger();
		final SLogger recieveLogger = new SLogger();
		final TextField url = new TextField(General.URL);
		final Label workermessage = new Label();
		final Label workerstate = new Label();
		final ProgressBar[] bottomSensors = new ProgressBar[] { //
				new ProgressBar(), new ProgressBar(), new ProgressBar() };
		final MapListView sonics = new MapListView();

		// colum row
		add(lightSwitch, 1, 1, 4, 1);
		add(illumination, 1, 5);

		add(directionSelector, 2, 1, 4, 1);
		add(acceleration, 2, 5);

		add(url, 3, 1);
		add(transmitSpeed, 3, 2, 2, 1);
		add(workermessage, 3, 3, 2, 1);
		add(workerstate, 3, 4, 2, 1);
		add(startButton, 3, 5);
		add(stopButton, 4, 5);

		add(transmittLogger, 1, 6, 4, 1);
		add(recieveLogger, 1, 7, 4, 1);
		{
			GridPane p = new GridPane();
			p.add(bottomSensors[0], 1, 1);
			p.add(bottomSensors[1], 1, 2);
			p.add(bottomSensors[2], 1, 3);

			add(p, 5, 1, 1, 3);
		}
		add(speed, 5, 4);
		add(mileage, 5, 5);
		add(sonics, 5, 6, 1, GridPane.REMAINING);

		///////////////////////////////
		//// Make Property connections
		mainframeControllerValues.isSelectedLightButton = lightSwitch.selectedProperty();
		mainframeControllerValues.disablePropertyStartButton = startButton.disableProperty();
		mainframeControllerValues.onActionPropertyStartButton = startButton.onActionProperty();
		mainframeControllerValues.onActionPropertyStopButton = stopButton.onActionProperty();
		mainframeControllerValues.itemsTransmitLogger = transmittLogger.itemsProperty();
		mainframeControllerValues.itemsReceiveLogger = recieveLogger.itemsProperty();
		mainframeControllerValues.transmitSpeed = transmitSpeed.valueProperty();
		mainframeControllerValues.speed = speed.textProperty();
		mainframeControllerValues.mileage = mileage.textProperty();
		mainframeControllerValues.url = url.textProperty();
		mainframeControllerValues.acceleration = acceleration.valueProperty();
		mainframeControllerValues.illumination = illumination.valueProperty();
		mainframeControllerValues.bottomSensors = new DoubleProperty[] { bottomSensors[0].progressProperty(),
				bottomSensors[1].progressProperty(), bottomSensors[2].progressProperty(), };
		mainframeControllerValues.sonics = sonics.getMap();
		mainframeControllerValues.workermessage = workermessage.textProperty();
		mainframeControllerValues.workerstate = workerstate.textProperty();
		mainframeControllerValues.direction = directionSelector.getDirectionProperty();
	}
}
