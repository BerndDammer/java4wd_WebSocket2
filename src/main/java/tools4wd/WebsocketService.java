package tools4wd;

import java.net.URI;
import java.util.concurrent.BlockingQueue;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class WebsocketService extends Service<String> {

	private final BlockingQueue<String> downlink;
	private URI uri;

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public WebsocketService(BlockingQueue<String> downlink ) {
		this.downlink = downlink;
	}

	@Override
	protected Task<String> createTask() {

		return new WebsocketTask(downlink, uri);
	}

}
