package muster.recognition.distance;

import muster.recognition.reference.Reference;


public class WeightedVolumeDistance extends DistanceMetrics {

	public double getDistance(int refAIndex, Reference sample, int refBIndex, Reference model) {
		double tmp = model.getElement(refBIndex, 0) - sample.getElement(refAIndex, 0);
		if(tmp < 0) return 4*tmp*tmp;
		else return tmp*tmp;
	}
	
	public double getDistance(double[] frame, int refBIndex, Reference model) {
		throw new UnsupportedOperationException();
	}
	
	public double getPenalty(int distance) {
		if(distance == 0) return 0;
		if(distance == 1) return 0;
		if(distance == 2) return 0;
		else throw new RuntimeException("Use a value between 0 and 2.");
	}
}
