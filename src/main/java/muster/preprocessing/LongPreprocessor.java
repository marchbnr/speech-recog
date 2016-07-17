package muster.preprocessing;

import java.util.ArrayList;
import java.util.Arrays;

import muster.preprocessing.steps.DCT;
import muster.preprocessing.steps.FFT;
import muster.preprocessing.steps.HammingWindow;
import muster.preprocessing.steps.LTimeChannel;
import muster.preprocessing.steps.LogScaler;
import muster.preprocessing.steps.MelScaler;
import muster.preprocessing.steps.PowerSpectrum;
import muster.preprocessing.steps.TimeSlope;
import muster.recognition.WDetectionIfc;

public class LongPreprocessor implements PreprocIfc {
	
	DPreprocIfc dftImpl;
	PreprocWrapper ltimeChannel = null;
	PreprocWrapper ltimeSlope = null;
	
	private abstract class PreprocWrapper implements DPreprocIfc {
		public ArrayList<double[]> list = new ArrayList<double[]>();
		DPreprocIfc dest;
		
		public PreprocWrapper(DPreprocIfc dest) {
			this.dest = dest;
		}
		
		@Override
		public void compute(double[] samples) {
			list.add(samples);
		}
		
		public abstract void finish();
	}
	
	
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
	public LongPreprocessor(WDetectionIfc detector, int steps) {
		
		dftImpl = (DPreprocIfc)detector;
		
		if(steps >= 7) {
			// add null vectors at the beginning and at the end, before calling timeSlope
			DPreprocIfc timeSlope = new TimeSlope(detector);
			dftImpl = ltimeSlope = new PreprocWrapper(timeSlope) {
				public void finish() {
					double[] nulls = new double[list.get(0).length];
					Arrays.fill(nulls, 0.0);
					ArrayList<double[]> nuList = new ArrayList<double[]>();
					nuList.add(nulls);
					nuList.add(nulls);
					nuList.addAll(list);
					nuList.add(nulls);
					nuList.add(nulls);
					list = nuList;
					for(double[] samples : list)
						dest.compute(samples);
				}
			};
		}
		if(steps >= 6) {
			// wait for all steps to finish and create a weight for all values
			dftImpl = ltimeChannel = new PreprocWrapper(dftImpl) {
				public void finish() {
//					list.remove(list.size()-1);
					double[][] value = new double[list.size()][list.get(0).length];
					for(int i=0; i<list.size(); i++)
						for(int u=0; u<list.get(0).length; u++)
							value[i][u] = list.get(i)[u];
					dest = new LTimeChannel(dest, value);
					for(double[] samples : list)
						dest.compute(samples);
				}
			};
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
	
	public void finish() {
		if(ltimeChannel != null) ltimeChannel.finish();
		if(ltimeSlope != null) ltimeSlope.finish();
	}

}
