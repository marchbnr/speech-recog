package muster.preprocessing.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import muster.preprocessing.DPreprocIfc;

public class MelScaler implements DPreprocIfc {
	
	DPreprocIfc log;
	int size;
	List<Triangle> triangles = new ArrayList<Triangle>();

	
	public class Triangle {
		HashMap<Integer, Double> weights = new HashMap<Integer, Double>();
		
		double start;
		double mid;
		double end;
		double height;
		double ascSlope;
		double desSlope;
		
		public double getSum(double[] samples) {
			double value = 0.0;
			for(Entry<Integer, Double> entry : weights.entrySet()) {
				int index = entry.getKey();
				double weight = entry.getValue();
				value += samples[index] * weight;
//				System.out.println("computing triangle, index: " + index +
//						" weight: " + weight + " value: " + samples[index]);
			}
			return value;
		}
		
		public String toString() {
			return "Tri s: " + start + ",m: " + mid + ",e: " + end + ",h: " + height + " - a1: " + ascSlope + " a2: " + desSlope; 
		}
	}
	
	public MelScaler(DPreprocIfc log, int samplesSize, int resultSize) {
		this.log = log;
		this.size = resultSize;
		
		double lowerMelFreqBound, higherMelFreqBound;
		lowerMelFreqBound = toMel(200);
		higherMelFreqBound = toMel(3700);
		double melDistance = (higherMelFreqBound - lowerMelFreqBound) / (size+1);
		double frqSteps = 1.0/((samplesSize*2)/8000.0);
//		System.out.println("freqsteps: " + frqSteps);
		
		// set the index to the first sample used
		int sample=((int)(200.0 / frqSteps)+1);
//		System.out.println("starting at: " + sample * frqSteps);
		
		for(int triangleIndex=0; triangleIndex<size; triangleIndex++) {
			Triangle triangle = new Triangle();
			triangles.add(triangle);
			double melPosition = lowerMelFreqBound + triangleIndex*melDistance;
			triangle.start = fromMel(melPosition);
			triangle.mid = fromMel(melPosition+melDistance);
			triangle.end = fromMel(melPosition+melDistance+melDistance);
			triangle.height = 2/(triangle.end - triangle.start);
			triangle.ascSlope = triangle.height/(triangle.mid - triangle.start);
			triangle.desSlope = -triangle.height/(triangle.end - triangle.mid);
			
			// increase the index only for the left side of the triangle
			int maxMidFreq = (int)(triangle.mid / frqSteps);
			for(; sample<= maxMidFreq; sample++) {
				double value = triangle.ascSlope * ((sample * frqSteps) - triangle.start);
				triangle.weights.put(sample, value);
			}
			int maxFreq = (int) (triangle.end / frqSteps);
			for(int u=sample; u<= maxFreq; u++) {
				double value = triangle.desSlope * ((u * frqSteps) - triangle.end);
				triangle.weights.put(u, value);
			}
		}
	}

	public void compute(double[] samples) {
		double results[] = new double[size];
		for(int i=0; i<size; i++) {
			results[i] = triangles.get(i).getSum(samples);
//			System.out.println(triangles.get(i));
//			System.out.println();
		}
		
		log.compute(results);
	}
	
	public double toMel(double value) {
		return 2595 * Math.log10(value/700 + 1);
	}

	public double fromMel(double value) {
		return 700 * (Math.pow(10, (value/2595)) -1);
	}
}
