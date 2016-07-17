package muster.preproc;

import muster.sound.*;
import junit.framework.TestCase;

public class ConverterTest extends TestCase implements ByteSource, DoubleSource {
	
	// convert 01110101 01011111
	byte[] bytes = { 95, 117};
	int counter = 0;
	double sample = 30047.0;
	
	public void testByteToDouble() {
		// convert 01110101 01011111 (dec: 30047) to double
		ByteConverter testConverter = new ByteConverter(this);
		assertEquals(30047.0, testConverter.getNextDouble(), Double.parseDouble("1.0E-9"));
	}
	
	public void testDoubleToByte() {
		DoubleConverter testConverter = new DoubleConverter(this);
		assertEquals(95, testConverter.getNextByte());
		assertEquals(117, testConverter.getNextByte());
	}

	public byte getNextByte() {
		return bytes[counter++];
	}

	public double getNextDouble() {
		return sample;
	}
	
	public boolean isFinished() {
		return false;
	}

	public boolean isEmpty() {
		return false;
	}

}
