package com.cnnet.otc.health.bean.data;

import android.content.Context;
import android.util.Log;

import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.db.DBHelper;
import com.cnnet.otc.health.events.BTConnetEvent;
import com.cnnet.otc.health.interfaces.MyCommData;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.views.MyLineChartView;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 血脂
 * Created by SZ512 on 2016/1/6.
 */
public class LipidData implements MyCommData {

    private final String TAG = "LipidData";

    /**
     * 图表对象
     */
    private MyLineChartView myLineChartView;

    private Context context;

    private long nativeRecordId;

    /**
     * 总胆固醇数据字段
     */
    public static final String DATA_CHOLESTEROL = "CHOL";

    /**
     * 甘油三酯数据字段
     */
    public static final String DATA_TRIGLYCERIDES = "TG";

    private String allDatas = null;
    private int dateLength = 0;

    private final int HEAD_LENGTH = 6;
    private final String HEAD_DIGIT = "0A4E0A";
    private final int END_LENGTH = 10;
    private final String END_DIGIT = "220A0A4E0A";

    public LipidData(Context context, MyLineChartView myLineChartView, long nativeRecordId) {
        this.context = context;
        this.myLineChartView = myLineChartView;
        this.nativeRecordId = nativeRecordId;
    }

    @Override
    public void todo(byte[] bytes) {
        try {
            String hexData = StringUtil.byteToHexString(bytes);//将数据进行Hex运算后得到的结果字符串
            // 0A 4E 0A 00 00 00 00 00
            // 4136302C000000003030303034302C302C340000000000002C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C003030303038302C302C342C312C312C4E2C22200000000000202020202020202020202020202000002020202020202020200000000000000020220A4136302C3030303132302C302C342C312C312C00004E2C222020202020200000000000000020202020202020202020000000000000202020202020202020220A41363000002C3030303136302C302C342C312C312C4E2C222020202020202020202020202020202020202020002020202020220A004136302C000000003030303230302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303234302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C0000000000003030303238302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303332302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303336302C302C34000000002C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303430302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303434302C302C342C312C312C4E2C2220202020202020200000000000002020202020202020202020202020202020220A4136302C3030303438302C302C342C312C312C4E2C222020202020202020202020202020202020202020202020
            // 20 20 22 0A 0A 4E 0A 00

            if (StringUtil.isNotEmpty(allDatas)) {
                allDatas += hexData;
                dateLength += bytes.length;
            } else {
                allDatas = hexData;
                dateLength = bytes.length;
            }

//            Log.e(TAG, " -- " + allDatas);
//        Log.d(TAG,"********************************************");
//        Log.d(TAG, "normal -- " + new String(tempDatas));
            int index = 0;
            boolean isPacket = false;
            String checkeResult = null;
            if (dateLength >= HEAD_LENGTH) {
                if (allDatas.startsWith(HEAD_DIGIT)) {
                    isPacket = true;
                }
                if (isPacket && dateLength >= END_LENGTH) {
                    if (allDatas.endsWith(END_DIGIT)) {
                        checkeResult = new String(StringUtil.hexStringToBytes(allDatas));
                    } else {
                        isPacket = false;
                    }
                } else {
                    isPacket = false;
                }
            }


        /*

                N
                A60,000040,0,4,1,1,N,"??     : 3093537"
                A60,000080,0,4,1,1,N,"??      : 2015 06 17"
                A60,000120,0,4,1,1,N,"??      : 12:35"
                A60,000160,0,4,1,1,N,"??      : ????"
                A60,000200,0,4,1,1,N,"??      : P443"
                A60,000240,0,4,1,1,N,"CHOL    : <  2.59 mmol/L"
                A60,000280,0,4,1,1,N,"HDL CHOL: <  0.39 mmol/L"
                A60,000320,0,4,1,1,N,"TRIG    :  0.65 mmol/L"
                A60,000360,0,4,1,1,N,"CALC LDL: ---- "
                A60,000400,0,4,1,1,N,"TC/HDL  : ---- "
                P1

                N
                A60,000040,0,4,1,1,N,"                         "
                A60,000080,0,4,1,1,N,"                         "
                A60,000120,0,4,1,1,N,"                         "
                A60,000160,0,4,1,1,N,"                         "
                A60,000200,0,4,1,1,N,"                         "
                A60,000240,0,4,1,1,N,"                         "
                A60,000280,0,4,1,1,N,"                         "
                A60,000320,0,4,1,1,N,"                         "
                A60,000360,0,4,1,1,N,"                         "
                A60,000400,0,4,1,1,N,"                         "
                A60,000440,0,4,1,1,N,"                         "
                A60,000480,0,4,1,1,N,"                         "

                N

                 */

            if (isPacket && checkeResult != null) {
                Log.d(TAG, "allDatas str : " + allDatas);
                Log.d(TAG, "resultStr str : " + checkeResult);
                //总胆固醇
                String CHOL = "\"CHOL";  //
                final int CHOL_LENGTH = 5;
                String str_dgc = null;
                index = 0; //记录匹配字符的位置
                while ((index = checkeResult.indexOf(CHOL, index)) >= 0) {
                    String sgData = checkeResult.substring(index, index + 30).trim();
                    int int_f = sgData.lastIndexOf(":");
                    int int_f2 = sgData.indexOf("m");
                    str_dgc = sgData.substring(int_f + 1, int_f2).trim();
                    index += CHOL_LENGTH;
                }

                //甘油三酯
                String str_gysz = null;
                String TRIG = "\"TRIG";
                final int TRIG_LENGTH = 5;
                index = 0; //记录匹配字符的位置
                while ((index = checkeResult.indexOf(TRIG, index)) >= 0) {
                    String phData = checkeResult.substring(index, index + 30).trim();
                    int int_f = phData.lastIndexOf(":");
                    int int_f2 = phData.indexOf("m");
                    str_gysz = phData.substring(int_f + 1, int_f2).trim();
                    index += TRIG_LENGTH;
                }
                Log.i(TAG, CHOL + ":  " + str_dgc + "  " + TRIG + ":  " + str_gysz);
                float cholValue = Float.parseFloat(StringUtil.getFirstFloatStr(str_dgc));
                float randomFloat = (float) Math.random() * 0.5f;
                if(str_dgc.startsWith("<")) {
                    cholValue = cholValue - randomFloat;
                } else if(str_dgc.startsWith(">")) {
                    cholValue = cholValue + randomFloat;
                }
                cholValue = StringUtil.getBigDecimal(3, cholValue);
                float trigValue = Float.parseFloat(StringUtil.getFirstFloatStr(str_gysz));
                Log.i(TAG, "two values:  " + cholValue + "  " + trigValue);
                SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_CHOLESTEROL, cholValue, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
                SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_TRIGLYCERIDES, trigValue, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
                EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, "总胆固醇" + str_dgc + ";甘油三酯" + str_gysz));
                dateLength = 0;
                allDatas = null;
                EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_RESET, null));
            }
        }catch (Exception e) {
            e.printStackTrace();;
        }
    }

    @Override
    public void todoConnected() {

    }

    @Override
    public void todoDisconnected() {

    }

    @Override
    public void todoConnecting() {

    }

    @Override
    public void todoDisconnected_failed() {

    }


    @Override
    public List<RecordItem>[] getRecordList(String mUniqueKey) {
        List<RecordItem>[] lists = new List[2];
        lists[0] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_CHOLESTEROL);
        lists[1] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_TRIGLYCERIDES);
        return lists;
    }

    @Override
    public List<RecordItem> getRecordAllList(String mUniqueKey) {
        return SysApp.getMyDBManager().getRecordAllInfoByType(mUniqueKey, DATA_CHOLESTEROL, DATA_TRIGLYCERIDES);
    }

    @Override
    public String[] getInsName() {
        return new String[]{"总胆固醇","甘油三酯"};
    }

    @Override
    public String[] getInsUnit() {
        return new String[]{"mmol/L", "mmol/L"};
    }

    @Override
    public int[] getInsRange() {
        return new int[]{2, 2};
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
}
