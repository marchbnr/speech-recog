package muster.unittests.preproc;

import java.util.ArrayList;
import java.util.List;

import muster.preprocessing.DPreprocIfc;
import muster.preprocessing.steps.LTimeChannel;
import muster.sound.DoubleSource;
import muster.util.DoublesParser;
import junit.framework.TestCase;

public class TimeChannelTests extends TestCase implements DPreprocIfc {

	List<double[]> results = new ArrayList<double[]>();
	int count = 0;
	
	public void testFromDct() throws Exception {
		int frameSize = 12;
		int frames = 33;
		{
			DoubleSource source = new DoublesParser("Files/dct.txt");
			double[][] samples = new double[frames][frameSize];
			for(int u=0; u<frames; u++) {
				for(int i=0; i<frameSize; i++) {
					samples[u][i] = source.getNextDouble();
				}
			}
			DPreprocIfc time = new LTimeChannel(this, samples);
			
			for(double[] frame : samples) {
				time.compute(frame);
			}
		}
		
		{
			DoubleSource resultsSource = new DoublesParser("Files/kanalnormierung.txt");
			for(double[] resultVector : results) {
				for(Double sample : resultVector) {
					Double expected = resultsSource.getNextDouble();
//					System.out.println("comparing: " + expected + " and " + sample);
					assertEquals(expected, sample, Double.parseDouble("1.09e-9"));
				}
				System.out.println();
			}
		}
	}

	@Override
	public void compute(double[] samples) {
		results.add(samples);
	}
}
