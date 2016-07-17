package muster.util;

import muster.preprocessing.LongPreprocessor;
import muster.preprocessing.PreprocIfc;
import muster.preprocessing.ShortPreprocessor;
import muster.recognition.WDetectionIfc;
import muster.recognition.distance.DistanceMetrics;
import muster.recognition.distance.MahalaDistance;

public class Props {
	// das erste ist default
	// variance = false, true
	// distance = EUCLID, MAHALA, MARKOV
	// viterbi = false, true
	// muster.preproc = LONG, SHORT
  	// silence = false, true
	// vectorsize = 24
	
	public static DistanceMetrics metrics;
	
	public static boolean originalModelSize = true;
	public static boolean verbosiveViterbi = true;
	public static double silenceLoopPenalty = 0;
	public static double silWordEndPenalty = 0;
	public static double silWordBeginPenalty = 0;
	
	public static void init() {
		if(System.getProperty("variance") == null) {
			System.setProperty("variance", "false");
		}
		if(System.getProperty("distance") == null) {
			System.setProperty("distance", "EUCLID");
		}
		if(System.getProperty("viterbi") == null) {
			System.setProperty("viterbi", "false");
		}
		if(System.getProperty("preproc") == null) {
			System.setProperty("preproc", "LONG");
		}
		if(System.getProperty("silence") == null) {
			System.setProperty("silence", "false");
		}
		if(System.getProperty("vectorsize") == null) {
			System.setProperty("vectorsize", "24");
		}
	}

	public static void setProp(String key, String value) {
		System.setProperty(key, value);
	}
	
	public static String getProp(String key) {
		String result;
		if((result = System.getProperty(key)) == null){
			init();
			return System.getProperty(key);
		}
		return result;
	}
	
	public static int getInt(String key) {
		String result;
		if((result = System.getProperty(key)) == null){
			init();
			result = System.getProperty(key);
		}
		return Integer.parseInt(result);
	}
	
	public static boolean getBool(String key) {
		String result;
		if((result = System.getProperty(key)) == null){
			init();
			result = System.getProperty(key);
		}
		return Boolean.parseBoolean(result);
	}
	
	public static DistanceMetrics getMetrics() {
		if(metrics == null) {
			if(Props.getProp("distance").equals("MAHALA"))
				  metrics = new MahalaDistance();
			  else if(Props.getProp("distance").equals("EUCLID"))
				  metrics = new DistanceMetrics();
			  else throw new RuntimeException("Distance metric undefined.");
		}
		return metrics;
	}
	
	public static PreprocIfc getPreprocessor(WDetectionIfc detector) {
		if(Props.getProp("preproc").equals("LONG"))
			return new LongPreprocessor(detector,7);
		else return new ShortPreprocessor(detector,7);
	}
	
	public static int getVectorSize() {
		return getInt("vectorsize");
	}
}
