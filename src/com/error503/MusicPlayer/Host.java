package com.error503.MusicPlayer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Host extends JFrame implements ActionListener {

	private BufferedInputStream bis = null;
	private DataOutputStream dos = null;
	private ServerSocket server = null;
	private Socket socket = null;
	private int port;
	private String path;
	private Thread t;

	private JButton close = new JButton("Close");

	public Host(String path, MusicPlayer mp) {
		this.path = path;

		try {
			server = new ServerSocket(0);
			port = server.getLocalPort();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setLayout(new GridLayout(2, 1));

		add(new JLabel("Your server is open on port " + String.valueOf(port)));
		add(close);

		t = new Thread(new Runnable() {
			public void run() {
				try {
					Start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();

		close.addActionListener(this);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				dispose();
			}
		});

		pack();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == close) {
			try {
				if (t.isAlive())
					t.interrupt();
				if (bis != null)
					bis.close();
				if (dos != null)
					dos.close();
				if (socket != null)
					socket.close();
				if (server != null)
					server.close();
			} catch (IOException er) {
			}
			System.out.println("Disconnected");
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	public void Start() throws IOException {
		
		File f = new File(path);
		bis = new BufferedInputStream(new FileInputStream(f));
		byte[] data = new byte[(int) f.length()];
		
		bis.read(data);
		
		socket = server.accept();
		System.out.println(socket + " connected");
		
		dos = new DataOutputStream(socket.getOutputStream());
		System.out.println("Sending data");
		Logger log = Logger.getLogger(this.getClass().getName());
		log.log(Level.INFO, "Sending data");
		dos.write(data);
		dos.flush();
		System.out.println("Data sent");
	}
}
