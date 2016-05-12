package com.css.pedometer.views;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.css.pedometer.R;


public class StepsCounterActivity extends Activity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private boolean isSensorPresent;
	private TextView mStepsSinceReboot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stepcounter_layout);
        
        mStepsSinceReboot = (TextView)findViewById(R.id.stepssincereboot);
        
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
			isSensorPresent = true;
		} else {
			isSensorPresent = false;
		}
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		if(isSensorPresent) {
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(isSensorPresent) {
			mSensorManager.unregisterListener(this);
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		mStepsSinceReboot.setText("Steps since reboot:" + String.valueOf(event.values[0]));
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSensorManager = null;
        mSensor = null;
    }

}
