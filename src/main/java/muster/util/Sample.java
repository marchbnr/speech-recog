package muster.util;

public class Sample {
	public String pattern;
	public String fileName;
	public String speaker;

	public Sample(String pattern, String speaker, String fileName) {
		this.pattern = pattern;
		this.speaker = speaker;
		this.fileName = fileName;
	}
	
	public String toString() { return pattern + " - " + fileName; }
}
