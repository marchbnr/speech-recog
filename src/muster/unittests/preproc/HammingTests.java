package muster.unittests.preproc;

import muster.preprocessing.DPreprocIfc;
import muster.preprocessing.steps.HammingWindow;
import muster.sound.DoubleSource;
import muster.sound.FrameSplitter;
import muster.util.DoublesParser;
import junit.framework.TestCase;

public class HammingTests extends TestCase implements DPreprocIfc{
	
	boolean running = true;
	double[] frame = null;
	int frameSize = 256;

	public void testHammingValues() throws Exception {
		DoubleSource source = new DoublesParser("Files/samples.txt");
		FrameSplitter splitter = new FrameSplitter(source, new HammingWindow(this), frameSize);
		splitter.start();
		while(running) {
			Thread.sleep(10);
		}
		
		for(double value : frame) {
			System.out.print(value + " ");
		}
		DoubleSource resultsParser = new DoublesParser("Files/hamming.txt");
		Double[] results = new Double[frameSize];
		for(int i=0; i<frameSize; i++) {
			results[i] = resultsParser.getNextDouble();
			assertEquals(results[i], frame[i], Double.parseDouble("1.0e-9"));
		}
	}

	@Override
	public void compute(double[] samples) {
		if(running) {
			frame = samples;
			running = false;
		}
	}
	
}
