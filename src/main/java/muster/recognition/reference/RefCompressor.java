package muster.recognition.reference;

import java.util.ArrayList;
import java.util.Arrays;

import muster.util.Props;

/**
 * A RefCompressor is used to sum up several vectors that were matched with a
 * reference, to compress them to a new model and compute the models variance.
 * 
 * Used e.g. for the Viterbi algorithm.
 */
public class RefCompressor {
	
	public static double VARIANCE_MIN = 0.0001;

	ArrayList<ArrayList<double[]>> vectors = new ArrayList<ArrayList<double[]>>();
	int vectorSize;

	public RefCompressor(int nVectors, int vectorSize) {
		init(nVectors, vectorSize);
	}

	public void init(int nVectors, int vectorSize) {
		this.vectorSize = vectorSize;
		for (int i = 0; i < nVectors; i++)
			vectors.add(new ArrayList<double[]>());
	}

	public void addVector(int vectorPos, double[] vector) {
		vectors.get(vectorPos).add(vector);
	}

	private boolean withVariance() {
		return Props.getBool("variance");
	}

	public Reference getCompressedReference() {
		Reference nuModel = new Reference(
				new double[vectors.size()][vectorSize]);

		double[] varianceSum = null;
		int[] varianceCount = null;
		if (withVariance()) {
			varianceSum = new double[vectorSize];
			varianceCount = new int[vectorSize];
			nuModel.variance = new double[nuModel.size()][vectorSize];
		}

		// iterate through every vector
		for (int vectorPos = 0; vectorPos < vectors.size(); vectorPos++) {
			ArrayList<double[]> list = vectors.get(vectorPos);

			// sum up all vectors in the list and save
			// the average as the new modelVector
			for (int element = 0; element < vectorSize; element++) {
				double tmpSum = 0;
				for (double[] vector : list) {
					tmpSum += vector[element];
				}
				
				if (list.isEmpty())
					nuModel.setElement(vectorPos, element, 0);
				else
					nuModel.setElement(vectorPos, element, tmpSum
									/ list.size());

				if (withVariance()) {
					double average = nuModel.getElement(vectorPos, element);
					for (double[] vector : list) {
						double var = (vector[element] - average);
						varianceSum[element] += var * var;
						varianceCount[element]++;
					}

					if (list.isEmpty())
						varianceSum[element] = 1;
					else
						varianceSum[element] /= varianceCount[element];
					if (varianceSum[element] < VARIANCE_MIN)
						varianceSum[element] = VARIANCE_MIN;
					nuModel.variance[vectorPos][element] = varianceSum[element];
				}
			}
			Arrays.fill(varianceSum, 0.0);
			Arrays.fill(varianceCount, 0);
		}
		
		if(withVariance())
			nuModel.createVarianceSum();
		return nuModel;
	}
	
}
