package muster.sound;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Records sound from a recording source, delivering byte values.
 * 
 * Can only be started or stopped, and only one recording can run at a time.
 */
public class SoundRecorder implements ByteSource, Runnable {
	
	private boolean recording = false;
	private Thread thread = null;
	
	private LinkedBlockingQueue<Byte> queue = new LinkedBlockingQueue<Byte>();
	private TargetDataLine targetDataLine = null;
	
	public SoundRecorder() {
		
	}
	
	public void record() throws LineUnavailableException
	{
		if(thread == null)
		{
			recording = true;
//			AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
//													8000.0f, 16, 2, 4, 8000.0f, false);
			AudioFormat audioFormat = new AudioFormat(8000.0f, 16, 1, true, false);
			targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
			targetDataLine.open(audioFormat);
			int bytesAvailable;
			while((bytesAvailable = targetDataLine.available()) > 0) {
				byte[] byteArray = new byte[bytesAvailable];
				targetDataLine.read(byteArray,0,bytesAvailable);
			}
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
		recording = false;
		thread = null;
	}

	@Override
	public void run() {
		System.out.println("Recording started...");
		while(recording)
		{
			int bytesAvailable = targetDataLine.available();
			if (bytesAvailable<0) break;
			byte[] byteArray = new byte[bytesAvailable];
			int state = targetDataLine.read(byteArray,0,bytesAvailable);
			
			// if the target source has finished stop getting new values
			if(state == -1) {
				finish();
				break;
			}
			try {
				for(Byte nextByte : byteArray)
					queue.put(nextByte);
				Thread.sleep(10);
			} catch (InterruptedException ie) { }
		}

		System.out.println("Recording stopped");
	}

	/**
	 * Used to test the recorder.
	 */
	public static void main(String[] args) throws Exception
	{
		System.out.println("Creating new recorder");
		SoundRecorder testRecorder = new SoundRecorder();
		testRecorder.record();
		
		System.out.println("Press enter to stop");
		System.in.read();
		testRecorder.finish();
		System.out.println("Quitting");
	}

	@Override
	public byte getNextByte() {
		try {
			Byte result = queue.poll(500, TimeUnit.MILLISECONDS);
			if(result == null) return 0;
			return result;
		} catch (InterruptedException e) {
			throw new RuntimeException("Recorder did return no values.");
		}
	}

	@Override
	public boolean isFinished() {
		return isEmpty() && !recording;
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}

}
