package muster.preprocessing.steps;

import muster.preprocessing.DPreprocIfc;

/**
 * The implementation uses a short time, only 5 vectors, for normalization.
 */
public class FloatingTimeChannel implements DPreprocIfc {
	
	DPreprocIfc timeSlope;
	double[] weights;
	double ratio = 0.5;
	boolean weightsFilled = false;
	
	public FloatingTimeChannel(DPreprocIfc timeSlope) {
		this.timeSlope = timeSlope;
	}

	@Override
	public void compute(double[] samples) {
		if(weights == null) {
			weights = new double[samples.length];
			for(int i=0; i<samples.length; i++)
				weights[i] = samples[i];
		}
		
		for(int i=0; i<samples.length; i++) {
			weights[i] = (samples[i] * ratio + weights[i] * (1-ratio));
			samples[i] = samples[i] - weights[i];
		}
		
		timeSlope.compute(samples);
	}

}
