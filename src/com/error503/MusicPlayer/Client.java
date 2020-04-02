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
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Client extends JFrame implements ActionListener {

	private int PORT = 0;
	private final static String SERVER = "localhost";
	private final static String RECIEVE_PATH = javax.swing.filechooser.FileSystemView.getFileSystemView()
			.getHomeDirectory().getPath() + "\\recieve.wav";
	private int size = 100000000;
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	private Socket socket = null;
	private DataInputStream dis = null;
	private Thread t = null;
	private JLabel label = new JLabel();
	private JButton close = new JButton("Close");
	private MusicPlayer mp;

	public Client(int port, MusicPlayer mp) {
		PORT = port;
		this.mp = mp;

		label.setText("Connecting to: " + PORT);

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
				if (fos != null) fos.close();
				if (bos != null) bos.close();
				if (socket != null) socket.close();
				if (dis != null) dis.close();
			} catch (IOException er) {}
			System.out.println("Disconnected");
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	public void Start() throws IOException {
		try {
			System.out.println("Connecting...");
			socket = new Socket(SERVER, PORT);

			System.out.println("Connected to " + socket.getInetAddress());

			// receive file
			dis = new DataInputStream(socket.getInputStream());
			fos = new FileOutputStream(RECIEVE_PATH);
			bos = new BufferedOutputStream(fos);
			byte[] array = new byte[size];
			dis.read(array);
			System.out.println(array);
			bos.write(array);
			bos.flush();
			
			System.out.println("File downloaded");

			System.out.println("Reading");
			long pos = dis.readLong();
			System.out.println("Red" + pos);
			mp.setPosition(pos);
		} catch (ConnectException e) {
			JOptionPane.showMessageDialog(null, "Couldn't connect to host", "Error", JOptionPane.ERROR_MESSAGE);
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}
}
