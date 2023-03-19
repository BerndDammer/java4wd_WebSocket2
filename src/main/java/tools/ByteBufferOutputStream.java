package tools;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {

	private final ByteBuffer byteBuffer;

	public ByteBufferOutputStream(int capacity) {
		byteBuffer = ByteBuffer.allocate(capacity);
	}

	@Override
	public void write(int i) throws IOException {
		if (!byteBuffer.hasRemaining())
			throw new IOException("Buffer Overrun");
		byteBuffer.put((byte) i);
	}

	public ByteBuffer getBb() {
		return byteBuffer;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (byteBuffer.remaining() < len)
			throw new IOException("Buffer Overrun");
		byteBuffer.put(b, off, len);
	}

	@Override
	public String toString() {
		return new String(byteBuffer.array(), 0, byteBuffer.remaining());
	}
}