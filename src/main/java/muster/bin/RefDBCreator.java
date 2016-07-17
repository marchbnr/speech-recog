package muster.bin;

import java.util.List;

import muster.preprocessing.PreprocIfc;
import muster.recognition.ViterbiTrainer;
import muster.recognition.reference.RefRecorder;
import muster.recognition.reference.Reference;
import muster.recognition.reference.ReferenceDB;
import muster.sound.ByteConverter;
import muster.sound.DoubleSource;
import muster.sound.FrameSplitter;
import muster.sound.FilePlayer;
import muster.util.Props;
import muster.util.Sample;
import muster.util.XMLSamplesReader;

public class RefDBCreator {

	public static void main(String[] args) throws Exception {
		XMLSamplesReader reader = new XMLSamplesReader("samples/", "samples.xml", true);
		
		Props.setProp("viterbi", "true");
		Props.setProp("variance", "true");
		Props.setProp("distance", "MAHALA");
		Props.setProp("silence", "true");
		Props.setProp("preproc", "LONG");
		Props.verbosiveViterbi = true;
		Props.originalModelSize = true;
		
		createDB(reader, "samples/singleRefDB", new ViterbiTrainer());
	}
	
	public static ReferenceDB createDB(XMLSamplesReader reader, String refDBfile, ViterbiTrainer trainer) throws Exception {
		List<Sample> samples = reader.getSamples();
		ReferenceDB refDB = new ReferenceDB();
		RefRecorder refRecorder = new RefRecorder(refDB);
		
		System.out.println("Creating references...");
		for(int i=0; i<samples.size(); i++) {
			FilePlayer player = new FilePlayer(samples.get(i).fileName);
			DoubleSource source = new ByteConverter(player);
			
			PreprocIfc preproc = Props.getPreprocessor(refRecorder);
			
			FrameSplitter splitter = new FrameSplitter(source, preproc, 256);
			player.start();
			Thread.sleep(20);
			
			splitter.start();
			while(!player.isFinished()) {
				Thread.sleep(10);
			}

			// make fileplayer-nonthreaded
			Thread.sleep(200);
			
			preproc.finish();
			Reference ref = refRecorder.createReference(samples.get(i).pattern);
			ref.speaker = samples.get(i).speaker;
		}
		System.out.println("References created.");
		
		if(trainer != null) {
			trainer.setRefDB(refDB);
			trainer.startLearning();
		}
		
		refRecorder.storeDB(refDBfile);
		return refDB;
	}

}
