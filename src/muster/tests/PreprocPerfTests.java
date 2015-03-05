package muster.tests;

import muster.preprocessing.DPreprocIfc;
import muster.preprocessing.LongPreprocessor;
import muster.preprocessing.ShortPreprocessor;
import muster.preprocessing.steps.Complex;
import muster.recognition.SimpleDetection;
import muster.util.PerformanceTest;
import muster.util.Props;

public class PreprocPerfTests extends PerformanceTest {

  public static void main(String[] args) throws Exception {
    PreprocPerfTests tests = new PreprocPerfTests();
    tests.runTests();
  }
  
  @PerfTest()
  public void testWholeShortSequence() {
	  Props.setProp("preproc", "SHORT");
    SimpleDetection simplImpl = new SimpleDetection(null);
    DPreprocIfc dft = new ShortPreprocessor(simplImpl,7);
    double[] samples = new double[256];
    for(int i=0; i<256; i++) {
      samples[i] = Math.sin(i * Math.PI/2);
    }
    startTime = System.currentTimeMillis();
    dft.compute(samples);
  }
  
  @PerfTest()
  public void test1kWholeShortSequence() {
	  Props.setProp("preproc", "SHORT");
	  Complex.objectCount = 0;
    SimpleDetection simplImpl = new SimpleDetection(null);
    DPreprocIfc dft = new ShortPreprocessor(simplImpl,7);
    double[] samples = new double[256];
    for(int i=0; i<256; i++) {
      samples[i] = Math.sin(i * Math.PI/2);
    }
    startTime = System.currentTimeMillis();
    for(int i=0; i<1000;i++) {
      dft.compute(samples);
    }
    System.out.println("complex objects: " + Complex.objectCount);
    
  }
  
  @PerfTest()
  public void testWholeLongSequence() {
	  Props.setProp("preproc", "LONG");
    SimpleDetection simplImpl = new SimpleDetection(null);
    LongPreprocessor dft = new LongPreprocessor(simplImpl,7);
    double[] samples = new double[256];
    for(int i=0; i<256; i++) {
      samples[i] = Math.sin(i * Math.PI/2);
    }
    startTime = System.currentTimeMillis();
    dft.compute(samples);
    dft.finish();
  }
  
  @PerfTest()
  public void test1HWholeLongSequence() {
	  Props.setProp("preproc", "LONG");
    SimpleDetection simplImpl = new SimpleDetection(null);
    LongPreprocessor dft = new LongPreprocessor(simplImpl,7);
    double[] samples = new double[256];
    for(int i=0; i<256; i++) {
      samples[i] = Math.sin(i * Math.PI/2);
    }
    startTime = System.currentTimeMillis();
    for(int i=0; i<100;i++) {
      dft.compute(samples);
      dft.finish();
    }
  }
}
