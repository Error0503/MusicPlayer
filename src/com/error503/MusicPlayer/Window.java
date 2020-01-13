package com.error503.MusicPlayer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

public class Window extends JFrame implements ActionListener {

	private JFrame f = new JFrame();
	
	private JMenuBar bar = new JMenuBar();

	private JMenu file = new JMenu("File");
	private JMenu window = new JMenu("Window");

	private JMenuItem open = new JMenuItem("Open...");
	private JMenuItem credits = new JMenuItem("Credits");
	private JMenuItem exit = new JMenuItem("Exit");

	private JButton play = new JButton();
	private JButton pause = new JButton();
	private JButton stop = new JButton();
	private JButton reverse = new JButton();
	private JButton forward = new JButton();

	private JLabel time = new JLabel(" ", JLabel.CENTER);

	private JProgressBar progress = new JProgressBar(JProgressBar.HORIZONTAL, 0);

	private Timer timer;
	private MusicPlayer mp;

	Window() {

		try {
			Image play_image = ImageIO.read(this.getClass().getResource("resources/play.png"));
			ImageIcon play_icon = new ImageIcon(play_image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

			Image pause_image = ImageIO.read(this.getClass().getResource("resources/pause.png"));
			ImageIcon pause_icon = new ImageIcon(pause_image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

			Image stop_image = ImageIO.read(this.getClass().getResource("resources/stop.png"));
			ImageIcon stop_icon = new ImageIcon(stop_image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

			Image rev_image = ImageIO.read(this.getClass().getResource("resources/backward.png"));
			ImageIcon rev_icon = new ImageIcon(rev_image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

			Image forw_image = ImageIO.read(this.getClass().getResource("resources/forward.png"));
			ImageIcon forw_icon = new ImageIcon(forw_image.getScaledInstance(50, 50, Image.SCALE_SMOOTH));

			play.setIcon(play_icon);
			pause.setIcon(pause_icon);
			stop.setIcon(stop_icon);
			reverse.setIcon(rev_icon);
			forward.setIcon(forw_icon);

		} catch (IOException e) {
			e.printStackTrace();
		}

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		file.add(open);

		window.add(credits);
		window.add(exit);

		bar.add(file);
		bar.add(window);

		setJMenuBar(bar);

		open.addActionListener(this);
		credits.addActionListener(this);
		exit.addActionListener(this);

		play.addActionListener(this);
		pause.addActionListener(this);
		stop.addActionListener(this);
		reverse.addActionListener(this);
		forward.addActionListener(this);

		progress.setPreferredSize(new Dimension(150, 30));

		play.setPreferredSize(new Dimension(50, 50));
		pause.setPreferredSize(new Dimension(50, 50));
		stop.setPreferredSize(new Dimension(50, 50));
		reverse.setPreferredSize(new Dimension(50, 50));
		forward.setPreferredSize(new Dimension(50, 50));

		play.setMultiClickThreshhold(1L);
		pause.setMultiClickThreshhold(1L);
		stop.setMultiClickThreshhold(1L);
		reverse.setMultiClickThreshhold(1L);
		forward.setMultiClickThreshhold(1L);

		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(10, 10, 10, 10);
		add(reverse, c);

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(10, 10, 10, 10);
		add(play, c);

		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(10, 10, 10, 10);
		add(pause, c);

		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(10, 10, 10, 10);
		add(stop, c);

		c.gridx = 4;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(10, 10, 10, 10);
		add(forward, c);

		c.weightx = 0;
		c.gridwidth = 5;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5, 5, 5, 5);
		add(progress, c);

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.weighty = 0;
		c.gridheight = 0;
		c.gridwidth = 0;
		c.insets = new Insets(0, 0, 0, 0);
		add(time, c);

		play.setEnabled(false);
		pause.setEnabled(false);
		stop.setEnabled(false);
		reverse.setEnabled(false);
		forward.setEnabled(false);

		pack();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);

		timer = new Timer(100, this);
		timer.setRepeats(true);

		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (getSize().width / 2),
				(Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (getSize().height / 2));

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();

		if (obj == open) {

			JFileChooser chooser = new JFileChooser();

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
						if (extension.equals(Utils.wav) || extension.equals(Utils.mp3)) {
							return true;
						} else {
							return false;
						}
					}

					return false;
				}
			});

			chooser.showOpenDialog(f);

			try {
				mp = new MusicPlayer(chooser.getSelectedFile().getAbsolutePath());
				play.setEnabled(true);
				pause.setEnabled(true);
				stop.setEnabled(true);
				reverse.setEnabled(true);
				forward.setEnabled(true);
			} catch (IOException | LineUnavailableException err) {
				err.printStackTrace();
			}

			timer.start();

		} else if (obj == play) {
			mp.start();
		} else if (obj == pause) {
			mp.pause();
		} else if (obj == stop) {
			play.setEnabled(false);
			pause.setEnabled(false);
			stop.setEnabled(false);
			reverse.setEnabled(false);
			forward.setEnabled(false);
			time.setText("--:-- / --:--");
			mp.stop();
		} else if (obj == exit) {
			int resp = JOptionPane.showConfirmDialog(null, "Do you really want to quit?", "Quit",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (resp == JOptionPane.YES_OPTION) {
				System.exit(0);
			}

		} else if (obj == timer && mp.isActive()) {
			mp.refresh();
			progress.setMaximum((int) mp.getLenght());
			progress.setValue((int) mp.getPosition());
			long pos_min = TimeUnit.MICROSECONDS.toMinutes(mp.getPosition());
			long len_min = TimeUnit.MICROSECONDS.toMinutes(mp.getLenght());
			long pos_sec = TimeUnit.MICROSECONDS.toSeconds(mp.getPosition());
			long len_sec = TimeUnit.MICROSECONDS.toSeconds(mp.getLenght());
			if (pos_sec % 60 < 10) {
				time.setText(pos_min % 60 + ":0" + pos_sec % 60 + " / " + len_min % 60 + ":" + len_sec % 60);
			} else if (len_sec % 60 < 10) {
				time.setText(pos_min % 60 + ":" + pos_sec % 60 + " / " + len_min % 60 + ":0" + len_sec % 60);
			} else {
				time.setText(pos_min % 60 + ":" + pos_sec % 60 + " / " + len_min % 60 + ":" + len_sec % 60);
			}
		} else if (obj == credits) {
			JOptionPane.showMessageDialog(null, "January 2020\nSimon Vargha", "Credits", JOptionPane.PLAIN_MESSAGE);
		} else if (obj == forward) {
			mp.forward();
		} else if (obj == reverse) {
			mp.reverse();
		}

	}

}