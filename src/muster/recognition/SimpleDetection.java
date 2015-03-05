package muster.recognition;

import java.util.ArrayList;

import muster.recognition.reference.RefComparison;
import muster.recognition.reference.Reference;
import muster.recognition.reference.ReferenceDB;
import muster.util.Props;


public class SimpleDetection implements WDetectionIfc {
	
	static final boolean DEBUG = false;
	ArrayList<double[]> frames = new ArrayList<double[]>();
	Reference sample;
	ReferenceDB db;
	
	public SimpleDetection(ReferenceDB db) {
		this.db = db;
	}

	@Override
	public void compute(double[] samples) {
		frames.add(samples);
	}
	
	//returns the textual pattern of the detected reference
	public String detect() {
		double minDist = -1;
		Reference minRef = null;
		System.out.println("Comparing " + frames.size() + " vectors");
		sample = new Reference(frames);
		if(DEBUG) {
//			new Reference(frames).toArrayFile("log/input_a.txt");
			new Reference(frames).toFile("log/input.txt");
		}
		
		{
			ArrayList<Reference> newReferences = new ArrayList<Reference>();
			for(Reference ref : db.references) {
				Reference newRef = new Reference(ref, db.silence);
				newReferences.add(newRef);
			}
			db.references = newReferences;
		}
		
		for(Reference ref : db.references) {
			if(DEBUG) {
//				ref.toArrayFile("log/" + ref.pattern + "_a.txt");
				ref.toFile("log/" + ref.pattern + ".txt");
			}
			double distance = compareTo(ref);
			System.out.println("Comparing to " + ref.pattern + " dist: " + distance);
			System.out.println("Reference: " + ref);
			if(minRef == null || distance < minDist) {
				minDist = distance;
				minRef = ref;
			}
		}
		return minRef.pattern;
	}
	
	public double compareTo(Reference ref) {
		RefComparison comp = new RefComparison(sample, ref, DEBUG, Props.getMetrics());
		
		double distance = comp.getDistance();
		if(DEBUG) {
			comp.toFile("log/" + "comp_" + ref.pattern + ".txt");
			comp.toDistFile("log/" + "dist_" + ref.pattern + ".txt");
		}
			
		return distance;
	}

}
