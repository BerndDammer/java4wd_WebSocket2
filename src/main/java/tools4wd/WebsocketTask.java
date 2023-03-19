package tools4wd;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javafx.concurrent.Task;
import tools.ByteBufferCharSequence;

public class WebsocketTask extends Task<String> implements WebSocket.Listener {

	private final Pipe.SourceChannel downlink;
	private final URI uri;
	private int counter = 0;

	public WebsocketTask(Pipe.SourceChannel downlink, URI uri) {
		this.downlink = downlink;
		this.uri = uri;
	}

	@Override
	protected String call() throws Exception {
		updateMessage("Starting ....");
		final ByteBuffer byteBuffer = ByteBuffer.allocate(General.BUFFER_SIZE);
		final HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
		final HttpClient httpClient = httpClientBuilder.build();
		final WebSocket.Builder webSocketBuilder = httpClient.newWebSocketBuilder();
		webSocketBuilder.connectTimeout(Duration.ofSeconds(3l));
		final CompletableFuture<WebSocket> cfWS = webSocketBuilder.buildAsync(uri, this);

		cfWS.thenRun(this::onCfWSRun); // at good end
		cfWS.exceptionally(this::handleError); // at bad end

		final WebSocket webSocket = cfWS.get();
		updateMessage("Init done ... Start loop ...");

		final Selector selector = Selector.open();
		downlink.configureBlocking(false);
		final SelectionKey readKey = downlink.register(selector, SelectionKey.OP_READ);
		
		while (!isCancelled()) {
			selector.select(333);
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			for( SelectionKey sk : selectedKeys)
			{
				if (sk.isReadable()) {
					byteBuffer.clear();
					downlink.read(byteBuffer);
					byteBuffer.flip();
					//String s = 
					webSocket.sendText( new ByteBufferCharSequence(byteBuffer), true);
				}
			}
		}
		readKey.cancel();
		selector.close();
		updateMessage("Bye bye ...");
		return null;
	}

	private void onCfWSRun() {
		updateMessage("Connected");
	}

	private WebSocket handleError(Throwable t) {
		updateMessage("Start failed .... : " + t.getMessage());
		//cancel(); // fail is better
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
