package com.cnnet.otc.health.activities;

import com.HBuilder.integrate.R;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.bean.data.OximetryData;
import com.cnnet.otc.health.comm.BaseActivity;
import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.events.BleEvent;
import com.cnnet.otc.health.managers.BleManager;
import com.cnnet.otc.health.views.MyLineChartView;
import com.cnnet.otc.health.views.adapter.DetectRecordListAdapter;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

public class DetectBle4Activity extends BaseActivity implements OnChartValueSelectedListener, OnClickListener {

	private TextView title=null;   //标题
	private MyLineChartView myLineView = null;  //绘图View
	private Button sampleBtn = null;  //采集

	private ListView listview;

	/**
	 * 4.0蓝牙管理中心
	 */
	private BleManager bleManager = null;
	/**
	 * 红色数组（r）
	 */
	private int redCount=255;
	/**
	 * 颜色透明度
	 */
	private int alphaFlag = 0;

	private static String[] devices = null;

	private String mUniqueKey = null;
	private long nativeRecordId = 0;
	private boolean isDetected = false;  //判断数据是否改变
	private boolean hasReal = false;  //判断是否存在实时数据
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detect);
		mUniqueKey = getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_MEMBER_UNIQUEKEY);
		nativeRecordId = getIntent().getLongExtra(CommConst.INTENT_EXTRA_KEY_NATIVE_RECORD_ID, new Date().getTime());
		hasReal = getIntent().getBooleanExtra(CommConst.INTENT_EXTRA_KEY_HAS_REAL, false);
		initCheckType(Integer.parseInt(getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_TYPE)));
		init();
	}
	
	private void init() {
		//注册EventBus
		EventBus.getDefault().register(this);

		if(devices == null) {
			devices = getResources().getStringArray(R.array.devices);
		}
		title = (TextView) findViewById(R.id.tv_detect_title);
		title.setText(devices[SysApp.check_type.ordinal()]);
		LinearLayout drawLinear = (LinearLayout) findViewById(R.id.detect_draw_linear);
		myLineView = new MyLineChartView(this, this);
		myLineView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		if(hasReal) {
			myLineView.refreshRealTimeByMP(null);
		}
		drawLinear.addView(myLineView);

		sampleBtn = (Button)findViewById(R.id.bt_click_sample);
		if(SysApp.check_type == CheckType.OXIMETRY) {
			sampleBtn.setOnClickListener(this);
			sampleBtn.setVisibility(View.VISIBLE);
		}

		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.bt_detect_connect).setOnClickListener(this);

		bleManager = new BleManager(this, null, myLineView, nativeRecordId, hasReal,mUniqueKey);
		bleManager.startConnectBtDevice(null);
		listview = (ListView) findViewById(R.id.listview);
		setData();
	}

	private void setData() {
		List<RecordItem>[] list = bleManager.getData().getRecordList(mUniqueKey);
		myLineView.addData(list, bleManager.getData().getInsName());

		DetectRecordListAdapter listAdapter = new DetectRecordListAdapter(this,
				bleManager.getData().getRecordAllList(mUniqueKey)
				,bleManager.getData());
		listview.setAdapter(listAdapter);
	}

	private void refreshLineByData() {
		isDetected = true;
		setData();
		myLineView.invalidate();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bleManager.startRunable();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(bleManager != null) {
			bleManager.removeCallback();
		}
	}
	
	public void onEventMainThread(BleEvent event) {
		switch (event.getBleEvent()) {
			case CommConst.FLAG_BLE_CONNECT_UPDATE_SCAN:  //
				myLineView.setTitleTextColor();
				break;
			case CommConst.FLAG_BLE_CONNECT_UPDATE_STATE:
				myLineView.setTitleText(event.getBlueStateStr());
				break;
			case CommConst.FLAG_BLE_CONNECT_SCUESS:
				myLineView.setTitleText(event.getBlueStateStr());
				myLineView.getTitleView().setTextColor(Color.GREEN);
				break;
			case CommConst.FLAG_REFRESH_REAL_TIME_DATA:
				myLineView.refreshRealTimeByMP(event.getDatas());
				break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(bleManager != null) {
				bleManager.destory();
				bleManager = null;
			}
			finishAndSendBack();
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.bt_detect_connect:
				bleManager.initConnectView(devices[SysApp.check_type.ordinal()]);
				break;
			case R.id.btn_back:
				finishAndSendBack();
				break;
			case R.id.bt_click_sample:
				boolean isSuccess= false;
				if(SysApp.check_type == CheckType.OXIMETRY) {
					isSuccess = ((OximetryData)bleManager.getData()).saveSampleInfo();
				}
				if(isSuccess) {
					refreshLineByData();// 重新设置ListView的数据适配器
				}
				break;
		default:
			break;
		}
	}

	private void finishAndSendBack() {
		Intent intent = new Intent();
		intent.putExtra(CommConst.INTENT_EXTRA_KEY_IS_DETECTED, isDetected);
		setResult(CommConst.INTENT_REQUEST_DETECT, intent);
		this.finish();
	}

	@Override   //预留
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

	}

	@Override //预留
	public void onNothingSelected() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(bleManager != null) {
			bleManager.destory();
			bleManager = null;
		}
		//反注册EventBus
		EventBus.getDefault().unregister(this);
	}
}
