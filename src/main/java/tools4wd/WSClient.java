package tools4wd;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

public class WSClient implements WebSocket.Listener {
	public WSClient() {
		HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
		HttpClient httpClient = httpClientBuilder.build();
		WebSocket.Builder webSocketBuilder = httpClient.newWebSocketBuilder();
		webSocketBuilder.connectTimeout(Duration.of(3, ChronoUnit.SECONDS));
		CompletableFuture<WebSocket> cfWS = webSocketBuilder.buildAsync(URI.create("ws://192.168.178.61:8765"), this);

		cfWS.thenRunAsync(this::onCfWSRun); // at good end
		cfWS.handle(this::handle); // at bad end
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
				webSocket.sendText(makeCommandMessage(), true);
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
		convert(data);

		return Listener.super.onText(webSocket, data, last);
	}

	private void convert(CharSequence data) {
		String sdata = String.valueOf(data);
		boolean fail = true;
		try {
			JsonReader factory = Json.createReader(new StringReader(sdata));
			JsonStructure js = factory.read();
			if (js instanceof JsonObject) {
				onJsonObject((JsonObject) js);
				fail = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fail)
			onNonJsonObject(sdata);
	}

	private void onNonJsonObject(String sdata) {
	}

	private void onJsonObject(JsonObject js) {
		final StringWriter sw = new StringWriter();
		JsonWriterFactory jwf = Json.createWriterFactory(Map.of(JsonGenerator.PRETTY_PRINTING, ""));
		jwf.createWriter(sw).writeObject(js);
		final String s = sw.toString();
		// System.out.println(s);
	}

	private String makeCommandMessage() {
		StringWriter sw = new StringWriter();
		JsonObject object = Json.createObjectBuilder() //
				//.add("", JsonValue.NULL) // ?
				.add("", 20) // ?
				.add("A", 15) // speed
				.add("K", "forward") // direction
				.add("G", JsonValue.TRUE) // lights
				.add("H", 200) // illumination strength
				.build();
		Json.createWriter(sw).writeObject(object);
		System.out.println("------------" + sw.toString() + "------");
		return sw.toString();
	}
}
