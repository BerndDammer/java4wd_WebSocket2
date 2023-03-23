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
import javafx.util.Duration;

public class MainframeLoader extends GridPane2 {

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
			prefHeight(30.0);
			prefWidth(60.0);
			minHeight(20.0);
		}
		@Override 
		protected double computeMinHeight(double width) {
			return 200.0;
		}
		@Override 
		protected double computePrefHeight(double width) {
			return computeMinHeight(200.0);
		}
		protected double computeMinWidth(double width) {
			return 750.0;
		}
		@Override 
		protected double computePrefWidth(double width) {
			return computeMinWidth(750.0);
		}
	}

	public MainframeLoader(final MainframeControllerValues mainframeControllerValues) {
		super(true);
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
		final Label workertitle = new Label();
		final Label workerstate = new Label();
		final ProgressBar[] bottomSensors = new ProgressBar[] { //
				new ProgressBar(), new ProgressBar(), new ProgressBar() };
		final MapListView sonics = new MapListView();

		// colum row
		add(lightSwitch, 0, 0, 1, 4, INSERTING.CENTER);
		add(illumination, 0, 4);

		add(directionSelector, 1, 0, 1, 4, INSERTING.CENTER);
		add(acceleration, 1, 4);

		add(url, 2, 0, 2, 1, INSERTING.CENTER);
		add(transmitSpeed, 2, 1, 2, 1);

		add(workertitle, 2, 2, 2, 1);
		add(workerstate, 2, 3);
		add(workermessage, 3, 3, 2, 1);

		add(startButton, 2, 4, INSERTING.CENTER);
		add(stopButton, 3, 4, INSERTING.CENTER);

		add(recieveLogger, 0, 5, 4, 1);
		add(transmittLogger, 0, 6, 4, 1);
		{
			GridPane2 p = new GridPane2(false);
			p.add(bottomSensors[0], 1, 1, INSERTING.HGROW);
			p.add(bottomSensors[1], 1, 2, INSERTING.HGROW);
			p.add(bottomSensors[2], 1, 3, INSERTING.HGROW);
			add(p, 4, 0, 1, 2, INSERTING.CENTER);
			// p.alignmentProperty().set(Pos.CENTER);
		}
		add(speed, 4, 2, INSERTING.CENTER);
		add(mileage, 4, 3, INSERTING.CENTER);
		add(sonics, 4, 5, 1, 2);
		sonics.prefHeight(30.0);
		sonics.minHeight(30.0);

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
		mainframeControllerValues.workertitle = workertitle.textProperty();
		mainframeControllerValues.workerstate = workerstate.textProperty();
		mainframeControllerValues.direction = directionSelector.getDirectionProperty();
	}

}
