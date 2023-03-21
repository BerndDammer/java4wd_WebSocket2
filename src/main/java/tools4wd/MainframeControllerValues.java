package tools4wd;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class MainframeControllerValues {
	public BooleanProperty isSelectedLightButton;
	public BooleanProperty disablePropertyStartButton;
	public ObjectProperty<EventHandler<ActionEvent>> onActionPropertyStartButton;
	public ObjectProperty<EventHandler<ActionEvent>> onActionPropertyStopButton;
	public ObjectProperty<ObservableList<String>> itemsTransmitLogger;
	public ObjectProperty<ObservableList<String>> itemsReceiveLogger;
	public ObjectProperty<Duration> transmitSpeed;
	public StringProperty speed;
	public StringProperty mileage;
	public StringProperty url;
	public DoubleProperty acceleration;
	public StringProperty workermessage;
	public StringProperty workerstate;
	public DoubleProperty[] bottomSensors;
	public DoubleProperty illumination;
	public MapProperty<Integer, Integer> sonics;
	public StringProperty direction;
}
