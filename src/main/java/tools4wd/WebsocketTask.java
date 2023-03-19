package tools4wd;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javafx.concurrent.Task;

public class WebsocketTask extends Task<String> implements WebSocket.Listener {

	private final BlockingQueue<String> downlink;
	private final URI uri;
	private int counter = 0;

	public WebsocketTask(BlockingQueue<String> downlink, URI uri) {
		this.downlink = downlink;
		this.uri = uri;
	}

	@Override
	protected String call() throws Exception {
		updateMessage("Starting ....");
		HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
		HttpClient httpClient = httpClientBuilder.build();
		WebSocket.Builder webSocketBuilder = httpClient.newWebSocketBuilder();
		webSocketBuilder.connectTimeout(Duration.of(3, ChronoUnit.SECONDS));
		CompletableFuture<WebSocket> cfWS = webSocketBuilder.buildAsync(uri, this);

		cfWS.thenRun(this::onCfWSRun); // at good end
		cfWS.exceptionally(this::handleError); // at bad end

		WebSocket webSocket = cfWS.get();
		updateMessage("Init done ... Start loop ...");

		while (!isCancelled()) {
			String s = downlink.take();
			webSocket.sendText(s, true);
		}

		updateMessage("Bye bye ...");
		return null;
	}

	private void onCfWSRun() {
		updateMessage("Connected");
	}

	private WebSocket handleError(Throwable t) {
		updateMessage("Start failed ....");
		cancel();
		return null;
	}

	@Override
	public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
		updateMessage("Index " + counter);
		counter++;
		updateValue(data.toString());
		return null;
	}
}
