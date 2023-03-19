package tools;

import java.nio.ByteBuffer;

public class ByteBufferCharSequence implements CharSequence{

	private final ByteBuffer byteBuffer;
	public ByteBufferCharSequence(final ByteBuffer byteBuffer)
	{
		this.byteBuffer = byteBuffer;
	}
	@Override
	public int length() {
		return byteBuffer.remaining();
	}

	@Override
	public char charAt(int index) {
		
		return (char)(byteBuffer.get(index));
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		StringBuilder result = new StringBuilder(end-start);
		for(int index = start; index < end; index++)
		{
			result.append((char)byteBuffer.getChar(index));
		}
		return result;
	}
}
