package test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class WSClient implements WebSocket.Listener {
	public WSClient() {
		HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
		HttpClient httpClient = httpClientBuilder.build();
		WebSocket.Builder webSocketBuilder = httpClient.newWebSocketBuilder();
		webSocketBuilder.connectTimeout(Duration.of(5, ChronoUnit.SECONDS));
		CompletableFuture<WebSocket> cfWS = webSocketBuilder.buildAsync(URI.create("ws://192.168.178.61:8765"), this);

		cfWS.thenRunAsync(this::onCfWSRun); // at good end
		cfWS.handle(this::handle);	// at bad end
		try {
			WebSocket webSocket = cfWS.get();
			worker(webSocket);
		} catch (Exception e) {
			System.out.println("------------------Exception while join------------------");
			e.printStackTrace();
		}
	}

	WebSocket handle(WebSocket webSocket, Throwable t) {
		System.out.println("------------------handle------------------");
		t.printStackTrace();
		System.out.println("------------------handle------------------");
		return webSocket;
	}

	void worker(WebSocket webSocket) {
		int i = 0;
		System.out.println("Worker Start");
		try {
			while (true) {
				webSocket.sendText("Message from PC" + i, true);
				i++;
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void onCfWSRun() {
		System.out.println("onCfWSRun");
	}

	public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
		System.out.println("onBinary: " + data.position());
		return Listener.super.onBinary(webSocket, data, last);
	}

	public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
		System.out.println("onClose: " + reason);
		return Listener.super.onClose(webSocket, statusCode, reason);
	}

	public void onError(WebSocket webSocket, Throwable error) {
		System.out.println("onError" + error.getMessage());
		Listener.super.onError(webSocket, error);
	}

	public void onOpen(WebSocket webSocket) {
		System.out.println("onOpen");
		Listener.super.onOpen(webSocket);
	}

	public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
		System.out.println("onPing");
		return Listener.super.onPing(webSocket, message);
	}

	public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
		System.out.println("onPong");
		return Listener.super.onPong(webSocket, message);
	}

	public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
		System.out.println("onText" + data);
		return Listener.super.onText(webSocket, data, last);
	}

}
