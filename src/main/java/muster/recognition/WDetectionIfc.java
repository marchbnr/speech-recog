package muster.recognition;

import muster.preprocessing.DPreprocIfc;

public interface WDetectionIfc extends DPreprocIfc {

	/**
	 * @param samples The next frame that has been preprocessed.
	 */
  public void compute(double[] samples);
  
}
