package com.foxchen.health.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.HBuilder.integrate.R;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cnnet.otc.health.managers.RequestManager;
import com.cnnet.otc.health.util.SHA1Util;
import com.cnnet.otc.health.util.ToastUtil;
import com.lifesense.ble.LsBleManager;
import com.lifesense.ble.ReceiveDataCallback;
import com.lifesense.ble.SearchCallback;
import com.lifesense.ble.bean.BloodPressureData;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.bean.PedometerData;
import com.lifesense.ble.bean.constant.BroadcastType;
import com.lifesense.ble.bean.constant.DeviceConnectState;
import com.lifesense.ble.bean.constant.DeviceType;
import com.lifesense.ble.bean.constant.PacketProfile;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 1、乐心服务器获取mac信息
 * 2、提交设备绑定关系
 * 3、扫描当前设备
 * 4、获取当前设备检测数据信息
 * 5、提交检查数据
 */
public class LSBlueAddSyncActivity extends Activity {
    private TextView textView;
    private RotateLoading rotateLoading;
    private String appid= "x8e0vrtofpopbnj23tsypep16wlpdn0a43qaqtgm";
    private String appsecrect= "csfxmp991g2zeklogosboeyg976proaavx10bxlq";
    private String timestamp ;
    private String nonce ="";
    private String sn;
    private String mac;
    private String checksum ;
    private Context mContext;
    private LsBleManager lsBleManager;
    private LsDeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lsblue_add_sync);
        mContext=this;
        init();
        initBletoothManager();
        initOptionalBletoothManager();

        sn=getIntent().getStringExtra("SN");


            if(!lsBleManager.isSupportLowEnergy())
            {
                //判断当前设备的手机是否支持蓝牙4.0
                ToastUtil.TextToast(getBaseContext(), R.string.device_not_surport_ble4, 5000);
                this.finish();
                return;

            }
            if(!lsBleManager.isOpenBluetooth())
            {
                //判断当前手机的蓝牙功能是否处于打开状态
                ToastUtil.TextToast(getBaseContext(), R.string.device_not_open_ble, 2000);
                Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
            }else{
                if(getIntent().getStringExtra("MAC")==null)
                    getDeviceInfo();
                else
                    startScan(mac);
            }
    }
    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            //搜索到设备
            if (msg.what == 000000) {
                textView.setText("已连接上设备,准备同步数据,请稍等．．．");
            }
            //成功连接到设备
            if (msg.what == 111111) {
                textView.setText("正在同步数据,请稍等．．．");
            }
            //成功获取到设备
            if(msg.what==666666){
                //激活定时器,隔两秒发现数据没有变化即认为已经读取完毕

            }
        }
    };
    private void init(){
        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading);
        rotateLoading.start();
        textView= (TextView) findViewById(R.id.lsblueaddsyncres);
        textView.setText("连接设备同步数据开始，请稍等．．．");
    }
    // 初始化
    private void initBletoothManager() {
        // 创建一个管理器对象实例,单例
        lsBleManager = LsBleManager.getInstance();
        // 对象实例初始化
        lsBleManager.initialize(getApplicationContext());
    }

    // 可选初始化
    private void initOptionalBletoothManager() {
        // 是否开启蓝牙调试日志
        lsBleManager.enableWriteDebugMessageToFiles(true,
                LsBleManager.WRITE_FILE_PERMISSION_KEY);
        // 设置蓝牙连接log信息写入的文件的路径
        lsBleManager.setBlelogFilePath(Environment
                .getExternalStorageDirectory().getPath()
                + File.separator
                + "LsBluetoothDemo\report", "+*", "2.0");
    }
    private  void getDeviceInfo(){

        timestamp = String.valueOf(new Date().getTime());
        Random random = new Random();
        for(int i=0;i<8;i++){
            nonce += String.valueOf(random.nextInt(10)) ;
        }

        Log.d("debug","Rand = "+nonce);

        sn = "0301570101062540";
//        sn=getIntent().getStringExtra("SN");
        checksum=appid+appsecrect+timestamp+nonce;
        try {
            Map parmMap= new HashMap<String,String>();
            parmMap.put("value",sn);
            parmMap.put("keytype","sn");
            RequestManager.getLSDeviceInfo(mContext,new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.d("getLSDeviceInfo:", String.valueOf(jsonObject));
                            try {
                                JSONObject dataJson= jsonObject.getJSONObject("data");
                                 mac = dataJson.getString("mac");
                                textView.setText("远程核对信息无误．．．");
                                Log.d("getmacinfo:", transStrToMac(mac));
                                startScan(transStrToMac(mac));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("getLSDeviceInfo:", String.valueOf(volleyError));
                            //未取到设备信息--直接跳回h5处理
                            textView.setText("设备信息没有取到，请核对输入设备信息是否有误．．．");
                        }
                    },parmMap,appid,timestamp,nonce.toString(), SHA1Util.getSHA(checksum));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    // 要扫描的设备类型
    private List<DeviceType> getDeviceTypes() {
        List<DeviceType> mScanDeviceType = null;

        mScanDeviceType = new ArrayList<DeviceType>();
        // 血压计
        mScanDeviceType.add(DeviceType.SPHYGMOMANOMETER);
        // 脂肪秤
        mScanDeviceType.add(DeviceType.FAT_SCALE);
        // 体重秤
        mScanDeviceType.add(DeviceType.WEIGHT_SCALE);
        // 体重秤
        mScanDeviceType.add(DeviceType.HEIGHT_RULER);
        // 计步器
        mScanDeviceType.add(DeviceType.PEDOMETER);
        // 厨房秤
        mScanDeviceType.add(DeviceType.KITCHEN_SCALE);

        return mScanDeviceType;
    }

    // 开始扫描
    private void startScan(final String mac) {
        // 广播类型，一般选全部广播
        BroadcastType mBroadcastType = BroadcastType.ALL;
        textView.setText("正在搜索该设备．．．");
        lsBleManager.searchLsDevice(new SearchCallback() {
            @Override
            public void onSearchResults(LsDeviceInfo lsDevice) {
                // 扫描到的设备
                if (lsDevice.getMacAddress().equalsIgnoreCase(
                        mac)) {
                    //D9:72:83:71:33:3F dc:0e:ae:71:b3:a0
                        System.err.println("lsDevice:" + lsDevice);
                    //连接上上设备
                    handler.sendEmptyMessage(000000);//发送消息

                    // 停止扫描
                    stopScan();
                    // 获取要连接设备
                    deviceInfo = lsDevice;
                    // 读取手环数据
                    addReceiveDevice();
                    startReceiveDate();
                    // 手环ota
                    // startOtaForPedometer();
                }
            }
        }, getDeviceTypes()/* 设备类型* */, mBroadcastType/* 广播类型* */);
    }

    // 停止扫描
    private void stopScan() {
        lsBleManager.stopSearch();
    }

    // 添加在接收数据的设备
    private void addReceiveDevice() {
        lsBleManager.addMeasureDevice(deviceInfo);
    }

    // 接收数据
    private void startReceiveDate() {
        lsBleManager.startDataReceiveService(new ReceiveDataCallback() {
            // 设备的连接状态
            @Override
            public void onDeviceConnectStateChange(
                    final DeviceConnectState connectState, String broadcastId) {

                if (connectState == DeviceConnectState.CONNECTED_SUCCESS) {
                    // 连接成功
                    handler.sendEmptyMessage(111111);//发送消息
//					setEncourage();
                } else if (connectState == DeviceConnectState.CONNECTED_FAILED
                        || connectState == DeviceConnectState.DISCONNECTED) {
                    // 连接失败
                }

            }

            // 血压计测量数据
            @Override
            public void onReceiveBloodPressureData(
                    final BloodPressureData bpData) {
            }

            // 体重秤数据
            @Override
            public void onReceiveWeightScaleMeasureData(Object dataObject,
                                                        PacketProfile packetType, String sourceData) {
            }

            // 接收手环返回的测量数据
            @Override
            public void onReceivePedometerMeasureData(final Object dataObject,
                                                      final PacketProfile packetType, final String sourceData) {

               /* if(dataObject!=null){
                    Message msg=new Message();
                    msg.what=666666;
                    msg.obj=dataObject;
                    handler.sendMessage(msg);
                }
                List dataObjectList=(List)dataObject;
                for (int i =0;i<=dataObjectList.size();i++){
                    PedometerData pd= (PedometerData) dataObjectList.get(i);

                }*/
//                RequestManager.postLSBlueData();

                System.err.println("dataObject:" + dataObject);

            }
        });
    }

    // 停止接收设备数据
    private void stopReceiver() {
        lsBleManager.stopDataReceiveService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopOtaForPedometer();
        stopReceiver();
    }
    // 取消手环升级
    private void stopOtaForPedometer() {
        lsBleManager.interruptUpgradeProcess(mac);
    }

    private String transStrToMac(String mac){
        String resMac="";
        for (int i = 0; i < mac.length(); i = i + 2) {
            resMac+=mac.substring(i,i+2);
            if(i+2 <mac.length())
                resMac+=":";
        }
        mac= resMac;
        return resMac;
    }
}
