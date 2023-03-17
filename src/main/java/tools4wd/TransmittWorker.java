package tools4wd;

import java.io.StringWriter;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;

public class TransmittWorker {

	private final Timeline timeline = new Timeline();

	private final TransmittLogger tTransmittLoggerl;
	private final List<IControlSource> js;

	public TransmittWorker(final TransmittLogger tlTransmittLogger, List<IControlSource> js) {
		this.js = js;
		this.tTransmittLoggerl = tlTransmittLogger;

		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(General.COMMAND_DELAY_MS, this::onKeyFrame));
		timeline.play();
	}

	public void onKeyFrame(ActionEvent event) {
		final JsonObjectBuilder ob = Json.createObjectBuilder();
		ob.add("", JsonValue.NULL);
		for (IControlSource ic : js) {
			ic.add2JSon(ob);
		}
		final JsonObject job = ob.build();
		final StringWriter sw = new StringWriter();
		Json.createWriter(sw).writeObject(job);
		tTransmittLoggerl.next(sw.toString());
	}
}
