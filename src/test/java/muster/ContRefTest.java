package muster;

import java.util.ArrayList;

import muster.recognition.reference.ContRefComparison;
import muster.recognition.reference.Reference;
import muster.recognition.reference.ReferenceDB;
import muster.recognition.reference.SilComparison;
import muster.util.Props;
import junit.framework.TestCase;

public class ContRefTest extends TestCase {
	
	public void setUp() {
		Props.setProp("muster/preproc", "SHORT");
		Props.setProp("variance", "true");
		Props.setProp("distance", "MAHALA");
	}
	
	public void tearDown() {
		ContRefComparison.DEBUG = false;
	}

	// compare a reference to itself and assert that it has no distance
	public void testSelfComparison() throws Exception {
		Props.setProp("distance", "EUCLID");
		Reference sample,compareRef = null;
		ReferenceDB refDB = ReferenceDB.fromFile("samples/contRefDB");
		for(Reference ref : refDB.references) {
			if(ref.pattern.equals("Stuttgart"))
				compareRef = ref;
		}
		
		sample = compareRef;
		System.out.println("Comparing sample with " + sample.size() + " vectors.");
		System.out.println("Comparing to reference with " + compareRef.size() + " vectors.");
		
		ContRefComparison comp = new ContRefComparison(compareRef, null, Props.getMetrics(), true);
		ContRefComparison.DEBUG = true;
		boolean detected = false;
		
		for(double[] vector : sample.getVectors()) {
			comp.compareStep(vector);
			if(comp.getHistory().equals(sample.pattern))
				detected = true;
		}
		// the pattern compared to its self must have distance 0
		assertEquals(0.0, comp.getLastCost(),0.0001);
		// the pattern must detect itself
		assertTrue(detected);
	}
	
	// assert that the first vector is NaN without silence
	public void testFirstVectorisNaN() {
		Reference ref = new Reference(vectorizeIt(10, 10.0));
		ContRefComparison comp = new ContRefComparison(ref, null, Props.getMetrics(), false);
		comp.compareStep(vectorizeIt(1,10)[0]);
		double[] distances = comp.getDistances();
		
		for(double distance : distances) {
			assertTrue(Double.isNaN(distance));
		}
	}
	
	// assert that the predecessor of the first vector is silence
	// and that the history is spread accordingly
	public void testHistoryFromSilence() {
		
		SilComparison silenceStub = new SilComparison(null, null) {
			public double getDistance() {
				return 0;
			}
			
			public String getHistory() {
				return "myTestHistory";
			}
		};
		Reference ref = new Reference(vectorizeIt(10, 10.0));
		ref.variance = vectorizeIt(10, 1.0);
		
		ContRefComparison comp = new ContRefComparison(ref, silenceStub, Props.getMetrics(), false);
		String histories[] = comp.getHistories();
		
		//assert the history is empty before fetching history from silence
		assertTrue(histories[0] == null || !histories[0].equals("myTestHistory"));
		
		//assert the history of silence was taken for the first vector
		comp.compareStep(vectorizeIt(1,10)[0]);
		histories = comp.getHistories();
		assertTrue(histories[0].equals("myTestHistory"));
		assertTrue(histories[1] == null || !histories[1].equals("myTestHistory"));
		assertTrue(histories[9] == null || !histories[9].equals("myTestHistory"));
		
		for(int i=1; i<10; i++)
			comp.compareStep(vectorizeIt(1,10)[0]);
		histories = comp.getHistories();
		
		//assert the history has spread over the whole word
		assertTrue(histories[0].equals("myTestHistory"));
		assertTrue(histories[1].equals("myTestHistory"));
		assertTrue(histories[9].equals("myTestHistory"));
	}
	
	/*
	 * Create a refdb with samples where it is known which sample is detected and how the
	 * implementation should spread its history.
	 */
	public void testContHistoryPerfect() throws Exception {
		Reference[] refs = createHistoryExample();
		
		SilComparison silence = new SilComparison(refs[0], Props.getMetrics());
		ArrayList<ContRefComparison> comparisons = new ArrayList<ContRefComparison>();
		ContRefComparison eqcomp = new ContRefComparison(refs[1], silence, Props.getMetrics(), false);
		ContRefComparison opcomp = new ContRefComparison(refs[2], silence, Props.getMetrics(), false);
		comparisons.add(eqcomp);
		comparisons.add(opcomp);
		silence.setComparisons(comparisons);
		
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
		
	}
	
	// -- helper stuff --
	
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
		
		// simulate good and bad reference
		refs[1] = new Reference(vectorizeIt(10, 100.0));
		refs[1].variance = vectorizeIt(10,1);
		refs[2] = new Reference(vectorizeIt(10, 60.0));
		refs[2].variance = vectorizeIt(10,1);
		
		return refs;
	}
}
