package muster.sound;

import java.util.Vector;

import muster.preprocessing.DPreprocIfc;

public class FrameSplitter extends Thread implements DoubleSource {
	
	DoubleSource source;
	int frameSize;
	DPreprocIfc dftImpl;
	double[] frame;
	Vector<Double> doubleBuffer = new Vector<Double>();


	public FrameSplitter(DoubleSource source, DPreprocIfc dftImpl, int frameSize) {
		this.source = source;
		this.frameSize = frameSize;
		frame = new double[frameSize];
		for(int l=0; l<frameSize; l++)
			frame[l] = 0.0;
		this.dftImpl = dftImpl;
	}
	
	@Override
	public void run() {
		// at the beginning always add one frame
		addHalfFrame(frame, source);
		addHalfFrame(frame, source);
		dftImpl.compute(frame.clone());
		
		// then go on by shifting half a frame
		while(!source.isFinished()) {
			if(source.isEmpty())
				try {
					sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			else
				while(!source.isEmpty()) {
					if(addHalfFrame(frame, source))
						dftImpl.compute(frame.clone());
					else {
						break;
					}
				}
			
		}
	}
	
	private boolean addHalfFrame(double[] frame, DoubleSource source) {
		int halfSize = frameSize/2;
		for(int l=0; l<frameSize; l++) {
			if(l<halfSize) {
				frame[l] = frame[l+halfSize];
			}
			else {
				if(source.isFinished())
//					frame[l] = 0.0;
					return false;
				else {
					frame[l] = source.getNextDouble();
					//if (recording){
						doubleBuffer.add(frame[l]);
					//}
				}
			}
		}
		return true;
	}

	@Override
	public double getNextDouble() {
		double tmp = 0;
		if (!doubleBuffer.isEmpty()){
			tmp = doubleBuffer.firstElement();
			doubleBuffer.remove(tmp);
		}
		return tmp;
	}

	@Override
	public boolean isEmpty() {
		return doubleBuffer.isEmpty();
	}

	@Override
	public boolean isFinished() {
		return source.isFinished() && doubleBuffer.isEmpty();
	}
}
