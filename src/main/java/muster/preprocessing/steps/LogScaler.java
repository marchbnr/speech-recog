package muster.preprocessing.steps;

import muster.preprocessing.DPreprocIfc;

public class LogScaler implements DPreprocIfc {
	
	DPreprocIfc dctImpl;
	
	public LogScaler(DPreprocIfc dctImpl) {
		this.dctImpl = dctImpl;
	}

	public void compute(double[] samples) {
		for(int i=0; i<samples.length; i++) {
			samples[i] = Math.log(samples[i]+1);
		}
		dctImpl.compute(samples);
	}

}
