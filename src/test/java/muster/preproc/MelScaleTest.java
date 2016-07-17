package muster.preproc;

import muster.preprocessing.DPreprocIfc;
import muster.preprocessing.steps.MelScaler;
import muster.sound.DoubleSource;
import muster.util.DoublesParser;
import junit.framework.TestCase;

public class MelScaleTest extends TestCase implements DPreprocIfc {
	
	double[] results;
	
	public void testFromPower() throws Exception {
		int melSize = 15;
		{
			DoubleSource source = new DoublesParser("Files/powerspectrum.txt");
			int frameSize = 128;
			double[] frame = new double[frameSize];
			for(int i=0; i<frameSize; i++) {
				frame[i] = source.getNextDouble();
//				System.out.println(frame[i]);
			}
			DPreprocIfc mel = new MelScaler(this, frameSize, melSize);
			mel.compute(frame);
		}
		
		{
			DoubleSource resultsSource = new DoublesParser("Files/filterbank.txt");
			for(int i=0; i<melSize; i++) {
				Double expected = resultsSource.getNextDouble();
//				System.out.println("comparing: " + expected + " and " + results[i]);
				assertEquals(expected, results[i], Double.parseDouble("1.09e-9"));
			}
		}
	}

	public void compute(double[] samples) {
//		for(Double value : samples) {
//			System.out.println(value + " ");
//		}
		results = samples;
	}
}
