package muster.sound;

public interface DoubleSource {
	
	/**
	 * Returns true if there are values left, or are incoming soon.
	 */
	public boolean isFinished();
	
	/**
	 * Returns the next value from cache or waits a certain time before throwing
	 * an exception.
	 */
	public double getNextDouble();
	
	/** 
	 * Returns true if the cache is currently empty
	 */
	public boolean isEmpty();
}
