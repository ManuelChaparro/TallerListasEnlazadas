package views;

import java.awt.BorderLayout;

import javax.swing.*;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public MainWindow() {
		setSize(600, 400);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		add(new MainPanel(), BorderLayout.CENTER);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new MainWindow();
	}

}
