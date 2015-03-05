package muster.preprocessing.steps;

import muster.preprocessing.DPreprocIfc;


/**
 * The implementation uses a long time, all vectors, for normalization.
 */
public class LTimeChannel implements DPreprocIfc {
	
	DPreprocIfc timeSlope;
	double[] weights;
	
	public LTimeChannel(DPreprocIfc timeSlope, double[][] weightedSamples) {
		this.timeSlope = timeSlope;
		int frames = weightedSamples.length;
		int frameSize = weightedSamples[0].length;
		weights = new double[frameSize];
		for(int i=0; i<frameSize; i++) {
			weights[i] = 0.0;
			for(int u=0; u<frames; u++) {
				weights[i] += weightedSamples[u][i];
			}
			weights[i] /= frames;
		}
	}

	@Override
	public void compute(double[] samples) {
		double[] result = new double[samples.length];
		for(int i=0; i<samples.length; i++) {
			result[i] = samples[i] - weights[i];
		}
		timeSlope.compute(result);
	}

}
