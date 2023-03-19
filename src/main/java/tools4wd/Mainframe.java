package tools4wd;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.Pipe;
import java.util.LinkedList;
import java.util.List;

import javax.json.JsonObjectBuilder;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;

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

	private final List<IControlSource> js = new LinkedList<>();
	private final SLogger transmittLogger = new SLogger();
	private final SLogger recieveLogger = new SLogger();
	private final Pipe downlink;
	private final WebsocketService websocketService;
	private final Label workermessage = new Label("ffioewihwohfewoi");
	private final Label workerstate = new Label();

	public Mainframe() throws IOException {
		downlink = Pipe.open();
		websocketService = new WebsocketService(downlink.source());

		final DirectionSelector directionSelector = new DirectionSelector();
		final LightSwitch lightSwitch = new LightSwitch();
		final HScroller acceleration = new HScroller("A");
		final HScroller illumination = new HScroller("H");

		add(directionSelector, 3, 2);
		add(workermessage, 1, 2);
		add(workerstate, 1, 3);
		add(transmittLogger, 3, 3, 1, 2);
		add(recieveLogger, 2, 4, 1, GridPane.REMAINING);
		new TransmittWorker(transmittLogger, js, downlink.sink());
		add(lightSwitch, 1, 1);
		add(acceleration, 2, 1);
		add(illumination, 3, 1);

		js.add(acceleration);
		js.add(directionSelector);
		js.add(lightSwitch);
		js.add(illumination);

		websocketService.setUri(URI.create("ws://192.168.178.61:8765"));
		websocketService.valueProperty().addListener(this::newObjectFromServer);
		workermessage.textProperty().bind(websocketService.messageProperty());
		websocketService.stateProperty().addListener(this::newWorkerState);
		websocketService.start();
	}

	void newObjectFromServer(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		recieveLogger.next(newValue);
	}

	void newWorkerState(ObservableValue<? extends State> observable, State oldValue, State newValue) {
		workerstate.setText(newValue.toString());
	}
}
