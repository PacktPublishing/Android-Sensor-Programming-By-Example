package com.css.fitnesstracker;

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

    public void navigateToSensorActivity(View v)
    {
        Intent mIntent = new Intent(this, SensorActivity.class);
        startActivity(mIntent);
    }

    public void navigateToHistoryActivity(View v)
    {
        Intent mIntent = new Intent(this, HistoryActivity.class);
        startActivity(mIntent);
    }

    public void navigateToSubscriptionActivity(View v)
    {
        Intent mIntent = new Intent(this, SubscriptionActivity.class);
        startActivity(mIntent);
    }

}
