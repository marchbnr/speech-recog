package muster.recognition.reference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class ReferenceDB implements Serializable {
  
  public Reference silence = null;
	
	public ArrayList<Reference> references = new ArrayList<Reference>();
	
	public static ReferenceDB fromFile(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(fileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		ReferenceDB refDB = (ReferenceDB)ois.readObject();
		ois.close();
		return refDB;
	}
	
	public void toFile(String fileName) throws IOException {
		File file = new File(fileName);
		if(!file.exists()){
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.close();
	}
}
