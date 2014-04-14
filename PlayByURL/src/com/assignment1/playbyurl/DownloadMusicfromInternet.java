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

public class DownloadMusicfromInternet extends
		AsyncTask<String, String, String> {
	private ProgressDialog prgDialogDownloadMusic;
	private FileOutputStream outputMusicFileStream;
	@SuppressWarnings("unused")
	private Context context;
	public ListenerOnCompleteDownload listenerDownloadFile;
	private boolean sizeIsKnown;
	private boolean downloadError = false;

	public void setListener(ListenerOnCompleteDownload listener) {
		this.listenerDownloadFile = listener;
	}

	public DownloadMusicfromInternet(Context context) {
		this.context = context;

		prgDialogDownloadMusic = new ProgressDialog(context);
		prgDialogDownloadMusic
				.setMessage("Downloading Mp3 file. \nPlease wait...");
		prgDialogDownloadMusic
				.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		prgDialogDownloadMusic.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
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
					conection.getInputStream(), 10 * 1024);
			File fileMusic = new File(param[1]);
			outputMusicFileStream = new FileOutputStream(fileMusic,
					fileMusic.createNewFile());
			int count;
			byte data[] = new byte[1024];
			long total = 0;
			if (lengthOfMP3File != -1) {// with status
				prgDialogDownloadMusic.setIndeterminate(false);
				prgDialogDownloadMusic.setMax(100);
				sizeIsKnown = true;
			} else {
				prgDialogDownloadMusic.setIndeterminate(true);
				lengthOfMP3File = 1;
				sizeIsKnown = false;
			}
			while ((count = inputURLMP3File.read(data)) != -1) {
				total += count;
				publishProgress("" + (int) ((total * 100) / lengthOfMP3File));
				outputMusicFileStream.write(data, 0, count);
			}

			outputMusicFileStream.flush();
			outputMusicFileStream.close();
			inputURLMP3File.close();
		} catch (Exception e) {
			downloadError = true;
			Log.e("Error: ", e.getMessage());
		}
		return null;
	}

	protected void onProgressUpdate(String... progress) {
		if (sizeIsKnown) {
			prgDialogDownloadMusic.setProgress(Integer.parseInt(progress[0]));
		} else {
			prgDialogDownloadMusic.setProgressNumberFormat(progress[0]);
		}
	}

	@Override
	protected void onPostExecute(String file_url) {
		prgDialogDownloadMusic.dismiss();
		prgDialogDownloadMusic.hide();
		if (!downloadError) {
			listenerDownloadFile.doFinalActions();
		} else {
			listenerDownloadFile
					.doErrorActions("Error\n" + "URL isn't correct");
		}
	}

	public static interface ListenerOnCompleteDownload {
		public void doFinalActions();

		public void doErrorActions(String message);
	}
}
