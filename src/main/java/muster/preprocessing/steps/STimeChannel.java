package muster.preprocessing.steps;

import muster.preprocessing.DPreprocIfc;

/**
 * The implementation uses a short time, only 5 vectors, for normalization.
 */
public class STimeChannel implements DPreprocIfc {
	
	DPreprocIfc timeSlope;
	double[][] weights;
	int index = 0;
	boolean weightsFilled = false;
	
	public STimeChannel(DPreprocIfc timeSlope) {
		this.timeSlope = timeSlope;
	}

	@Override
	public void compute(double[] samples) {
		if(weights == null) {
			weights = new double[samples.length][5];
		}
		
		for(int i=0; i<samples.length; i++) {
			weights[i][index] = samples[i];
		}
		if(++index > 4) {
			index = 0;
			weightsFilled = true;
		}
		
		if(weightsFilled) {
			int computedIndex = (index+3)%5;
			double[] newSamples = new double[samples.length];
			for(int i=0; i<samples.length; i++) {
				double sum = 0.0;
				for(int u=0; u<5; u++) {
					sum += weights[i][u];
				}
				newSamples[i] = weights[i][computedIndex] - (sum /5.0);
			}
			timeSlope.compute(newSamples);
		}
		
	}

}
