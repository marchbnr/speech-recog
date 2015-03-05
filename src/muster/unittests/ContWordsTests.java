package muster.unittests;

import java.util.ArrayList;

import muster.recognition.ContDetection;
import muster.recognition.reference.ContRefComparison;
import muster.recognition.reference.RefRecorder;
import muster.recognition.reference.Reference;
import muster.recognition.reference.ReferenceDB;
import muster.recognition.reference.SilComparison;
import muster.util.Props;
import junit.framework.TestCase;

public class ContWordsTests extends TestCase {
	
	public void setUp() {
		Props.setProp("preproc", "SHORT");
		Props.setProp("variance", "true");
		Props.setProp("distance", "MAHALA");
	}
	
	// assert that a comparison to another pattern results in higher costs
	public void testCompareSeparateModels() throws Exception {
		Reference sample = RefRecorder.createReference("samples/koss_clear_heilbronn1.wav", "Heilbronn");
		Reference equalRef = null, opponentRef = null;
		
		ReferenceDB refDB = ReferenceDB.fromFile("samples/contRefDB");
		for(Reference ref : refDB.references) {
			if(ref.pattern.equals(sample.pattern))
				equalRef = ref;
			else
				opponentRef = ref;
		}
		
		ContRefComparison eqcomp;
		SilComparison eqSilence;
		{
			eqSilence = new SilComparison(refDB.silence, Props.getMetrics());
			ArrayList<ContRefComparison> eqComparisons = new ArrayList<ContRefComparison>();
			eqcomp = new ContRefComparison(equalRef, eqSilence, Props.getMetrics(), false);
			eqComparisons.add(eqcomp);
			eqSilence.setComparisons(eqComparisons);
		}
		
		ContRefComparison opcomp;
		SilComparison opSilence;
		{
			opSilence = new SilComparison(refDB.silence, Props.getMetrics());
			ArrayList<ContRefComparison> opComparisons = new ArrayList<ContRefComparison>();
			opcomp = new ContRefComparison(opponentRef, opSilence, Props.getMetrics(), false);
			opComparisons.add(opcomp);
			opSilence.setComparisons(opComparisons);
		}
		
		for(double[] vector : sample.getVectors()) {
			eqSilence.compareStep(vector);
			opSilence.compareStep(vector);
			eqcomp.compareStep(vector);
			opcomp.compareStep(vector);
		}
		
		System.out.println("same pattern(" + equalRef.pattern + ") cost: \t\t" + eqcomp.getLastCost());
		System.out.println("diff pattern(" + opponentRef.pattern + ") cost: \t\t" + opcomp.getLastCost());
		System.out.println("same silence(" + equalRef.pattern + ") cost: \t\t" + eqSilence.getDistance());
		System.out.println("diff silence(" + opponentRef.pattern + ") cost: \t\t" + opSilence.getDistance());
		// assert that a comparison to another pattern results in higher costs
		assertTrue(opcomp.getLastCost() > eqcomp.getLastCost());
	}
	
	public void testCombCont2SoundfileComparison() throws Exception {
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
		
		for(double[] vector : sample.getVectors()) {
			silence.compareStep(vector);
			eqcomp.compareStep(vector);
			opcomp.compareStep(vector);
			
			String history = silence.getNuHistory();
			for (ContRefComparison comp : eqComparisons) {
				//comp.compareStep(vector);
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
		}
		
		double[][] pushers = new double[1][24];
		for(double[] vector : pushers) {
			silence.compareStep(vector);
			eqcomp.compareStep(vector);
			opcomp.compareStep(vector);
		}
		
		System.out.println();
		System.out.println("same pattern(" + equalRef.pattern + ") cost: \t\t" + eqcomp.getLastCost());
		System.out.println("diff pattern(" + opponentRef.pattern + ") cost: \t\t" + opcomp.getLastCost());
		System.out.println("shared silence cost: \t\t\t\t" + silence.getDistance());
		
		System.out.println();
		System.out.println("equal hist: " + eqcomp.getHistory());
		System.out.println("diff hist: " + opcomp.getHistory());
		System.out.println("silence hist: " + silence.getHistory());
		opcomp.printHistory();
		// assert that a comparison to another pattern results in higher costs
		assertTrue(opcomp.getLastCost() > eqcomp.getLastCost());
	}
	
	public void teftContDetection() throws Exception {
	  Reference sample = RefRecorder.createReference("samples/koss_clear_heilbronn1.wav", "Heilbronn");
		
		ContDetection detection = new ContDetection(ReferenceDB.fromFile("samples/contRefDB"));
		for(double[] vector : sample.getVectors())
		  detection.compareStep(vector);
	}
	
	private double[][] vectorizeIt(int count, double value) {
		double[][] vector = new double[count][24];
		for(int u=0; u<count; u++) {
			for(int i=0; i<24; i++) {
				vector[u][i] = value;
			}
		}
		return vector;
	}
	
	private Reference[] createHistoryExample() {
		Reference[] refs = new Reference[3];
		
		// simulate silence
		refs[0] = new Reference(vectorizeIt(1, 0.0));
		refs[0].variance = vectorizeIt(1,1);
		refs[0].pattern = "silence";
		
		// simulate good and bad reference
		refs[1] = new Reference(vectorizeIt(10, 100.0));
		refs[1].variance = vectorizeIt(10,1);
		refs[1].pattern = "goodWord";
		refs[2] = new Reference(vectorizeIt(10, 60.0));
		refs[2].variance = vectorizeIt(10,1);
		refs[2].pattern = "badWord";
		
		return refs;
	}
	
	/*
	 * Create a refdb with samples where it is known which sample is detected and how the
	 * implementation should spread its history.
	 */
	public void testContHistoryPerfect() throws Exception {
		Reference[] refs = createHistoryExample();
		
		SilComparison silence = new SilComparison(refs[0], Props.getMetrics());
		ArrayList<ContRefComparison> eqComparisons = new ArrayList<ContRefComparison>();
		ContRefComparison eqcomp = new ContRefComparison(refs[1], silence, Props.getMetrics(), false);
		ContRefComparison opcomp = new ContRefComparison(refs[2], silence, Props.getMetrics(), false);
		eqComparisons.add(eqcomp);
		eqComparisons.add(opcomp);
		silence.setComparisons(eqComparisons);
		
		double[][] samples = vectorizeIt(10, 100);
		
		for(double[] vector : samples) {
			silence.compareStep(vector);
			eqcomp.compareStep(vector);
			opcomp.compareStep(vector);
		}
		
		double eqCost = eqcomp.getLastCost();
		double opCost = opcomp.getLastCost();
		
		System.out.println("equal cost vector: " + eqCost);
		System.out.println("opp cost vector: " + opCost);
		assertEquals(0.0, eqcomp.getLastCost(), 0.001);
		assertEquals(384000.0, opcomp.getLastCost(), 0.001);
		assertEquals("goodWord ", eqcomp.getLastHistory());
	}
}
