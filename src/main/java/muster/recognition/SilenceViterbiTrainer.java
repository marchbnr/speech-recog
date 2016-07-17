package muster.recognition;

import java.util.ArrayList;
import java.util.List;

import muster.gui.PlotterWindow;
import muster.gui.TwoDimPlotter;
import muster.recognition.distance.DistanceMetrics;
import muster.recognition.distance.MahalaDistance;
import muster.recognition.reference.RefComparison;
import muster.recognition.reference.RefCompressor;
import muster.recognition.reference.Reference;
import muster.util.Props;

public class SilenceViterbiTrainer {

	static final boolean DEBUG = false;
	int vectorSize = Props.getVectorSize();
	int[] latestPath; // the path created after the latest comparison
	Reference silenceModel = null;
	DistanceMetrics metrics = new MahalaDistance();
	int count = 0;
	PlotterWindow plots = new PlotterWindow();

	public void removeSilence(List<Reference> references) {

		// -- create inital model --
		if(silenceModel == null) {
			silenceModel = createInitialModel(references);
		}
		
		// -- iterate improve model --
		double distance = improveModel(silenceModel, references);
		if (DEBUG) System.out.println("Silence init distance: " + distance);
		
		double diff = 1;
		int iteration = 1;
		while (diff > 0) {
			double nuDistance = improveModel(silenceModel, references);
			if (DEBUG) System.out.println("Silence new distance: " + nuDistance);
			if (iteration++ == 10)
				break;
			diff = Math.abs(nuDistance - distance);
			distance = nuDistance;
		}
		
		ArrayList<double[]> cache = new ArrayList<double[]>();
		
		// remove silence
		for(Reference reference : references) {
			detectSilence(silenceModel, reference, cache);
			cache.clear();
		}
		
		if(Props.verbosiveViterbi)
			plots.showPlots("Silence model");
	}

	public Reference createInitialModel(List<Reference> references) {
		RefCompressor compressor = new RefCompressor(3, vectorSize);

		for (Reference ref : references) {
			int splitLength = ref.size()/3 + 1;

			for (int i = 0; i < ref.size(); i++) {
				int match = i/splitLength;
				if(match == 1) compressor.addVector(1, ref.getVector(i));
				else compressor.addVector(0, ref.getVector(i));
			}
		}
		Reference model = compressor.getCompressedReference();
		model.setVector(2, model.getVector(0));
		model.variance[2] = model.variance[0];
		model.varianceSum[2] = model.varianceSum[0];
		return model;
	}

	public double improveModel(Reference model, List<Reference> references) {
		double distSum = 0;

		RefCompressor compressor = new RefCompressor(model.size(), vectorSize);

		for (Reference ref : references) {
			RefComparison comp = new RefComparison(ref, model, true, metrics);

			distSum += comp.getDistance();
			latestPath = comp.getPath();
			
			// -- super low level debug --
//			 for(int i=0; i<latestPath.length; i++)
//			 System.out.print(latestPath[i] + " ");
//			 System.out.println();
//			 
			if(DEBUG) {
				comp.toFile("log/sil_pathfile_" + ref.pattern + (count++) + ".txt");
			}
			for (int i = 0; i < latestPath.length; i++)
				if(latestPath[i] == 1) {
					compressor.addVector(1, ref.getVector(i));
				}
				else compressor.addVector(0, ref.getVector(i));
		}
		model = compressor.getCompressedReference();
		model.setVector(2, model.getVector(0));
		model.variance[2] = model.variance[0];
		model.varianceSum[2] = model.varianceSum[0];
		
		return distSum;
	}
	
	public void detectSilence(Reference model, Reference ref, ArrayList<double[]> cache) {
		RefComparison comp = new RefComparison(ref, model, true, metrics);
		comp.getDistance();
		latestPath = comp.getPath();
		
		if(DEBUG) System.out.println("Presilence modelsize: " + ref.size());
		
		
		for(int i=0; i<ref.size(); i++)
			if(latestPath[i]==1)
				cache.add(ref.getVector(i));
		
		if(Props.verbosiveViterbi)
			plotSilence(ref);
		
		assert(cache.size() > 0);
		ref.setVectors(cache);
		assert(ref.size() == cache.size());
		if(DEBUG) System.out.println("Postsilence modelsize: " + ref.size());
	}
	
	public void plotSilence(Reference ref) {
		int lo = -1,hi = -1;
		for(int i=0; i<latestPath.length; i++) {
			if(latestPath[i] == 1 && lo == -1) lo = i;
			if(latestPath[i] == 2 && hi == -1) hi = i;
		}
		double[] volume = new double[ref.size()];
		for(int i=0; i<ref.size(); i++) {
			volume[i] = ref.getVector(i)[0];
		}
		
		plots.addPlot(ref.pattern + " final", new TwoDimPlotter(volume, lo, hi));
	}
	
	public Reference getSilenceModel() {
		Reference silence = new Reference(1);
		silence.setVector(0, silenceModel.getVector(0));
		silence.variance[0] = silenceModel.variance[0];
		silence.varianceSum = new double[1];
		silence.varianceSum[0] = silenceModel.varianceSum[0];
//		System.out.println(" silence viterbi --------");
//		for(double value : silenceModel.getVector(1))
//			System.out.print(value + " ");
//		System.out.println();
//		for(double var : silenceModel.variance[1])
//			System.out.print(var + " ");
//		System.out.println();
//		System.out.println(" silence viterbi --------");
		//silence.toFile("log/trained_silence.txt");
		return silence;
	}

}
