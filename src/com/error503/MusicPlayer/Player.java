package com.error503.MusicPlayer;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Player extends JFrame implements ActionListener, WindowListener {

	private final JFrame f = new JFrame();

	private final JMenuBar bar = new JMenuBar();

	private final JMenu file = new JMenu("File");
	private final JMenu window = new JMenu("Window");

	private final JMenuItem open = new JMenuItem("Open...");
	private final JMenuItem connect = new JMenuItem("Connect");
	private final JCheckBoxMenuItem repeat = new JCheckBoxMenuItem("Repeat");

	private final JMenuItem credits = new JMenuItem("Credits");
	private final JMenuItem exit = new JMenuItem("Exit");

	private final JButton play = new JButton();
	private final JButton pause = new JButton();
	private final JButton stop = new JButton();
	private final JButton reverse = new JButton();
	private final JButton forward = new JButton();

	private final JLabel title = new JLabel("Now playing:", JLabel.CENTER);

	private final JLabel time = new JLabel("--:-- / --:--", JLabel.CENTER);

	private final JProgressBar progress = new JProgressBar(SwingConstants.HORIZONTAL, 0, 0);
	private final JSlider vol = new JSlider(JProgressBar.HORIZONTAL, -80, 6, 0);
	private final Timer timer;
	private String path;
	private MusicPlayer mp;

	public Player() {

		// Assigning icons to the buttons with scaling
		try {
			Image play_image = ImageIO.read(getClass().getResource("resources/play.jpg"));
			ImageIcon play_icon = new ImageIcon(play_image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

			Image pause_image = ImageIO.read(getClass().getResource("resources/pause.jpg"));
			ImageIcon pause_icon = new ImageIcon(pause_image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

			Image stop_image = ImageIO.read(getClass().getResource("resources/stop.jpg"));
			ImageIcon stop_icon = new ImageIcon(stop_image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

			Image rev_image = ImageIO.read(getClass().getResource("resources/backward.jpg"));
			ImageIcon rev_icon = new ImageIcon(rev_image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

			Image forw_image = ImageIO.read(getClass().getResource("resources/forward.jpg"));
			ImageIcon forw_icon = new ImageIcon(forw_image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

			Image repeat_image = ImageIO.read(getClass().getResource("resources/repeat.png"));
			ImageIcon repeat_icon = new ImageIcon(repeat_image.getScaledInstance(20, 20, Image.SCALE_SMOOTH));

			play.setIcon(play_icon);
			pause.setIcon(pause_icon);
			stop.setIcon(stop_icon);
			reverse.setIcon(rev_icon);
			forward.setIcon(forw_icon);
			repeat.setIcon(repeat_icon);

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Layout declaration
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// Hot keys for menu bar items
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		connect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		repeat.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

		// Filling the menu
		file.add(open);
		file.add(connect);
		file.addSeparator();
		file.add(repeat);

		window.add(credits);
		window.add(exit);

		bar.add(file);
		bar.add(window);

		setJMenuBar(bar);

		// Menu items getting action listeners
		open.addActionListener(this);
		connect.addActionListener(this);
		credits.addActionListener(this);
		exit.addActionListener(this);
		// repeat is a check box so it's state change should be listened for
		repeat.addItemListener(e -> mp.toggleRepeat());

		// Action listeners to buttons
		play.addActionListener(this);
		pause.addActionListener(this);
		stop.addActionListener(this);
		reverse.addActionListener(this);
		forward.addActionListener(this);
		vol.addChangeListener(e -> mp.setVolume(vol.getValue()));


		// Sizing components
		progress.setPreferredSize(new Dimension(150, 5));
		vol.setPreferredSize(new Dimension(150, 20));
		play.setPreferredSize(new Dimension(51, 51));
		pause.setPreferredSize(new Dimension(51, 51));
		stop.setPreferredSize(new Dimension(51, 51));
		reverse.setPreferredSize(new Dimension(51, 51));
		forward.setPreferredSize(new Dimension(51, 51));

		// Adding all components with appropriate properties -> c
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(5, 5, 5, 5);
		add(reverse, c);

		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		add(play, c);

		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		add(pause, c);

		c.gridx = 3;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		add(stop, c);

		c.gridx = 4;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		add(forward, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 5;
		c.insets = new Insets(5, 5, 5, 5);
		add(vol, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 5;
		c.insets = new Insets(5, 5, 5, 5);
		add(title, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 5;
		c.insets = new Insets(5, 5, 5, 5);
		add(progress, c);

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 5;
		c.insets = new Insets(0, 0, 0, 0);
		add(time, c);

		// All buttons are disable until the user chooses a file to be played
		play.setEnabled(false);
		pause.setEnabled(false);
		stop.setEnabled(false);
		reverse.setEnabled(false);
		forward.setEnabled(false);
		repeat.setEnabled(false);
		vol.setEnabled(false);

		// Standard JFrame part: size, default close operation, visibility, title, and
		// blocking of resizing
		pack();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		setTitle("Music Player");
		setResizable(true);
		addWindowListener(this);

		// Declaring a timer that will be used to refresh the clip's info every 100
		// milliseconds
		timer = new Timer(100, this);
		timer.setRepeats(true);

		// Setting the location of the window in the centre of the screen
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (getSize().width / 2),
				(Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (getSize().height / 2));

		// Assigning a look and feel
		// On mac it uses it's own instead of win
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Player();
	}

	// Action listeners
	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();

		if (obj == open) {

			// The file that will be played can be selected using Java built-in JFileChooser
			JFileChooser chooser = new JFileChooser();

			// Filtering for only .wav files
			chooser.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return null;
				}

				@Override
				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					}

					String extension = Utils.getExtension(f);
					if (extension != null) {
						return extension.equals(Utils.wav);
					}

					return false;
				}
			});

			// Showing the file chooser
			chooser.showOpenDialog(f);

			// Opening the selected file
			if (mp == null) {
				try {
					path = chooser.getSelectedFile().getAbsolutePath();
					mp = new MusicPlayer(path);
				} catch (IOException | LineUnavailableException err) {
					err.printStackTrace();
				}
			} else {
				try {
					path = chooser.getSelectedFile().getAbsolutePath();
					mp.kill();
					mp.init(path);
				} catch (IOException | LineUnavailableException err) {
					err.printStackTrace();
				}
			}
			play.setEnabled(true);
			pause.setEnabled(true);
			stop.setEnabled(true);
			reverse.setEnabled(true);
			forward.setEnabled(true);
			repeat.setEnabled(true);
			repeat.setState(false);
			vol.setEnabled(true);
			timer.start();
			vol.setValue(0);
			title.setText("Now playing: " + chooser.getSelectedFile().getName());
			timer.start();

		} else if (obj == connect) {
			if (path == null) {
				JOptionPane.showMessageDialog(null, "Please select a file before connecting", "Error",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				new Connect(path, mp);
			}
		} else if (obj == play) { // Playing / restarting
			mp.start();
		} else if (obj == pause) { // Pause
			mp.pause();
		} else if (obj == stop) { // Full stop, disables the control buttons and unloads the clip
			play.setEnabled(false);
			pause.setEnabled(false);
			stop.setEnabled(false);
			reverse.setEnabled(false);
			forward.setEnabled(false);
			repeat.setState(false);
			repeat.setEnabled(false);
			title.setText("Now playing:");
			vol.setEnabled(false);
			time.setText("--:-- / --:--");
			mp.stop();
			progress.setValue(0);
			progress.setMaximum(0);
		} else if (obj == exit) { // Exiting
			int resp = JOptionPane.showConfirmDialog(null, "Do you really want to quit?", "Quit",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); // Showing a confirm dialog

			if (resp == JOptionPane.YES_OPTION) { // If the response is yes the program quits
				System.exit(0);
			}

		} else if (obj == timer && mp.isActive()) { // Timer for controlling the progressbar
			mp.refresh();
			progress.setMaximum((int) mp.getLength()); // Setting the maximum value for the progressbar
			progress.setValue((int) mp.getPosition()); // Setting the current value
			long pos_min = TimeUnit.MICROSECONDS.toMinutes(mp.getPosition()); // Getting the position in minutes
			long len_min = TimeUnit.MICROSECONDS.toMinutes(mp.getLength()); // Getting the full length in minutes
			long pos_sec = TimeUnit.MICROSECONDS.toSeconds(mp.getPosition()); // Getting the position in seconds
			long len_sec = TimeUnit.MICROSECONDS.toSeconds(mp.getLength()); // Getting the full length in seconds
			if (pos_min == len_min && pos_sec == len_sec) { // If the clip's end is reached the timer resets back to
				// zero
				time.setText("00:00 / 00:00");
			}
			if (pos_sec % 60 < 10) { // Displaying the current time with proper formatting
				time.setText(pos_min % 60 + ":0" + pos_sec % 60 + " / " + len_min % 60 + ":" + len_sec % 60);
			} else if (len_sec % 60 < 10) {
				time.setText(pos_min % 60 + ":" + pos_sec % 60 + " / " + len_min % 60 + ":0" + len_sec % 60);
			} else {
				time.setText(pos_min % 60 + ":" + pos_sec % 60 + " / " + len_min % 60 + ":" + len_sec % 60);
			}
		} else if (obj == credits) { // Credits window
			JOptionPane.showMessageDialog(null, "January 2020\nSimon Vargha", "Credits", JOptionPane.PLAIN_MESSAGE);
		} else if (obj == forward) { // Forwarding the clip
			mp.forward();
		} else if (obj == reverse) { // Reversing the clip
			mp.reverse();
		}

	}

	public void windowClosing(WindowEvent e) { // Listening for X press / alt+f4
		int resp = JOptionPane.showConfirmDialog(null, "Do you really want to quit?", "Quit", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE); // Confirmation dialog

		if (resp == JOptionPane.YES_OPTION) {
			System.exit(0); // Quitting
		}
	}

	// Unnecessary but mandatory event listeners
	public void windowOpened(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

}
