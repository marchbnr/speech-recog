package muster;

import java.util.ArrayList;

import muster.recognition.ViterbiTrainer;
import muster.recognition.reference.RefCompressor;
import muster.recognition.reference.Reference;
import muster.recognition.reference.ReferenceDB;
import muster.util.Props;
import junit.framework.TestCase;

public class ViterbiTest extends TestCase {
	
	public void setUp() {
		Props.setProp("viterbi", "true");
		Props.setProp("variance", "true");
		Props.setProp("distance", "EUCLID");
		Props.setProp("silence", "true");
		Props.setProp("muster/preproc", "NONE");
		Props.verbosiveViterbi = true;
		Props.originalModelSize = true;
	}
	
	double[][] vectorsA = {
			{1., 2., 3.},
			{2., 3., 2.},
			{3., 4., 1.}
	};
	
	double[][] vectorsB = {
			{2., 8., 2.},
			{4., 10., 2.},
			{3.5, 1., 4.},
			{7.5, 6., 1.}
	};
	
	double[][] sampleVectors = {
			{1., 2., 3., 4., 5.},
			{1., 2., 6., 5.},
			{3., 4., 5., 7., 8., 7., 7.}
	};
	
	double[] initModel = {11./5. , 31./6., 28./5.};
	
	
	public void testCompressorSmallVector() {
		RefCompressor compr = new RefCompressor(1, 3);
		compr.addVector(0, vectorsA[0]);
		compr.addVector(0, vectorsA[1]);
		compr.addVector(0, vectorsA[2]);
		
		Reference result = compr.getCompressedReference();
		assertEquals(1,result.size());
		
		double[] resultsVector = result.getVector(0);
		assertEquals(2., resultsVector[0]);
		assertEquals(2./3, result.variance[0][0]);
		assertEquals(3., resultsVector[1]);
		assertEquals(2./3, result.variance[0][1]);
		assertEquals(2., resultsVector[2]);
		assertEquals(2./3, result.variance[0][2]);
	}
	
	public void testCompressorNullVector() {
		RefCompressor compr = new RefCompressor(3, 3);
		compr.addVector(0, vectorsB[0]);
		compr.addVector(0, vectorsB[1]);
		compr.addVector(2, vectorsB[2]);
		compr.addVector(2, vectorsB[3]);
		
		Reference result = compr.getCompressedReference();
		assertEquals(3,result.size());
		
		double[] resultsVector = result.getVector(0);
		assertEquals(3., resultsVector[0]);
		assertEquals(1., result.variance[0][0]);
		assertEquals(9., resultsVector[1]);
		assertEquals(1., result.variance[0][1]);
		assertEquals(2., resultsVector[2]);
		assertEquals(RefCompressor.VARIANCE_MIN, result.variance[0][2]);
		
		resultsVector = result.getVector(1);
		assertEquals(0., resultsVector[0]);
		assertEquals(1., result.variance[1][0]);
		assertEquals(0., resultsVector[1]);
		assertEquals(1., result.variance[1][1]);
		assertEquals(0., resultsVector[2]);
		assertEquals(1., result.variance[1][2]);
		
		resultsVector = result.getVector(2);
		assertEquals(5.5, resultsVector[0]);
		assertEquals(4., result.variance[2][0]);
		assertEquals(3.5, resultsVector[1]);
		assertEquals(6.25, result.variance[2][1]);
		assertEquals(2.5, resultsVector[2]);
		assertEquals(2.25, result.variance[2][2]);
	}
	
	private Reference createReference(int sample) {
		Reference ref = new Reference();
		ref.pattern = "testReference";
		double[][] refVectors = new double[sampleVectors[sample].length][1];
		for(int vector=0; vector<sampleVectors[sample].length; vector++) {
			refVectors[vector][0] = sampleVectors[sample][vector];
		}
		ref.setVectors(refVectors);
		return ref;
	}
	
	public void testInitialModel() {
		// create an arraylist to pass to the initial model creation
		ArrayList<Reference> refs = new ArrayList<Reference>();
		for(int sample=0; sample<sampleVectors.length; sample++) {
			refs.add(createReference(sample));
		}
		
		ViterbiTrainer trainer = new ViterbiTrainer(null, 1);
		int modelLength = trainer.getModelSize(refs);
		Reference initModel = trainer.createInitialModel(refs,modelLength);
		
		assertEquals(this.initModel.length, initModel.size());
		
		assertEquals(2.25, initModel.getElement(0,0), Double.parseDouble("1.0e-9"));
		assertEquals(4.71428, initModel.getElement(1,0), Double.parseDouble("1.0e-5"));
		assertEquals(5.6, initModel.getElement(2,0), Double.parseDouble("1.0e-9"));
	}
	
	public void testSingleModelImprovement() {
		Props.setProp("vectorsize", "1");
//		 create an arraylist to pass to the initial model creation
		ArrayList<Reference> refs = new ArrayList<Reference>();
		for(int sample=0; sample<sampleVectors.length; sample++) {
			refs.add(createReference(sample));
		}
		
		ViterbiTrainer trainer = new ViterbiTrainer(null, 1);
		
		Reference model = new Reference(new double[initModel.length][1]);
		for(int i=0; i< initModel.length; i++) {
			model.setElement(i, 0, initModel[i]);
		}
		
//		RefComparison comp = new RefComparison(refs.get(0).vectors, model, true);
//		System.out.println("Distance(1-model): " + comp.getDistance());
//		comp.toFile("viterbi.txt");
		
		trainer.improveModel(model, refs);
		assertEquals(1.8,model.getElement(0,0), Double.parseDouble("1.0e-9"));
		assertEquals(4.33333333333,model.getElement(1,0), Double.parseDouble("1.0e-9"));
		assertEquals(6.,model.getElement(2,0), Double.parseDouble("1.0e-9"));
	}
	
	public void testModelImprovement() {
		Props.setProp("vectorsize", "1");
		ReferenceDB refDB = new ReferenceDB();
		refDB.references.add(createReference(0));
		refDB.references.add(createReference(1));
		refDB.references.add(createReference(2));
		ViterbiTrainer trainer = new ViterbiTrainer(refDB, 1);
		
		trainer.startLearning();
	}
}
