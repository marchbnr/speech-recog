package muster.sound;

/**
 * Converts values of double sources to byte values.
 */
public class DoubleConverter implements ByteSource {

	DoubleSource source;
	Byte cache = null;

	public DoubleConverter(DoubleSource source) {
		this.source = source;
	}

	public byte[] encode(double sample) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (((short)sample) & 255);
		bytes[1] = (byte) (((short)sample) >> 8);
		return bytes;
	}

	@Override
	public byte getNextByte() {
		if (cache != null) {
			byte value = cache;
			cache = null;
			return value;
		} else {
			byte[] bytes = encode(source.getNextDouble());
			cache = bytes[1];
			return bytes[0];
		}
	}

	@Override
	public boolean isFinished() {
		return source.isFinished() && cache == null;
	}

	@Override
	public boolean isEmpty() {
		return source.isEmpty() && cache == null;
	}

}
