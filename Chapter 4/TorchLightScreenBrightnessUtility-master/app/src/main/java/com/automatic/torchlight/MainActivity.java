package com.automatic.torchlight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void navigateToLightSensorActivity(View v)
    {
        Intent mIntent = new Intent(this, LightSensorActivity.class);
        startActivity(mIntent);
    }

    public void navigateToProximitySensorActivity(View v)
    {
        Intent mIntent = new Intent(this, ProximitySensorActivity.class);
        startActivity(mIntent);
    }

}
