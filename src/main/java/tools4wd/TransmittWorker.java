package tools4wd;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import tools.ByteBufferOutputStream;

public class TransmittWorker {

	private final Timeline timeline = new Timeline();

	private final SLogger tTransmittLoggerl;
	private final List<IControlSource> js;
	private final Pipe.SinkChannel downlink;
	final ByteBufferOutputStream bbaos =new ByteBufferOutputStream(General.BUFFER_SIZE);


	public TransmittWorker(SLogger transmittLogger, List<IControlSource> js, Pipe.SinkChannel downlink) {
		this.js = js;
		this.tTransmittLoggerl = transmittLogger;
		this.downlink = downlink;

		timeline.setCycleCount(Animation.INDEFINITE);
		//timeline.setCycleCount(5);
		timeline.getKeyFrames().add(new KeyFrame(General.COMMAND_DELAY_MS, this::onKeyFrame));
		timeline.play();
		// TODO Auto-generated constructor stub
	}

	public void onKeyFrame(ActionEvent event) {
		final JsonObjectBuilder ob = Json.createObjectBuilder();
		ob.add("", JsonValue.NULL);
		for (IControlSource ic : js) {
			ic.add2JSon(ob);
		}
		final JsonObject job = ob.build();
		bbaos.getBb().clear();
		Json.createWriter(bbaos).writeObject(job);
		bbaos.getBb().flip();
		tTransmittLoggerl.next(bbaos.toString());
		try {
			// TODO one in one out ????
			downlink.write(bbaos.getBb());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
