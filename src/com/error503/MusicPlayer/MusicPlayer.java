package com.error503.MusicPlayer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class MusicPlayer {

	Clip clip;

	AudioInputStream audioInputStream;
	String filePath;

	long lenght;
	long position;

	int loop;

	public MusicPlayer(String filePath) throws LineUnavailableException, IOException {

		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}

		clip = AudioSystem.getClip();

		clip.open(audioInputStream);

		clip.start();
		lenght = clip.getMicrosecondLength();
	}

	public void start() {
		clip.start();
	}

	public void pause() {
		clip.stop();
	}

	public void stop() {
		clip.stop();
		clip.close();
	}

	public long getPosition() {
		return position;
	}

	public long getLenght() {
		return lenght;
	}

	public boolean isActive() {
		return clip.isOpen();
	}

	public boolean isPlaying() {
		return clip.isRunning();
	}

	public void toggleRepeat() {
		if (loop == Clip.LOOP_CONTINUOUSLY) {
			loop = 0;
		} else {
			loop = Clip.LOOP_CONTINUOUSLY;
		}
		clip.loop(loop);
	}

	public void refresh() {
		new Thread(new Runnable() {
			public void run() {
				if (clip.getMicrosecondPosition() > lenght) {
					position = clip.getMicrosecondPosition() - clip.getMicrosecondLength();
				} else {
					position = clip.getMicrosecondPosition();
				}
			}
		}).start();
	}

	public void reverse() {
		clip.setMicrosecondPosition(position - TimeUnit.SECONDS.toMicros(10));
	}

	public void forward() {
		clip.setMicrosecondPosition(position + TimeUnit.SECONDS.toMicros(10));
	}

}