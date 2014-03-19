package com.assignment1.paybyurl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MP3playActivity extends Activity {
	static final String file_url = "http://cs1-29v4.vk.me/p33/27d44612a54fe5.mp3";
	static final String file_path_name = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
			.getPath()
			+ "/Trololo.mp3";
	MediaPlayer mediaPlayer;
	public ProgressBar myProgressBar;
	private ToggleButton btnPlayMusic;
	private MediaPlayer mPlayer;
	private ProgressDialog prgDialog;
	public static final int progress_bar_type = 0;
	File file;
	TextView tx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_mp3_play);
		btnPlayMusic = (ToggleButton) findViewById(R.id.toggleButton1);
		tx = (TextView) findViewById(R.id.textView1);
		tx.setText("Idle");
		btnPlayMusic.setChecked(false);
		file = new File(file_path_name);
		if (file.exists()) {
			Toast.makeText(getApplicationContext(),
					"File already exist under SD card, playing Music",
					Toast.LENGTH_LONG).show();
			playMusic();
		} else {
			Toast.makeText(
					getApplicationContext(),
					"File doesn't exist under SD Card, downloading Mp3 from Internet",
					Toast.LENGTH_LONG).show();
			new DownloadMusicfromInternet().execute(file_url);
		}
		btnPlayMusic.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (mPlayer.isPlaying()) {
					mPlayer.pause();
					btnPlayMusic.setChecked(false);
				} else {
					mPlayer.start();
					btnPlayMusic.setChecked(true);
				}
			}
		});
	}

	@Override
	public void finish() {
		super.finish();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mPlayer.stop();
		File file = new File(file_path_name);
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		prgDialog = new ProgressDialog(this);
		prgDialog.setMessage("Downloading Mp3 file. \nPlease wait...");
		prgDialog.setIndeterminate(false);
		prgDialog.setMax(100);
		prgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		prgDialog.setCancelable(false);
		prgDialog.show();
		return prgDialog;
	}

	// Async Task Class
	class DownloadMusicfromInternet extends AsyncTask<String, String, String> {

		// Show Progress bar before downloading Music
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tx.setText("Idle");
			showDialog(0);
		}

		// Download Music File from Internet
		@Override
		protected String doInBackground(String... f_url) {
			int count;
			tx.setText("Download");
			try {
				URL url = new URL(f_url[0]);
				URLConnection conection = url.openConnection();
				conection.connect();
				int lenghtOfMP3File = conection.getContentLength();
				InputStream input = new BufferedInputStream(url.openStream(),
						10 * 1024);

				File file = new File(file_path_name);
				file.createNewFile();
				FileOutputStream output = new FileOutputStream(file);


				byte data[] = new byte[1024];
				long total = 0;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress(""
							+ (int) ((total * 100) / lenghtOfMP3File));
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}
			return null;
		}

		protected void onProgressUpdate(String... progress) {
			prgDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String file_url) {
			tx.setText("Complete");
			dismissDialog(progress_bar_type);
			Toast.makeText(getApplicationContext(),
					"Download complete, playing Music", Toast.LENGTH_LONG)
					.show();
			playMusic();
		}
	}

	protected void playMusic() {
		btnPlayMusic.setEnabled(true);
		btnPlayMusic.setChecked(true);
		Uri myUri1 = Uri.parse(file_path_name);
		mPlayer = new MediaPlayer();

		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mPlayer.setDataSource(getApplicationContext(), myUri1);
			mPlayer.prepare();
			mPlayer.start();
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					Toast.makeText(getApplicationContext(),
							"Music completed playing", Toast.LENGTH_LONG)
							.show();
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