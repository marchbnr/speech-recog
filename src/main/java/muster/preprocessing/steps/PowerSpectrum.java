package muster.preprocessing.steps;

import muster.preprocessing.DPreprocIfc;
import muster.preprocessing.PowerIfc;

public class PowerSpectrum implements PowerIfc{
	
	private DPreprocIfc melScaler;
	private double[] power;
	
	public PowerSpectrum(DPreprocIfc mel){
		melScaler = mel;
	}

	public void compute(Complex[] samples) {
		if(power == null || power.length != samples.length/2)
			power = new double[samples.length/2];
//		double[] power = new double[samples.length/2];
		for(int i=0; i<power.length; i++){
			power[i] = samples[i].power();
		}
		melScaler.compute(power);
	}

}
