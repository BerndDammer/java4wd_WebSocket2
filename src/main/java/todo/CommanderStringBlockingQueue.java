package todo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// package private
class CommanderStringBlockingQueue extends CommanderString {

	private final BlockingQueue<String> queue = new LinkedBlockingQueue<>(20);
	
	public final void put(final String s) {
		queue.add(s);
	}

	public final String get() throws InterruptedException {
		return queue.take();
	}
}
