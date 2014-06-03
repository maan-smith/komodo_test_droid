package com.example.wifiapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
			Toast.makeText(getApplicationContext(),
					"wifi is disabled..making it enabled", Toast.LENGTH_LONG)
					.show();
			mainWifiObj.setWifiEnabled(true);
		}
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
			wifiScanList = mainWifiObj.getScanResults();
			// wifis = new WifiConfig[wifiScanList.size()];

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

				wifis.add(obj);// (new
								// WifiConfig(wifiScanList.get(i)).toString());
				name += (wifiScanList.get(i)).SSID + " , ";
				wifisName[i] = (wifiScanList.get(i)).SSID;

			}

			// list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
			// android.R.layout.simple_list_item_1, wifis));

			list.setAdapter(new AccountDetailsAdapter(getApplicationContext(),
					wifis));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		connectToWifi(arg2);
	}

	private void connectToWifi(int position) {
		// HashMap<String, String> item = arraylist.get(position);

		// get SSID and BSSID from item

		WifiConfiguration wifiConfiguration = new WifiConfiguration();
		wifiConfiguration.SSID = wifiScanList.get(position).SSID;
		wifiConfiguration.allowedKeyManagement.set(KeyMgmt.NONE);
		wifiConfiguration.BSSID = wifiScanList.get(position).BSSID; // you

		// should
		// also put
		// the BSSID
		// in the
		// map
		wifiConfiguration.hiddenSSID = false;

		int inetId = mainWifiObj.addNetwork(wifiConfiguration);
		mainWifiObj.enableNetwork(inetId, true);
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

		int position;

		public ViewHolderAccountDetails(Context context, View v) {

			this.mContext = context;
			mNameWifi = (TextView) v.findViewById(R.id.name_wifi);
			mNameWAP = (TextView) v.findViewById(R.id.name_wap);
			mStrenght = (TextView) v.findViewById(R.id.strenght);
		}

		public void updateDataInView(WifiConfig data, int pos) {
			// this.mAccountCategory = data;
			mNameWAP.setText(data.nNameWAP);
			mNameWifi.setText(data.nNameWifi);

			mStrenght.setText("" + data.nStrenght);

		}

	}

	@SuppressWarnings("serial")
	public class WifiConfig implements Serializable {
		public String nNameWifi;
		public String nNameWAP;
		public int nStrenght;
	}

}