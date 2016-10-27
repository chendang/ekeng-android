package com.cnnet.otc.health.managers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.HBuilder.integrate.R;
import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.cnnet.otc.health.bean.data.OximetryData;
import com.cnnet.otc.health.bluetooth.TaixinDialog;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.events.BleEvent;
import com.cnnet.otc.health.interfaces.MyCommData;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.cnnet.otc.health.views.MyLineChartView;
import com.example.blelib.BtEngineManager;
import com.example.blelib.ProtocolManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import de.greenrobot.event.EventBus;


public class BleManager {
	
	private final String TAG = "BleManager";
	
	private Activity mActivity;  //上下文对象

	private MyLineChartView myLineView;  //绘图对象

	private long navtiveRecordId;
	private String mUniqueKey = null;
	/**
	 * 蓝牙启动管理
	 */
	private BtEngineManager mEngineManager;
	
	private LeScanCallback mLeScanCallback;
	/**
	 * 获取蓝牙传递信息的对象
	 */
	private MyCommData data;
	/**
	 * 蓝牙连接线程
	 */
	private Runnable mainRunnable;
	/**
	 * 更新view线程
	 */
	private Runnable mViewTimer;
	/**
	 * 进行主线程，和UI线程
	 */
	private Handler mainHandler,GrapviewHandler;

	private Timer timer = null;  //定时器
	private TimerTask taskcc =null ;  //定时器执行
	private boolean timerStart = false;  //判断定时器是否开启
	private boolean hasReal = false; //是否存在实施数据

	/**
	 * 当前连接蓝牙地址
	 */
	private MyBlueToothDevice manualBT = null;  //手动连接蓝牙对象
	private MyBlueToothDevice currentBT = new MyBlueToothDevice();  //当前正在连接蓝牙对象
	private int btListIndex = -1;  //蓝牙列表搜索index
	private List<MyBlueToothDevice> btLists = null;

	private int connectState = 0;  //当前连接状态，0：未连接，1连接中，2已连接
	
	public BleManager(Activity mActivity, LeScanCallback mLeScanCallback, MyLineChartView myLineView, long navtiveRecordId, boolean hasReal,String mUniqueKey) {
		this.mActivity = mActivity;
		this.myLineView = myLineView;
		this.mLeScanCallback = mLeScanCallback;
		this.navtiveRecordId = navtiveRecordId;
		this.mUniqueKey = mUniqueKey;
		mainHandler = new Handler();
		this.hasReal = hasReal;
		if(hasReal) {
			GrapviewHandler = new Handler();
		}
		initBtList();
		initEngineManager();
		initRunable();
	}
	
	/**
	 * 获取蓝牙控制管理实例对象
	 * @return
	 */
	public static BleManager getInstance(Activity mActivity, LeScanCallback mLeScanCallback, MyLineChartView myLineView, long navtiveRecordId, boolean hasReal,String mUniqueKey) {
		BleManager mBleManager = new BleManager(mActivity, mLeScanCallback, myLineView, navtiveRecordId, hasReal, mUniqueKey);
		return mBleManager;
	}
	
	/**
	 * 设置扫描蓝牙设备回调方法
	 * @param mLeScanCallback
	 */
	public void setLeScanCallBack(LeScanCallback mLeScanCallback) {
		this.mLeScanCallback = mLeScanCallback;
	}
	
	/**
	 * 初始化蓝牙启动管理,并开始进行蓝牙连接
	 */
	private void initEngineManager() {
		mEngineManager = new BtEngineManager(mActivity);
		data = getData();
		//设置蓝牙信息回调
		mEngineManager.setBtCallBack(new ProtocolManager(mActivity, mEngineManager, ProtocolManager.DEFAULT_PORT, data));
		//设置获取数据的Data
		mEngineManager.setMyData(data);
		//停止蓝牙扫描
		stopScan();
		//开始连接
		startConnectBtDevice(currentBT.getAddress());
	}

	/**
	 * 重置当前蓝牙管理，并开启连接蓝牙
	 * @param btAddress
	 */
	private void resetEngineManager(String btAddress) {
		mEngineManager = new BtEngineManager(mActivity);
		data = getData();
		//设置蓝牙信息回调
		mEngineManager.setBtCallBack(new ProtocolManager(mActivity, mEngineManager, ProtocolManager.DEFAULT_PORT, data));
		//设置获取数据的Data
		mEngineManager.setMyData(data);
		startConnectBtDevice(btAddress);
	}

	/**
	 * 获取解析数据Data类
	 * @return
	 */
	public MyCommData getData() {
		if(data == null) {
			switch (SysApp.check_type) {
				case OXIMETRY:   //血氧仪
					data = new OximetryData(mActivity, myLineView, navtiveRecordId,mUniqueKey);
					break;
			}

		}
		return data;
	}
	
	/**
	 * 连接蓝牙 : 连接地址对应的蓝牙
	 * @param Sharperaddress
	 */
	public void startConnectBtDevice(String Sharperaddress) {
		mEngineManager.connectBtDevice(Sharperaddress);
	}

	/**
	 * 初始化蓝牙列表
	 */
	public void initBtList() {
		btListIndex = -1;
		btLists = SysApp.getMyDBManager().getConnectedBtList(SysApp.check_type.ordinal());
		if(btLists != null && btLists.size() > 0) {
			btListIndex = 0;
		}
	}

	/**
	 * 当前要连接的蓝牙对象
	 * @param btAddress
	 */
	public void setConnectAddress(String btName, String btAddress) {
		this.manualBT = new MyBlueToothDevice();
		this.manualBT.setName(btName);
		this.manualBT.setAddress(btAddress);
		this.manualBT.setDeviceType(SysApp.check_type.ordinal());
		btListIndex = -1;
		btLists = null;
	}
	
	/**
	 * 重新搜索蓝牙设备：当前状态已连接时，提示请先断开蓝牙连接后再进行搜索
	 * @param Sharper
	 */
	public void initConnectView(String Sharper) {
		if (mEngineManager != null) {
			if (mEngineManager.isConnected()) {
				ToastUtil.TextToast(mActivity, R.string.TOAST_HOSET_CONNECT, 2000);
			} else {
				stopScan();
				TaixinDialog deviceDialog = new TaixinDialog(mActivity, R.style.dialog, Sharper, this);
				deviceDialog.show();
				
			}

		}
	}
	
	/**
	 * 断开蓝牙连接
	 */
	public void disConnectBle() {
		if(mEngineManager != null) {
			mEngineManager.disconnectBtDevice();
			mEngineManager.closeBtConection();
		}
	}
	
	/**
	 * 开始扫描蓝牙
	 */
	public void startScan() {
		if(mLeScanCallback != null) {
			mEngineManager.startscan(mLeScanCallback);
		} else {
			mEngineManager.startscan(mNormalLeScanCallback);
		}
	}
	
	/**
	 * 停止扫描蓝牙
	 */
	public void stopScan() {
		if (mEngineManager != null) {
			if (mEngineManager.isConnected()) {
				if(mLeScanCallback != null) {
					mEngineManager.stopscan(mLeScanCallback);
				} else {
					mEngineManager.stopscan(mNormalLeScanCallback);
				}
				disConnectBle();
			}
		}
	}
	
	/**
	 * 获取当前蓝牙连接状态
	 * @return
	 */
	public int getConnectState() {
		return mEngineManager.getConnectState();
	}
	

	@SuppressLint("NewApi")
	private LeScanCallback mNormalLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {
			Log.d("ss", device.getAddress());
		}
	};
	/**
	 * 初始化定时线程
	 */
	public void initRunable() {
		if(hasReal) {
			if (mViewTimer == null) {
				mViewTimer = new Runnable() {
					@Override
					public void run() {
						switch (SysApp.check_type) {
							case OXIMETRY:
								data.refreshRealTime();
								break;
						}
						GrapviewHandler.postDelayed(this, 250);  //开启定时刷新方法
					}
				};
			}
		}
		if(mainRunnable == null) {
			mainRunnable = new Runnable() {
				private int connectCount = 0;
				@Override
				public void run() {
					int bleState = mEngineManager.getConnectState();
					data.refreshData();

					Log.i(TAG, "mEngineManager.getConnectState() = " + bleState);

					//判断当前蓝牙连接状态
					switch (bleState) {
						case 0:   //连接失败
							startTimer();
							EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_CONNECT_UPDATE_STATE, mActivity.getString(R.string.StateClose), Color.WHITE));
							break;
						case 1:   //正在连接
							startTimer();
							EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_CONNECT_UPDATE_STATE, mActivity.getString(R.string.StateConnecting)));
							break;
						case 2:   //连接成功
							if(connectState != bleState) {
								connectCount = 0;
								stopTimer();
								sendInfoHandler.post(sendMessage);
								SysApp.btDevice = currentBT;
								EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_CONNECT_SCUESS, mActivity.getString(R.string.Stateconnected)));
								SysApp.getMyDBManager().saveBTDeviceInfo(currentBT, SysApp.check_type.ordinal());
							}
							break;
					}
					connectState = bleState;

					Log.d(TAG, "data.getdisconnected_failed() is " + data.getdisconnected_failed() + ";  mEngineManager.getConnectState() is " + bleState);

					if ((data.getdisconnected_failed() == 3 | bleState == 0)
							|| connectCount >=2) {
						connectCount=0;
						disConnectBle();
						Log.d(TAG, "onConnectionStateChange_mConnectionState___failed  === >"
								+ bleState
								+ "-------------- mEngineManager"
								+ mEngineManager.toString());

					} else if(bleState == 1) {  //蓝牙状态为1时，代表正在连接中
						connectCount++;
					}
					String Sharperaddress = null;

					if(manualBT != null && StringUtil.isNotEmpty(manualBT.getAddress())) {
						Sharperaddress = manualBT.getAddress();
						currentBT = manualBT;
					} else if(btListIndex >= 0) {
						if(btLists != null) {
							if(btListIndex >= btLists.size()) {
								btListIndex = 0;
							}
							currentBT = btLists.get(btListIndex);
							Sharperaddress = currentBT.getAddress();

							btListIndex++;
						}
					}
					Log.d(TAG, "Sharperaddress == >>ֵ==" + Sharperaddress + "; btListIndex=" + btListIndex);
					//未连接上时，重新初始化对象
					if (Sharperaddress != null & mEngineManager.getConnectState() == 0) {
						resetEngineManager(Sharperaddress);
						Log.d(TAG, "mEngineManager====>>>>>mEngineManager.getState"
								+ mEngineManager.getConnectState());
					}

					mainHandler.postDelayed(this, 1500);

				}
			};
		}
		
	}

	private Handler sendInfoHandler = new Handler();
	private Runnable sendMessage = new Runnable() {
		@Override
		public void run() {
			boolean flag = false;
			int delayTime = 0;
			switch (SysApp.check_type) {
				default:
					flag = false;
					delayTime = 0;
					break;
			}
			if(flag) {
				sendInfoHandler.postDelayed(sendMessage, delayTime);
			}
		}
	};

	public void startRunable() {
		if(GrapviewHandler != null) {
			GrapviewHandler.postDelayed(mViewTimer, 1000);
		}
		if(mainHandler != null) {
			mainHandler.postDelayed(mainRunnable, 1000);
		}
	}
	
	/**
	 * 开启定时器
	 */
	private void startTimer() {
		Log.d(TAG, "开始定时器...");
		try {
			//redCount = 0;
			//alphaFlag = 0;
			if (timer == null) {
				timer = new Timer();
			}

			if (taskcc == null) {
				taskcc = new TimerTask() {
					@Override
					public void run() {
						EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_CONNECT_UPDATE_SCAN));
						//handler.sendEmptyMessage(0);
					}
				};
			}

			if (timer != null && taskcc != null && !timerStart) {
				timer.schedule(taskcc, 100, 100);
				timerStart = true;
			}
		} catch (Exception e) {
			Log.e(TAG, "定时器已打开");
		}
	}

	/**
	 * 取消定时器
	 */
	private void stopTimer() {
		Log.i(TAG, "取消定时器");
		// isStop = true;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (taskcc != null) {
			taskcc.cancel();
			taskcc = null;
		}
		timerStart = false;
		Log.i(TAG, "FudakangActivity: onResume");
		//Log.i(TAG, "connectStatus=" + connectStatus);
		//handler.sendEmptyMessage(2);
		EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_CONNECT_SCUESS));
	}
	
	/**
	 * 停止回调
	 */
	public void removeCallback() {
		if(mainHandler != null) {
			mainHandler.removeCallbacks(mainRunnable);
		}
		if(GrapviewHandler != null) {
			GrapviewHandler.removeCallbacks(mViewTimer);
		}
	}
	
	/**
	 * 销毁当前对象
	 */
	public void destory() {
		stopTimer();
		stopScan();
		removeCallback();
	}
	
}
