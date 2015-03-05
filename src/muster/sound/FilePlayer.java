package muster.sound;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class FilePlayer extends Thread implements ByteSource {

	AudioInputStream inputStream = null;
	
	public boolean playing = false;
	private LinkedBlockingQueue<Byte> queue = new LinkedBlockingQueue<Byte>();
	
	public FilePlayer(String fileName) throws UnsupportedAudioFileException, IOException {
		inputStream = AudioSystem.getAudioInputStream(new File(fileName));
		playing = true;
	}
	
	public void run() {
		while(playing)
		{
			
			try {
				int bytesAvailable = inputStream.available();
				byte[] byteArray = new byte[bytesAvailable];
				int state = inputStream.read(byteArray,0,bytesAvailable);
				
				if(state <= 0) {
					playing = false;
					break;
				}
				
				for(Byte nextByte : byteArray)
					queue.put(nextByte);
				Thread.sleep(10);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			} catch (InterruptedException _) { }
		}
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isFinished() {
		return isEmpty() && !playing;
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public byte getNextByte() {
		try {
			return queue.poll(500, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Recorder did return no values.");
		}
	}

}
