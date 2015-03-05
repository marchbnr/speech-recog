package muster.unittests.preproc;

import java.util.ArrayList;
import java.util.List;

import muster.preprocessing.DPreprocIfc;
import muster.preprocessing.LongPreprocessor;
import muster.recognition.WDetectionIfc;
import muster.sound.ByteConverter;
import muster.sound.DoubleSource;
import muster.sound.FilePlayer;
import muster.sound.FrameSplitter;
import muster.util.DoublesParser;
import junit.framework.TestCase;

public class PreprocTests extends TestCase implements WDetectionIfc, DPreprocIfc {
	List<double[]> results;

	int count = 0;
	
	public void setUp() {
		results = new ArrayList<double[]>();
	}
	
	public void testHamming() throws Exception {
		runTest(1, "Files/hamming.txt");
	}
	
	public void testPower() throws Exception {
		runTest(2, "Files/powerspectrum.txt");
	}
	
	public void testMel() throws Exception {
		runTest(3, "Files/filterbank.txt");
	}
	
	public void testLog() throws Exception {
		runTest(4, "Files/log.txt");
	}
	
	public void testDCT() throws Exception {
		runTest(5, "Files/dct.txt");
	}
	
	public void testChannel() throws Exception {
		runTest(6, "Files/kanalnormierung.txt");
	}
	
	public void testSlope() throws Exception {
		runTest(7, "Files/ableitung.txt");
	}

	public void runTest(int step, String fileName) throws Exception {
		{
			FilePlayer player = new FilePlayer("Files/vielleicht.wav");
			DoubleSource source = new ByteConverter(player);
			LongPreprocessor preproc = new LongPreprocessor(this, step);
			FrameSplitter splitter = new FrameSplitter(source, preproc, 256);
			player.start();
			splitter.start();
			
			while(player.playing) {
				Thread.sleep(20);
			}
			Thread.sleep(200);
			preproc.finish();
		}

		{
			DoubleSource resultsSource = new DoublesParser(fileName);
			results.remove(results.size()-1);
			for (double[] resultVector : results) {
				for (Double sample : resultVector) {
					Double expected = resultsSource.getNextDouble();
					System.out.println("comparing: " + expected + " and " + sample);
					 assertEquals(expected, sample, Double.parseDouble("1.09e-4"));
				}
				System.out.println();
			}
		}
	}

	public void compute(double[] samples) {
		results.add(samples.clone());
	}
}
