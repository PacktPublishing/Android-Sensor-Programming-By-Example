package com.weatherutility;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class PressureAltitudeActivity extends Activity implements SensorEventListener{
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private boolean isSensorPresent;
	private TextView mPressureValue;
	private TextView mAltitudeValue;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pressurealtitude_layout);
        mPressureValue = (TextView)findViewById(R.id.pressuretext);
        mAltitudeValue = (TextView)findViewById(R.id.altitudetext);
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
			 mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
			 isSensorPresent = true;
		} else {
        	isSensorPresent = false;
        	mPressureValue.setText("Pressure Sensor is not available!");
        	mAltitudeValue.setText("Cannot calculate altitude, as pressure Sensor is not available!");
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
   		float pressure = event.values[0];
   		mPressureValue.setText("Pressure in mbar is " + pressure);
   		float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
   		mAltitudeValue.setText("Current altitude is " + altitude);
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
