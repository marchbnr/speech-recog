package muster.preprocessing;

import muster.preprocessing.steps.DCT;
import muster.preprocessing.steps.FFT;
import muster.preprocessing.steps.FloatingTimeChannel;
import muster.preprocessing.steps.HammingWindow;
import muster.preprocessing.steps.LogScaler;
import muster.preprocessing.steps.MelScaler;
import muster.preprocessing.steps.PowerSpectrum;
import muster.preprocessing.steps.STimeChannel;
import muster.preprocessing.steps.TimeSlope;
import muster.recognition.WDetectionIfc;
import muster.util.Props;

public class ShortPreprocessor implements PreprocIfc {
	
	DPreprocIfc dftImpl;
	
	/**
	 * @param steps - how far to preprocess:
	 *  0 - samples
	 *  1 - hamming weights
	 *  2 - fft + power spectrum
	 *  3 - mel-scaled
	 *  4 - log-scaled
	 *  5 - dct
	 *  6 - long time channel normed
	 *  7 - time slope
	 */
	public ShortPreprocessor(WDetectionIfc detector, int steps) {
		
		dftImpl = (DPreprocIfc)detector;
		
		if(steps >= 7) {
			dftImpl = new TimeSlope(detector);
			dftImpl.compute(new double[12]);
			dftImpl.compute(new double[12]);
		}
		if(steps >= 6) {
			String preproc = Props.getProp("preproc");
			if(preproc.equals("SHORT"))
				dftImpl = new STimeChannel(dftImpl);
			else if(preproc.equals("FLOATING"))
				dftImpl = new FloatingTimeChannel(dftImpl);
			else if(preproc.equals("NONE"));
			else throw new RuntimeException("unknown preprocessor");
		}
		if(steps >= 5) {
			dftImpl = new DCT(dftImpl);
		}
		if(steps >= 4) {
			dftImpl = new LogScaler(dftImpl);
		}
		if(steps >= 3) {
			dftImpl = new MelScaler(dftImpl, 128, 15);
		}
		if(steps >= 2) {
			dftImpl = new FFT(new PowerSpectrum(dftImpl));
		}
		if(steps >= 1) {
			dftImpl = new HammingWindow(dftImpl);
		}
	}

	@Override
	public void compute(double[] samples) {
		dftImpl.compute(samples);
	}

	@Override
	public void finish() { }

}
