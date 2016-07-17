package muster.preproc;

import muster.preprocessing.steps.Complex;
import junit.framework.TestCase;

public class ComplexTest extends TestCase {
	
	Complex numberA;
	Complex numberB;
	Complex complexNull;
	
	public void setUp()
	{
		numberA = new Complex(2.3, 4.1);
		numberB = new Complex(1.3, 2.2);
		complexNull = new Complex(0.0, 0.0);
	}
	
	public void testGetters()
	{
		assertEquals(2.3, numberA.getReal());
		assertEquals(4.1, numberA.getImaginary());
		assertEquals(1.3, numberB.getReal());
		assertEquals(2.2, numberB.getImaginary());
	}

	public void testAddition()
	{
		Complex numC = numberA;
		numC.add(numberB);
		assertSimilar(3.6, numC.getReal());
		assertSimilar(6.3, numC.getImaginary());
	}
	
	public void testSubtraction()
	{
		Complex numC = numberA;
		numC.subtract(numberB);
		assertSimilar(1.0, numC.getReal());
		assertSimilar(1.9, numC.getImaginary());
	}
	
	public void testMultiplication()
	{
		Complex numC = numberA;
		numC.multiply(numberB);
		assertSimilar(-6.03, numC.getReal());
		assertSimilar(10.39, numC.getImaginary());
	}
	
	public void testDivision()
	{
		Complex numC = numberA;
		numC.divide(numberB);
		assertSimilar(1.839203675344563, numC.getReal());
		assertSimilar(0.041347626339969, numC.getImaginary());
	}
	
	public void testDivisionByZero()
	{
		try {
			numberA.divide(complexNull);
			assertTrue(false);
		}
		catch(RuntimeException re) {
			assertTrue(true);
		}
	}
	
	public void testAbs()
	{
		assertSimilar(4.701063709, numberA.abs());
	}
	
	public void testEquals()
	{
		assertTrue(!numberA.equals(numberB));
		assertTrue(numberA.equals(numberA));
		
		Complex valueA = new Complex(numberA.getReal(), numberA.getImaginary());
		assertTrue(numberA.equals(valueA));
		assertTrue(valueA.equals(numberA));
		
		valueA = new Complex(numberA.getReal()+Double.MIN_VALUE, 
				numberA.getImaginary()-Double.MIN_VALUE);
		assertTrue(numberA.equals(valueA));
		assertTrue(valueA.equals(numberA));
	}
	
	public void testRepresentation() {
		Complex testValue = Complex.fromPolar(1, Math.PI/2);
		System.out.println(testValue.toString());
	}
	
	public void assertSimilar(double expected, double value)
	{
		//System.out.println(Math.abs(expected-value));
		assertEquals(expected, value, Double.parseDouble("1.0E-9"));
	}
}
