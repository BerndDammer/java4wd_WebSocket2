package tools4wd;

import javafx.scene.layout.BorderPane;

import javax.json.JsonObjectBuilder;

import javafx.scene.control.Button;

public class DirectionSelector extends BorderPane implements IControlSource {
	private final Button bForward = new Button("forward");
	private final Button bBackward = new Button("backward");
	private final Button bLeft = new Button("left");
	private final Button bRight = new Button("right");

	public DirectionSelector() {
		setTop(bForward);
		setBottom(bBackward);
		setLeft(bLeft);
		setRight(bRight);
	}

	@Override
	public void add2JSon(JsonObjectBuilder job) {
		if(bForward.isArmed())
		{
			job.add("K", "forward");
		}
		else if(bBackward.isArmed())
		{
			job.add("K", "backward");
		}
		else if(bLeft.isArmed())
		{
			job.add("K", "left");
		}
		else if(bRight.isArmed())
		{
			job.add("K", "right");
		}
		else 
		{
			job.add("K", "stop");
		}
	}
}
