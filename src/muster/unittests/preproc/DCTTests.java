package muster.unittests.preproc;

import muster.preprocessing.DPreprocIfc;
import muster.preprocessing.steps.DCT;
import muster.sound.DoubleSource;
import muster.util.DoublesParser;
import junit.framework.TestCase;

public class DCTTests extends TestCase implements DPreprocIfc {
	
	double[] results;
	
	public void testFromMel() throws Exception {
		{
			int size = 15;
			DoubleSource source = new DoublesParser("Files/log.txt");
			double[] frame = new double[size];
			for(int i=0; i<size; i++) {
				frame[i] = source.getNextDouble();
//				System.out.println(frame[i]);
			}
			DPreprocIfc log = new DCT(this);
			log.compute(frame);
		}
		
		{
			DoubleSource resultsSource = new DoublesParser("Files/dct.txt");
			for(int i=0; i<results.length; i++) {
				double expected = resultsSource.getNextDouble();
//				System.out.println("comparing: " + expected + " and " + results[i]);
				assertEquals(expected, results[i], Double.parseDouble("1.09e-9"));
			}
		}
	}

	public void compute(double[] samples) {
		results = samples;
	}
}
