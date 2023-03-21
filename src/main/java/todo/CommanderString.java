package todo;

public abstract class CommanderString {

	public abstract void put(final String s);
	public abstract String get() throws InterruptedException;
	public static CommanderString getCommander()
	{
		return new CommanderStringBlockingQueue();
	}
}
