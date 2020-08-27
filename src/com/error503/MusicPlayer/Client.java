package com.error503.MusicPlayer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Client extends JFrame implements ActionListener {

	private int port = 0;
	private final static String RECEIVE_PATH = javax.swing.filechooser.FileSystemView.getFileSystemView()
			.getHomeDirectory().getPath() + "\\recieve.wav";
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	private Socket socket = null;
	private DataInputStream dis = null;
	private Thread t = null;
	private JButton close = new JButton("Close");

	public Client(int port, MusicPlayer mp) {
		this.port = port;

		JLabel label = new JLabel();
		label.setText("Connecting to: " + port);

		setLayout(new GridLayout(2, 1));

		close.addActionListener(this);

		add(label);
		add(close);

		t = new Thread(new Runnable() {
			public void run() {
				try {
					Start();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "IOException", "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});
		t.start();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				dispose();
			}
		});

		pack();
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == close) {
			if (t.isAlive())
				t.interrupt();
			try {
				if (fos != null)
					fos.close();
				if (bos != null)
					bos.close();
				if (socket != null)
					socket.close();
				if (dis != null)
					dis.close();
			} catch (IOException er) {
			}
			System.out.println("Disconnected");
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	public void Start() throws IOException {
		
		socket = new Socket("localhost", port);

		// 1GB
		int size = 1000000000;
		byte[] file = new byte[size];
		
		dis = new DataInputStream(socket.getInputStream());
		bos = new BufferedOutputStream(new FileOutputStream(RECEIVE_PATH));
		
		dis.read(file);
		System.out.println(Arrays.toString(file));
		bos.write(file);
		bos.flush();
	}
}
