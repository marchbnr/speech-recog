package muster.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TwoDimPlotter extends JPanel {
	
	double[] data;
	int lowerBorder;
	int upperBorder;
	double maxValue;
	
	public TwoDimPlotter(double[] data, int lowerBorder, int upperBorder) {
		this.lowerBorder = lowerBorder;
		this.upperBorder = upperBorder;
		this.data = data;
		maxValue = 0;
		for(int i=0; i<data.length; i++) {
			data[i] = Math.abs(data[i]);
			if(data[i] > maxValue)
				maxValue = data[i];
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
		
		// draw the actual values
		double valueWidth = width/(data.length + 1.0);
		double valueHeight = height/maxValue;
		
		int i = 0;
		g.setColor(Color.GREEN);
		for(double doubleValue : data)
		{
			int value = (int)(valueHeight*doubleValue);
			g.fillRect((int)(i*valueWidth), (height-value), (int)valueWidth, height);
			
			i++;
		}
		
		// draw a vertical line for the beginning/ending of a word
		g.setColor(Color.RED);
		g.drawLine((int) (valueWidth*lowerBorder), 0, (int) (valueWidth*lowerBorder), height);
		g.drawLine((int) (valueWidth*upperBorder), 0, (int) (valueWidth*upperBorder), height);
	}
	
	public static void plotSilence(double[] data, int lo, int hi, String title) {
		JFrame frame = new JFrame();
		TwoDimPlotter testPlotter = new TwoDimPlotter(data, lo, hi);
		frame.add(testPlotter);
		frame.setSize(800,200);
		frame.setTitle(title);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) throws LineUnavailableException
	{
		JFrame frame = new JFrame();
		double[] testData = new double[256];
		for(int i=0; i<testData.length; i++)
			testData[i] = Math.sin(Math.PI * (i/16.0));
		TwoDimPlotter testPlotter = new TwoDimPlotter(testData, 100, 200);
		frame.add(testPlotter);
		frame.setSize(800,200);
		frame.setVisible(true);
	}
}
