package com.assignment1.playbyurl;

import com.assignment1.playbyurl.R;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MP3playActivity extends Activity implements
		DownloadMusicfromInternet.ListenerOnCompleteDownload {
	public static final String FILE_PATH_NAME = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
			.getPath()
			+ "/ja_ho.mp3";
	public static final String FILE_URL = "http://cs1-51v4.vk.me/p8/4109717d62ef07.mp3";// "http://android.programmerguru.com/wp-content/uploads/2014/01/jai_ho.mp3";
	private ToggleButton btnPlayPauseMusic;
	private MediaPlayer musicPlayer;
	private TextView statusOfFileTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.a_mp3_download_play);

		btnPlayPauseMusic = (ToggleButton) findViewById(R.id.btn_play_pause_music);
		statusOfFileTextView = (TextView) findViewById(R.id.status_of_file_textview);
		statusOfFileTextView.setText(R.string.idle);
		if (!isOnline()) {
			doErrActions("Error. Turn on wifi & Restart app, please");
		} else {
			DownloadMusicfromInternet downloadMusic = (DownloadMusicfromInternet) new DownloadMusicfromInternet(
					this).execute(FILE_URL, FILE_PATH_NAME);
			downloadMusic.setListener(this);
			statusOfFileTextView.setText(R.string.downloading);

			btnPlayPauseMusic.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					if (btnPlayPauseMusic.isChecked()) {
						if (musicPlayer == null) {
							playMusicFile();
						}
						musicPlayer.start();
						statusOfFileTextView.setText(R.string.playing);
						btnPlayPauseMusic.setChecked(true);
					} else {
						statusOfFileTextView.setText(R.string.pausing);
						musicPlayer.pause();
						btnPlayPauseMusic.setChecked(false);
					}
				}
			});
		}
	}

	@Override
	public void finish() {
		super.finish();
		if (musicPlayer != null) {
			musicPlayer.stop();
		}
		File file = new File(FILE_PATH_NAME);
		if (file.exists()) {
			file.delete();
		}
	}

	private void playMusicFile() {
		musicPlayer = new MediaPlayer();
		musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			musicPlayer.setDataSource(getApplicationContext(),
					Uri.parse(FILE_PATH_NAME));
			musicPlayer.prepare();
			musicPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					Toast.makeText(getApplicationContext(),
							"Music completed playing", Toast.LENGTH_LONG)
							.show();
					statusOfFileTextView.setText(R.string.complete);
					btnPlayPauseMusic.setChecked(false);
				}
			});
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public void doFinalActions() {
		btnPlayPauseMusic.setEnabled(true);
		statusOfFileTextView.setText(R.string.complete);
	}

	@Override
	public void doErrActions(String message) {
		btnPlayPauseMusic.setEnabled(false);
		statusOfFileTextView.setText(message);
	}
}