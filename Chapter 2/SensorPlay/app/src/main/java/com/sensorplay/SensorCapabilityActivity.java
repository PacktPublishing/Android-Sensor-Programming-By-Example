package com.sensorplay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.TextView;

public class SensorCapabilityActivity extends Activity {

	private SensorManager mSensorManager;
	private int mSensorType;
	private Sensor mSensor;
	private TextView mSensorNameTextView;
	private TextView mSensorMaximumRangeTextView;
	private TextView mSensorMinDelayTextView;
	private TextView mSensorPowerTextView;
	private TextView mSensorResolutionTextView;
	private TextView mSensorVendorTextView;
	private TextView mSensorVersionTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 setContentView(R.layout.capability_layout);
		
		 Intent intent = getIntent();
		 mSensorType = intent.getIntExtra(getResources().getResourceName(R.string.sensor_type), 0);
		 mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		 mSensor = mSensorManager.getDefaultSensor(mSensorType);
		 
		 mSensorNameTextView = (TextView)findViewById(R.id.sensor_name);
		 mSensorMaximumRangeTextView = (TextView)findViewById(R.id.sensor_range);
		 mSensorMinDelayTextView = (TextView)findViewById(R.id.sensor_mindelay);
		 mSensorPowerTextView = (TextView)findViewById(R.id.sensor_power);
		 mSensorResolutionTextView = (TextView)findViewById(R.id.sensor_resolution);
		 mSensorVendorTextView = (TextView)findViewById(R.id.sensor_vendor);
		 mSensorVersionTextView = (TextView)findViewById(R.id.sensor_version);
		 
		 
		 mSensorNameTextView.setText(mSensor.getName());
		 mSensorMaximumRangeTextView.setText(String.valueOf(mSensor.getMaximumRange()));
		 mSensorMinDelayTextView.setText(String.valueOf(mSensor.getMinDelay()));
		 mSensorPowerTextView.setText(String.valueOf(mSensor.getPower()));
		 mSensorResolutionTextView.setText(String.valueOf(mSensor.getResolution()));
		 mSensorVendorTextView.setText(String.valueOf(mSensor.getVendor()));
		 mSensorVersionTextView.setText(String.valueOf(mSensor.getVersion()));
		 
	}
	
	public void onClickSensorValues(View v)
	{
		Intent intent = new Intent(getApplicationContext(), SensorValuesActivity.class);
		intent.putExtra(getResources().getResourceName(R.string.sensor_type), mSensorType);
		startActivity(intent);
	}
	

	@Override
	public void onBackPressed() {
		finish();
		NavUtils.navigateUpFromSameTask(this);
	}
	
}
