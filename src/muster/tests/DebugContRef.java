package muster.tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import muster.recognition.reference.ContRefComparison;
import muster.recognition.reference.RefRecorder;
import muster.recognition.reference.Reference;
import muster.recognition.reference.ReferenceDB;
import muster.recognition.reference.SilComparison;
import muster.util.Props;

public class DebugContRef {
	
	public void setUp() {
		Props.setProp("preproc", "SHORT");
		Props.setProp("variance", "true");
		Props.setProp("distance", "MAHALA");
	}
	
	public void testCombCont2SoundfileComparison() throws Exception {
		setUp();
		//Props.setProp("distance", "EUCLID");
		Reference sample = RefRecorder.createReference("samples/koss_clear_heilbronn1.wav", "Heilbronn");
		Reference equalRef = null, opponentRef = null;
		
		ReferenceDB refDB = ReferenceDB.fromFile("samples/contRefDB");
		for(Reference ref : refDB.references) {
			if(ref.pattern.equals(sample.pattern))
				equalRef = ref;
			else
				opponentRef = ref;
		}
		
		SilComparison silence = new SilComparison(refDB.silence, Props.getMetrics());
		ArrayList<ContRefComparison> eqComparisons = new ArrayList<ContRefComparison>();
		ContRefComparison eqcomp = new ContRefComparison(equalRef, silence, Props.getMetrics(), false);
		ContRefComparison opcomp = new ContRefComparison(opponentRef, silence, Props.getMetrics(), false);
		eqComparisons.add(eqcomp);
		eqComparisons.add(opcomp);
		silence.setComparisons(eqComparisons);
		
		int count = 1;
		
		for(double[] vector : sample.getVectors()) {
			silence.compareStep(vector);
			eqcomp.compareStep(vector);
			opcomp.compareStep(vector);
			
			String history = silence.getNuHistory();
			for (ContRefComparison comp : eqComparisons) {
				comp.compareStep(vector);
				if (history != null
						&& comp.getHistory().equals(history))
					history = comp.getHistory();
				else
					history = null;
			}
			
			// - check if all comparisons have the same hist
			// if so -> remove frames, print det, update index
			if(history != null && history.length() > 0){
				System.out.println("Detected: " + history);
				silence.clearHistory();
				for (ContRefComparison comp : eqComparisons)
					comp.clearHistory();
			}
			
			if(--count == 0) {
				BufferedReader inputStream =  new BufferedReader(new InputStreamReader(System.in));
				String input = inputStream.readLine();
				
				try {
					count = Integer.parseInt(input);
				}
				catch(NumberFormatException nfe) { count = 1; }
				
				System.out.println("\t =============== STEP =============== ");
				
				System.out.println();
				System.out.println("same pattern(" + equalRef.pattern + ") cost: \t\t" + eqcomp.getLastCost());
				System.out.println("diff pattern(" + opponentRef.pattern + ") cost: \t\t" + opcomp.getLastCost());
				System.out.println("shared silence cost: \t\t\t\t" + silence.getDistance());
				
				System.out.println();
				System.out.println("equal hist: " + eqcomp.getHistory());
				System.out.println("diff hist: " + opcomp.getHistory());
				System.out.println("silence hist: " + silence.getHistory());
				opcomp.printDistHists();
				eqcomp.printDistHists();
				System.out.println();
				System.out.println();
			}
			
		}
		
//		double[][] pushers = new double[1][24];
//		for(double[] vector : sample.getVectors()) {
//			silence.compareStep(vector);
//			eqcomp.compareStep(vector);
//			opcomp.compareStep(vector);
//		}
		
		// assert that a comparison to another pattern results in higher costs
		if(opcomp.getLastCost() > eqcomp.getLastCost()) {
			System.out.println("assert true: opcomp.getLastCost() > eqcomp.getLastCost()");
		}
	}
	
	public static void main(String[] args) throws Exception {
		new DebugContRef().testCombCont2SoundfileComparison();
	}
}
