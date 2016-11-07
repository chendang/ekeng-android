package com.foxchen.health.activities;

import android.content.Context;
import android.content.Intent;
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
import com.cnnet.otc.health.activities.LoginActivity;
import com.cnnet.otc.health.activities.LoginForgetPassActivity;
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
        findViewById(R.id.btn_back).setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
        Button wifiConectBtn = (Button) findViewById(R.id.wifiConectBtn);
        wifiConectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wifipassword = wifiPassWordText.getText().toString();
//                wifipassword = "13601812485";
                if(wifipassword.length() == 0){
                    Toast.makeText(mContext,"请输入WIFI密码",Toast.LENGTH_SHORT).show();    //弹出一个自动消失的提示框
                    return;
                }
                Intent intent = new Intent(mContext, DetectWifiSerchActivity.class);
                intent.putExtra("SSID", wifiInfo.getSSID());
                intent.putExtra("WIFIPassword", wifipassword);
                startActivity(intent);

            }
        });

    }

}
