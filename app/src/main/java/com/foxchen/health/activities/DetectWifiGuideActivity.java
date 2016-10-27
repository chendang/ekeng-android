package com.foxchen.health.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.HBuilder.integrate.R;

public class DetectWifiGuideActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_wifi_guide);
        Button btn = (Button) findViewById(R.id.wifiNextBtn);
        btn.setOnClickListener(new  Button.OnClickListener(){//创建监听
            @Override
            public void onClick(View v) {
                 //下一步到设置页面
                Intent intent= new Intent(DetectWifiGuideActivity.this,DetectWifiActivity.class);
                startActivity(intent);
            }

        });
    }

}
