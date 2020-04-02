package com.error503.MusicPlayer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Connect extends JFrame implements ActionListener {

	private JButton client = new JButton("Client");
	private JButton host = new JButton("Host");
	private String path;
	private MusicPlayer mp;

	public Connect(String path, MusicPlayer mp) {
		this.path = path;
		this.mp = mp;

		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		client.setSize(70, 40);
		client.setMinimumSize(new Dimension(70, 40));
		client.setPreferredSize(new Dimension(70, 40));
		client.setMaximumSize(new Dimension(70, 40));

		host.setSize(70, 40);
		host.setMinimumSize(new Dimension(70, 40));
		host.setPreferredSize(new Dimension(70, 40));
		host.setMaximumSize(new Dimension(70, 40));

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		add(new JLabel("Select mode", JLabel.CENTER), c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		add(client, c);

		c.gridx = 1;
		c.gridy = 1;
		add(host, c);

		client.addActionListener(this);
		host.addActionListener(this);

		pack();
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();

		if (obj == client) {
			do {
				try {
					int port = 0;
					String in = JOptionPane.showInputDialog(null, "Port", "Connect to host",
							JOptionPane.QUESTION_MESSAGE);
					if (in.length() < 6) {
						port = Integer.parseInt(in);
						new Client(port, mp);
						dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
						break;
					} else {
						JOptionPane.showMessageDialog(null, "Port number can not exceed 6 characters!", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "Please input a port number", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} while (true);
		} else if (obj == host) {
			new Thread(new Runnable() {
				public void run() {
					new Host(path, mp);
				}
			}).start();
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

}
