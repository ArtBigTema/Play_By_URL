package com.assignment1.playbyurl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.assignment1.playbyurl.R;

public class DownloadMusicfromInternet extends
		AsyncTask<String, String, String> {
	private ProgressDialog prgDialogDownloadMusic;
	private FileOutputStream outputMusicFileStream;
	@SuppressWarnings("unused")
	private Context context;

	public DownloadMusicfromInternet(Context context) {
		this.context = context;

		prgDialogDownloadMusic = new ProgressDialog(context);
		prgDialogDownloadMusic
				.setMessage("Downloading Mp3 file. \nPlease wait...");
		prgDialogDownloadMusic.setIndeterminate(false);
		prgDialogDownloadMusic.setMax(100);
		prgDialogDownloadMusic
				.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		prgDialogDownloadMusic.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		MP3playActivity.statusOfFileTextView.setText(R.string.downloading);
		prgDialogDownloadMusic.show();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		try {
			outputMusicFileStream.flush();
			outputMusicFileStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String doInBackground(String... param) {
		try {
			URL url = new URL(param[0]);
			URLConnection conection = url.openConnection();
			conection.connect();
			int lengthOfMP3File = conection.getContentLength();
			InputStream inputURLMP3File = new BufferedInputStream(
					url.openStream(), 10 * 1024);

			File fileMusic = new File(MP3playActivity.FILE_PATH_NAME);
			if (!fileMusic.exists()) {
				fileMusic.createNewFile();
				outputMusicFileStream = new FileOutputStream(fileMusic, false);
			} else {
				outputMusicFileStream = new FileOutputStream(fileMusic, true);
			}
			inputURLMP3File.skip(lengthOfMP3File);
			int count;
			byte data[] = new byte[1024];
			long total = fileMusic.length();
			while ((count = inputURLMP3File.read(data)) != -1) {
				total += count;
				publishProgress("" + (int) ((total * 100) / lengthOfMP3File));
				outputMusicFileStream.write(data, 0, count);
			}
			outputMusicFileStream.flush();
			outputMusicFileStream.close();
			inputURLMP3File.close();
		} catch (Exception e) {
			Log.e("Error: ", e.getMessage());
		}
		return null;
	}

	protected void onProgressUpdate(String... progress) {
		prgDialogDownloadMusic.setProgress(Integer.parseInt(progress[0]));
	}

	@Override
	protected void onPostExecute(String file_url) {
		prgDialogDownloadMusic.dismiss();
		prgDialogDownloadMusic.hide();
		MP3playActivity.statusOfFileTextView.setText(R.string.complete);
		prgDialogDownloadMusic.setMessage("Download complete, playing Music");
		MP3playActivity.btnPlayPauseMusic.setEnabled(true);
	}
}