package muster.preprocessing.steps;

import java.util.ArrayList;

import muster.preprocessing.DPreprocIfc;
import muster.recognition.WDetectionIfc;

public class TimeSlope implements DPreprocIfc {

  WDetectionIfc detector;

  ArrayList<double[]> cache = new ArrayList<double[]>();

  public TimeSlope(WDetectionIfc detector) {
    this.detector = detector;
  }

  public void compute(double[] samples) {
    int frameSize = samples.length;
    cache.add(samples);

    if (cache.size() > 4) {
      double[] results = new double[2 * frameSize];
      
      for (int i = 0; i < frameSize; i++) {
	results[i] = cache.get(2)[i];

	// compute slope for a fixed 5 values-Array
	double slopeSum = 2 * (cache.get(4)[i] - cache.get(0)[i]);
	slopeSum += cache.get(3)[i] - cache.get(1)[i];
	results[i + frameSize] = slopeSum / 6.0;
      }
      //only 5 elements are allowed to be in the cache
      cache.remove(0);
      detector.compute(results);
    }
  }

}
