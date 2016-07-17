package muster.recognition.reference;

import java.util.List;

import muster.recognition.distance.DistanceMetrics;
import muster.util.Props;

public class SilComparison {
	
	Reference silence;
	double oldDistance;
	double nuDistance;
	DistanceMetrics metrics;
	List<ContRefComparison> comparisons;
	
	double loopPenalty;
	double wordEndPenalty;
	double wordBeginPenalty;
	
	// silence is starting out with an empty word-history
	String oldHistory = null;
	String nuHistory = null;
	
	public double silPenalty = 0;
	public double entrancePenalty = 0;
	
	public SilComparison(Reference silence, DistanceMetrics metrics) {
		this.silence = silence;
		this.metrics = metrics;
		loopPenalty = Props.silenceLoopPenalty;
		wordEndPenalty = Props.silWordEndPenalty;
		wordBeginPenalty = Props.silWordBeginPenalty;
	}
	
	public void compareStep(double[] frame) {
		// update a step later, so other comps cant see into the future
		oldDistance = nuDistance;
		oldHistory = nuHistory;
		
		// silence can either loop in greater silence
		double stepDistance = metrics.getDistance(frame, 0, silence);
		nuHistory = oldHistory;
		nuDistance = oldDistance + stepDistance + loopPenalty;
		
		// or come from a word
		double compDistance;
		for(ContRefComparison comp : comparisons) {
			compDistance = comp.getLastCost();
			if(comp.isWordFinished() && compDistance < oldDistance) {
				nuDistance = compDistance + stepDistance + wordEndPenalty;
				nuHistory = comp.getLastHistory();
			}
		}
	}
	
	public void setComparisons(List<ContRefComparison> comparisons) {
		this.comparisons = comparisons;
	}

	public String getHistory() {
		return oldHistory == null || oldHistory.length() == 0 ?
				oldHistory :
					oldHistory.split(" ")[0] + " ";
	}
	
	public double getDistance() {
		return oldDistance;
	}
	
	public double getWordBeginPenalty() {
		return wordBeginPenalty;
	}
	
	public void clearHistory() {
		int separator = nuHistory.indexOf(' ')+1;
		nuHistory = nuHistory.substring(separator);
	}
	
	// after a step the detection may look a bit into the future, to
	// determine if there is a global match
	public String getNuHistory() {
		return nuHistory == null || nuHistory.length() == 0 ?
				nuHistory :
					nuHistory.split(" ")[0] + " ";
	}
	
	public String getCompleteNuHistory() {
		return nuHistory;
	}
}
