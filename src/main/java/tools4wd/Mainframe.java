package tools4wd;

import java.util.LinkedList;
import java.util.List;

import javax.json.JsonObjectBuilder;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;

public class Mainframe extends GridPane {
	private class LightSwitch extends ToggleButton implements EventHandler<javafx.event.ActionEvent>, IControlSource {
		private LightSwitch() {
			super("Lights");
			setOnAction(this);
		}

		@Override
		public void handle(javafx.event.ActionEvent event) {
		}

		@Override
		public void add2JSon(JsonObjectBuilder job) {
			job.add("G", isSelected());
		}
	}

	private class HScroller extends Slider implements IControlSource {
		final String jsonTag;

		private HScroller(final String jsonTag) {
			this.jsonTag = jsonTag;
			setMin(0.0);
			setMax(100);
			setShowTickMarks(true);
			setShowTickLabels(true);
			setMajorTickUnit(10.0);
			setOrientation(Orientation.HORIZONTAL);
		}

		public void add2JSon(JsonObjectBuilder job) {
			job.add(jsonTag, (int) getValue());
		}
	}

	private final List<IControlSource> js = new LinkedList<>();
	private final TransmittLogger transmittLogger = new TransmittLogger();

	public Mainframe() {
		final DirectionSelector directionSelector = new DirectionSelector();
		final LightSwitch lightSwitch = new LightSwitch();
		final HScroller acceleration = new HScroller("A");
		final HScroller illumination = new HScroller("H");

		add(directionSelector, 3, 2);

		add(transmittLogger, 3, 3, 1, 2);
		add(new RecieveLogger(), 1, 4, 1, GridPane.REMAINING);
		new TransmittWorker(transmittLogger, js);
		add(lightSwitch, 1, 1);
		add(acceleration, 2, 1);
		add(illumination, 3, 1);

		js.add(acceleration);
		js.add(directionSelector);
		js.add(lightSwitch);
		js.add(illumination);
	}
}
