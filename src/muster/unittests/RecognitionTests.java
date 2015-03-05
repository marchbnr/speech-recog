package muster.unittests;

import muster.recognition.reference.RefComparison;
import muster.recognition.reference.Reference;
import muster.util.Props;
import junit.framework.TestCase;

/*
 * Tests are deprecated, because they rely on old settings.
 */
public class RecognitionTests extends TestCase {

	public void testSingleVectorComparison() {
		Props.setProp("vectorsize", "3");
		double[][] vectorA = {{ 6, 2, 1 }};
		double[][] vectorB = {{ 9, 1 ,1 }};
		
		Reference sample = new Reference(vectorA);
		Reference ref = new Reference(vectorB);
		
		double distance = RefComparison.getDistance(0, sample, 0, ref);
//		System.out.println("Distance: " + distance);
		assertEquals(10.0, distance, Double.parseDouble("1.09e-9"));
		
		double symmetricDistance = RefComparison.getDistance(0, sample, 0, ref);
//		System.out.println("Distance: " + distance);
		assertEquals(10.0, symmetricDistance, Double.parseDouble("1.09e-9"));
	}
	
	public void testReferenceComparison() {
		Props.setProp("vectorsize", "3");
		Props.setProp("distance", "EUCLID");
		double[][] vectors = { { 9, 1 ,1 }, { 8, 1 ,0 }, { 4, 2 ,1 }, { 4, 3 ,1 }, { 3, 2 ,0 }, { 2, 0 ,1 } };
		double[][] refVectors = { { 9, 0 ,1 }, { 6, 2 ,1 }, { 4, 2 ,1 }, { 1, 0 ,2 } };
		Reference sample = new Reference(vectors);
		Reference ref = new Reference(refVectors);
		RefComparison comparison = new RefComparison(sample, ref);
		
		comparison.compareStep();
		double distance = comparison.getCurrentDistance();
//		System.out.println("Distance index=0: " + distance);
		assertEquals(1.0, distance, Double.parseDouble("1.09e-9"));
		
		comparison.compareStep();
		distance = comparison.getCurrentDistance();
//		System.out.println("Distance index=1: " + distance);
		assertEquals(4.0, distance, Double.parseDouble("1.09e-9"));
		
		comparison.compareStep();
		distance = comparison.getCurrentDistance();
//		System.out.println("Distance index=2: " + distance);
		assertEquals(4.0, distance, Double.parseDouble("1.09e-9"));

		distance = comparison.getDistance();
//		System.out.println("Total distance: " + distance);
		assertEquals(9.0, distance, Double.parseDouble("1.09e-9"));
	}
	
	public void testCloseVielleichtComparison() {
		TestData data = new TestData();
		Reference sample = new Reference(data.vielleichtB);
		Reference ref = new Reference(data.vielleichtA);
		RefComparison comparison = new RefComparison(sample, ref);
		
		double distance = comparison.getDistance();
		System.out.println("Total distance: " + distance);
	}
	
	public void testFarAufnahmeComparison() {
		TestData data = new TestData();
		Reference sample = new Reference(data.aufnahme);
		Reference ref = new Reference(data.vielleichtA);
		RefComparison comparison = new RefComparison(sample, ref);
		comparison.setPath(true);
		
		double distance = comparison.getDistance();
		comparison.toFile("log/comp.txt");
		comparison.toDistFile("log/comp_dist.txt");
		System.out.println("Total distance: " + distance);
	}
}
