package com.css.pedometer.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.css.pedometer.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void navigateToStepCounterActivity(View v)
    {
        Intent mIntent = new Intent(this, StepsCounterActivity.class);
        startActivity(mIntent);
    }

    public void navigateToStepHistoryActivity(View v)
    {
        Intent mIntent = new Intent(this, StepsHistoryActivity.class);
        startActivity(mIntent);
    }

    public void navigateToCustomAlgoActivity(View v)
    {
        Intent mIntent = new Intent(this, CustomAlgoResultsActivity.class);
        startActivity(mIntent);
    }

}
