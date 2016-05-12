package com.weatherutility;

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

    public void navigateToTemperatureActivity(View v)
    {
        Intent mIntent = new Intent(this, TemperatureActivity.class);
        startActivity(mIntent);
    }

    public void navigateToPressureAltitudeActivity(View v)
    {
        Intent mIntent = new Intent(this, PressureAltitudeActivity.class);
        startActivity(mIntent);
    }

    public void navigateToHumidityActivity(View v)
    {
        Intent mIntent = new Intent(this, HumidityActivity.class);
        startActivity(mIntent);
    }

    public void navigateToCompareSensorActivity(View v)
    {
        Intent mIntent = new Intent(this, CompareSensorActivity.class);
        startActivity(mIntent);
    }



}
