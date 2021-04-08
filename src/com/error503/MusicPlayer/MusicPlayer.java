package com.error503.MusicPlayer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


// This class plays the music and controls it's behaviour.

public class MusicPlayer {

	// The clip that will be played later
	// Was put here to be accessible globally.
	private Clip clip;
	private AudioInputStream audioInputStream;

	// The length and position of the opened file in microseconds
	private long length;
	private long position;

	// Loop count
	// Set for -1 as infinite or 0 for no loop
	private int loop;

	// Constructor that receives the selected file's path
	public MusicPlayer(String filePath) throws LineUnavailableException, IOException {
		init(filePath);
	}

	public void init(String filePath) throws LineUnavailableException, IOException {
		// Opening the file as an input stream
		// No need of getResourceAsStream() because it's not a resource stored in the jar
		
		if (clip != null) kill();
		
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
		} catch (Exception e) {
			Logger.getLogger(Server.class.getName()).log(Level.INFO, "File of unsupported format");
		}

		// clip get's the selected file as a clip from AudioSystem
		clip = AudioSystem.getClip();

		// The stream is opened
		clip.open(audioInputStream);

		// and started
		clip.start();
		// The length of the sound file is stored
		length = clip.getMicrosecondLength();
	}

	// Re-starting the clip
	public void start() {
		clip.start();
	}

	// Pausing
	public void pause() {
		clip.stop();
	}

	// Stopping the clip entirely
	public void stop() {
		clip.stop();
		clip.close();
		clip.flush();
	}

	// Resets all variables so a new file can be played
	public void kill() throws IOException {
		clip.close();
		clip.flush();
		clip.drain();
		audioInputStream.skip(audioInputStream.available());
		length = 0L;
		position = 0L;
	}

	// Get method of position
	public long getPosition() {
		return position;
	}

	public void setPosition(long pos) {
		clip.setMicrosecondPosition(pos);
	}

	// Get method of length
	public long getLength() {
		return length;
	}

	// Get method for checking if the clip is opened
	public boolean isActive() {
		return clip.isOpen();
	}

	// Get method for checking if the clip is being played
	public boolean isPlaying() {
		return clip.isRunning();
	}

	// Control of looping
	public void toggleRepeat() {
		// If looping is on it's set to off and vice versa
		if (loop == Clip.LOOP_CONTINUOUSLY) {
			loop = 0;
		} else {
			loop = Clip.LOOP_CONTINUOUSLY;
		}
	}

	// Updating the position
	public void refresh() {
		new Thread(() -> {
			// Looping the clip caused the position to overflow the length so it is checked
			if (clip.getMicrosecondPosition() == length && loop == Clip.LOOP_CONTINUOUSLY) {
				clip.setMicrosecondPosition(0);
				clip.start();
			} else {
				position = clip.getMicrosecondPosition();
			}
		}).start();
	}

	// Method reverses 10 seconds
	public void reverse() {
		clip.setMicrosecondPosition(position - TimeUnit.SECONDS.toMicros(10));
	}

	// Method forwards 10 seconds
	public void forward() {
		clip.setMicrosecondPosition(position + TimeUnit.SECONDS.toMicros(10));
	}

	public void setVolume(float value) {
		try {
			FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			fc.setValue(value);
		} catch (IllegalArgumentException ignored) {
		}
	}
}
