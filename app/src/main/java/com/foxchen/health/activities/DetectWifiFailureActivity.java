package com.foxchen.health.activities;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.HBuilder.integrate.R;
import com.HBuilder.integrate.SDK_WebView;

public class DetectWifiFailureActivity extends Activity {

    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_wifi_failure);
        ctx=this;
        //创建监听
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new  Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, DetectWifiGuideActivity.class);
                startActivity(intent);
                finish();
            }

        });
        findViewById(R.id.btn_back).setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(this, DetectWifiGuideActivity.class);
        startActivity(intent);
    }
}
