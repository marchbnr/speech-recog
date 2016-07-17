package muster.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;
import javax.swing.JPanel;

import muster.sound.DoubleSource;

@SuppressWarnings("serial")
public class SoundPlotter extends JPanel implements Runnable {

	private int maxValueCount = 300;
	private double valueMax = 20000.0;
	private Vector<Double> valueList = new Vector<Double>();
	private DoubleSource source;
	
	public SoundPlotter(DoubleSource source)
	{
		init(source);
	}
	
	public SoundPlotter(DoubleSource source, int _maxValues, double _scale)
	{
		maxValueCount = _maxValues;
		init(source);
	}
	
	public void init(DoubleSource source)
	{
		setSource(source);
		setBackground(Color.BLACK);
		//setSize(150,150);
		for(int i = 0; i<maxValueCount; i++)
			valueList.add(0.0);
	}
	
	private void setSource(DoubleSource source) {
		this.source = source;
	}
	
	public void putValue(double value)
	{
		synchronized(this)
		{
			valueList.remove(0);
			valueList.add(value);
		}
	}
	
	@Override
	public void paint(Graphics g)
	{
		int width = this.getWidth();
		int height = this.getHeight();
		
		// draw the background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		// draw a horizontal line without values
		int horizon = height/2;
		g.setColor(Color.GREEN);
		g.drawLine(0, horizon, width, horizon);
		
		// draw the actual values
		double valueWidth = width/(maxValueCount+1.0);
		double valueHeight = height/(2*valueMax);
		synchronized(this)
		{
			int i = 0;
			for(double doubleValue : valueList)
			{
				int value = (int)(valueHeight*doubleValue);
				if(value > 0.0)
					g.fillRect((int)(i*valueWidth), (horizon-value), (int)valueWidth, value);
				else
					g.fillRect((int)(i*valueWidth), horizon, (int)valueWidth, -value);
				
				i++;
			}
		}
	}
	
//	public static void main(String[] args) throws LineUnavailableException
//	{
//		System.out.println("Creating new recorder");
//		SoundRecorder testRecorder = new SoundRecorder();
//		testRecorder.record();
//
////		testRecorder.finish();
////		System.out.println("Recording stopped");
//		
//		JFrame frame = new JFrame();
//		SoundPlotter testPlotter = new SoundPlotter(new ByteConverter(testRecorder), 512, 1.0);
//		new Thread(testPlotter).start();
//		testPlotter.setSize(180,200);
////		for(int i=0; i<60; i++)
////		{
////			testPlotter.putValue((Math.random()-0.5)*30.0);
////		}
//		frame.add(testPlotter);
//		frame.setSize(180,200);
//		frame.setVisible(true);
//	}

	@Override
	public void run() {
		System.out.println("Soundplotter running");
		try {
			while(!source.isFinished()) {
				while(!source.isEmpty()) {
					putValue(source.getNextDouble());
				}
				repaint();
				Thread.sleep(40);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Soundplotter stopped");
	}
}
