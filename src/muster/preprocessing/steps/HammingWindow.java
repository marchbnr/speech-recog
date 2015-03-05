package muster.preprocessing.steps;

import muster.preprocessing.DPreprocIfc;

public class HammingWindow implements DPreprocIfc {
	
	DPreprocIfc dftImpl;
	
	public HammingWindow(DPreprocIfc dftImpl) {
		this.dftImpl = dftImpl;
	}
	
	public static double compute(int n, int l){
		return 0.54 - (0.46 * Math.cos(2 * Math.PI * l/n));
	}

	@Override
	public void compute(double[] samples) {
		for(int i=0; i<samples.length; i++) {
			samples[i] = samples[i] * compute(samples.length, i);
		}
		dftImpl.compute(samples);
	}

}
