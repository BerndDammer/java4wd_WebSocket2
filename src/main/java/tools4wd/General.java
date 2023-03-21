package tools4wd;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

public class General {
	public static final Duration COMMAND_DELAY_MS = Duration.millis(1000);
	public static final String URL = "ws://192.168.178.61:8765";
	public static final int BUFFER_SIZE = 4096;
	public static final Duration DEFAULT_SPEED = Duration.millis(1000.0);
	public static final ObservableList<Duration> SPEEDS = FXCollections.observableArrayList( //
			Duration.millis(1000.0), //
			Duration.millis(500.0), //
			Duration.millis(333.0), //
			Duration.millis(200.0),//
			Duration.seconds(15.0)//
	);
}
