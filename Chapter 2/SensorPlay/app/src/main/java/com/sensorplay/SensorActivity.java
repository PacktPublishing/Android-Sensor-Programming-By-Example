package com.sensorplay;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class SensorActivity extends Activity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_main);
		 
		 mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		 if(mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null)
		 {
			 mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		 }
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mSensorManager = null;
		mSensor = null;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

}
