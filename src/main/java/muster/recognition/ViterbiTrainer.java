package muster.recognition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import muster.gui.PlotterWindow;
import muster.gui.TwoDimPlotter;
import muster.recognition.reference.RefComparison;
import muster.recognition.reference.RefCompressor;
import muster.recognition.reference.Reference;
import muster.recognition.reference.ReferenceDB;
import muster.util.Props;

public class ViterbiTrainer {

	static final boolean DEBUG = false;
	ReferenceDB refDB;
	int vectorSize = Props.getVectorSize();
	ArrayList<Reference> finishedReferences = new ArrayList<Reference>();
	int[] latestPath; // the path created after the latest comparison
	SilenceViterbiTrainer silenceTrainer = null;
	
	PlotterWindow plots = new PlotterWindow();

	public ViterbiTrainer() {
	}
	
	public ViterbiTrainer(ReferenceDB refDB) {
		this.refDB = refDB;
	}
	
	public ViterbiTrainer(ReferenceDB refDB,int vectorSize) {
		this.refDB = refDB;
		this.vectorSize = vectorSize;
	}

	public void startLearning() {
		ArrayList<Reference> references = new ArrayList<Reference>();
		// remove silence from all references and save the silence model
		if(Props.getBool("silence")) {
			SilenceViterbiTrainer silTrainer = new SilenceViterbiTrainer();
			silTrainer.removeSilence(refDB.references);
			refDB.silence = silTrainer.getSilenceModel();
		}

		// remove a list of references with the same pattern, that are to be
		// combined to a new model
		while (!refDB.references.isEmpty()) {
			// -- group all samples with the same pattern --
			Reference sampleRef = refDB.references.get(0);
			System.out.println("Training started: " + sampleRef.pattern);
			for (Reference ref : refDB.references)
				if (ref.pattern.equals(sampleRef.pattern))
					references.add(ref);

			for (Reference ref : references)
				refDB.references.remove(ref);

			// -- create inital model --		
			Collections.sort(references);
			int modelLength = getModelSize(references);
			Reference model = createInitialModel(references, modelLength);
			model.pattern = sampleRef.pattern;
			//model.toFile("log/initial_" + model.pattern + ".txt");

			// -- iterate improve model --
			double distance = improveModel(model, references);
			if (DEBUG)
				System.out.println("Initial distance: " + distance);
			double diff = 1;
			int iteration = 1;
			while (diff > 0) {
				double nuDistance = improveModel(model, references);
				if (DEBUG)
					System.out.println("New distance: " + nuDistance);
				if (iteration++ == 10)
					break;
				diff = Math.abs(nuDistance - distance);
				distance = nuDistance;
			}
			
			if(Props.verbosiveViterbi)
				plotSilence(model);

			if (DEBUG)
				model.toFile("log/model_" + model.pattern + ".txt");

			System.out.println("Training finished: " + iteration
					+ " iterations.");
			finishedReferences.add(model);
			if(Props.getBool("variance"))
				model.createVarianceSum();
			references.clear();
		}
		if(Props.verbosiveViterbi)
			plots.showPlots("Viterbi models");
		refDB.references = finishedReferences;
	}

	public Reference createInitialModel(List<Reference> references,
			int modelLength) {
		RefCompressor compressor = new RefCompressor(modelLength, vectorSize);

		for (Reference ref : references) {
			if (DEBUG)
				System.out.println("assigning reference");
			int refLength = ref.size();
			double slope = (modelLength - 1) / (double) (refLength - 1);

			for (int i = 0; i < refLength; i++) {
				int match = (int) (i * slope + 0.5);
				compressor.addVector(match, ref.getVector(i));
			}
		}
		return compressor.getCompressedReference();
	}

	/** returns a model size for a sorted list of references */
	public int getModelSize(List<Reference> references) {
		if(Props.originalModelSize) {
			int meridiansLength = 0;
			if(references.size() == 1)
				meridiansLength = references.get(0).size();
			else
				meridiansLength = references.get((references.size() + 1) / 2).size();
			return meridiansLength / 2;
		}
		else return references.get(0).size();
	}

	public double improveModel(Reference model, List<Reference> references) {
		double distSum = 0;

		RefCompressor compressor = new RefCompressor(model.size(), vectorSize);

		for (Reference ref : references) {
			RefComparison comp = new RefComparison(ref, model, true, Props.getMetrics());

			distSum += comp.getDistance();
			latestPath = comp.getPath();
			
			// -- super low level debug --
//			 for(int i=0; i<latestPath.length; i++)
//			 System.out.print(latestPath[i] + " ");
//			 System.out.println();
//			 
//			comp.toDistFile("log/testfile.txt");
			for (int i = 0; i < latestPath.length; i++)
				compressor.addVector(latestPath[i], ref.getVector(i));
		}
		
		Reference compressed = compressor.getCompressedReference();
		model.setVectors(compressed.getVectors());
		model.variance = compressed.variance;
		if(compressed.varianceSum == null)
			throw new RuntimeException("already null");
		model.varianceSum = compressed.varianceSum;
		return distSum;
	}
	
	public void plotSilence(Reference ref) {
		int lo = -1,hi = -1;
		for(int i=0; i<latestPath.length; i++) {
			if(latestPath[i] == 1 && lo == -1) lo = i;
			if(latestPath[i] == (ref.size()-1) && hi == -1) hi = i;
		}
		double[] volume = new double[ref.size()];
		for(int i=0; i<ref.size(); i++) {
			volume[i] = ref.getVector(i)[0];
		}
		
		plots.addPlot(ref.pattern + " final", new TwoDimPlotter(volume, lo, hi));
	}

	public void setRefDB(ReferenceDB refDB) {
		this.refDB = refDB;
	}

}
