package muster.recognition.distance;

import muster.recognition.reference.Reference;
import muster.util.Props;


public class MahalaDistance extends DistanceMetrics {
	
	@Override
	public double getDistance(double[] frame, int modelVector, Reference model) {
		if(model.variance == null)
			throw new RuntimeException("Variances not set, or illegal distance call");
		
		double result = model.varianceSum[modelVector];
		int vectorSize = Props.getInt("vectorsize");
		for (int i = 0; i < vectorSize; i++) {
			double variance = model.variance[modelVector][i];
			
			double dist = frame[i] - model.getElement(modelVector,i);
			result += (dist*dist)/variance;
		}
		return result;
	}
	
	@Override
	public double getPenalty(int distance) {
		if(distance == 0) return 50;
		if(distance == 1) return 0;
		if(distance == 2) return 1000;
		else throw new RuntimeException("Use a value between 0 and 2.");
	}
}
