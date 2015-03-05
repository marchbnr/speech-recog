package muster.recognition;

import java.util.ArrayList;
import java.util.LinkedList;

import muster.recognition.reference.ContRefComparison;
import muster.recognition.reference.Reference;
import muster.recognition.reference.ReferenceDB;
import muster.recognition.reference.SilComparison;
import muster.util.Props;

public class ContDetection extends Thread implements WDetectionIfc,
		DetectionViewIfc {

	ArrayList<ContRefComparison> comparisons = new ArrayList<ContRefComparison>();
	SilComparison silenceComparison;
	LinkedList<double[]> sample = new LinkedList<double[]>();
	boolean running = true;
	int finished = 0;
	DetectionViewIfc view = this;

	public ContDetection(ReferenceDB refDB) {
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
		synchronized (sample) {
			sample.push(samples);
		}
	}

	public void finish() {
		running = false;
	}

	@Override
	public void run() {
		while (running) {
			synchronized (sample) {
				if (sample.size() > 0) {

					double[] vector = sample.pop();
					compareStep(vector);
					finished++;
				}
//				if (finished % 100 == 0);
//					System.out.println("stack size: " + sample.size()
//							+ " done: " + finished + " det: " + history);
			}

			// sleep with released lock
			try {
				sleep(7);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void compareStep(double[] vector) {
		silenceComparison.compareStep(vector);
		String history = silenceComparison.getNuHistory();
		for (ContRefComparison comp : comparisons) {
			comp.compareStep(vector);
			if(comp.getHistory() == null) {
				history = null;
			}
			else if (history != null && history.length()>0 && comp.getHistory().equals(history.trim())){
				history = comp.getHistory();
			}
			else{
				history = null;
			}
		}
		
		// - check if all comparisons have the same hist
		// if so -> remove frames, print det, update index
		if(history != null && history.length() > 0){
			view.printDetection(history);
			silenceComparison.clearHistory();
			for (ContRefComparison comp : comparisons)
				comp.clearHistory();
		}
	}

	public void printDebugInfo() {
		System.out.println("Silence cost: " + silenceComparison.getDistance() + "\t\t history: " + silenceComparison.getNuHistory());
		for(ContRefComparison comp : comparisons)
			System.out.println(comp.getPattern() + " cost: " + comp.getLastCost() + "\t\t history: " + comp.getHistory());
	}
}
