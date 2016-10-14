package com.cnnet.otc.health.bean.data;

import android.content.Context;
import android.util.Log;

import com.HBuilder.integrate.R;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.db.DBHelper;
import com.cnnet.otc.health.events.BTConnetEvent;
import com.cnnet.otc.health.interfaces.MyCommData;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.views.MyLineChartView;

import java.math.BigDecimal;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 血糖
 * @author Administrator
 *
 */
public class BloodGlucoseData implements MyCommData {

	private final String TAG = "BloodGlucoseData";

	/**
	 * 连机命令
	 */
	private final String BT_ONLINE_COMMAND = "FE6A755A55AABBCC";
	/**
	 * 请滴血
	 */
	private final String BT_PLEASE_BLEEDING = "FE6A755A55BBBBCC";
	/**
	 * 倒数时间显示
	 */
	private final String BT_COUNTDOWN_TIME_DISPLAY = "FE6A755A55";
	/**
	 * 显示结果
	 */
	private final String BT_DISPLAY_RESULT = "FE6A755A";

	/**
	 * 血糖数据字段
	 */
	public static final String DATA_BLOOD_GLUCOSE = "GLU";

	/**
	 * 测试血糖时是否将试纸插入卡槽:默认为false
	 */
	private boolean inserted = false;;

	public int todoDisconnected_failed, todoDisconnected ;

	private long nativeRecordId;

	private Context context;
	/**
	 * 血糖争对的图表对象
	 */
	private MyLineChartView myLineChartView;
	

	public BloodGlucoseData(Context ctx, MyLineChartView myLineChartView, long nativeRecordId){
		this.myLineChartView = myLineChartView;
		this.context = ctx;
		this.nativeRecordId = nativeRecordId;
	}

	/**
	 * 将从蓝牙获取到的字节数组传递进来，进行数据解析
	 * @param bytes   蓝牙传递过来的数据
	 */
	@Override
	public void todo(byte[] bytes) {
		String hexString = StringUtil.byteToHexString(bytes);//将数据进行Hex运算后得到的结果字符串
		if (hexString.equals(BT_ONLINE_COMMAND)) {
			String text = myLineChartView.getTitle();
			if (text.equals(context.getString(R.string.CONNECT_GLUCOSE))) {
				// // 插入蓝牙MAC地址
				// DisplayView.fa.hander.sendEmptyMessage(2);
				EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_SAVE_BLUETOOTH_ADDRESS, null));
				Log.d(TAG, "插入成功");
			}
			if (inserted == false) {
				//DisplayView.fa.hander.sendEmptyMessage(5);
				EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
						context.getString(R.string.INSERT_TEST_PAPER)));
			} else if (inserted == true) {
				//DisplayView.fa.hander.sendEmptyMessage(5);
				EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
						context.getString(R.string.DRIP_BLOOD)));
			}
		} else if (hexString.equals(BT_PLEASE_BLEEDING)) {
			//DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
					context.getString(R.string.DRIP_BLOOD)));
			inserted = true;
		} else if (hexString.startsWith(BT_COUNTDOWN_TIME_DISPLAY)) {
			/*myLineChartView.setTitleText(context.getString(R.string.WAIT)
					+ (int) bytes[5]
					+ context.getString(R.string.SECOND));*/
			//DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
					(context.getString(R.string.WAIT)+ (int) bytes[5] + context.getString(R.string.SECOND))));
		} else if (hexString.startsWith(BT_DISPLAY_RESULT)) {
			byte[] temp = new byte[2];
			inserted = false;
			temp[0] = bytes[4];
			temp[1] = bytes[5];
			int consistency = (Integer.parseInt(StringUtil.byteToHexString(temp), 16));
			String data = null;
			float values = 0;
			if (myLineChartView.isMmol()) {
				float consistencyMmolpl = consistency / 18.0f;
				int scale = 1;// 设置位数
				int roundingMode = 4;// 表示四舍五入
				BigDecimal bd = new BigDecimal(consistencyMmolpl);
				bd = bd.setScale(scale, roundingMode);
				consistencyMmolpl = bd.floatValue();
				values = consistencyMmolpl;
				data = consistencyMmolpl + "mmol/L";
			} else {
				data = consistency + "mg/dl";
			}
			SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_BLOOD_GLUCOSE, values, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
			//EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_SAVE_RECORD_ITEM, context.getString(R.string.CHECK_TYPE_INFO_BLOOD_GLUCOSE)));
			//DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, data));
			/*if (DisplayView.fa != null) {
				DisplayView.fa.save();
				DisplayView.fa.hander.sendEmptyMessage(0);
			}*/
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_RESET, null));
		} else {

		}
	}

	@Override
	public void todoConnected() {
//		todoConnected=2;
		Log.d(TAG, "todoConnected");
	}

	@Override
	public void todoDisconnected() {
		todoDisconnected=0;
		Log.d(TAG, "todoDisconnected");
	}

	@Override
	public void todoConnecting() {
//		todoConnected=1;
		Log.d(TAG, "todoConnecting");
	}

	@Override
	public void todoDisconnected_failed() {
		todoDisconnected_failed=3;
		Log.d(TAG, "todoDisconnected_failed");
	}

	@Override
	public List<RecordItem>[] getRecordList(String mUniqueKey) {
		List<RecordItem>[] lists = new List[1];
		lists[0] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_BLOOD_GLUCOSE);
		return lists;
	}

	@Override
	public List<RecordItem> getRecordAllList(String mUniqueKey) {
		return SysApp.getMyDBManager().getRecordAllInfoByType(mUniqueKey, DATA_BLOOD_GLUCOSE);
	}

	@Override
	public String[] getInsName() {
		return new String[]{"血糖"};
	}

	@Override
	public String[] getInsUnit() {
		return new String[]{"mmol/L"};
	}

	@Override
	public int[] getInsRange() {
		return new int[]{2};
	}

	@Override
	public void refreshRealTime() {

	}

	@Override
	public boolean refreshData() {

		return false;
	}

	@Override
	public int getdisconnected_failed() {
		return todoDisconnected_failed;
	}

}
