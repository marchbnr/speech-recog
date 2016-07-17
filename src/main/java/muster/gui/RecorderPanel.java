package muster.gui;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import muster.sound.SoundWriter;
import muster.util.Sample;
import muster.util.XMLSamplesReader;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class RecorderPanel extends JPanel implements ActionListener, TimeDisplayIFC {
	private static final long serialVersionUID = 1L;
	private JTextField pattern;
	private JButton record;
	private JLabel message;
	private JTextField filename;
	private JTextField speaker;
	private boolean recording = false;
	private SoundWriter writer;
	XMLSamplesReader xmlReader;
	
	private SimpleTimer timerThread;
	public File selectedFile;
	
	private static String lastFileName = "filename";
	private static String lastSpeaker = "speaker";
	private static String lastPattern = "pattern";

	public RecorderPanel(SoundWriter writer, XMLSamplesReader xmlReader) {
		this.writer = writer;
		this.xmlReader = xmlReader;
		this.timerThread = new SimpleTimer(this);
		timerThread.start();
		timerThread.displayActive = true;
		
		JPanel textFields;
		JPanel innerButtons;
		JPanel innerPanel;
		JPanel buttons;
		
		this.setVisible(true);
		BoxLayout thisLayout = new BoxLayout(this, javax.swing.BoxLayout.Y_AXIS);
		this.setLayout(thisLayout);
		this.setPreferredSize(new java.awt.Dimension(260, 180));
		this.setSize(260, 180);
		{
			textFields = new JPanel();
			FlowLayout textFieldsLayout = new FlowLayout();
			textFields.setLayout(textFieldsLayout);
			this.add(textFields);
			textFields.setPreferredSize(new java.awt.Dimension(262, 117));
			{
				innerPanel = new JPanel();
				textFields.add(innerPanel);
				innerPanel.setPreferredSize(new java.awt.Dimension(180, 105));
				{
					speaker = new JTextField();
					innerPanel.add(speaker);
					speaker.setText(lastSpeaker);
					speaker.setPreferredSize(new java.awt.Dimension(180, 29));
				}
				{
					pattern = new JTextField();
					innerPanel.add(pattern);
					pattern.setText(lastPattern);
					pattern.setPreferredSize(new java.awt.Dimension(180, 25));
				}
				{
					filename = new JTextField();
					innerPanel.add(filename);
					filename.setText(lastFileName);
					filename.setPreferredSize(new java.awt.Dimension(180, 29));
				}
			}
		}
		{
			buttons = new JPanel();
			this.add(buttons);
			buttons.setPreferredSize(new java.awt.Dimension(262, 68));
			{
				innerButtons = new JPanel();
				buttons.add(innerButtons);
				innerButtons.setPreferredSize(new java.awt.Dimension(233, 63));
				{
					message = new JLabel();
					innerButtons.add(message);
					message.setText("");
					message.setSize(180, 22);
					message.setPreferredSize(new java.awt.Dimension(130, 22));
					message.setHorizontalAlignment(SwingConstants.CENTER);
				}
				{
					record = new JButton();
					innerButtons.add(record);
					record.setText("record");
					record.addActionListener(this);
					record.setPreferredSize(new java.awt.Dimension(130, 29));
				}
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		SoundWriter writer = new SoundWriter();
		XMLSamplesReader xmlReader = new XMLSamplesReader("samples/", "samples.xml", true);
		JFrame frame = new JFrame();
		frame.add(new RecorderPanel(writer, xmlReader));
		frame.setSize(265,200);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(recording) {
			message.setText(message.getText() + "done.");
			record.setText("record");
			recording = false;
			writer.finish();
		}
		else {
			try {
				lastFileName = filename.getText();
				lastSpeaker = speaker.getText();
				lastPattern = pattern.getText();
				
				String exactFileName = selectedFile.getCanonicalPath().substring(0, (selectedFile.getCanonicalPath().lastIndexOf(File.separatorChar)) + 1) + lastFileName + ".wav";
				writer.setFile(exactFileName);
				Sample sample = new Sample(lastPattern, lastSpeaker, exactFileName);
				xmlReader.getSamples().add(sample);
				xmlReader.toXML();
				xmlReader.readXML();
				
				writer.record();
				message.setText("recording...");
				record.setText("stop");
				recording = true;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "File already exists, \n choose a different name.", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	public void setTime(String time){
		message.setText(time);
	}
	
	public void stopTimerThread(){
		this.timerThread.displayActive = false;
	}

	@Override
	public boolean isRecording() {
		return recording;
	}
}
