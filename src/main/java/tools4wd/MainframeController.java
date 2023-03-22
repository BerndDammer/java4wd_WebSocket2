package tools4wd;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import todo.CommanderString;
import tools4wd.WebsocketService.NonFXThreadEventReciever;

public class MainframeController extends MainframeControllerValues implements NonFXThreadEventReciever {

	private final BlockingQueue<String> incommingQueue = new LinkedBlockingQueue<>(20);
	
	
	private final TransmittWorker transmittWorker = new TransmittWorker(this::onTransmitt);
	private final CommanderString downlink = CommanderString.getCommander();
	private final WebsocketService websocketService = new WebsocketService(downlink,incommingQueue, this);
	private final GridPane rootNode;
	
	
	public MainframeController() {
		rootNode = new MainframeLoader(this);

		//////////////////////////////
		/////////// Action Connections
		transmitSpeed.addListener(this::onChangedSpeed);
		onActionPropertyStartButton.setValue(this::onStart);
		onActionPropertyStopButton.setValue(this::onStop);

	}

	public void onStart(ActionEvent event) {
		websocketService.reset();
		websocketService.setUri(URI.create(url.getValue()));
		websocketService.valueProperty().addListener(this::onNewObjectFromServer);
		workermessage.bind(websocketService.messageProperty());
		websocketService.stateProperty().addListener(this::onNewWorkerState);
		websocketService.start();
	}

	private void onTransmitt() {
		final JsonObjectBuilder ob = Json.createObjectBuilder();
		ob.add("", JsonValue.NULL);
		ob.add("A", acceleration.intValue());
		ob.add("H", illumination.intValue());
		ob.add("G", isSelectedLightButton.get());
		ob.add("K", direction.get());
		final JsonObject job = ob.build();

		final StringWriter sw = new StringWriter();
		Json.createWriter(sw).writeObject(job);

		if (itemsTransmitLogger.get().size() > General.LOG_AUTODELETE)
			itemsTransmitLogger.get().clear();
		itemsTransmitLogger.get().add(sw.toString());

		downlink.put(sw.toString());
	}

	public void onStop(ActionEvent event) {
		websocketService.cancel();
	}

	void onNewObjectFromServer(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		if (itemsReceiveLogger.get().size() > General.LOG_AUTODELETE)
			itemsReceiveLogger.get().clear();
		itemsReceiveLogger.get().add(newValue);

		JsonReader factory = Json.createReader(new StringReader(newValue));
		JsonStructure js = factory.read();
		JsonObject job = (JsonObject) js;
		{
			JsonArray hs = job.getJsonArray("H");
			bottomSensors[0].setValue(((JsonNumber) hs.get(0)).doubleValue() / 32768.0);
			bottomSensors[1].setValue(((JsonNumber) hs.get(1)).doubleValue() / 32768.0);
			bottomSensors[2].setValue(((JsonNumber) hs.get(2)).doubleValue() / 32768.0);
		}
		mileage.setValue("Mil: " + job.getInt("C"));
		speed.setValue("Speed: " + job.getInt("B"));
		{
			JsonArray hs = job.getJsonArray("D");
			int k = hs.getInt(0);
			int v = hs.getInt(1);
			sonics.get().put(k, v);
		}
	}

	void onNewWorkerState(ObservableValue<? extends State> observable, State oldValue, State newValue) {

		workerstate.setValue(newValue.toString());
		switch (newValue) {
		case CANCELLED:
		case FAILED:
		case READY:
		case SCHEDULED:
		case SUCCEEDED:
			disablePropertyStartButton.setValue(false);
			transmittWorker.setEnabled(false);
			break;
		case RUNNING:
			disablePropertyStartButton.setValue(true);
			transmittWorker.setEnabled(true);
			break;
		}
	}

	public void onChangedSpeed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
		transmittWorker.setRate(newValue);
	}

	public GridPane getRootNode() {
		return rootNode;
	}

	@Override
	public void xonNewText() {
		Platform.runLater(this::onNewText);
	}
	public void onNewText() {
		try {
			String s = incommingQueue.take();
			onNewObjectFromServer(null, null, s);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
