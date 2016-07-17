package muster.recognition.distance;

import muster.recognition.reference.Reference;
import muster.util.Props;


public class DistanceMetrics {

	public double getDistance(int refAIndex, Reference sample, int refBIndex, Reference model) {
		return getDistance(sample.getVector(refAIndex), refBIndex, model);
	}
	
	public double getDistance(double[] frame, int refBIndex, Reference model) {
		double result = 0;
		int vectorSize = Props.getInt("vectorsize");
		for (int i = 0; i < vectorSize; i++) {
			double tmp = model.getElement(refBIndex, i) - frame[i];
			result += (tmp*tmp);
		}
		return result;
	}
	
	public double getPenalty(int distance) {
		if(distance == 0) return 5;
		if(distance == 1) return 0;
		if(distance == 2) return 10;
		else throw new RuntimeException("Use a value between 0 and 2.");
	}
}
