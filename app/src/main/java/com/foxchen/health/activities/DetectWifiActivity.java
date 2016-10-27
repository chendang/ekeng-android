package com.foxchen.health.activities;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.HBuilder.integrate.R;
import com.lifesense.wificonfig.LSWifiConfig;
import com.lifesense.wificonfig.LSWifiConfigCode;
import com.lifesense.wificonfig.LSWifiConfigDelegate;

public class DetectWifiActivity extends Activity {


    private EditText wifiNameText =null;
    private EditText wifiPassWordText=null;
    private WifiInfo wifiInfo =null;
    private String wifipassword=null;
    private Context mContext ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.loadLibrary("LSWifiConfig");
        setContentView(R.layout.activity_detect_wifi);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID",wifiInfo.getSSID());

        mContext=this;
        wifiNameText = (EditText) findViewById(R.id.wifiName);
        wifiNameText.setText("当前WIFI名称："+wifiInfo.getSSID());
        wifiNameText.setFocusable(false);
        wifiNameText.setEnabled(false);

        wifiPassWordText = (EditText) findViewById(R.id.wifipassword);
//
        Button wifiConectBtn = (Button) findViewById(R.id.wifiConectBtn);
        wifiConectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                wifipassword = wifiPassWordText.getText().toString();
                wifipassword = "13601812485";
                if(wifipassword.length() == 0){
                    Toast.makeText(mContext,"请输入WIFI密码",Toast.LENGTH_SHORT).show();    //弹出一个自动消失的提示框
                    return;
                }

                LSWifiConfig lsWifiConfig= LSWifiConfig.shared();
                lsWifiConfig.setTimeout(60);
                lsWifiConfig.startConfig(wifiInfo.getSSID(),wifipassword);
                lsWifiConfig.setDelegate(new LSWifiConfigDelegate(){
                    @Override
                    public void wifiConfigCallBack(LSWifiConfig lsWifiConfig, LSWifiConfigCode lsWifiConfigCode) {
                        Log.d("搜索结果", String.valueOf(lsWifiConfigCode.ordinal()));;
                    }
                });
            }
        });

    }

}
