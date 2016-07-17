package muster.preprocessing.steps;

import muster.preprocessing.DPreprocIfc;
import muster.preprocessing.PowerIfc;
import muster.util.BitReverser;

public class FFT implements DPreprocIfc {
	
	PowerIfc powerSpectrum;
	static Complex cloneLeft = new Complex(0,0);
	static Complex cloneRight = new Complex(0,0);
	static Complex[] results = null;
	
	public FFT(PowerIfc powerSpectrum) {
		this.powerSpectrum = powerSpectrum;
	}

	public static Complex[] recCompute(Complex[] _samples) {
		int n = _samples.length;
		Complex[] samples = new Complex[n];
		for(int i=0; i<n; i++) {
			samples[i] = _samples[BitReverser.reverse(n-1, i)];
		}
		
		Complex[] results = compute(n,0,samples);
		for(int i=0; i<n; i++) {
			results[i].divide(n,0);
		}
		return results;
	}
	
	private static Complex[] compute(int n, int index, Complex[] samples) {
		if(n == 1) {
//			System.out.println("returning " + index);
			Complex[] results = new Complex[n];
			results[0] = samples[index];
			return results;
		}
		
//		System.out.println("computing " + index + " to " + (index+n));
		Complex[] resultsA = compute(n/2, index, samples);
		Complex[] resultsB = compute(n/2, index+n/2, samples);
		
		Complex[] results = new Complex[n];
		
		for(int l=0; l<n/2; l++) {
			Complex polarFirst = Complex.fromPolar(1, Math.PI * -2 * l/n);
			Complex polarSecond = Complex.fromPolar(1, Math.PI * -2 * (l+n/2)/n);

			Complex resultsBFirst = (Complex)resultsB[l].clone();
			Complex resultsBSecond = (Complex)resultsB[l].clone();
			Complex resultsAFirst = (Complex)resultsA[l].clone();
			Complex resultsASecond = (Complex)resultsA[l].clone();

			resultsBFirst.multiply(polarFirst);
			resultsBSecond.multiply(polarSecond);
			
			resultsAFirst.add(resultsBFirst);
			results[l] = resultsAFirst;
			resultsASecond.add(resultsBSecond);
			results[l+n/2] = resultsASecond;
		}
		return results;
	}
	
	public static Complex[] iterCompute(Complex[] samples) {
		int dim = samples.length;
		if(results == null || results.length != samples.length)
			results = new Complex[dim];
		
		for(int i=0; i<dim; i++) {
			results[i] = samples[BitReverser.reverse(dim-1, i)];
		}
	
		return innerIterCompute(dim);
	}
	
	public static Complex[] iterCompute(double[] samples) {
		int dim = samples.length;
		if(results == null || results.length != samples.length)
			results = new Complex[dim];
		
		for(int i=0; i<dim; i++) {
			results[i] = new Complex(samples[BitReverser.reverse(dim-1, i)],0);
		}
	
		return innerIterCompute(dim);
	}
	
	private static Complex[] innerIterCompute(int dim) {	
		// for each size of the Matrix from 1 .. n/2
		for(int i=1; i<dim; i*= 2) {
			int n = i*2;
			// for each part of the Matrix with size i (i-Matrix)
			for(int index=0; index < dim; index+=n) {
				// for each index of the partial Matrix of the i-Matrix
				for(int l=index; l<(index+i); l++) {
					Complex left = results[l];
					Complex right = results[l+i];
					
					cloneLeft.setPolar(1, Math.PI * -2 * l/n);
					cloneRight.setPolar(1, Math.PI * -2 * (l+i)/n);
	
					cloneLeft.multiply(right);
					cloneLeft.add(left);
					results[l] = cloneLeft;
					
					cloneRight.multiply(right);
					cloneRight.add(left);
					results[l+i] = cloneRight;
					
					cloneLeft = left;
					cloneRight = right;
//					System.out.println("i: " + i + " u: " + u + " copying: " + l + " to " + (l+i));
				}
			}
		}
		for(int i=0; i<dim; i++){
			results[i].divide(dim, 0.0);
		}
		return results;
	}

	@Override
	public void compute(double[] samples) {
		powerSpectrum.compute(iterCompute(samples));
	}
}
