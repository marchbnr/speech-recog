package muster.gui;

public class SimpleTimer extends Thread{
	
	private float timeRunning = 0;
	private TimeDisplayIFC tdi;
	public boolean displayActive = false;
	
	public SimpleTimer(TimeDisplayIFC timeDisplay){
		tdi = timeDisplay;
	}

	
	public void run() {
		while(displayActive){
			if(!tdi.isRecording()) timeRunning = 0;
			if(tdi.isRecording()){
				tdi.setTime(((Math.round(timeRunning*100))/100.0) + " sec recorded. ");
				timeRunning += 0.1;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
