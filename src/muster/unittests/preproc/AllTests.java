package muster.unittests.preproc;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(ComplexTests.class);
		suite.addTestSuite(ConverterTests.class);
		suite.addTestSuite(SplitterTests.class);
		suite.addTestSuite(DFTTests.class);
		suite.addTestSuite(BitReverserTests.class);
		suite.addTestSuite(HammingTests.class);
		suite.addTestSuite(ParserTests.class);
		suite.addTestSuite(PowerSpectrumTests.class);
		suite.addTestSuite(MelScaleTests.class);
		suite.addTestSuite(LogTests.class);
		suite.addTestSuite(DCTTests.class);
		suite.addTestSuite(PowerSpectrumTests.class);
		suite.addTestSuite(TimeChannelTests.class);
		suite.addTestSuite(TimeSlopeTests.class);
		suite.addTestSuite(PreprocTests.class);
		return suite;
	}

}
