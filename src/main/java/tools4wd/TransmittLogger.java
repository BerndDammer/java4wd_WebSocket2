package tools4wd;

import javafx.scene.control.ListView;

public class TransmittLogger extends ListView<String> {
	
	public TransmittLogger() {
		setEditable(false);
		getItems().add("hiuhui");
		getItems().add("nananan");
	}

	public void next(final String s) {
		if( getItems().size() > 20)
		{
			getItems().clear();
		}
		getItems().add(s);
	}
}
