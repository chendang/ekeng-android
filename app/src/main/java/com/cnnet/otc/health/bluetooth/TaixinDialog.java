package com.cnnet.otc.health.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.HBuilder.integrate.R;
import com.cnnet.otc.health.bean.DeviceListItem;
import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.managers.BleManager;
import com.cnnet.otc.health.util.StringUtil;

public class TaixinDialog extends Dialog implements
		View.OnClickListener {
	
	private final String TAG = "TaixinDialog";

	private HashMap<String, String> map = new HashMap<String, String>();
	private Button seachButton, stopButton;

	private Context mContext;

	public ProgressDialog progressDialog;


	private String TaixinAddress;
	private BleManager mBleManager;
	private ListView mListView;
	private DeviceListAdapter mAdapter;
	private ArrayList<MyBlueToothDevice>list;
	private String Sharepare;

	public TaixinDialog(Context context, int theme,String sharepare, BleManager mBle) {
		super(context, theme);
		setContentView(R.layout.devices);
		this.mBleManager = mBle;
		this.mBleManager.setLeScanCallBack(mLestartScanCallback);
		mContext = context;
		Sharepare = sharepare;
		init();
	}

	private void init() {
		
		list = new ArrayList<MyBlueToothDevice>();

		mAdapter = new DeviceListAdapter(mContext, list);
		progressDialog = new ProgressDialog(getContext());
		progressDialog.setMessage(getContext().getString(R.string.CONNECTING));
		progressDialog.setCancelable(true);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setFastScrollEnabled(true);
		mListView.setOnItemClickListener(mDeviceClickListener);
		seachButton = (Button) findViewById(R.id.start_seach);
		stopButton = (Button) findViewById(R.id.stop_seach);
		seachButton.setOnClickListener(this);
		stopButton.setOnClickListener(this);
		setTitle(Sharepare);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.start_seach:
			mBleManager.startScan();

			mAdapter.notifyDataSetChanged();
			mListView.setSelection(list.size() - 1);


			mHandlerslist.post(TimerProcess1);

			break;
		case R.id.stop_seach:
			mBleManager.stopScan();
			mHandlerslist.removeCallbacks(TimerProcess1);
			map.clear();
			break;

		}

	}

	Handler mHandlerslist = new Handler();
	private Runnable TimerProcess1 = new Runnable() {
		public void run() {
			//
			mAdapter.notifyDataSetChanged();
			mListView.setSelection(list.size() - 1);
			mHandlerslist.postDelayed(this, 1000);
		}
	};

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

			final MyBlueToothDevice item = list.get(arg2);
			if (StringUtil.isEmpty(item.getAddress())) {
				return;
			}

			AlertDialog.Builder StopDialog = new AlertDialog.Builder(mContext);// 定义一个弹出框对象
			StopDialog.setTitle(getContext().getString(R.string.CONNECT));// 标题
			StopDialog.setMessage(item.getName() + "\n" + item.getAddress());

			StopDialog.setPositiveButton(
					getContext().getString(R.string.CONNECT),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							seachButton.setText(getContext().getString(
									R.string.REDISCOVERY));
							Log.i(TAG, "mEngineManager当前连接状态" + mBleManager.getConnectState());

							mBleManager.setConnectAddress(item.getName(), item.getAddress());

							map.clear();

							mBleManager.disConnectBle();
							TaixinDialog.this.dismiss();
						}
					});

			StopDialog.setNegativeButton(getContext()
					.getString(R.string.CANCEL), null);
			StopDialog.show();
		}
	};

	// 获取蓝牙设备列表
	@SuppressLint("NewApi")
	private BluetoothAdapter.LeScanCallback mLestartScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {
			String address = device.getAddress();
			if (!map.containsKey(address)) {
				map.put(address, address);
				Log.i(TAG, "ss  -- " + device.getName() + " _" + device.getAddress());
				MyBlueToothDevice item = new MyBlueToothDevice(device.getName(),  device.getName());
				item.setName(device.getName());
				item.setAddress(device.getAddress());
				list.add(item);
			}

		}
	};

	
}