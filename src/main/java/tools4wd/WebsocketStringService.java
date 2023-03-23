package tools4wd;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

// TODO add logger
public class WebsocketStringService extends Service<Void> {
	public interface NonFXThreadEventReciever {
		public void xonNewText();
	}

	public class WebsocketTask extends Task<Void> implements WebSocket.Listener {

		private int counter = 0;

		public WebsocketTask() {
		}

		@Override
		protected Void call() throws Exception {
			WebSocket webSocket = null;
			try {
				updateMessage("Starting ....");
				final HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
				final HttpClient httpClient = httpClientBuilder.build();
				final WebSocket.Builder webSocketBuilder = httpClient.newWebSocketBuilder();
				webSocketBuilder.connectTimeout(General.CONNECT_TIMEOUT);
				final CompletableFuture<WebSocket> cfWS = webSocketBuilder.buildAsync(uri, this);

				cfWS.thenRun(this::futureConnected); // at good end
				cfWS.exceptionally(this::futureException); // at bad end

				webSocket = cfWS.get();
				updateMessage("Init done ... Start loop ...");

				while (!isCancelled()) {
					String s = sinkQueue.take();
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

		private void futureConnected() {
			updateMessage("Connected");
		}

		private WebSocket futureException(Throwable t) {
			updateTitle("Start failed .... : " + t.getMessage());
			// cancel(); // fail is better
			return null;
		}

		@Override
		public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
			updateMessage("Index " + counter);
			counter++;
			sourceQueue.add(data.toString());
			nonFXThreadEventReciever.xonNewText();
			// updateValue(data.toString());
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
			updateTitle("onClose: reason : " + reason);
			return null; // Close immediately
		}

		@Override
		public void onError(WebSocket webSocket, Throwable error) {
			updateTitle("onError  Message :  " + error.getMessage());
			Listener.super.onError(webSocket, error);
		}

		@Override
		public void onOpen(WebSocket webSocket) {
			updateTitle("onOpen : s :  " + webSocket.getSubprotocol());
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

	public LinkedBlockingQueue<String> getSourceQueue() {
		return sourceQueue;
	}

	public LinkedBlockingQueue<String> getSinkQueue() {
		return sinkQueue;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	private final LinkedBlockingQueue<String> sourceQueue = new LinkedBlockingQueue<>(General.QUEUE_DEPTH);
	private final LinkedBlockingQueue<String> sinkQueue = new LinkedBlockingQueue<>(General.QUEUE_DEPTH);
	private final NonFXThreadEventReciever nonFXThreadEventReciever;
	private URI uri;

	public WebsocketStringService(final NonFXThreadEventReciever nonFXThreadEventReciever) {
		this.nonFXThreadEventReciever = nonFXThreadEventReciever;
	}

	@Override
	protected Task<Void> createTask() {

		return new WebsocketTask();
	}
}
