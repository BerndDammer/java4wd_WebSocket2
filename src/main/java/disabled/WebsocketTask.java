package disabled;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javafx.concurrent.Task;
import todo.CommanderString;

public class WebsocketTask extends Task<String> implements WebSocket.Listener {

	private final CommanderString downlink;
	private final URI uri;
	private int counter = 0;

	public WebsocketTask(CommanderString downlink, URI uri) {
		this.downlink = downlink;
		this.uri = uri;
	}

	@Override
	protected String call() throws Exception {
		WebSocket webSocket = null;
		try {
			updateMessage("Starting ....");
			final HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
			final HttpClient httpClient = httpClientBuilder.build();
			final WebSocket.Builder webSocketBuilder = httpClient.newWebSocketBuilder();
			webSocketBuilder.connectTimeout(Duration.ofSeconds(3l));
			final CompletableFuture<WebSocket> cfWS = webSocketBuilder.buildAsync(uri, this);

			cfWS.thenRun(this::onCfWSRun); // at good end
			cfWS.exceptionally(this::handleError); // at bad end

			webSocket = cfWS.get();
			updateMessage("Init done ... Start loop ...");

			while (!isCancelled()) {
				String s = downlink.get();
				if (s != null)
					webSocket.sendText(s, true);
				// webSocket.request(22);
			}
		}
		/*
		 * catch(Exception e) { e.printStackTrace(); throw e; }
		 */
		finally {
			webSocket.abort();
			updateMessage("Bye bye ...");
		}
		return null;
	}

	private void onCfWSRun() {
		updateMessage("Connected");
	}

	private WebSocket handleError(Throwable t) {
		updateMessage("Start failed .... : " + t.getMessage());
		// cancel(); // fail is better
		return null;
	}

	@Override
	public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
		updateMessage("Index " + counter);
		counter++;
		updateValue(data.toString());
		// standard implementation
		webSocket.request(1); // must request next block
		return null; // data is free
	}

	
	/////////////////////////////////////////////////////////////////////////////////
	///
	/// all other events
	/// added for information
	@Override
	public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
		updateMessage("onBinary");
		webSocket.request(1);
		return null; // Byte Buffer is free
	}

	@Override
	public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
		updateMessage("onClose: reason : " + reason);
		return null; // Close immediately
	}

	@Override
	public void onError(WebSocket webSocket, Throwable error) {
		updateMessage("onError  Message :  " + error.getMessage());
		Listener.super.onError(webSocket, error);
	}

	@Override
	public void onOpen(WebSocket webSocket) {
		updateMessage("onOpen");
		Listener.super.onOpen(webSocket); // this makes WebSocket.request(1)
	}

	@Override
	public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
		updateMessage("onPing");
		// webSocket.request(1);
		return Listener.super.onPing(webSocket, message); // make Pong
	}

	@Override
	public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
		updateMessage("onPong");
		webSocket.request(1);
		return null; // byte buffer is free
	}
}
