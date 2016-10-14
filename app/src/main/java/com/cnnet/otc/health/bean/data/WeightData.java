package com.cnnet.otc.health.bean.data;

import android.content.Context;
import android.util.Log;

import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.db.DBHelper;
import com.cnnet.otc.health.events.BTConnetEvent;
import com.cnnet.otc.health.events.BleEvent;
import com.cnnet.otc.health.interfaces.MyCommData;
import com.cnnet.otc.health.util.DateUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.views.MyLineChartView;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 体重
 * Created by SZ512 on 2016/1/23.
 */
public class WeightData implements MyCommData {

    private static final String TAG = "WeightData";

    private Context ctx;
    /**
     * 绘图对象
     */
    private MyLineChartView myLineView;
    /**
     * 本地检查记录ID
     */
    private long nativeRecordId;

    /**
     * 体重
     */
    private final String DATA_WEIGHT = "WEIGHT";
    /**
     * 身高
     */
    public final String DATA_HEIGHT = "HEIGHT";
    /**
     * 腰围
     */
    public final String DATA_WAIST = "WAIST";
    /**
     * 体脂肪率
     */
    private final String DATA_BFP = "BFP";
    /**
     * 体质指数
     */
    private final String DATA_BMI = "BMI";

    private final String GET_ERROR_DATA = "FD31000000000031";

    private final String GET_BFP_DATA = "FD33000000000033";

    private final byte DATA_HEAD = (byte) 0xCF;

    private final int DATA_LENGTH = 16;

    private int todoDisconnected_failed, todoDisconnected ;
    private int height = 0;
    int gender = -1;  //1男，0女
    int age = 0;

    public WeightData(Context ctx, MyLineChartView myLineView, long nativeRecordId) {
        this.myLineView = myLineView;
        this.ctx = ctx;
        this.nativeRecordId = nativeRecordId;
    }

    @Override
    public void todo(byte[] bytes) {
        // FE 03 01 00 AA 19 01 B0  (APP发送信息)
        // CF 03 99 AA 01 38 00 00 00 00 00 00 00 00 00 00
        String hexData = StringUtil.byteToHexString(bytes);//将数据进行Hex运算后得到的结果字符串
        Log.d(TAG, "hexData str : " + hexData);
        if(hexData.contains(GET_BFP_DATA)) {  //脂肪
            EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_CONNECT_UPDATE_STATE,"获取的脂肪数据有误"));
        } else if (hexData.contains(GET_ERROR_DATA)) {  //数据错误
            EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_CONNECT_UPDATE_STATE,"发送数据有误，请检查数据是否正确！"));
        } else if (bytes.length >= DATA_LENGTH) { // 正确数据
            Log.i(TAG, "start checking......");
            //====================正确数据，开始解析====================
            //<协议解析>
            boolean catchOneData = false;//是否找到一个完整的数据帧,
            byte[] datas = null;
            int index = 0;
            //2.完整性判断
            while (bytes != null && (bytes.length - index) >= DATA_LENGTH) {
                //2.1 查找数据头
                if (bytes[index] == DATA_HEAD) {
                    datas = new byte[DATA_LENGTH];
                    //复制一条完整数据到具体的数据缓存
                    for (int i = 0; i < DATA_LENGTH; i++) {
                        datas[i] = bytes[i + index];
                    }
                    Log.d(TAG, "Hex DATA " + StringUtil.byteToHexString(datas));
                    catchOneData = true;//找到了一个完整的
                    break;
                } else {
                    index++;
                }
            }
            Log.d(TAG, "catchOneData:  " + catchOneData + "-----------true代表拿到值------------");
            //分析数据
            if(catchOneData) {//如果找到了一个完整的数据帧
                //CF 03 99 B1 00 47 00 00 00 00 00 00 00 00 00 00
                if (datas[0] == DATA_HEAD)//体重秤
                {
                    int iWValue = (datas[4]&0xFF) * 0x100 + (datas[5]&0xFF);
                    float fWValue = iWValue / 10f;  //得到的体重kg
                    int iBFPValue = (datas[6]&0xFF) * 0x100 + datas[7]&0xFF;
                    int BFPValue = iBFPValue / 10;  //得到的体脂肪率：BFP
                    float height = (datas[3]&0xFF) / 100f;  //以米为单位的身高
                    float BMIValue = StringUtil.getDecimalsOne(fWValue / Math.pow(height, 2));  //体质指数

                    System.out.println(fWValue + "; " + iBFPValue + "; " + BMIValue);

                    SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_WEIGHT, fWValue, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
                    SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_BFP, BFPValue, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
                    SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_BMI, BMIValue, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
                    Log.d(TAG, "value is ..... " + fWValue + "kg;  ");
                    EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, "体重结果为" + fWValue + "kg"));
                    EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CLOSE_BT_DEVICE, null));
                    EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_RESET, null));
                }
            }
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
        List<RecordItem>[] lists = new List[5];
        lists[0] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_WEIGHT);
        lists[1] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_HEIGHT);
        lists[2] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_WAIST);
        lists[3] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_BFP);
        lists[4] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_BMI);
        return lists;
    }

    @Override
    public List<RecordItem> getRecordAllList(String mUniqueKey) {
        return SysApp.getMyDBManager().getRecordAllInfoByType(mUniqueKey,DATA_WEIGHT, DATA_HEIGHT, DATA_WAIST, DATA_BFP, DATA_BMI);
    }

    @Override
    public String[] getInsName() {
        return new String[]{"体重", "身高", "腰围", "体脂", "BMI指数"};
    }

    @Override
    public String[] getInsUnit() {
        return new String[]{"kg", "cm", "cm", "%", ""};
    }

    @Override
    public int[] getInsRange() {
        return new int[]{2,1,1,1,2};
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
        return 0;
    }

    public byte[] getSendWeightBytes() {
        int height;
        RecordItem item = SysApp.getMyDBManager().getOneRecordItem(nativeRecordId, DATA_HEIGHT);
        if(item != null) {
            height = (int) item.getValue1();
            if (gender < 0) {
                Member member = SysApp.getMyDBManager().getWaitInspectorMemberInfo(SysApp.getAccountBean().getUniqueKey(), nativeRecordId);
                if (member != null) {
                    gender = StringUtil.getGenderInt(member.getSex());
                    age = DateUtil.getAgeByBirthDayStr(member.getmBrithday());
                } else {
                    height = 0;
                    gender = 1;
                }
            }
            //        FE  P3   男     普通   170  25   KG
            //HEX数据：FE  03   01      00   AA   19   01    B0
            //BYTE8 校验和：BYTE2-BYTE7的校验和

            byte[] send = new byte[8];
            send[0] = (byte) 0xFE;
            send[1] = (byte) 0x00;    //组别
            send[2] = (byte) gender;  //性别字段
            send[3] = (byte) 0x00;     //运动员级别
            send[4] = (byte) height;   //身高
            send[5] = (byte) age;     //年龄
            send[6] = (byte) 0x01;     //体重单位：01（kg）
            send[7] = (byte) 0xB0;     //异或校验和(byte2-byte7)
            int total = send[1] & 0xFF;
            for (int i = 2; i < 7; i++) {
                int value = send[i] & 0xFF;
                total = total ^ value;
            }
            send[7] = (byte) total;
            return send;
        }
        return null;
    }
}
