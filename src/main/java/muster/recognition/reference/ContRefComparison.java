package muster.recognition.reference;

import java.util.Arrays;

import muster.recognition.distance.DistanceMetrics;
import muster.util.Props;

/**
 * A RefComparison is used to compare the minimum distance between
 * two references.
 * 
 * Distance metrics and path-creation can be changed via parameters.
 */
public class ContRefComparison {
	
	public static boolean DEBUG = false;
	
	static DistanceMetrics metrics = null;
	int vectorSize = 0;

	SilComparison silComp = null;
	Reference reference;
	int nRefVectors;
	double[] distances;
	double[] nuDistances;
	double preIndexCost = Double.NaN;
	
	String[] histories;
	String[] nuHistories;
	
	/** totalHistory is initially null and empty if histories conflict */
	String totalHistory = null;
	
	public ContRefComparison(Reference reference, SilComparison silenceComparison, DistanceMetrics metrics) {
		init(reference, silenceComparison, metrics, false);
	}
	
	public ContRefComparison(Reference reference, SilComparison silenceComparison, DistanceMetrics metrics, boolean withPath) {
		init(reference, silenceComparison, metrics, withPath);
	}
	
	private void init(Reference reference, SilComparison silenceComparison, DistanceMetrics metrics, boolean withPath) {
		this.reference = reference;
		nRefVectors = reference.size();
		
		distances = new double[nRefVectors];
		nuDistances = new double[nRefVectors];
		Arrays.fill(distances, Double.NaN);
		Arrays.fill(nuDistances, Double.NaN);
		
		silComp = silenceComparison;
		ContRefComparison.metrics = metrics;
		
		histories = new String[nRefVectors];
		nuHistories = new String[nRefVectors];
		vectorSize = Props.getVectorSize();
	}
	
	private int getPreIndex(int i) {
	  	// if possible find the lowest cost of the 3 possible predecessors + silence
		// otherwise keep -2 and forget about history
		int preIndex = -2;
		preIndexCost = Double.NaN;
		double cost = 0;
		
		if(!Double.isNaN(distances[i]) && (i != 0)) {
			preIndex = i;
			preIndexCost = metrics.getPenalty(0) + distances[i];
		}
		if(isLegalIndex(i-1) && !Double.isNaN(distances[i-1])) {
			cost = metrics.getPenalty(1) + distances[i-1];
			if(preIndex == -2 || cost < preIndexCost) {
				preIndex = i-1;
				preIndexCost = cost;
			}
		}
		if(isLegalIndex(i-2) && !Double.isNaN(distances[i-2])) {
			cost = metrics.getPenalty(2) + distances[i-2];
			if(preIndex == -2 || cost < preIndexCost) {
				preIndex = i-2;
				preIndexCost = cost;
			}
		}
		if(i == 0) {
			if(silComp == null) return preIndex;
			cost = silComp.getWordBeginPenalty() + silComp.getDistance();
			if(preIndex == -2 || cost < preIndexCost || Double.isNaN(preIndexCost)) {
				preIndex = -1;
				preIndexCost = cost;
			}
		}
		return preIndex;
	}

	// update the distance for the next frame/vector of the recorded vectors
	public void compareStep(double[] sample) {
		totalHistory = null;
		
		for(int i=0; i<reference.size(); i++) {
			int preIndex = getPreIndex(i);
			assert(!Double.isNaN(preIndexCost));
			
			// or that of a regular index predecessor
			if(preIndex > -1) {
				nuDistances[i] = preIndexCost + metrics.getDistance(sample, i, reference);
				nuHistories[i] = histories[preIndex];
			}
			// either get the silence distance
			else if(preIndex == -1) {
				nuDistances[i] = preIndexCost + metrics.getDistance(sample, i, reference);
				nuHistories[i] = silComp.getHistory();
				if(nuHistories[i] == null)
					nuHistories[i] = "";
				nuHistories[i] += reference.pattern + " ";
			}
			// if the index is unchanged, this is the first vector
			else if(preIndex == -2 && i==0 && silComp == null) {
				nuHistories[i] = reference.pattern + " ";
				nuDistances[i] = metrics.getDistance(sample, i, reference);
			}
			else break;
		}
		
		totalHistory = null;
		for(String history : nuHistories) {
			if(history == null || history.length() == 0) {
				totalHistory = null;
				break;
			}
			else
				totalHistory = history.split(" ")[0];
		}

		// reuse arrays
		double[] swap = distances;
		distances = nuDistances;
		nuDistances = swap;
		Arrays.fill(nuDistances, Double.NaN);
		
		String[] swap2 = histories;
		histories = nuHistories;
		nuHistories = swap2;
		Arrays.fill(nuHistories, "");
	}
	
	protected boolean isLegalIndex(int index) {
		return index > -1;
	}
	
	public String getHistory() {
		return totalHistory;
	}
	
	public String getCompleteHistory() {
		return totalHistory;
	}
	
	public String[] getHistories() {
		return histories;
	}
	
	public String getPattern() {
		return reference.pattern;
	}
	
	/** interface for silence comp */
	public double getLastCost() {
		return distances[distances.length-1];
	}
	
	/** ifc for silence, can any word already be finished? */
	public boolean isWordFinished() {
		return !Double.isNaN(distances[reference.size()-1]);
	}
	
	/** interface for silence comp */
	public String getLastHistory() {
		return histories[distances.length-1];
	}
	
	public double[] getDistances() {
		return distances;
	}
	
	public void clearHistory() {
		totalHistory = null;
		for(int i=0; i<histories.length; i++) {
			int separator = histories[i].indexOf(' ')+1;
			histories[i] = histories[i].substring(separator);
		}
		
//		for(int i=0; i<histories.length; i++) {
//			System.out.println("clear_hist " + reference.pattern + ": " + histories[i]);
//		}
			
	}
	
	public String toString() {
		return reference.pattern;
	}
	
	public void printVectorState() {
		for(int i=distances.length; i>0; i--) {
			System.out.println(distances[i]);
		}
		System.out.println(" ---- ");
	}
	
	public void printDists() {
		System.out.println(reference.pattern + " distances:");
		for(double distance : distances)
			System.out.println(distance);
	}
	
	public void printDistHists() {
		System.out.println(reference.pattern + " distances:");
		for(int i=0; i<distances.length; i++)
			System.out.println(distances[i] + "\t\t" + histories[i]);
	}
	
	public void printHistory() {
		System.out.println(reference.pattern + " history:");
		for(int i=histories.length-1; i>=0; i--) {
			System.out.println(histories[i]);
		}
		System.out.println(" ---- ");
	}
}
