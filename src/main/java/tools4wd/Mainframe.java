package tools4wd;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.json.JsonObjectBuilder;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class Mainframe extends GridPane {
	private class LightSwitch extends ToggleButton implements EventHandler<javafx.event.ActionEvent>, IControlSource {
		private LightSwitch() {
			super("Lights");
			setOnAction(this);
		}

		@Override
		public void handle(javafx.event.ActionEvent event) {
		}

		@Override
		public void add2JSon(JsonObjectBuilder job) {
			job.add("G", isSelected());
		}
	}

	private class HScroller extends Slider implements IControlSource {
		final String jsonTag;

		private HScroller(final String jsonTag) {
			this.jsonTag = jsonTag;
			setMin(0.0);
			setMax(100);
			setShowTickMarks(true);
			setShowTickLabels(true);
			setMajorTickUnit(10.0);
			setOrientation(Orientation.HORIZONTAL);
		}

		public void add2JSon(JsonObjectBuilder job) {
			job.add(jsonTag, (int) getValue());
		}
	}

	private class StartButton extends Button implements EventHandler<ActionEvent> {
		public StartButton() {
			super("Start");
			setOnAction(this);
		}

		@Override
		public void handle(ActionEvent event) {
			websocketService.reset();
			websocketService.setUri(URI.create(url.getText()));
			websocketService.valueProperty().addListener(Mainframe.this::newObjectFromServer);
			workermessage.textProperty().bind(websocketService.messageProperty());
			websocketService.stateProperty().addListener(Mainframe.this::newWorkerState);
			websocketService.start();
		}
	}

	private class StopButton extends Button implements EventHandler<ActionEvent> {
		public StopButton() {
			super("Stop");
			setOnAction(this);
		}

		@Override
		public void handle(ActionEvent event) {
			websocketService.cancel();
		}
	}

	private final List<IControlSource> js = new LinkedList<>();
	private final SLogger transmittLogger = new SLogger();
	private final SLogger recieveLogger = new SLogger();
	private final BlockingQueue<String> downlink = new LinkedBlockingQueue<>();
	private final WebsocketService websocketService;
	private final Label workermessage = new Label();
	private final Label workerstate = new Label();
	private final TextField url = new TextField(General.URL);
	private final StartButton startButton = new StartButton();
	private final TransmittWorker transmittWorker;
	private final ChoiceBox<Duration> transmitSpeed = new ChoiceBox<Duration>(General.SPEEDS);

	public Mainframe() {
		// setGridLinesVisible(true);
		transmitSpeed.setValue(General.DEFAULT_SPEED);
		transmitSpeed.valueProperty().addListener(this::changedSpeed);
		websocketService = new WebsocketService(downlink);

		final DirectionSelector directionSelector = new DirectionSelector();
		final LightSwitch lightSwitch = new LightSwitch();
		final HScroller acceleration = new HScroller("A");
		final HScroller illumination = new HScroller("H");
		transmittWorker = new TransmittWorker(transmittLogger, js, downlink);

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
		add(new StopButton(), 4, 5);

		add(transmittLogger, 1, 6, GridPane.REMAINING, 1);
		add(recieveLogger, 1, 7, GridPane.REMAINING, 1);

		js.add(acceleration);
		js.add(directionSelector);
		js.add(lightSwitch);
		js.add(illumination);
	}

	void newObjectFromServer(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		recieveLogger.next(newValue);
	}

	void newWorkerState(ObservableValue<? extends State> observable, State oldValue, State newValue) {
		workerstate.setText(newValue.toString());
		switch (newValue) {
		case CANCELLED:
		case FAILED:
		case READY:
		case SCHEDULED:
		case SUCCEEDED:
			startButton.disableProperty().setValue(false);
			transmittWorker.setEnabled(false);
			break;
		case RUNNING:
			startButton.disableProperty().setValue(true);
			transmittWorker.setEnabled(true);
			break;
		}
	}

	public void changedSpeed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
		transmittWorker.setRate(newValue);
	}
}
