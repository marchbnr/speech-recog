package muster.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import muster.sound.DoubleSource;

public class DoublesParser implements DoubleSource {

	private Queue<Double> valuesQueue = new LinkedList<Double>();
	
	public DoublesParser(String fileName) throws NumberFormatException, IOException {
		parseValues(fileName, valuesQueue);
	}
	
	public static List<Double> parseFile(String fileName)
			throws NumberFormatException, IOException {
		List<Double> valuesList = new ArrayList<Double>();
		parseValues(fileName, valuesList);
		return valuesList;
	}
	
	private static void parseValues(String fileName, Collection<Double> collection)
			throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.length()>0 && line != null) {
				if ((line.charAt(0) >= '0' && line.charAt(0) <= '9') || (line.charAt(0) == '-')){
					double value = Double.parseDouble(line);
					collection.add(value);
				}
			}
		}
		reader.close();
	}

	@Override
	public double getNextDouble() {
		return valuesQueue.poll();
	}

	@Override
	public boolean isFinished() {
		return valuesQueue.isEmpty();
	}

	@Override
	public boolean isEmpty() {
		return valuesQueue.isEmpty();
	}
}
