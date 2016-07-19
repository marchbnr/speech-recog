package muster.preproc;

import java.util.ArrayList;
import java.util.List;

import muster.preprocessing.DPreprocIfc;
import muster.preprocessing.steps.TimeSlope;
import muster.recognition.WDetectionIfc;
import muster.sound.DoubleSource;
import muster.util.DoublesParser;
import junit.framework.TestCase;

public class TimeSlopeTest extends TestCase implements WDetectionIfc {

	List<double[]> results = new ArrayList<double[]>();

	int count = 0;

	public void testFromDct() throws Exception {
		int frameSize = 12;
		int frames = 37;
		{
			DoubleSource source = new DoublesParser("kanalnormierung.txt");
			double[][] samples = new double[frames][frameSize];
			for (int u = 0; u < frames; u++) {
				for (int i = 0; i < frameSize; i++) {
					if (u < 2 || u > 34)
						samples[u][i] = 0.0;
					else
						samples[u][i] = source.getNextDouble();
				}
			}
			DPreprocIfc slope = new TimeSlope(this);

			for (double[] frame : samples) {
				slope.compute(frame);
			}
		}

		{
			DoubleSource resultsSource = new DoublesParser("ableitung.txt");
			for (double[] resultVector : results) {
				for (double sample : resultVector) {
					Double expected = resultsSource.getNextDouble();
//					System.out.println("comparing: " + expected + " and " + sample);
					 assertEquals(expected, sample, Double.parseDouble("1.09e-9"));
				}
				System.out.println();
			}
		}
	}

	public void compute(double[] samples) {
		results.add(samples);
	}
}
