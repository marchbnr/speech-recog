package muster.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Records sound from a recording source, delivering byte values.
 * 
 * Can only be started or stopped, and only one recording can run at a time.
 */
public class SoundWriter implements Runnable {
	
	private Thread thread = null;
	private File outputFile = null;
	
	private TargetDataLine targetDataLine = null;
	private AudioInputStream inputStream = null;
	
	public SoundWriter() { }
	
	public SoundWriter(String fileName) {
		setFile(fileName);
	}
	
	public void setFile(String fileName) {
		outputFile = new File(fileName);
		if(outputFile.exists())
			throw new RuntimeException("File already exists, to implement: create unique name");
	}
	
	public void record() throws LineUnavailableException
	{
		if(thread == null)
		{
//			AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
//													8000.0f, 16, 2, 4, 8000.0f, false);
			AudioFormat audioFormat = new AudioFormat(8000.0f, 16, 1, true, false);
			targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
			inputStream = new AudioInputStream(targetDataLine);
			targetDataLine.open(audioFormat);
			thread = new Thread(this);
			targetDataLine.start();
			thread.start();
		}
		else
		{
			throw new RuntimeException("Sound recording is already in use");
		}
	}
	
	public void finish()
	{
		if(targetDataLine != null)
		{
			targetDataLine.stop();
			targetDataLine.close();
		}
		thread = null;
	}

	@Override
	public void run() {
		System.out.println("Recording started...");
		
		try {
			AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		System.out.println("Recording finished.");
	}

}
