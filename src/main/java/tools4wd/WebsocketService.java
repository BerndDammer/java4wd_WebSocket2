package tools4wd;

import java.net.URI;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import todo.CommanderString;

public class WebsocketService extends Service<String> {

	private final CommanderString downlink;
	private URI uri;

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public WebsocketService(CommanderString downlink) {
		this.downlink = downlink;
	}

	@Override
	protected Task<String> createTask() {

		return new WebsocketTask(downlink, uri);
	}
}
