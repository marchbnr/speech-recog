package muster.preproc;

import muster.util.BitReverser;
import junit.framework.TestCase;

public class BitReverserTest extends TestCase {

	public void testReversing() {
		int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7};
//		int[] numbers = {0, 1, 2, 3};
		int[] expected  = {0, 4, 2, 6, 1, 5, 3, 7};
		
		for(int i=0; i<numbers.length; i++) {
			numbers[i] = BitReverser.reverse(7, numbers[i]);
		}
		
		for(int i=0; i<numbers.length; i++) {
//			System.out.print(numbers[i] + " ");
			assertEquals(expected[i], numbers[i]);
		}
	}
}
