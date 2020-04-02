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
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Host extends JFrame implements ActionListener {

	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private DataOutputStream dos = null;
	private ServerSocket server = null;
	private Socket socket = null;
	private int port;
	private String path;
	private Thread t;
	private MusicPlayer mp;
	private JLabel label = new JLabel("Starting server");

	private JButton close = new JButton("Close");

	public Host(String path, MusicPlayer mp) {
		this.path = path;
		this.mp = mp;

		setLayout(new GridLayout(2, 1));

		add(label);
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
			} catch (IOException er) {}
			System.out.println("Disconnected");
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} else {
			label.setText("Your server is open on port " + String.valueOf(port));
			pack();
			revalidate();
			repaint();
		}
	}

	public void Start() throws IOException {
		new ActionEvent(new JButton(), 0, "refresh");

		server = new ServerSocket(0);
		port = server.getLocalPort();

		try {
			System.out.println("Listening on " + port);
			socket = server.accept();
			System.out.println("Accepted connection: " + socket);
			File send = new File(path);
			byte[] array = new byte[(int) send.length()];
			fis = new FileInputStream(send);
			bis = new BufferedInputStream(fis);
			bis.read(array);
			dos = new DataOutputStream(socket.getOutputStream());
			System.out.println("Sending " + path);
			dos.write(array);
			dos.flush();
			System.out.println("Sent");

			System.out.println("Sending " + mp.getPosition());
			dos.writeLong(mp.getPosition());
			dos.flush();
			System.out.println("Sent");
		} catch (SocketException e) {
			JOptionPane.showMessageDialog(null, "Connection lost", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
