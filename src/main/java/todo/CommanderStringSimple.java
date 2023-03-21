package todo;

// package private
class CommanderStringSimple extends CommanderString {

	private transient String s = null;
	private transient boolean putWaiting = false;
	private transient boolean getWaiting = false;

	private final Object lock = new Object();

	public final void put(final String s) {
		synchronized (lock) {
			if (s != null) {
				//throw new RuntimeException("You should not be here!");
				putWaiting = true;
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				putWaiting = false;
				if (s != null) {
					throw new RuntimeException("You should not be here!");
				}
			}
			if (getWaiting) {
				this.s = s;
				lock.notify();
			} else {
				this.s = s;
			}
		}
	}

	public final String get() throws InterruptedException {
		synchronized (lock) {
			if (putWaiting) {
				throw new RuntimeException("You should not be here!");
			} else {
				if (s == null) {
					getWaiting = true;
					lock.wait();
					getWaiting = false;
					if (s == null)
						throw new RuntimeException("You should not be here!");
				}
				String result = s;
				s = null;
				return result;
			}
		}
	}
}
