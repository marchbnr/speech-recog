package muster.recognition;

import java.util.ArrayList;

import muster.recognition.reference.ContRefComparison;
import muster.recognition.reference.Reference;
import muster.recognition.reference.ReferenceDB;
import muster.recognition.reference.SilComparison;
import muster.util.Props;

public class MultiDetection implements WDetectionIfc, DetectionViewIfc {

	ArrayList<ContRefComparison> comparisons = new ArrayList<ContRefComparison>();
	SilComparison silenceComparison;
	ArrayList<double[]> vectors = new ArrayList<double[]>();
	DetectionViewIfc view = this;
	String detected = "";

	public MultiDetection(ReferenceDB refDB) {
		silenceComparison = new SilComparison(refDB.silence, Props.getMetrics());
		for (Reference ref : refDB.references) {
			comparisons.add(new ContRefComparison(ref, silenceComparison, Props.getMetrics(), true));
		}
		silenceComparison.setComparisons(comparisons);
	}

	public void setDetectionView(DetectionViewIfc view) {
		this.view = view;
	}

	public void printDetection(String word) {
		System.out.println("Detected: " + word);
	}

	public void compute(double[] samples) {
		vectors.add(samples);
	}
	
	public void compareStep(double[] vector) {
		silenceComparison.compareStep(vector);
		String history = silenceComparison.getNuHistory();
		for (ContRefComparison comp : comparisons) {
			comp.compareStep(vector);
			if(comp.getHistory() == null) {
				history = null;
				continue;
			}
			if (history != null && history.length()>0 && comp.getHistory().equals(history.trim())){
				history = comp.getHistory();
			}
			else{
				history = null;
				continue;
			}
		}
		
		if(history != null && history.length() > 0){
			detected += " " + history;
			System.out.println("detected: " + history);
			silenceComparison.clearHistory();
			for (ContRefComparison comp : comparisons)
				comp.clearHistory();
		}
	}
	
	public String detect() {
		for(double[] vector : vectors) {
			compareStep(vector);
		}
//		view.printDetection(detected);
		return detected;
	}
	
	public void printDebugInfo() {
		System.out.println("Silence cost: " + silenceComparison.getDistance() + "\t\t history: " + silenceComparison.getCompleteNuHistory());
		for(ContRefComparison comp : comparisons)
			System.out.println(comp.getPattern() + " cost: " + comp.getLastCost() + "\t\t history: " + comp.getHistory());
	}

}
