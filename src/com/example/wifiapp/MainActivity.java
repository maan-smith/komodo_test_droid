package com.example.wifiapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener,
		OnClickListener {
	WifiManager mainWifiObj;
	WifiScanReceiver wifiReciever;
	ListView list;
	ArrayList<WifiConfig> wifis;
	String wifisName[];
	View mVDataHolder;
	View mVDataLoader;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		list = (ListView) findViewById(R.id.list);

		wifis = new ArrayList<WifiConfig>();

		mVDataHolder = (RelativeLayout) findViewById(R.id.rl_holder);
		mVDataLoader = (LinearLayout) findViewById(R.id.ll_progress_bar);
		mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiReciever = new WifiScanReceiver();
		mainWifiObj.startScan();
		((Button) findViewById(R.id.button1)).setOnClickListener(this);
		if (mainWifiObj.isWifiEnabled() == false)

		{
			mainWifiObj.setWifiEnabled(true);
		}

		list.setOnItemClickListener(this);

	}

	protected void onPause() {
		unregisterReceiver(wifiReciever);
		super.onPause();
	}

	protected void onResume() {
		registerReceiver(wifiReciever, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		super.onResume();
	}

	List<ScanResult> wifiScanList = null;

	class WifiScanReceiver extends BroadcastReceiver {
		@SuppressLint("UseValueOf")
		public void onReceive(Context c, Intent intent) {
			mVDataHolder.setVisibility(View.VISIBLE);
			mVDataLoader.setVisibility(View.GONE);

			if (wifis != null && wifis.size() > 0) {
				wifis.clear();
				// list.notify();
			}
			if (wifiScanList != null)
				wifiScanList.clear();

			wifiScanList = mainWifiObj.getScanResults();

			wifisName = new String[wifiScanList.size()];

			String name = "";
			for (int i = 0; i < wifiScanList.size(); i++) {
				WifiConfig obj = new WifiConfig();
				obj.nNameWAP = wifiScanList.get(i).capabilities;
				obj.nNameWifi = wifiScanList.get(i).SSID;
				int signalStrangth = 0;
				// for (ScanResult result : wifiScanList) {

				// if(wifiScanList.get(i).BSSID.equals(mainWifiObj.getConnectionInfo().getBSSID()))
				// {
				int level = WifiManager.calculateSignalLevel(mainWifiObj
						.getConnectionInfo().getRssi(),
						wifiScanList.get(i).level);
				int difference = level * 100 / wifiScanList.get(i).level;

				if (difference >= 100)
					signalStrangth = 4;
				else if (difference >= 75)
					signalStrangth = 3;
				else if (difference >= 50)
					signalStrangth = 2;
				else if (difference >= 25)
					signalStrangth = 1;

				// }

				// }

				obj.nStrenght = signalStrangth;
				obj.nConnectiivity = "(disconnected)";
				wifis.add(obj);// (new
								// WifiConfig(wifiScanList.get(i)).toString());
				name += (wifiScanList.get(i)).SSID + " , ";
				wifisName[i] = (wifiScanList.get(i)).SSID;

			}

			// list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
			// android.R.layout.simple_list_item_1, wifis));

			if (wifiAdapter == null) {
				wifiAdapter = new AccountDetailsAdapter(
						getApplicationContext(), wifis);
				list.setAdapter(wifiAdapter);
			} else
				wifiAdapter.notifyDataSetChanged();

		}
	}

	AccountDetailsAdapter wifiAdapter;

	View view_currentSelected;

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		view_currentSelected = arg1;
		alertBoxPassword(arg2, arg1);
	}

	int selectedIndex =0;
	
	private void connectToWifi(int position, String password) {
		// get SSID and BSSID from item
		// WifiConfiguration wifiConfiguration = new WifiConfiguration();
		// wifiConfiguration.SSID = wifiScanList.get(position).SSID;
		// wifiConfiguration.allowedKeyManagement.set(KeyMgmt.NONE);
		// wifiConfiguration.BSSID = wifiScanList.get(position).BSSID; // you
		// // should
		// // also put
		// // the BSSID
		// // in the
		// // map
		// wifiConfiguration.hiddenSSID = false;
		// int inetId = mainWifiObj.addNetwork(wifiConfiguration);
		// mainWifiObj.enableNetwork(inetId, true);

		selectedIndex = position;
				
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiConfiguration wc = new WifiConfiguration();

		// wc.SSID = "\""+wifiScanList.get(position).SSID+"\"";
		// wc.preSharedKey = "\"12345678h\"";

		wc.SSID = "\"".concat(wifiScanList.get(position).SSID).concat("\"");
		wc.preSharedKey = "\"".concat(password).concat("\"");

		wc.hiddenSSID = true;
		wc.BSSID = wifiScanList.get(position).BSSID; // you

		wc.priority = 40;

		wc.status = WifiConfiguration.Status.ENABLED;
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

		int res = mainWifiObj.addNetwork(wc);
		Log.e("WifiPreference", "add Network returned " + res);
		boolean es = wifi.saveConfiguration();
		Log.e("WifiPreference", "saveConfiguration returned " + es);
		boolean b = mainWifiObj.enableNetwork(res, true);
		Log.e("WifiPreference", "enableNetwork returned " + b);
		b = mainWifiObj.reconnect();
		Log.e("WifiPreference", "reconnect Network returned " + b);
		handler.removeMessages(UPDATE_UI);
		handler.sendEmptyMessageDelayed(UPDATE_UI, TRIGGER_DELAY_IN_MS);

		// List<WifiConfiguration> netWorkList = wifi.getConfiguredNetworks();
		// WifiConfiguration wifiCong = null;

		// if (netWorkList != null) {
		// for(WifiConfiguration item:netWorkList) {
		// if (item.SSID.equalsIgnoreCase( wc.SSID )) {
		// wifiCong = item;
		// }
		// }
		// }

//		new java.util.Timer().schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//				isInternetAvailable();
//			}
//		}, 1000, 1000);

	}
	int i =0;
	void isInternetAvailable() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			wifiAdapter.getData().get(selectedIndex).nConnectiivity = "(connected)";
			wifiAdapter.notifyDataSetInvalidated();
		}

		handler.removeMessages(UPDATE_UI);
		handler.sendEmptyMessageDelayed(UPDATE_UI, TRIGGER_DELAY_IN_MS);
	}

	private final int UPDATE_UI = 1;
	private final long TRIGGER_DELAY_IN_MS = 2000;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPDATE_UI) {
				isInternetAvailable();

			}
		}
	};

	void alertBoxPassword(final int position, final View view) {
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(this.getBaseContext());
		View promptsView = li.inflate(R.layout.prompts, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// get user input and set it to result
						// edit text
						// result.setText(userInput.getText());

						String password = userInput.getText().toString();
						if (password.length() > 0)
							connectToWifi(position, password);
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		startActivity(new Intent(MainActivity.this, SpeedometerActivity.class));

	}

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.activity_main);
	// }

	public class AccountDetailsAdapter extends BaseAdapter {

		private ArrayList<WifiConfig> mAllData;
		private LayoutInflater mInflater;
		public static final int VISIBLE_LIST_ITEMS = 10;
		private int size = 0;
		private ViewHolderAccountDetails holder = null;
		private Context mContext;

		public AccountDetailsAdapter(Context context,
				ArrayList<WifiConfig> pPosts) {
			this.mAllData = pPosts;
			this.mContext = context;

			if (mContext != null) {
				mInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
		}

		@Override
		public int getCount() {
			return mAllData != null ? mAllData.size() : 0;
		}

		public void setData(ArrayList<WifiConfig> mAllPostData) {
			this.mAllData = mAllPostData;
		}
		
		public ArrayList<WifiConfig>  getData() {
			return this.mAllData ;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;

			if (view == null) {
				view = mInflater.inflate(R.layout.row, null);
				holder = new ViewHolderAccountDetails(mContext, view);

				view.setTag(holder);
			} else {
				holder = (ViewHolderAccountDetails) view.getTag();
			}

			WifiConfig data = mAllData.get(position);
			holder.updateDataInView(data, position);

			return view;
		}

	}

	public class ViewHolderAccountDetails {

		private final String TAG = "SearchHolderPost";
		/** The m mContext. */
		private Context mContext;
		private TextView mNameWAP;
		private TextView mNameWifi;
		private TextView mStrenght;
		private TextView mConnectivity;

		int position;

		public ViewHolderAccountDetails(Context context, View v) {

			this.mContext = context;
			mNameWifi = (TextView) v.findViewById(R.id.name_wifi);
			mNameWAP = (TextView) v.findViewById(R.id.name_wap);
			mStrenght = (TextView) v.findViewById(R.id.strenght);
			mConnectivity = (TextView) v.findViewById(R.id.tv_connectivity);
		}

		public void updateDataInView(WifiConfig data, int pos) {
			// this.mAccountCategory = data;
			mNameWAP.setText(data.nNameWAP);
			mNameWifi.setText(data.nNameWifi);
			mStrenght.setText("" + data.nStrenght);
			mConnectivity.setText(data.nConnectiivity);

		}

	}

	@SuppressWarnings("serial")
	public class WifiConfig implements Serializable {
		public String nNameWifi;
		public String nNameWAP;
		public int nStrenght;
		public String nConnectiivity;

	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		handler.removeMessages(UPDATE_UI);
	}

}