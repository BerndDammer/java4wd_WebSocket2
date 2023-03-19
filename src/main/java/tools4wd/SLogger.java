package tools4wd;


import javafx.scene.control.ListView;

public class SLogger extends ListView<String> {
	
	public SLogger() {
		setEditable(false);
		getItems().add("hiuhui");
		getItems().add("nananan");
	}

	public void next(final String s) {
		if( getItems().size() > 10)
		{
			getItems().clear();
		}
		getItems().add(s);
	}
}
