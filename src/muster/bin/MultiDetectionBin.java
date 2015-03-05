package muster.bin;

import javax.sound.sampled.LineUnavailableException;

import muster.preprocessing.PreprocIfc;
import muster.recognition.DetectionViewIfc;
import muster.recognition.MultiDetection;
import muster.recognition.reference.ReferenceDB;
import muster.sound.ByteConverter;
import muster.sound.DoubleSource;
import muster.sound.DoubleSourceDup;
import muster.sound.FrameSplitter;
import muster.sound.SoundRecorder;
import muster.util.Props;

public class MultiDetectionBin implements DetectionViewIfc {
	
	public FrameSplitter splitter;
	public String detected;
	public DoubleSource clonedSource;
	
	SoundRecorder recorder;
	PreprocIfc preproc;
	MultiDetection detector;
	boolean splitterStarted = false;
	long timer;
	DetectionViewIfc view = this;
	
	public MultiDetectionBin() {
		Props.setProp("variance", "true");
		Props.setProp("distance", "MAHALA");
	}
	
	public void init(ReferenceDB refDB) throws Exception {
		detector = new MultiDetection(refDB);
		recorder = new SoundRecorder();
		preproc = Props.getPreprocessor(detector);
		DoubleSourceDup sourceDup = new DoubleSourceDup(new ByteConverter(recorder));
		clonedSource = sourceDup.getClone();
		splitter = new FrameSplitter(sourceDup, preproc, 256);
	}
	
	public void setDetectionView(DetectionViewIfc view) {
		this.view = view;
	}

	public void start() throws LineUnavailableException{
		System.out.println("Creating new recorder");
		timer = System.currentTimeMillis();
		recorder.record();
		if(!splitterStarted){
			splitter.start();
			splitterStarted = true;
		}
		//splitter.recording = true;
	}
	
	public void finish() throws InterruptedException{
		recorder.finish();
		System.out.println("Quitting");
		Thread.sleep(100);
		
		preproc.finish();
		
		detected = detector.detect();
		System.out.println(detected + " detected in " + (System.currentTimeMillis() - timer) + "ms");
		//splitter.recording = false;
		recorder = null;
		preproc = null;
		splitter = null;
		splitterStarted = false;
		view.printDetection(detected);
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
		
		MultiDetectionBin det = new MultiDetectionBin();
		det.init(ReferenceDB.fromFile("samples/contRefDB"));
		det.start();
		
		System.out.println("Recording, press enter to stop...");
		System.in.read();

		det.finish();
	}

	@Override
	public void printDetection(String word) {
		System.out.println("Word detected: " + word);
	}

}
