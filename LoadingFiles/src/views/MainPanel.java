package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.temporal.JulianFields;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class MainPanel extends JPanel {

	private static final String READ_FILE = "Leyendo Archivo";
	private static final Font DEFAULT_FONT = new Font("Segoe UI", Font.BOLD, 16);
	private static final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	private static final Color DEFAULT_COLOR = Color.decode("#00BCF2");
	private static final String PATH_ICON_FILE = "/img/iconFile.png";
	private static final String PATH_ICON_ACCEPT = "/img/iconAccept.png";
	private static final String GIGABYTE_EXT = " Gb";
    private static final int ONE_GB_IN_BYTES = 1073741824;
    private static final int ONE_MB_IN_BYTES = 1048576;
    private static final int ONE_KB_IN_BYTES = 1024;
    private static final String MEGABYTE_EXT = " Mb";
    private static final String KYLOBYTE_EXT = " kb";
    private static final String BYTES_EXT = " Bytes";
	private JPanel right, left, north;
	private JLabel infoActualFile, img;
	private static String PATH_FILE = "";
	private static String QUANTITY_MB = "";
	private static String actualFile = "";
	private static final long serialVersionUID = 1L;
	private static final int ONE_MEGA = 1048576;
	private SwingWorker<Integer, Integer> worker;
	private File selectedFile;
	private int megas;
	private JTextField field;
	private double percentage;
	private double counterActualSize;
	private double sizeFolder, totalSizeFolder;
	String sizeFile;

	public MainPanel() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		initComponents();
		initNorth();
		initCenter();
		initWorker();
		worker.execute();
	}

	private void initCenter() {
		JPanel center = new JPanel();
		JScrollPane scroll = createPanelStatus();
		center.setBackground(Color.decode("#eeeeee"));
		JPanel left = createActualFile();
		
		center.add(left, BorderLayout.CENTER);
		center.add(scroll, BorderLayout.EAST);
		add(center, BorderLayout.CENTER);
	}

	private JPanel createActualFile() {
		left = new JPanel();		
		left.setBorder(BorderFactory.createTitledBorder(new EmptyBorder(0, 20, 0, 0), "LEYENDO ARCHIVOS"));
		Image icon = new ImageIcon(getClass().getResource(PATH_ICON_FILE)).getImage();
		Icon rescaledIcon = new ImageIcon(icon.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
		img = new JLabel();
		img.setIcon(rescaledIcon);
		infoActualFile = new JLabel();
		left.setBackground(Color.decode("#eeeeee"));
		left.setPreferredSize(new Dimension(300, 300));
		left.setLayout(new BorderLayout());
		left.add(img, BorderLayout.CENTER);
		
		infoActualFile.setForeground(Color.BLACK);
		infoActualFile.setFont(DEFAULT_FONT);
		infoActualFile.setBorder(BorderFactory.createTitledBorder(READ_FILE));
		left.add(infoActualFile, BorderLayout.SOUTH);
		return left;
	}

	private JScrollPane createPanelStatus() {
		right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.setBorder(new EmptyBorder(10, 10, 10, 10));
		JScrollPane scroll = new JScrollPane(right);
		scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });
		scroll.setPreferredSize(new Dimension(280, 300));
		right.setBackground(Color.decode("#dae1e7"));
		return scroll;
	}

	private void initNorth() {
		north = new JPanel();
		north.setPreferredSize(new Dimension(600, 60));
		north.setBackground(DEFAULT_COLOR);
		
		field = new JTextField();
		field.setText(PATH_FILE);
		field.setBackground(DEFAULT_COLOR);
		field.setFont(DEFAULT_FONT);
		field.setForeground(Color.WHITE);
		field.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(Color.decode("#eeeeee"), Color.decode("#eeeeee")), "Ruta del archivo"));
		north.add(field);
		add(north, BorderLayout.NORTH);
	}

	private void initComponents() {
		try {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int seleccion = fileChooser.showOpenDialog(this);
			if (seleccion == JFileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
				PATH_FILE = selectedFile.getAbsolutePath();
				sizeFolder = folderSize(selectedFile);
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
	

	
	

	private void initWorker() {
		worker = new SwingWorker<Integer, Integer>(){

			protected Integer doInBackground() throws Exception {
				read(selectedFile);
				return 0;
			}
			
			protected void process(List<Integer> chunks) {

				super.process(chunks);
			}
			
			protected void done() {
				super.done();
				infoActualFile.setText("");
				JLabel fileRead = new JLabel();
				fileRead.setFont(new Font("Segoe UI", Font.BOLD, 15));
				fileRead.setForeground(Color.BLACK);
				fileRead.setText(actualFile);
				right.setBackground(Color.decode("#45C3BA"));
				north.setBackground(Color.decode("#45C3BA"));
				field.setBackground(Color.decode("#45C3BA"));
				Image icon = new ImageIcon(getClass().getResource(PATH_ICON_ACCEPT)).getImage();
				Icon rescaledIcon = new ImageIcon(icon.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
				img.setIcon(rescaledIcon);
				right.add(fileRead);
				for (int i = 0; i < 22; i++) {
					JLabel info = (JLabel) right.getComponent(i);
					info.setForeground(Color.WHITE);
				}
			}	
			
			protected void read(File sourceLocation) throws IOException {
				if (sourceLocation.isDirectory()) {
					for (String f : sourceLocation.list()) {
						read(new File(sourceLocation, f));
					}
				} else {
					try (InputStream in = new FileInputStream(sourceLocation)) {
						byte[] buffer = new byte[(int) ONE_MEGA];
						megas = 0;
						while ((in.read(buffer)) > 0) {
							try {
								megas++;
								publish(megas);
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if (sizeFile != null) {
							totalSizeFolder += sourceLocation.length();
							JLabel fileRead = new JLabel();
							fileRead.setFont(new Font("Segoe UI", Font.BOLD, 15));
							fileRead.setForeground(Color.BLACK);
							fileRead.setText(infoActualFile.getText() + "       " + sizeFile);
							right.add(fileRead);
							actualFile = sourceLocation.getName() + "  " + validateBytes(sourceLocation.length());
							infoActualFile.setText(sourceLocation.getName());
							sizeFile = validateBytes(sourceLocation.length());
							revalidate();
							repaint();
						}else {
							sizeFile = "Archivos Leídos";
						}
					}
				}
			}
			
			private String validateBytes(long size) {
		        String unid = null;
		        if (size < 1024) {
		            unid = String.valueOf(size) + BYTES_EXT;
		        } else if (size >= ONE_KB_IN_BYTES && size < ONE_MB_IN_BYTES) {
		            unid = String.valueOf(size / ONE_KB_IN_BYTES) + KYLOBYTE_EXT;
		        } else if (size >= ONE_MB_IN_BYTES && size < ONE_GB_IN_BYTES) {
		            unid = String.valueOf(size / ONE_MB_IN_BYTES) + MEGABYTE_EXT;
		        } else if (size >= ONE_GB_IN_BYTES) {
		            unid = String.valueOf(size / ONE_GB_IN_BYTES) + GIGABYTE_EXT;
		        }
		            return unid;
		    }
		};		
		
	}

	public long folderSize(File directory) {
		long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile())
				length += file.length();
			else
				length += folderSize(file);
		}
		percentage = length * 0.1;
		return length;
	}


	private void percentageFile() {
		percentage = (sizeFolder * 20) / 200;
		String resultado = String.format( "%.0f", Float.parseFloat(String.valueOf(percentage)));
		System.out.println(resultado);
		counterActualSize = percentage;
	}
	
//	@Override
//	public void paint(Graphics g) {
//		Graphics2D g2 = (Graphics2D) g;
//		super.paint(g2);
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g2.setColor(Color.decode("#00BCF2").darker());
//		
//		g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
//		g2.drawString(READ_FILE, 25, 320);
//		
//	}
	
	
}
