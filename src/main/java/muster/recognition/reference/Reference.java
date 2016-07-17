package muster.recognition.reference;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;

import muster.util.Props;

/**
 * The reference class is used to store and load reference values.
 * Additionally a reference sample knows to which textual pattern it belongs.
 */
@SuppressWarnings("serial")
public class Reference implements Serializable, Comparable<Reference>, Cloneable {
	
	private double[][] vectors;
	public String pattern;
	public double[][] variance;
	public double[] varianceSum;
	public String speaker;
	
	public Reference(int size) {
		vectors = new double[size][24];
		variance = new double[size][24];
	}
	
	public Reference() { }
	
	public Reference(List<double[]> vectors) {
		int frames = vectors.size();
		int frameSize = vectors.get(0).length;
		this.vectors = new double[frames][frameSize];
		for(int i=0; i<frames; i++)
			for(int u=0; u<frameSize; u++)
				this.vectors[i][u] = vectors.get(i)[u];
	}
	
	public Reference(Double[][] vectors) {
		int frames = vectors.length;
		int frameSize = vectors[0].length;
		this.vectors = new double[frames][frameSize];
		for(int i=0; i<vectors.length; i++)
			for(int u=0; u<vectors[0].length; u++)
				this.vectors[i][u] = vectors[i][u];
	}
	
	public Reference(double[][] vectors) {
		this.vectors = vectors.clone();
	}
	
	public Reference(Reference equalRef, Reference silence) {
		this.pattern = equalRef.pattern;
		this.speaker = equalRef.speaker;
		int refSize = equalRef.size()+2;
		
		int vectorSize = Props.getVectorSize();
		vectors = new double[refSize][vectorSize];
		vectors[0] = silence.getVector(0);
		vectors[vectors.length-1] = silence.getVector(0);
		
		variance = new double[refSize][vectorSize];
		varianceSum = new double[refSize];
		
		for(int i=1; i<refSize-1; i++) {
			vectors[i] = equalRef.getVector(i-1);
			variance[i] = equalRef.variance[i-1];
			varianceSum[i] = equalRef.varianceSum[i-1];
		}
		
		for(int element=0; element<vectorSize; element++) {
			variance[0][element] = silence.variance[0][element];
			variance[refSize-1][element] = silence.variance[0][element];
		}
		varianceSum[0] = silence.varianceSum[0];
		varianceSum[refSize-1] = silence.varianceSum[0];
	}

	public static Reference fromList(List<double[]> vectors) {
		Reference newReference = new Reference();
		newReference.init(vectors);
		return newReference;
	}
	
	private void init(List<double[]> vectors) {
		int frames = vectors.size();
		int frameSize = vectors.get(0).length;
		this.vectors = new double[frames][frameSize];
		for(int i=0; i<frames; i++)
			for(int u=0; u<frameSize; u++)
				this.vectors[i][u] = vectors.get(i)[u];
	}
	
	public void createVarianceSum() {
		varianceSum = new double[variance.length];
		for(int i=0; i<variance.length; i++) {
			varianceSum[i] = 0;
			for(int u=0; u<variance[i].length; u++)
				varianceSum[i] += Math.log(variance[i][u]);
		}
//		if(variance.length != varianceSum.length)
//			throw new RuntimeException();
	}
	
	public double[] getVector(int nVector) {
		return vectors[nVector];
	}
	
	public void setVector(int nVector, double[] vector) {
		vectors[nVector] = vector;
	}
	
	public double[][] getVectors() {
		return vectors;
	}
	
	public void setVectors(double[][] vectors) {
		this.vectors = vectors;
	}
	
	public void setVectors(List<double[]> vectors) {
		if(this.vectors.length != vectors.size())
			this.vectors = new double[vectors.size()][Props.getVectorSize()];
		
		for(int i=0; i<vectors.size(); i++) {
			this.vectors[i] = vectors.get(i);
		}
	}
	
	public double getElement(int nVector, int nElement) {
		return vectors[nVector][nElement];
	}
	
	public void setElement(int nVector, int nElement, double value) {
		vectors[nVector][nElement] = value;
	}
	
	public int size() {
		return vectors.length;
	}
	
	public String toString() {
		return pattern + ", " + vectors.length + " vectors, " + vectors[0].length + " vectorSize";
	}
	
	public void toFile(String fileName) {
		try {
			new File(fileName).createNewFile();
			PrintStream stream = new PrintStream(fileName);
			for(int i=0; i<vectors.length; i++) {
				stream.println("Frame " + i);
				for(int u=0; u<vectors[0].length; u++)
					stream.print(vectors[i][u] + " ");
				stream.println();
				if(variance != null) {
				  for(int u=0; u<vectors[0].length; u++)
					stream.print(variance[i][u] + " ");
				  stream.println();
				}
				stream.println();
			}
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void toArrayFile(String fileName) {
		try {
			new File(fileName).createNewFile();
			PrintStream stream = new PrintStream(fileName);
			stream.print("{ ");
			for(int i=0; i<vectors.length; i++) {
				stream.print("{ ");
				for(int u=0; u<vectors[0].length; u++)
					stream.print(vectors[i][u] + ", ");
				stream.println(" },");
			}
			stream.println("}");
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object clone() {
		Reference clonedReference = new Reference(vectors);
		clonedReference.pattern = pattern;
		clonedReference.speaker = speaker;
		for(int i=0; i<variance.length; i++)
			for(int u=0; u<variance[i].length; u++)
				clonedReference.variance[i][u] = variance[i][u];
		return clonedReference;
	}

	@Override
	public int compareTo(Reference otherRef) {
		if(otherRef.size() > size()) return -1;
		else if(otherRef.size() < size()) return 1;
		return 0;
	}
}
