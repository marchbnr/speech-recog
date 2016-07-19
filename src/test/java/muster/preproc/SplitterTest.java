package muster.preproc;

import java.util.ArrayList;

import muster.preprocessing.DPreprocIfc;
import muster.sound.ByteConverter;
import muster.sound.DoubleSource;
import muster.sound.FilePlayer;
import muster.sound.FrameSplitter;
import muster.util.DoublesParser;
import junit.framework.TestCase;

public class SplitterTest extends TestCase implements DPreprocIfc {
	
	ArrayList<double[]> frames = new ArrayList<double[]>();
	
	public void testFromWave() throws Exception {
		int frameSize = 256;
		{
			FilePlayer player = new FilePlayer("vielleicht.wav");
			FrameSplitter splitter = new FrameSplitter(new ByteConverter(player), this, frameSize);
			player.start();
			splitter.start();
			while(player.playing) {
				Thread.sleep(50);
			}
		}
		
		{
			DoubleSource resultsSource = new DoublesParser("samples.txt");
			for(int i=0; i<33; i++) {
				for(int u=0; u<frameSize; u++) {
					Double expected = resultsSource.getNextDouble();
					System.out.println("comparing: " + expected + " and " + frames.get(i)[u]);
					assertEquals(expected, frames.get(i)[u], Double.parseDouble("1.09e-9"));
				}
				
			}
		}
	}

	public void compute(double[] samples) {
		frames.add(samples);
	}
}
