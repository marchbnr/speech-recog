package muster.util;

public class BitReverser {

	public static int reverse(int maxSize, int value) {
		int tmpValue = Integer.reverse(value);
		return (tmpValue >>> (32 - Integer.bitCount(maxSize)));
	}
}
