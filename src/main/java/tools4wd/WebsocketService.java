package tools4wd;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import todo.CommanderString;

public class WebsocketService implements Listener {

	private final CommanderString downlink;
	private URI uri;

	private final StringProperty message = new SimpleStringProperty();
	private final StringProperty value = new SimpleStringProperty();
	private final ObjectProperty<State> state = new SimpleObjectProperty<>();

	////////////////////////////////////////////////////////////////
	///// Interface to FX
	///// Visible to FX
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public ObservableValue<String> valueProperty() {
		// TODO Auto-generated method stub
		return value;
	}

	public ObservableValue<String> messageProperty() {
		// TODO Auto-generated method stub
		return message;
	}

	public ObservableValue<State> stateProperty() {
		// TODO Auto-generated method stub
		return state;
	}

	public WebsocketService(CommanderString downlink) {
		this.downlink = downlink;
		state.setValue(State.READY);
	}

	public void reset() {
		if (transmitWorker != null) {
			try {
				transmitWorker.interrupt();
				transmitWorker.join();
				transmitWorker = null;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		state.setValue(State.READY);
	}

	public void start() {
		transmitWorker = new Thread(this::call, "Transmit Worker");
		transmitWorker.start();
	}

	public void cancel() {
		transmitWorker.interrupt();
	}

	//////////////////////////////////////////////////////////////////////
	///// Platform.runlater()
	private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>(20);
	private final BlockingQueue<String> valueQueue = new LinkedBlockingQueue<>(20);
	private final BlockingQueue<State> stateQueue = new LinkedBlockingQueue<>(20);

	private void updateMessage(String message) {
		messageQueue.add(message);
		Platform.runLater(this::pushMessage);
	}

	private void updateValue(String value) {
		valueQueue.add(value);
		Platform.runLater(this::pushValue);
	}

	private void updateState(State state) {
		stateQueue.add(state);
		Platform.runLater(this::pushState);

	}

	private void pushMessage() {
		String s = messageQueue.poll();
		message.set(s);
	}

	private void pushValue() {
		String s = valueQueue.poll();
		value.set(s);
	}

	private void pushState() {
		State s = stateQueue.poll();
		state.set(s);
	}

	/////////////////////////////////////////////////////////////////////
	///// internal runner
	private int counter = 0;
	private Thread transmitWorker = null;

	private void call() {
		WebSocket webSocket = null;
		try {
			updateState(State.SCHEDULED);
			updateMessage("Starting ....");
			final HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
			final HttpClient httpClient = httpClientBuilder.build();
			final WebSocket.Builder webSocketBuilder = httpClient.newWebSocketBuilder();
			webSocketBuilder.connectTimeout(Duration.ofSeconds(3l));
			final CompletableFuture<WebSocket> cfWS = webSocketBuilder.buildAsync(uri, this);

			cfWS.thenRun(this::futureConnected); // at good end
			cfWS.exceptionally(this::futureException); // at bad end

			webSocket = cfWS.get();
			updateMessage("Init done ... Start loop ...");
			updateState(State.RUNNING);
			while (true) {
				String s = downlink.get();
				if (s != null)
					webSocket.sendText(s, true);
				// webSocket.request(22);
			}
			// state.set(State.SUCCEEDED);
		} catch (InterruptedException e) {
			// e.printStackTrace();
			updateState(State.CANCELLED);
		} catch (Exception e) {
			// e.printStackTrace();
			updateState(State.FAILED);
		} finally {
			if (webSocket != null && !webSocket.isInputClosed() && !webSocket.isOutputClosed()) {
				webSocket.abort();
				webSocket = null;
			}
			updateMessage("Bye bye ...");
		}
	}

	/////////////////////////////////
	/// Connect Future
	private void futureConnected() {
		updateMessage("Connected");
	}

	private WebSocket futureException(Throwable t) {
		updateMessage("Start failed .... : " + t.getMessage());
		return null;
	}

	////////////////////////////////////////////////////
	///// WebSocket Listener
	@Override
	public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
		updateMessage("Index " + counter);
		counter++;
		updateValue(data.toString());
		// standard implementation
		webSocket.request(1); // must request next block
		return null; // data is free
	}
}
