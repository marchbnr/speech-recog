package muster.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PlotterWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	JPanel silencePanel = new JPanel();
	
	public PlotterWindow() {
		silencePanel.setLayout(new BoxLayout(silencePanel, BoxLayout.Y_AXIS));
	}
	
	public void addPlot(String title, TwoDimPlotter plot) {
		plot.setSize(800,200);
		plot.setPreferredSize(new Dimension(800,200));
		silencePanel.add(new JLabel(title));
		JPanel tmpPanel = new JPanel();
		tmpPanel.add(plot);
		silencePanel.add(tmpPanel);
	}
	
	public void showPlots(String title) {
		JScrollPane scrollPane = new JScrollPane(silencePanel);
		add(scrollPane);
		setSize(900, 700);
		setTitle(title);
		setVisible(true);
	}
}
