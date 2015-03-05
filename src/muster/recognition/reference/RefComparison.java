package muster.recognition.reference;

import java.io.File;
import java.io.PrintStream;

import muster.recognition.distance.DistanceMetrics;

/**
 * A RefComparison is used to compare the minimum distance between
 * two references.
 * 
 * Distance metrics and path-creation can be changed via parameters.
 */
public class RefComparison {
	
	static final boolean DEBUG = false;
	boolean withPath = false;
	int[][] backPointers;
	double[][] costHistory;
	Reference sample;
	Reference reference;
	static DistanceMetrics metrics = null;

	int nVectors;
	int nRefVectors;
	int index = 0;
	double currentDistance = -1;
	double[] distances;
	double preIndexCost = 0;

	public RefComparison(Reference sample, Reference reference) {
		init(sample, reference, false);
	}
	
	public RefComparison(Reference sample, Reference reference, boolean withPath) {
		init(sample, reference, withPath);
	}
	
	public RefComparison(Reference sample, Reference reference, boolean withPath, DistanceMetrics metrics) {
		init(sample, reference, withPath);
		RefComparison.metrics = metrics;
	}
	
	public void setPath(boolean withPath) {
		if(withPath) {
			this.withPath = true;
			backPointers = new int[nVectors][nRefVectors];
			costHistory = new double[nVectors][nRefVectors];
		}
	}
	
	private void init(Reference sample, Reference reference, boolean withPath) {
		this.sample = sample;
		this.reference = reference;
		nRefVectors = reference.size();
		nVectors = sample.size();
		distances = new double[nRefVectors];
		setPath(withPath);
	}

	// update the distance for the next frame/vector of the recorded vectors
	public void compareStep() {
		double[] nuDistances = new double[distances.length];
//		System.out.println();

		int minDistanceIndex = -1;
		double minDistance = -1;
		int minIndex = index + ((sample.size() - index) * -2) - 1;
		if (minIndex < 0)
			minIndex = 0;

		if (index == 0) {
			nuDistances[0] = getDistance(index, sample, index, reference);
			minDistance = nuDistances[0];
			if(withPath)
				costHistory[0][0] = nuDistances[0];
			if(DEBUG)
				System.out.println("Comparing index=0 i=0: " + nuDistances[0]);
		} else {
			for (int i=getLowerBound(index); isLegalIndex(i, index); i++) {
				// find the lowest cost of the 3 possible predecessors
				int preIndex = getPreIndex(i, index);

				// and add the additional distance
				nuDistances[i] = distances[preIndex] + preIndexCost + getDistance(index, sample, i, reference);
				if(DEBUG) {
					System.out.println("Comparing index=" + index +" i=" + i + ": " + nuDistances[i] + " : " + distances[preIndex] + " + " + getDistance(index, sample, i, reference));
				}
				

				if(withPath) {
//					costHistory[index][i] = getDistance(vectors[index], reference.vectors[i]);
					costHistory[index][i] = nuDistances[i];
					if(DEBUG)
						System.out.println("Backpointer index=" + index +" i=" + i + ": " + preIndex);
					backPointers[index][i] = preIndex;
				}
				
				// update the currently minimum distance
				if (minDistanceIndex == -1 || nuDistances[i] < minDistance) {
					minDistance = nuDistances[i];
					minDistanceIndex = i;
				}
//				 System.out.println("distance found: " + nuDistances[i]);
			}
		}

		distances = nuDistances;
		currentDistance = minDistance;
		index++;
	}
	
	private int getPreIndex(int i, int index) {
	  	//	 find the lowest cost of the 3 possible predecessors
		int preIndex = -1;
		boolean preIndexSet = false;
		preIndexCost = 0;
		if(!preIndexSet && isLegalIndex(i,index-1)) {
			preIndexSet = true;
			preIndex = i;
			preIndexCost = metrics.getPenalty(0);
		}
		if(isLegalIndex(i-1,index-1) && (!preIndexSet || distances[i-1] < distances[preIndex])) {
			preIndexSet = true;
			preIndex = i-1;
			preIndexCost = metrics.getPenalty(1);
		}
		if(isLegalIndex(i-2,index-1) && (!preIndexSet || distances[i-2] < distances[preIndex])) {
			preIndexSet = true;
			preIndex = i-2;
			preIndexCost = metrics.getPenalty(2);
		}
		if(index == 0 || index == reference.size()-1) preIndexCost = 0;
		return preIndex;
	}
	
	protected boolean isLegalIndex(int refIndex, int vectorIndex) {
		return refIndex >= getLowerBound(vectorIndex) && refIndex <= getUpperBound(vectorIndex);
	}
	
	protected int getLowerBound(int vectorIndex) {
		int tmpIndex = (nVectors - vectorIndex -1) * -2 + nRefVectors-1;
//		System.out.println("lower bound: " + tmpIndex);
		if(tmpIndex > 0)
			return tmpIndex;
		else return 0;
	}
	
	protected int getUpperBound(int vectorIndex) {
		int tmpIndex = vectorIndex * 2;
//		System.out.println("upper bound: " + tmpIndex);
		if(tmpIndex >= nRefVectors)
			return nRefVectors-1;
		else
			return tmpIndex;
	}

	public static double getDistance(int refAIndex, Reference refA, int refBIndex, Reference refB) {
		return metrics.getDistance(refAIndex, refA, refBIndex, refB);
	}

	// checks if both vectors have reached the end node
	public boolean isFinished() {
		return (index == sample.size());
	}

	// compute the real distance, always using all frames
	public double getDistance() {
		while (!isFinished())
			compareStep();
		return getCurrentDistance();
	}

	// get the distance between the two vectors so far
	public double getCurrentDistance() {
		return currentDistance;
	}
	
	public int[] getPath() {
		int[] path = new int[nVectors];
		// the first back pointer is the last vector combination to be compared,
		// accordingly the path is filled by getting the consecutive back pointers
		int backPointer = path[nVectors-1] = nRefVectors-1;
		for(int i=nVectors-2; i>=0; i--) {
			backPointer = path[i] = backPointers[i+1][backPointer];
		}
		if(DEBUG) {
			System.out.print("path: ");
			for(int step : path)
				System.out.print(step + " ");
			System.out.println();
		}
		return path;
	}
	
	public void toFile(String fileName) {
		try {
			new File(fileName).createNewFile();
			PrintStream stream = new PrintStream(fileName);
			int[] vectorPath = getPath();
			stream.println("Path:");
			stream.print("\t");
			for(int i=0; i<nVectors; i++)
				stream.print(i + "\t");
			stream.println();
			
			for(int u=0; u<nRefVectors; u++) {
				stream.print(u + "\t");
				for(int i=0; i<nVectors; i++)
					if(u == vectorPath[i])
						stream.print(Math.round(costHistory[i][u]) + "\t");
					else
						stream.print("\t");
				stream.println();
			}
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void toDistFile(String fileName) {
		try {
			new File(fileName).createNewFile();
			PrintStream stream = new PrintStream(fileName);
			stream.println("Distances:");
			stream.print("\t");
			for(int i=0; i<nVectors; i++)
				stream.print(i + "\t");
			stream.println();
			
			for(int u=0; u<nRefVectors; u++) {
				stream.print(u + "\t");
				for(int i=0; i<nVectors; i++)
					stream.print((Math.round(getDistance(i, sample, u, reference)*100.0)/100.0) + "\t");
				stream.println();
			}
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
