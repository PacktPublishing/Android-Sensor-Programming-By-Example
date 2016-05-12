package com.weatherutility;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class TemperatureActivity extends Activity implements SensorEventListener{
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private boolean isSensorPresent;
	private TextView mTemperatureValue;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temperature_layout);
        
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mTemperatureValue = (TextView)findViewById(R.id.temperaturetext);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
			 mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
			 isSensorPresent = true;
		} else {
        	mTemperatureValue.setText("Ambient Temperature Sensor is not available!");
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
		mTemperatureValue.setText("Temperature in degree Celsius is " + event.values[0]);
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
