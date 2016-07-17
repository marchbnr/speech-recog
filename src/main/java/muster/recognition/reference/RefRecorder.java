package muster.recognition.reference;

import java.io.IOException;
import java.util.ArrayList;

import muster.preprocessing.PreprocIfc;
import muster.recognition.WDetectionIfc;
import muster.sound.ByteConverter;
import muster.sound.FilePlayer;
import muster.sound.FrameSplitter;
import muster.util.Props;


/**
 * Used to record and convert sound files into references of the DB.
 */
public class RefRecorder implements WDetectionIfc {
	
	ArrayList<double[]> frames = new ArrayList<double[]>();
	ReferenceDB db = null;
	
	public RefRecorder() {
	}

	public RefRecorder(ReferenceDB db) {
		this.db = db;
	}
	
	@Override
	public void compute(double[] samples) {
		frames.add(samples);
	}
	
	public Reference createReference(String pattern) {
		Reference ref = new Reference(frames);
		ref.pattern = pattern;
		if(db != null)
			db.references.add(ref);
		//System.out.println("Created Reference: " + ref);
		frames.clear();
		return ref;
	}
	
	public static Reference createReference(String file, String pattern) throws Exception {
		Reference sample = null;
		try{
			FilePlayer player = new FilePlayer(file.toString());
			RefRecorder refRecorder = new RefRecorder(null);
			PreprocIfc preproc = Props.getPreprocessor(refRecorder);
			FrameSplitter splitter = new FrameSplitter(new ByteConverter(player), preproc, 256);

			player.start();
			splitter.start();
			
			while(player.playing) {
				Thread.sleep(20);
			}
			Thread.sleep(400);
			preproc.finish();
			sample = refRecorder.createReference(pattern);
		} catch(Exception e) { e.printStackTrace(); }
		return sample;
	}
	
	public void storeDB(String fileName) throws IOException {
		db.toFile(fileName);
		System.out.println("Stored db with " + db.references.size() + " references.");
	}
	
	public ReferenceDB getDB() {
		return db;
	}

}
