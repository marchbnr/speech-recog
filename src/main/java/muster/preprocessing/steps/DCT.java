package muster.preprocessing.steps;

import muster.preprocessing.DPreprocIfc;

public class DCT implements DPreprocIfc {
	
	DPreprocIfc channelImpl;
	
	public DCT(DPreprocIfc channelImpl) {
		this.channelImpl = channelImpl;
	}

	public void compute(double[] samples) {
		double[] results = new double[samples.length-3];
		int n=samples.length;
		for(int i=1; i<=results.length; i++) {
			double sum = 0;
			if(i==1) {
				for(int j=1; j <= samples.length; j++) {
					sum += samples[(j-1)];
				}
				sum *= Math.sqrt(1.0/n);
			}
			else {
				for(int j=1; j <= samples.length; j++) {
					sum += samples[(j-1)] * Math.cos((j-0.5) * Math.PI * (i-1)/n);
				}
				sum *= Math.sqrt(2.0/n);
			}
			results[(i-1)] = sum;
		}
		channelImpl.compute(results);
	}

}
