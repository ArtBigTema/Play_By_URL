package com.assignment1.playbyurl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.assignment1.paybyurl.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MP3playActivity extends Activity {
	// static final String FILE_URL = "http://cs1-29v4.vk.me/p33/27d44612a54fe5.mp3";
	static final String FILE_PATH_NAME = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
			.getPath()
			+ "/jai_ho.mp3";
	private static String FILE_URL = "http://android.programmerguru.com/wp-content/uploads/2014/01/jai_ho.mp3";
	static final String MUSIC_SAVE_START_TIME = "musicStartTime";
	private static int musicStartTime = 0;
	private ToggleButton btnPlayPauseMusic;
	private MediaPlayer musicPlayer;
	private ProgressDialog prgDialogDownloadMusic;
	public static TextView statusOfFileTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.a_mp3_download_play);
		btnPlayPauseMusic = (ToggleButton) findViewById(R.id.btn_play_pause_music);
		statusOfFileTextView = (TextView) findViewById(R.id.status_of_file_textview);
		statusOfFileTextView.setText(R.string.idle);
		btnPlayPauseMusic.setChecked(false);
		File fileMusic = new File(FILE_PATH_NAME);
		if (savedInstanceState != null) {
			musicStartTime = savedInstanceState.getInt(MUSIC_SAVE_START_TIME);
		}
		if (fileMusic.exists()) {
			btnPlayPauseMusic.setEnabled(true);
		} else {
			Toast.makeText(
					getApplicationContext(),
					"File doesn't exist under SD Card, downloading Mp3 from Internet",
					Toast.LENGTH_LONG).show();
			new DownloadMusicfromInternet().execute(FILE_URL);
		}

		btnPlayPauseMusic.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (btnPlayPauseMusic.isChecked()) {
					if (musicPlayer == null) {
						playMusicFile();
					}
					statusOfFileTextView.setText(R.string.playing);
					musicPlayer.seekTo(musicStartTime);
					musicPlayer.start();
					btnPlayPauseMusic.setChecked(true);
				} else {
					statusOfFileTextView.setText(R.string.pausing);
					musicPlayer.pause();
					btnPlayPauseMusic.setChecked(false);
				}
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		musicPlayer.stop();
		File file = new File(FILE_PATH_NAME);
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		musicPlayer.stop();
		File file = new File(FILE_PATH_NAME);
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle SaveInstance) {
		super.onSaveInstanceState(SaveInstance);
		if (musicPlayer != null) {
			musicPlayer.pause();
			SaveInstance.putInt(MUSIC_SAVE_START_TIME,
					musicPlayer.getCurrentPosition());
			musicPlayer.stop();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		prgDialogDownloadMusic = new ProgressDialog(this);
		prgDialogDownloadMusic
				.setMessage("Downloading Mp3 file. \nPlease wait...");
		prgDialogDownloadMusic.setIndeterminate(false);
		prgDialogDownloadMusic.setMax(100);
		prgDialogDownloadMusic
				.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		prgDialogDownloadMusic.setCancelable(false);
		prgDialogDownloadMusic.show();
		return prgDialogDownloadMusic;
	}

	class DownloadMusicfromInternet extends AsyncTask<String, String, String> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			statusOfFileTextView.setText(R.string.downloading);
			showDialog(0);
		}

		@Override
		protected String doInBackground(String... f_url) {
			int count;
			try {
				URL url = new URL(f_url[0]);
				URLConnection conection = url.openConnection();
				conection.connect();
				int lenghtOfMP3File = conection.getContentLength();
				InputStream inputURLFile = new BufferedInputStream(
						url.openStream(), 10 * 1024);

				File fileMusic = new File(FILE_PATH_NAME);
				fileMusic.createNewFile();
				FileOutputStream outputMusicFile = new FileOutputStream(
						fileMusic);

				byte data[] = new byte[1024];
				long total = 0;
				while ((count = inputURLFile.read(data)) != -1) {
					total += count;
					publishProgress(""
							+ (int) ((total * 100) / lenghtOfMP3File));
					outputMusicFile.write(data, 0, count);
				}
				outputMusicFile.flush();
				outputMusicFile.close();
				inputURLFile.close();
			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}
			return null;
		}

		protected void onProgressUpdate(String... progress) {
			prgDialogDownloadMusic.setProgress(Integer.parseInt(progress[0]));
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String file_url) {
			statusOfFileTextView.setText(R.string.complete);
			dismissDialog(0);
			Toast.makeText(getApplicationContext(),
					"Download complete, playing Music", Toast.LENGTH_LONG)
					.show();
			btnPlayPauseMusic.setEnabled(true);
		}
	}

	protected void playMusicFile() {
		btnPlayPauseMusic.setEnabled(true);
		btnPlayPauseMusic.setChecked(false);
		musicPlayer = new MediaPlayer();
		musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			musicPlayer.setDataSource(getApplicationContext(),
					Uri.parse(FILE_PATH_NAME));
			musicPlayer.prepare();
			// musicPlayer.start();
			musicPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					Toast.makeText(getApplicationContext(),
							"Music completed playing", Toast.LENGTH_LONG)
							.show();
					btnPlayPauseMusic.setChecked(false);
				}
			});
		} catch (IllegalArgumentException e) {
			Toast.makeText(getApplicationContext(),
					"You might not set the URI correctly!", Toast.LENGTH_LONG)
					.show();
		} catch (SecurityException e) {
			Toast.makeText(getApplicationContext(),
					"URI cannot be accessed, permissed needed",
					Toast.LENGTH_LONG).show();
		} catch (IllegalStateException e) {
			Toast.makeText(getApplicationContext(),
					"Media Player is not in correct state", Toast.LENGTH_LONG)
					.show();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "IO Error occured",
					Toast.LENGTH_LONG).show();
		}
	}
}