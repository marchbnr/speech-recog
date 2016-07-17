package muster.oldtests;

import muster.recognition.ContDetection;
import muster.recognition.reference.RefRecorder;
import muster.recognition.reference.Reference;
import muster.recognition.reference.ReferenceDB;
import muster.util.Props;

public class TestContinuousDetection {
	
	public void setUp() {
		Props.setProp("muster/preproc", "SHORT");
		Props.setProp("variance", "true");
		Props.setProp("distance", "MAHALA");
	}

	public void noTestCombCont2SoundfileComparison() throws Exception {
		setUp();
		Reference sample = RefRecorder.createReference("samples/koss_clear_heilbronn1.wav", "Heilbronn");
		
		ReferenceDB refDB = ReferenceDB.fromFile("samples/contRefDB");
		int size = refDB.references.size()-1;
		for(int i=size; i>=2; i--) {
			refDB.references.remove(i);
		}
		ContDetection detection = new ContDetection(refDB);
		
		for(double[] vector : sample.getVectors()) {
			detection.compareStep(vector);
		}
	}
}
