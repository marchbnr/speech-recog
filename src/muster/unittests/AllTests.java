package muster.unittests;

import muster.unittests.ContRefTests;
import muster.unittests.ViterbiTests;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(ViterbiTests.class);
		suite.addTestSuite(ContRefTests.class);
		suite.addTestSuite(ContWordsTests.class);
		suite.addTestSuite(RecognitionTests.class);
		return suite;
	}

}
