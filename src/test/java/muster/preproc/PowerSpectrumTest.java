package muster.preproc;

import muster.preprocessing.DPreprocIfc;
import muster.preprocessing.steps.FFT;
import muster.preprocessing.steps.PowerSpectrum;
import muster.sound.DoubleSource;
import muster.util.DoublesParser;
import junit.framework.TestCase;

public class PowerSpectrumTest extends TestCase implements DPreprocIfc {
	
	int frameSize = 256;
	double[] result;
	
	public void testFromHamming() throws Exception {
		{
			DoubleSource source = new DoublesParser("Files/hamming.txt");
			double[] frame = new double[frameSize];
			for(int i=0; i<frameSize; i++) {
				frame[i] = source.getNextDouble();
			}
			DPreprocIfc fft = new FFT(new PowerSpectrum(this));
			fft.compute(frame);
		}
		
		{
			DoubleSource resultsSource = new DoublesParser("Files/powerspectrum.txt");
			for(int i=0; i<frameSize/2; i++) {
				Double expected = resultsSource.getNextDouble();
//				System.out.println("comparing: " + expected + " and " + result[i]);
				assertEquals(expected, result[i], Double.parseDouble("1.09e-9"));
			}
		}
	}

	public void compute(double[] samples) {
		result = samples;
	}

}
