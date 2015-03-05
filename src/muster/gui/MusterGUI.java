package muster.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import muster.bin.ContinuousDetection;
import muster.bin.MultiDetectionBin;
import muster.bin.SingleDetection;
import muster.bin.RefDBCreator;
import muster.recognition.DetectionViewIfc;
import muster.recognition.ViterbiTrainer;
import muster.recognition.reference.ReferenceDB;
import muster.sound.SoundWriter;
import muster.util.Props;
import muster.util.XMLSamplesReader;

public class MusterGUI extends JFrame implements ActionListener, DetectionViewIfc {
	
	private static final long serialVersionUID = 1L;
	private GridBagConstraints c;
	
	private JTextField txRecognized;
	public final int txRecHeight = 25;
	
	private JButton btnSimpleDetection;
	private JButton btnMultiDetection;
	private JButton btnInteractiveDetection;
	private JButton btnOpenFileRecorderWindow;
	private JButton btnCreateSingleDatabase;
	private JButton btnCreateContDatabase;
	private JButton btnOpenSettingsWindow;
	
	private ImageIcon titleIcon;
	private ImageIcon startIcon;
	private ImageIcon stopIcon;
	private ImageIcon singleDbIcon;
	private ImageIcon contDbIcon;
	private ImageIcon recordIcon;
	private ImageIcon settingsIcon;
	
	private JPanel pnSimpleDetectionButtons;
	private JPanel pnMultiDetectionButtons;
	private JPanel pnInteractiveDetectionButtons;
	
	RecorderPanel recorder;
	
	private JComboBox cmbXMLFilesList;
	
	private JFileChooser fileChooserXML;
	
	private ArrayList<File> XMLFilesList;
	private String[] selectedFile;
	private XMLSamplesReader xmlReader;
	private File rootDir;
	private File defaultXMLFile;
//	private final String[] defaultXMLFile = {"samples", "samples.xml"};
	
	
	static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	private Dimension BTN_SIZE;
	
	private boolean started = false;
	
	private SoundPlotter plotter;
	SingleDetection singleDetection = new SingleDetection();
	MultiDetectionBin multiDetection = new MultiDetectionBin();
	ContinuousDetection contDetection = new ContinuousDetection();
	ReferenceDB refDB;

	
	public MusterGUI() throws Exception{
		initComponents();
	}
	
	public void initComponents() throws Exception{
		
		JPanel pnButton;
		JPanel pnDatabaseButtons;
		JPanel pnText;
		JPanel pnFileChooser;
		
		c = new GridBagConstraints();
		
		
		
		//Icons
		titleIcon = createImageIcon("Files/Icons/agt_mp3.png", "");
		startIcon = createImageIcon("Files/Icons/player_play.png", "Start recognition");
		stopIcon = createImageIcon("Files/Icons/player_stop.png", "Stop recognition");
		singleDbIcon = createImageIcon("Files/Icons/db_icon_single.png", "Create singleref DB");
		contDbIcon = createImageIcon("Files/Icons/db_icon_cont.png", "Create contref DB");
		recordIcon = createImageIcon("Files/Icons/record.png", "Start recording");
		settingsIcon = createImageIcon("Files/Icons/settings.png", "Change settings");
		
		BTN_SIZE = new Dimension(startIcon.getIconWidth() + 5, startIcon.getIconHeight() + 5);
		
		//Simple detection buttons & panel
		btnSimpleDetection = new JButton();
		btnSimpleDetection.setSize(BTN_SIZE);
		btnSimpleDetection.addActionListener(this);
		btnSimpleDetection.setToolTipText("Start");
		btnSimpleDetection.setIcon(startIcon);
		
		pnSimpleDetectionButtons = new JPanel();
		pnSimpleDetectionButtons.setLayout(new GridBagLayout());
		pnSimpleDetectionButtons.setBorder(new TitledBorder("Single"));
		
		setGridBagConstraints(0, 0, 0.5, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		pnSimpleDetectionButtons.add(btnSimpleDetection, c);
		
		
		//Simple detection buttons & panel
		btnMultiDetection = new JButton();
		btnMultiDetection.setSize(BTN_SIZE);
		btnMultiDetection.addActionListener(this);
		btnMultiDetection.setToolTipText("Start");
		btnMultiDetection.setIcon(startIcon);
		
		pnMultiDetectionButtons = new JPanel();
		pnMultiDetectionButtons.setLayout(new GridBagLayout());
		pnMultiDetectionButtons.setBorder(new TitledBorder("Multi"));
		
		setGridBagConstraints(0, 0, 0.5, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		pnMultiDetectionButtons.add(btnMultiDetection, c);
		
		
		//Interactive detection buttons & panel
		btnInteractiveDetection = new JButton();
		btnInteractiveDetection.setSize(BTN_SIZE);
		btnInteractiveDetection.setIcon(startIcon);
		btnInteractiveDetection.setToolTipText("Start");
		btnInteractiveDetection.addActionListener(this);

		
		pnInteractiveDetectionButtons = new JPanel();
		pnInteractiveDetectionButtons.setLayout(new GridBagLayout());
		pnInteractiveDetectionButtons.setBorder(new TitledBorder("Interactive"));
		
		setGridBagConstraints(0, 0, 0.5, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		pnInteractiveDetectionButtons.add(btnInteractiveDetection, c);

		
		//recording and db creator buttons & panel
		btnOpenFileRecorderWindow = new JButton();
		btnOpenFileRecorderWindow.setSize(BTN_SIZE);
		btnOpenFileRecorderWindow.setIcon(recordIcon);
		btnOpenFileRecorderWindow.addActionListener(this);
		
		btnCreateSingleDatabase = new JButton();
		btnCreateSingleDatabase.setSize(BTN_SIZE);
		btnCreateSingleDatabase.setIcon(singleDbIcon);
		btnCreateSingleDatabase.addActionListener(this);
		
		btnCreateContDatabase = new JButton();
		btnCreateContDatabase.setSize(BTN_SIZE);
		btnCreateContDatabase.setIcon(contDbIcon);
		btnCreateContDatabase.addActionListener(this);
		
		btnOpenSettingsWindow = new JButton();
		btnOpenSettingsWindow.setSize(BTN_SIZE);
		btnOpenSettingsWindow.setIcon(settingsIcon);
		btnOpenSettingsWindow.addActionListener(this);
		
		pnDatabaseButtons = new JPanel();
		pnDatabaseButtons.setLayout(new GridBagLayout());
		pnDatabaseButtons.setBorder(new TitledBorder("Record DB"));
		
		setGridBagConstraints(0, 0, 0.5, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		pnDatabaseButtons.add(btnOpenFileRecorderWindow, c);
		setGridBagConstraints(1, 0, 0.5, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		pnDatabaseButtons.add(btnCreateSingleDatabase, c);
		setGridBagConstraints(2, 0, 0.5, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		pnDatabaseButtons.add(btnCreateContDatabase, c);
		//setGridBagConstraints(3, 0, 0.5, 1, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		//pnDatabaseButtons.add(btnOpenSettingsWindow, c);
		
		//Combobox with XML Files and FileChooser
		readXMLList();
		if(XMLFilesList==null) XMLFilesList = new ArrayList<File>();
		
		fileChooserXML = new JFileChooser();
		fileChooserXML.setAcceptAllFileFilterUsed(false);
		fileChooserXML.addChoosableFileFilter(new XMLFilter());
		rootDir = new File(new File(".").getCanonicalPath());
        fileChooserXML.setCurrentDirectory(rootDir);

        defaultXMLFile =  new File(rootDir.toString() + File.separatorChar + "samples" + File.separatorChar + "samples.xml");
        boolean containsDefaultFile = false;
        for(File file : XMLFilesList){
        	if (file.toString().equals(defaultXMLFile.toString())) containsDefaultFile = true;
        }
		if (!containsDefaultFile){
			XMLFilesList.add(defaultXMLFile);
			if(!(defaultXMLFile.exists())){
				try {
					selectedFile = getPathAndFilename(defaultXMLFile.toString());
					xmlReader = new XMLSamplesReader(selectedFile[0] + File.separatorChar, selectedFile[1], false);
					xmlReader.toXML();
					xmlReader = null;
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

		{
			ArrayList<Object> deleteables = new ArrayList<Object>();
			for(Object obj : XMLFilesList)
				if(!((File)obj).exists()) deleteables.add(obj);
			for(Object deleteable : deleteables)
				XMLFilesList.remove(deleteable);
		}
		
		
		Object[] tmpFilesList = XMLFilesList.toArray();
			
		for (int i=0; i<tmpFilesList.length; i++){
			if(((File)tmpFilesList[i]).exists()){
				int j = ((File)tmpFilesList[i]).toString().lastIndexOf(File.separator);
				tmpFilesList[i] = ((File)tmpFilesList[i]).toString().substring(j+1);
			}
		}
		cmbXMLFilesList = new JComboBox(tmpFilesList);
		cmbXMLFilesList.addItem("Datei wählen ...");
		cmbXMLFilesList.addActionListener(this);
		if(!(cmbXMLFilesList.getSelectedItem()+"").equals("Datei wählen ...")){
			try {
				selectedFile = getPathAndFilename(XMLFilesList.get(cmbXMLFilesList.getSelectedIndex()).getCanonicalPath());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		pnFileChooser = new JPanel();
		pnFileChooser.setLayout(new GridBagLayout());
		pnFileChooser.setBorder(new TitledBorder("Wortschatz wählen: "));
		
		setGridBagConstraints(0, 0, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		pnFileChooser.add(cmbXMLFilesList, c);
		
		//Buttonpanel
		pnButton = new JPanel();
		pnButton.setLayout(new GridBagLayout());
		
		setGridBagConstraints(0, 0, 0.5, 1, GridBagConstraints.NONE);
		pnButton.add(pnSimpleDetectionButtons);
		
		setGridBagConstraints(0, 1, 0.5, 1, GridBagConstraints.NONE);
		pnButton.add(pnMultiDetectionButtons);
		
		setGridBagConstraints(0, 2, 0.5, 1, GridBagConstraints.NONE);
		pnButton.add(pnInteractiveDetectionButtons);
		
		setGridBagConstraints(0, 3, 1, 1, GridBagConstraints.NONE);
		pnButton.add(pnDatabaseButtons);
		
		setGridBagConstraints(0, 4, 0.5, 1, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
		pnButton.add(pnFileChooser);
		
			
		//Recognized textfield & panel
		txRecognized = new JTextField();
		txRecognized.setSize(600, 25);
		txRecognized.setMinimumSize(new Dimension(600, 25));
		txRecognized.setPreferredSize(new Dimension(600, 25));
		txRecognized.setMaximumSize(new Dimension(2000, 25));
		txRecognized.setEditable(false);
		txRecognized.setBackground(Color.WHITE);
		txRecognized.setHorizontalAlignment(JTextField.CENTER);
		
		pnText = new JPanel();
		pnText.setBorder(new TitledBorder("Detected word(s): "));
		setGridBagConstraints(0, 0, 0.7, 0, GridBagConstraints.HORIZONTAL);
		pnText.add(txRecognized, c);
		
		//Init simple recognition components
		plotter = new SoundPlotter(singleDetection.splitter);
		
		//Add components to main window
		setGridBagConstraints(0, 0, 0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
		this.setLayout(new GridBagLayout());
		this.add(pnButton, c);

		setGridBagConstraints(0, 2, 1, 1, GridBagConstraints.BOTH);
		this.add(plotter, c);
		
		setGridBagConstraints(0, 1, 1, 0, GridBagConstraints.HORIZONTAL);
		this.add(pnText, c);
		
		
		//Window settings
		this.setTitle("Speech Recognition");
		this.setIconImage(titleIcon.getImage());
		this.setSize(640,480);
		this.setVisible(true);
		
		this.setLocation(SCREEN_SIZE.width/2 - getWidth()/2, 
                SCREEN_SIZE.height/2 - getHeight()/2);
		
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try {
					saveXMLList();
				} catch (Exception e1) {}
				System.exit(0);
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnSimpleDetection && !started)
			startSingleDetection();
		
		else if(e.getSource() == btnSimpleDetection && started)
			stopSingleDetection();
		
		else if(e.getSource() == btnMultiDetection && !started)
			startMultiDetection();
		
		else if(e.getSource() == btnMultiDetection && started)
			stopMultiDetection();
		
		else if(e.getSource() == btnInteractiveDetection && !started)
			startInteractiveDetection();
		
		else if(e.getSource() == btnInteractiveDetection && started)
			stopInteractiveDetection();
		
		else if(e.getSource() == btnOpenFileRecorderWindow)
			openFileRecorder();
		
		else if(e.getSource() == btnCreateSingleDatabase)
			createSingleDatabase();
		
		else if(e.getSource() == btnCreateContDatabase)
			createContDatabase();
		
		else if(e.getSource() == btnOpenSettingsWindow)
			openSettings();
		
		else if(e.getSource() == cmbXMLFilesList){
			if (((String)cmbXMLFilesList.getSelectedItem()).equals("Datei wählen ...")){
				int returnVal = 0;
				try{
					returnVal = fileChooserXML.showOpenDialog(this);	
				} catch (Exception e1) {}
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File selection = fileChooserXML.getSelectedFile();
					cmbXMLFilesList.removeItem(cmbXMLFilesList.getSelectedItem());
					cmbXMLFilesList.addItem(selection.getName().toString());
					try {
						XMLFilesList.add(new File(selection.getCanonicalPath()));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
						
					selectedFile = getPathAndFilename(selection.toString());
					
					if(!selection.exists()){
						try {
							selection.createNewFile();
							xmlReader = new XMLSamplesReader(selectedFile[0], selectedFile[1], false);
							xmlReader.toXML();
							xmlReader = null;
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					cmbXMLFilesList.setSelectedIndex(cmbXMLFilesList.getItemCount()-1);
					cmbXMLFilesList.addItem("Datei wählen ...");
				}
				if (returnVal == JFileChooser.CANCEL_OPTION){
					cmbXMLFilesList.setSelectedIndex(0);
				}
			}
			else 
				selectedFile = getPathAndFilename(XMLFilesList.get(cmbXMLFilesList.getSelectedIndex()).toString());
		}
	}
	
	@Override
	public void printDetection(String word) {
		String oldText = txRecognized.getText();
		String[] parts = oldText.split(" ");
		oldText = "";
		for(String part : parts) {
			if(part.length() > 0) {
				oldText += part.trim() + " ";
			}
		}
		
		oldText += "  " + word;
		
		if(oldText.length() > 90) {
			oldText = oldText.substring(oldText.length()-90);
		}
		txRecognized.setText(oldText);
	}
	
	public void setGridBagConstraints(int gridx, int gridy, double weightx, double weighty, int fill){
		c.gridx = gridx;
		c.gridy = gridy;
		c.weightx = weightx;
		c.weighty = weighty;
		c.fill = fill;
	}
	
	public void setGridBagConstraints(int gridx, int gridy, double weightx, double weighty, int fill, int anchor){
		c.gridx = gridx;
		c.gridy = gridy;
		c.weightx = weightx;
		c.weighty = weighty;
		c.fill = fill;
		c.anchor = anchor;
	}
	
	public void setSimpleDetectioButtonStart(){
		btnSimpleDetection.setToolTipText("Start");
		btnSimpleDetection.setIcon(startIcon);
		btnInteractiveDetection.setEnabled(true);
		btnMultiDetection.setEnabled(true);
		this.repaint();
	}
	
	public void setSimpleDetectioButtonStop(){
		btnSimpleDetection.setToolTipText("Stop");
		btnSimpleDetection.setIcon(stopIcon);
		btnInteractiveDetection.setEnabled(false);
		btnMultiDetection.setEnabled(false);
		this.repaint();
	}
	
	public void setInteractiveDetectioButtonStart(){
		btnInteractiveDetection.setToolTipText("Start");
		btnInteractiveDetection.setIcon(startIcon);
		btnSimpleDetection.setEnabled(true);
		btnMultiDetection.setEnabled(true);
		this.repaint();
	}
	
	public void setInteractiveDetectioButtonStop(){
		btnInteractiveDetection.setToolTipText("Stop");
		btnInteractiveDetection.setIcon(stopIcon);
		btnSimpleDetection.setEnabled(false);
		btnMultiDetection.setEnabled(false);
		this.repaint();
	}
	
	public void setMultiDetectioButtonStart(){
		btnMultiDetection.setToolTipText("Start");
		btnMultiDetection.setIcon(startIcon);
		btnSimpleDetection.setEnabled(true);
		btnInteractiveDetection.setEnabled(true);
		this.repaint();
	}
	
	public void setMultiDetectioButtonStop(){
		btnMultiDetection.setToolTipText("Stop");
		btnMultiDetection.setIcon(stopIcon);
		btnSimpleDetection.setEnabled(false);
		btnInteractiveDetection.setEnabled(false);
		this.repaint();
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path,
	                                           String description) {
	    if (path != null) {
	        return new ImageIcon(path, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}

	public void saveXMLList(){
		try {
			FileOutputStream fos = new FileOutputStream(new File("Files/XML_History.jobj"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(XMLFilesList);
			fos.close();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void readXMLList(){
		try {
			FileInputStream fis = new FileInputStream("Files/XML_History.jobj");
			ObjectInputStream ois = new ObjectInputStream(fis);
			XMLFilesList = (ArrayList<File>)ois.readObject();
			ois.close();
		} catch (Exception e) {}
	}
	
	public String getRelativePath(String path){
//		System.out.println("Relative Path: " + path.substring(path.lastIndexOf(File.separator + "Muster") + 8));
		return path.substring(path.lastIndexOf(File.separator + "Muster") + 8);
	}
	
	public String[] getPathAndFilename(String pathAndFileName){
		String[] res = new String[2];
		res[0] = getRelativePath(pathAndFileName.substring(0, pathAndFileName.lastIndexOf(File.separatorChar) + 1));
		res[1] = pathAndFileName.substring(pathAndFileName.lastIndexOf(File.separatorChar) + 1);
//		System.out.println("GetPathAndFileName:" + res[0] + res[1]);
		return res;
	}
	

	private void createDBwarning() {
		JOptionPane.showMessageDialog(this, "You have to create the database \n before starting recognition.", "Warning", JOptionPane.WARNING_MESSAGE);
	}

	private boolean singleDBcreated() {
		if(new File(selectedFile[0] + "singleRefDB").exists()) return true;
		return false;
	}
	
	private boolean contDBcreated() {
		if(new File(selectedFile[0] + "contRefDB").exists()) return true;
		return false;
	}
	
	private void openFileRecorder() {	
		txRecognized.setText("");
		SoundWriter writer = new SoundWriter();
		XMLSamplesReader xmlReader = null;
		try {
			xmlReader = new XMLSamplesReader(selectedFile[0], selectedFile[1], true);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		JFrame frame = new JFrame();
		recorder = new RecorderPanel(writer, xmlReader);
		recorder.selectedFile = new File(selectedFile[0] + selectedFile[1]);
		System.out.println(selectedFile[0] + selectedFile[1]);
		frame.add(recorder);
		frame.setSize(265,200);
		frame.setAlwaysOnTop(true);
		frame.setIconImage(recordIcon.getImage());
		frame.setLocation(SCREEN_SIZE.width/2 - 200, 
                SCREEN_SIZE.height/2 - 100);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				recorder.stopTimerThread();
			}
		});
	}
		
	private void openSettings() {
		JFrame frame = new JFrame();
		frame.add(new JLabel("TODO: change settings of props"));
		frame.setSize(300,100);
		frame.setIconImage(settingsIcon.getImage());
		frame.setLocation(SCREEN_SIZE.width/2 - 200, 
                SCREEN_SIZE.height/2 - 100);
		frame.setVisible(true);
	}
	
	private void startSingleDetection() {
		if(singleDBcreated()){
			try {
				txRecognized.setText("");
				Props.setProp("variance", "true");
				Props.setProp("distance", "MAHALA");
				Props.setProp("preproc", "LONG");
				
				setSimpleDetectioButtonStop();
				System.out.println("Start detection Selected dir: " + selectedFile[0]);
				refDB = ReferenceDB.fromFile(selectedFile[0] + "singleRefDB");
				singleDetection.init(refDB);
				singleDetection.setDetectionView(this);
				singleDetection.start();
				started = true;
				
				plotter.init(singleDetection.clonedSource);
				new Thread(plotter).start();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		else createDBwarning();
	}
	
	private void stopSingleDetection() {
		try {
			singleDetection.finish();
			started = false;
			setSimpleDetectioButtonStart();
//			btnStartInteractiveDetection.setEnabled(true);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	private void startMultiDetection() {
		if(singleDBcreated()){
			try {
				txRecognized.setText("");
				Props.setProp("variance", "true");
				Props.setProp("distance", "MAHALA");
				Props.setProp("preproc", "LONG");
				
				setMultiDetectioButtonStop();
				System.out.println("Start detection Selected dir: " + selectedFile[0]);
				refDB = ReferenceDB.fromFile(selectedFile[0] + "singleRefDB");
				multiDetection.init(refDB);
				multiDetection.setDetectionView(this);
				multiDetection.start();
				started = true;
				
				plotter.init(multiDetection.clonedSource);
				new Thread(plotter).start();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		else createDBwarning();
	}
	
	private void stopMultiDetection() {
		try {
			multiDetection.finish();
			started = false;
			setMultiDetectioButtonStart();
//			btnStartInteractiveDetection.setEnabled(true);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	private void startInteractiveDetection() {
		if(contDBcreated()){
			try {
				Props.setProp("variance", "true");
				Props.setProp("distance", "MAHALA");
				Props.setProp("preproc", "NONE");
				Props.setProp("vectorsize", "24");
				
				setInteractiveDetectioButtonStop();
				System.out.println("Start interactive detection Selected dir: " + selectedFile[0]);
				refDB = ReferenceDB.fromFile(selectedFile[0] + "contRefDB");
				contDetection.init(refDB);
				contDetection.setDetectionView(this);
				contDetection.start();
				started = true;
				
				plotter.init(contDetection.clonedSource);
				new Thread(plotter).start();
				System.out.println("started");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		else createDBwarning();
	}
	
	private void stopInteractiveDetection() {
		try {
			contDetection.finish();
			setInteractiveDetectioButtonStart();
			started = false;
			System.out.println("stopped");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	
	private void createSingleDatabase() {
		try {
			txRecognized.setText("");
			Props.setProp("viterbi", "true");
			Props.setProp("variance", "true");
			Props.setProp("distance", "MAHALA");
			Props.setProp("silence", "true");
			Props.setProp("preproc", "LONG");
			System.out.println("CreateDB Selected dir: " + selectedFile[0] + ", file: " + selectedFile[1]);
			XMLSamplesReader xmlReader = new XMLSamplesReader(selectedFile[0], selectedFile[1], true);
			RefDBCreator.createDB(xmlReader, selectedFile[0] + "singleRefDB" , new ViterbiTrainer());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void createContDatabase() {
		try {
			txRecognized.setText("");
			Props.setProp("viterbi", "true");
			Props.setProp("variance", "true");
			Props.setProp("distance", "MAHALA");
			Props.setProp("silence", "true");
			Props.setProp("preproc", "NONE");
			XMLSamplesReader xmlReader = new XMLSamplesReader(selectedFile[0], selectedFile[1], true);
			RefDBCreator.createDB(xmlReader, (selectedFile[0]) + "contRefDB", new ViterbiTrainer());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new MusterGUI();
	}
}
