package muster.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class PerformanceTest {
  
  protected long startTime;
  
  @Retention(RetentionPolicy.RUNTIME)
  public @interface PerfTest {
  }

  public static void main(String[] args) throws Exception {
    PerformanceTest tests = new PerformanceTest();
    tests.runTests();
  }
  
  public void runTests() throws Exception {
    // find all performance tests
    ArrayList<Method> methods = new ArrayList<Method>();
    for(Method method : getClass().getMethods()) {
      for(Annotation anno : method.getDeclaredAnnotations()) {
	if(anno instanceof PerfTest) {
	  methods.add(method);
	}
      }
    }
    System.out.println("Invoking tests");
    // run all performance tests
    for(Method method : methods) {
      System.out.println("Calling " + method.getName());
      measureTime(method);
      System.gc();
      Thread.sleep(100);
    }
    System.out.println("All tests finished");
  }
  
  private void measureTime(Method method) throws Exception {
	  long wholeTime = 0;
	  int runs = 10;
	  for(int i=0; i<runs; i++) {
		  method.invoke(this,(Object[])null);
		    wholeTime += System.currentTimeMillis() - startTime;
	  }
	  System.out.println("Time: " + wholeTime/runs);
  }

}
