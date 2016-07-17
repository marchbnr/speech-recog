package muster.bin;

import javax.sound.sampled.LineUnavailableException;

import muster.preprocessing.PreprocIfc;
import muster.recognition.ContDetection;
import muster.recognition.DetectionViewIfc;
import muster.recognition.reference.ReferenceDB;
import muster.sound.ByteConverter;
import muster.sound.DoubleSource;
import muster.sound.DoubleSourceDup;
import muster.sound.FrameSplitter;
import muster.sound.SoundRecorder;
import muster.util.Props;

public class ContinuousDetection implements DetectionViewIfc {
	
	public FrameSplitter splitter;
	public String detected;
	SoundRecorder recorder;
	PreprocIfc preproc;
	ContDetection detector;
	boolean splitterStarted = false;
	public DoubleSource clonedSource;
	
	public ContinuousDetection() throws Exception{
	}
	
	public void init(ReferenceDB refDB){
		detector = new ContDetection(refDB);
		detector.setDetectionView(this);
		
		recorder = new SoundRecorder();
		preproc = Props.getPreprocessor(detector);
		DoubleSourceDup sourceDup = new DoubleSourceDup(new ByteConverter(recorder));
		splitter = new FrameSplitter(sourceDup, preproc, 256);
		clonedSource = sourceDup.getClone();
	}
	
	public void setDetectionView(DetectionViewIfc view) {
		detector.setDetectionView(view);
	}

	public void start() throws LineUnavailableException{
		System.out.println("Creating new recorder");
		recorder.record();
		if(!splitterStarted){
			splitter.start();
			splitterStarted = true;
			new Thread(detector).start();
		}
		//splitter.recording = true;
	}
	
	public void finish() throws InterruptedException{
		recorder.finish();
		System.out.println("Quitting");
		Thread.sleep(100);
		
		preproc.finish();
		
		splitterStarted = false;
		recorder = null;
		preproc = null;
		splitter = null;
	}
	
	public void exit() throws InterruptedException{
		recorder.finish();
		System.out.println("Quitting");
		Thread.sleep(100);
		
		preproc.finish();
		recorder = null;
		preproc = null;
		splitter = null;
	}

	public static void main(String[] args) throws Exception {
		Props.setProp("preproc", "NONE");
		Props.setProp("variance", "true");
		Props.setProp("distance", "MAHALA");
		Props.setProp("vectorsize", "24");
		
		ContinuousDetection det = new ContinuousDetection();
		det.init(ReferenceDB.fromFile("samples/contRefDB"));
		det.start();
		
		System.out.println("Recording, press enter to stop...");
		System.in.read();
		
		det.finish();
	}
	
	public void printDetection(String word) {
		System.out.println("Word detected: " + word);
	}

}
