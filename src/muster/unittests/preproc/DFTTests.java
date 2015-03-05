package muster.unittests.preproc;

import java.util.ArrayList;
import java.util.List;

import muster.preprocessing.steps.Complex;
import muster.preprocessing.steps.DFT;
import muster.preprocessing.steps.DFTMatrix;
import muster.preprocessing.steps.FFT;
import muster.preprocessing.steps.IDFT;
import muster.sound.DoubleSource;
import muster.util.DoublesParser;

import junit.framework.TestCase;

public class DFTTests extends TestCase {

	List<Complex> shortSamples = new ArrayList<Complex>();
	List<Complex> shortSolutions = new ArrayList<Complex>();
	List<Complex> longSamples = new ArrayList<Complex>();
	DFTMatrix shortMatrix;
	DFT longDFT;

	public void setUp() {
		{
			shortSamples.add(new Complex(0, 0));
			shortSamples.add(new Complex(1, 0));
			shortSamples.add(new Complex(0, 0));
			shortSamples.add(new Complex(-1, 0));
			
			shortMatrix = new DFTMatrix(shortSamples.size());
			// fill vector

			shortSolutions.add(new Complex(0, 0));
			shortSolutions.add(new Complex(0, -0.5));
			shortSolutions.add(new Complex(0, 0));
			shortSolutions.add(new Complex(0, 0.5));
		}
		
		{
			longSamples.add(new Complex(0,0));
			for(int i=1; i<16; i++) {
				double sinValue = Math.sin((Math.PI * 2.0)/i);
//				System.out.println(sinValue);
				longSamples.add(new Complex(sinValue,0));
			}
			
			longDFT = new DFT(longSamples.size());
		}
	}

	public void testDFT() {
		DFT testDFT = new DFT(shortSamples.size());

		List<Complex> resultList = testDFT.compute(shortSamples);
		for (int i = 0; i < resultList.size(); i++) {
			Complex result = resultList.get(i);
			Complex expected = shortSolutions.get(i);
//			System.out.println("expected,result: \t" + expected + " \t "
//					 + result);

			assertTrue(result.equals(expected));
			assertEquals(expected.getReal(), result.getReal(), Double.parseDouble("1.0E-9"));
			assertEquals(expected.getImaginary(), result.getImaginary(), Double.parseDouble("1.0E-9"));
		}
		// System.out.println();
	}

	public void testInverseDFT() {
		// DFT
		DFT testDFT = new DFT(shortSamples.size());
		List<Complex> tempList = testDFT.compute(shortSamples);
		// IDFT
		IDFT testIDFT = new IDFT(shortSamples.size());
		List<Complex> resultList = testIDFT.compute(tempList);

		// test if the values after the IDFT are equal to the original values
		for (int i = 0; i < resultList.size(); i++) {
			Complex result = resultList.get(i);
			Complex expected = shortSamples.get(i);
//			System.out.println("expected,result: \t" + expected + " \t "
//					 + result);

			assertTrue(result.equals(expected));
			assertEquals(expected.getReal(), result.getReal(), Double.parseDouble("1.0E-9"));
			assertEquals(expected.getImaginary(), result.getImaginary(), Double.parseDouble("1.0E-9"));
		}
		// System.out.println();
	}

	public void testRecurFFT() {
		int size = shortSamples.size();
		Complex[] samples = new Complex[size];
		for (int i = 0; i < size; i++) {
			samples[i] = shortSamples.get(i);
		}

		Complex[] results = FFT.recCompute(samples);
		for (int i = 0; i < samples.length; i++) {
			Complex result = results[i];
			Complex expected = shortSolutions.get(i);
//			System.out.println("expected,result: \t" + expected + " \t "
//					 + result);

			assertTrue(result.equals(expected));
			assertEquals(expected.getReal(), result.getReal(), Double.parseDouble("1.0E-9"));
			assertEquals(expected.getImaginary(), result.getImaginary(), Double.parseDouble("1.0E-9"));
		}
	}
	
	public void testIterFFT() {
		int size = shortSamples.size();
		Complex[] samples = new Complex[size];
		for (int i = 0; i < size; i++) {
			samples[i] = shortSamples.get(i);
		}

		Complex[] results = FFT.iterCompute(samples);
		for (int i = 0; i < samples.length; i++) {
			Complex result = results[i];
			Complex expected = shortSolutions.get(i);
//			System.out.println("expected,result: \t" + expected + " \t "
//					 + result);

			assertTrue(result.equals(expected));
			assertEquals(expected.getReal(), result.getReal(), Double.parseDouble("1.0E-9"));
			assertEquals(expected.getImaginary(), result.getImaginary(), Double.parseDouble("1.0E-9"));
		}
	}

	public void testFFTequalsDFT() {
		int size = longSamples.size();
		Complex[] samples = new Complex[size];
		for (int i = 0; i < size; i++) {
			samples[i] = longSamples.get(i);
		}

		DFT testDFt = new DFT(size);
		List<Complex> longSolutions = testDFt.compute(longSamples);
		Complex[] results = FFT.recCompute(samples);
		for (int i = 0; i < samples.length; i++) {
			Complex result = results[i];
			Complex expected = longSolutions.get(i);
//			 System.out.println("expected,result: \t" + expected + " \t "
//			 + result);
			 
			assertTrue(result.equals(expected));
			assertEquals(expected.getReal(), result.getReal(), Double.parseDouble("1.0E-9"));
			assertEquals(expected.getImaginary(), result.getImaginary(), Double.parseDouble("1.0E-9"));
		}
	}
	
	public void testSmallIterFFTequalsDFT() {
		Complex[] samples = { new Complex(1.0, 1.0), new Complex(1.0, 2.0) };
		ArrayList<Complex> valList = new ArrayList<Complex>();
		valList.add(new Complex(1.0, 1.0));
		valList.add(new Complex(1.0, 2.0));

		DFT testDFt = new DFT(samples.length);
		shortSolutions = testDFt.compute(valList);
		Complex[] results = FFT.iterCompute(samples);
		for (int i = 0; i < samples.length; i++) {
			Complex result = results[i];
			Complex expected = shortSolutions.get(i);
//			 System.out.println("expected,result: \t" + expected + " \t "
//			 + result);
			 
			assertTrue(result.equals(expected));
			assertEquals(expected.getReal(), result.getReal(), Double.parseDouble("1.0E-9"));
			assertEquals(expected.getImaginary(), result.getImaginary(), Double.parseDouble("1.0E-9"));
		}
	}
	
	public void testLongRecFFT() {
		Complex[] samples = new Complex[longSamples.size()];
		for(int i=0; i<longSamples.size(); i++)
			samples[i] = longSamples.get(i);
		
		List<Complex> longSolutions = longDFT.compute(longSamples);
		Complex[] results = FFT.recCompute(samples);
		for (int i = 0; i < samples.length; i++) {
			Complex result = results[i];
			Complex expected = longSolutions.get(i);
//			 System.out.println("expected,result: \t" + expected + " \t "
//			 + result);
			 
			assertTrue(result.equals(expected));
			assertEquals(expected.getReal(), result.getReal(), Double.parseDouble("1.0E-9"));
			assertEquals(expected.getImaginary(), result.getImaginary(), Double.parseDouble("1.0E-9"));
		}
	}
	
	public void testLongIterFFT() {
		Complex[] samples = new Complex[longSamples.size()];
		for(int i=0; i<longSamples.size(); i++)
			samples[i] = longSamples.get(i);
		
		List<Complex> longSolutions = longDFT.compute(longSamples);
		Complex[] results = FFT.iterCompute(samples);
		for (int i = 0; i < samples.length; i++) {
			Complex result = results[i];
			Complex expected = longSolutions.get(i);
//			 System.out.println("expected,result: \t" + expected + " \t "
//			 + result);
			 
			assertTrue(result.equals(expected));
			assertEquals(expected.getReal(), result.getReal(), Double.parseDouble("1.0E-9"));
			assertEquals(expected.getImaginary(), result.getImaginary(), Double.parseDouble("1.0E-9"));
		}
	}
	
	public static void main(String[] args) throws Exception {
		double[] samples = new double[256];
		DoubleSource source = new DoublesParser("Files/hamming.txt");
		for(int i=0; i<samples.length; i++)
			samples[i] = source.getNextDouble();
		
		long startTime = System.currentTimeMillis();
		FFT.iterCompute(samples);
		System.out.println("FFT finished in: " + (System.currentTimeMillis() - startTime) + "ms");
	}

}
