package com.hardware.sensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SignificantMotionActivity extends Activity{

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sigmotion_layout);
        mTextView = (TextView)findViewById(R.id.text);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);

    }

    public void requestSignificantMotionTrigger(View v) {
        mSensorManager.requestTriggerSensor(new SignificantMotionListener(), mSensor);
        mTextView.setText("Significant Motion Trigger is Set, Waiting for Trigger!");
    }

    class SignificantMotionListener extends TriggerEventListener  {

        @Override
        public void onTrigger(TriggerEvent event) {
            mTextView.setText("Significant Motion Triggered!, Click again to request again.");
        }
    };

}
