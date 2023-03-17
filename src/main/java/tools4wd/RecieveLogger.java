package tools4wd;

import javafx.scene.control.ListView;

public class RecieveLogger extends ListView<String> {
	public RecieveLogger() {
		setEditable(false);
		getItems().add("hiuhui");
		getItems().add("nananan");
	}

	public void next(final String s) {
		getItems().add(s);
	}
}
