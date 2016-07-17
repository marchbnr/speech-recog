package muster.sound;

/**
 * Converts values of byte sources to double values.
 */
public class ByteConverter implements DoubleSource {
	
	ByteSource source;
	
	public ByteConverter(ByteSource source) {
		this.source = source;
	}
	
	private double decode(byte lowerByte, byte higherByte) {
		int l, h;
		l = (int) lowerByte & 255;
		h = (int) higherByte & 255;
		h = (h << 8) | l;
		return (double) ((short) h);
	}

	@Override
	public double getNextDouble() {
		if(source.isFinished()) return 0;
		byte lowerByte, higherByte;
		Byte value = source.getNextByte();
		
		if(value == null) return 0;
		else lowerByte = value;
		
		if(!source.isFinished())
			higherByte = source.getNextByte();
		else
			higherByte = 0;
		return decode(lowerByte, higherByte);
	}

	@Override
	public boolean isFinished() {
		return source.isFinished();
	}

	@Override
	public boolean isEmpty() {
		return source.isEmpty();
	}
}
