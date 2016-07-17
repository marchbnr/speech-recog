package muster.oldtests;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import muster.recognition.ContDetection;
import muster.recognition.reference.RefRecorder;
import muster.recognition.reference.Reference;
import muster.recognition.reference.ReferenceDB;
import muster.util.Props;

public class DebugContDetection {
	
	public void setUp() {
		Props.setProp("muster/preproc", "SHORT");
		Props.setProp("variance", "true");
		Props.setProp("distance", "MAHALA");
	}
	
	public void testCombCont2SoundfileComparison() throws Exception {
		setUp();
		Reference sample = RefRecorder.createReference("samples/koss_clear_heilbronn1.wav", "Heilbronn");
		
		ReferenceDB refDB = ReferenceDB.fromFile("samples/contRefDB");
		ContDetection detector = new ContDetection(refDB);
		
		int count = 1;
		
		for(double[] vector : sample.getVectors()) {
			detector.compareStep(vector);
			
			if(--count == 0) {
				BufferedReader inputStream =  new BufferedReader(new InputStreamReader(System.in));
				String input = inputStream.readLine();
				
				try {
					count = Integer.parseInt(input);
				}
				catch(NumberFormatException nfe) { count = 1; }
				
				System.out.println("\t =============== STEP =============== ");
				detector.printDebugInfo();
			}
			
		}
		
		for(double[] vector : sample.getVectors()) {
			detector.compareStep(vector);
			
			if(--count == 0) {
				BufferedReader inputStream =  new BufferedReader(new InputStreamReader(System.in));
				String input = inputStream.readLine();
				
				try {
					count = Integer.parseInt(input);
				}
				catch(NumberFormatException nfe) { count = 1; }
				
				System.out.println("\t =============== STEP =============== ");
				detector.printDebugInfo();
			}
			
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		new DebugContDetection().testCombCont2SoundfileComparison();
	}
}
