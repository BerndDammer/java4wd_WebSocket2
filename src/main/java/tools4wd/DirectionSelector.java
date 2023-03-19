package tools4wd;

import javax.json.JsonObjectBuilder;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class DirectionSelector extends GridPane implements IControlSource {
	private final Button bForward = new Button("forward");
	private final Button bBackward = new Button("backward");
	private final Button bLeft = new Button("left");
	private final Button bRight = new Button("right");

	public DirectionSelector() {
		add(bForward, 2, 1);
		add(bBackward, 2, 3);
		add(bLeft, 1, 2);
		add(bRight, 3, 2);
	}

	@Override
	public void add2JSon(JsonObjectBuilder job) {
		if (bForward.isArmed()) {
			job.add("K", "forward");
		} else if (bBackward.isArmed()) {
			job.add("K", "backward");
		} else if (bLeft.isArmed()) {
			job.add("K", "left");
		} else if (bRight.isArmed()) {
			job.add("K", "right");
		} else {
			job.add("K", "stop");
		}
	}
}
