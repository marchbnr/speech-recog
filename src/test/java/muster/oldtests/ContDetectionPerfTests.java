package muster.oldtests;

import muster.recognition.ContDetection;
import muster.recognition.DetectionViewIfc;
import muster.recognition.reference.ReferenceDB;
import muster.util.PerformanceTest;

public class ContDetectionPerfTests extends PerformanceTest implements DetectionViewIfc {

	public static void main(String[] args) throws Exception {
		ContDetectionPerfTests tests = new ContDetectionPerfTests();
		tests.runTests();
	}

	@PerfTest()
	public void test1kcomparison5Words() throws Exception {
		ReferenceDB refDB = ReferenceDB.fromFile("samples/performance/5perfDB");
		ContDetection detector = new ContDetection(refDB);
		
		double[] performanceSample = new double[24];
		
		startTime = System.currentTimeMillis();
		
		for(int i=0; i<1000; i++)
			detector.compareStep(performanceSample);
	}
	
	@PerfTest()
	public void test1kcomparison25Words() throws Exception {
		ReferenceDB refDB = ReferenceDB.fromFile("samples/performance/25perfDB");
		ContDetection detector = new ContDetection(refDB);
		
		double[] performanceSample = new double[24];
		
		startTime = System.currentTimeMillis();
		
		for(int i=0; i<1000; i++)
			detector.compareStep(performanceSample);
	}
	
	@PerfTest()
	public void test1kcomparison50Words() throws Exception {
		ReferenceDB refDB = ReferenceDB.fromFile("samples/performance/50perfDB");
		ContDetection detector = new ContDetection(refDB);
		
		double[] performanceSample = new double[24];
		
		startTime = System.currentTimeMillis();
		
		for(int i=0; i<1000; i++)
			detector.compareStep(performanceSample);
	}
	
	@PerfTest()
	public void test1kcomparison100Words() throws Exception {
		ReferenceDB refDB = ReferenceDB.fromFile("samples/performance/100perfDB");
		ContDetection detector = new ContDetection(refDB);
		
		double[] performanceSample = new double[24];
		
		startTime = System.currentTimeMillis();
		
		for(int i=0; i<1000; i++)
			detector.compareStep(performanceSample);
	}

	public void printDetection(String word) { }
}
