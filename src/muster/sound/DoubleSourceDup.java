package muster.sound;

import java.util.ArrayList;

public class DoubleSourceDup implements DoubleSource {
	
	private class ClonedSource implements DoubleSource {
		private ArrayList<Double> doubles = new ArrayList<Double>();
		
		public synchronized void add(double value) {
			doubles.add(value);
//			System.out.println("cloning");
		}

		@Override
		public double getNextDouble() {
			synchronized(this) {
				double value = doubles.get(0);
				doubles.remove(0);
				return value;
			}
		}

		@Override
		public boolean isEmpty() {
			return doubles.isEmpty();
		}

		@Override
		public boolean isFinished() {
			return doubles.isEmpty() && source.isFinished();
		}
		
	}
	
	private DoubleSource source;
	private ClonedSource clone;
	
	public DoubleSourceDup(DoubleSource source) {
		this.source = source;
		this.clone = new ClonedSource();
	}
	
	public DoubleSource getClone() {
		return clone;
	}

	@Override
	public double getNextDouble() {
		double value = source.getNextDouble();
		clone.add(value);
		return value;
	}

	@Override
	public boolean isEmpty() {
		return source.isEmpty();
	}

	@Override
	public boolean isFinished() {
		return source.isFinished();
	}

}
