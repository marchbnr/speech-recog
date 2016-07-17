package muster.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class XMLSamplesReader {
	
  	static final boolean DEBUG = false;
	ArrayList<Sample> samples = new ArrayList<Sample>();
	String path;
	String xmlFile;
	

	
	public XMLSamplesReader(String path, String fileName, boolean readFile) throws Exception {
		// parse the file
		xmlFile = path + fileName;
		this.path = path;
		if(readFile) {
			readXML();
			
			if(DEBUG)
				for(Sample sample : samples)
					System.out.println(sample);
			System.out.println("All samples read.");
		}
	}
	
	public ArrayList<Sample> getSamples() {
		return samples;
	}
	
	@SuppressWarnings("unchecked")
	public void readXML() throws Exception {
		FileInputStream stream = new FileInputStream(xmlFile);
		samples.clear();
		
		SAXBuilder parser = new SAXBuilder();
		Document doc = parser.build(stream);
		stream.close();
		System.out.println("Reading samples library...");
		List<Element> samplesNode = doc.getRootElement().getChildren();
		for(Element sampleNode : samplesNode)
			samples.add(new Sample(sampleNode.getAttributeValue("pattern"),
					sampleNode.getAttributeValue("speaker"),
					path + sampleNode.getValue()));
	}
	
	public void toXML() throws Exception {
		File xmlFilet = new File(xmlFile);
		if(!xmlFilet.exists()){
			xmlFilet.createNewFile();
		}
		PrintStream stream = new PrintStream(new FileOutputStream(xmlFile));
		
		stream.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		stream.println("<samples>");
		for(Sample sample : samples) {
			String sampleString = "<sample pattern=\"";
			sampleString += sample.pattern;
			sampleString += "\" speaker=\"";
			sampleString += sample.speaker;
			sampleString += "\">";
			sampleString += sample.fileName.substring(sample.fileName.lastIndexOf(File.separatorChar)+1) + "</sample>";
			stream.println(sampleString);
		}
		stream.println("</samples>");
		
		stream.close();
	}
	
	public void toXML(String fileName) throws Exception {
		xmlFile = fileName;
		toXML();
	}
	
	public static void main(String[] args) throws Exception {
		XMLSamplesReader reader = new XMLSamplesReader("samples/", "samples.xml", true);
		reader.getSamples().add(new Sample("buxtehude","", "samples/bux.wav"));
		reader.toXML("samples/test.xml");
	}
}
