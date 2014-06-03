package com.example.wifiapp;

import java.util.List;

import org.codeandmagic.android.gauge.GaugeView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SpeedometerActivity extends Activity {

	long id;

	Handler h;

	DownloadManager manager;

	Long startTime;

	int check = 0;

	String uri;

	private GaugeView mGaugeView1;
	private GaugeView mGaugeView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.speedometer);
		mGaugeView1 = (GaugeView) findViewById(R.id.gauge_view1);
		mGaugeView2 = (GaugeView) findViewById(R.id.gauge_view2);

	}

	@Override
	protected void onResume() {

		super.onResume();

		h = new Handler();

		new speedTask().execute();

	}

	public class speedTask extends AsyncTask<Void, String, Void> {

		protected void onPreExecute() {

		}

		protected void onProgressUpdate(final String... message) {

			try {

			} catch (Exception e) {

				e.printStackTrace();

			}

		}

		@Override
		protected Void doInBackground(Void... params) {

			try {

			} catch (Exception e) {

				e.printStackTrace();

			}

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {

			// TODO Auto-generated method stub

			super.onPostExecute(result);

			try {

				downloadFile("testFile",
				// "https://s3.amazonaws.com/TranscodeAppVideos2/macdonald3.mp4");

						// "http://ia600803.us.archive.org/17/items/MickeyMouse-RunawayTrain/Film-42.mp4");
						"http://www.gregbugaj.com/wp-content/uploads/2009/03/dummy.txt");

				registerReceiver(broadcast, new IntentFilter(

				DownloadManager.ACTION_DOWNLOAD_COMPLETE));

				startTime = System.currentTimeMillis();

				check = 1;

			} catch (Exception e) {

				e.printStackTrace();

			}

		}

	}

	@SuppressLint("NewApi")
	public void downloadFile(String fileName, String downloadUrl) {

		String DownloadUrl = downloadUrl;

		DownloadManager.Request request = new DownloadManager.Request(

		Uri.parse(DownloadUrl));// .replace("https://", "http://")));

		request.setDescription("Downloading..."); // appears the same in

		// Notification bar

		// while downloading

		request.setTitle(fileName);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			request.allowScanningByMediaScanner();

			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

		}

		// Environment.getExternalStorageDirectory().
		// request.
		request.setDestinationInExternalPublicDir("/0/", fileName);

		request.setVisibleInDownloadsUi(true);

		// get download service and enqueue file

		manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

		id = manager.enqueue(request);

		try {

			final Runnable r2 = new Runnable() {

				public void run() {

					h.postDelayed(this, 0000);

					DownloadManager.Query q = new DownloadManager.Query();

					q.setFilterById(id);

					Cursor cursor = manager.query(q);

					cursor.moveToFirst();

					Integer bytes_downloaded = cursor

							.getInt(cursor

									.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));

					Long endTime = System.currentTimeMillis();

					Long toSeconds = (endTime - startTime);

					Long downloadedLength = Long.parseLong(bytes_downloaded

					.toString());

					Long tokB = (downloadedLength) / toSeconds;

					Long percentage = (downloadedLength / 11405) / 10;

					uri = cursor

					.getString(cursor

					.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));

					Integer totalLength = cursor
							.getInt(cursor
									.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

					if (downloadedLength < totalLength) {

						try {

							mGaugeView1.setTargetValue(tokB);
							mGaugeView2.setTargetValue(tokB);

						} catch (Exception e) {

						}

					}

					cursor.close();

				}

			};

			h.postDelayed(r2, 0000);

		} catch (Exception e) {

			Toast.makeText(getBaseContext(), "try again", 3000).show();

		}

	}

	public boolean isDownloadManagerAvailable(Context context) {

		try {

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {

				return false;

			}

			Intent intent = new Intent(Intent.ACTION_MAIN);

			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			intent.setClassName("com.android.providers.downloads.ui",

			"com.android.providers.downloads.ui.DownloadList");

			List<ResolveInfo> list = context.getPackageManager()

			.queryIntentActivities(intent,

			PackageManager.MATCH_DEFAULT_ONLY);

			return list.size() > 0;

		} catch (Exception e) {

			return false;

		}

	}

	BroadcastReceiver broadcast = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

				Long endTime = System.currentTimeMillis();

				Long toSeconds = (endTime - startTime);

				Long tokB = 11405 / (toSeconds / 1000);
//				mGaugeView1.setTargetValue(tokB);
//				mGaugeView2.setTargetValue(tokB);

			}

		}

	};

	@Override
	public void onDestroy() {

		if (check == 1)

			unregisterReceiver(broadcast);

		super.onDestroy();

	}

}
