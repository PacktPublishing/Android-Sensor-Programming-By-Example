package com.weatherutility;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CompareSensorActivity extends Activity implements SensorEventListener, ConnectionCallbacks, OnConnectionFailedListener{

	private SensorManager mSensorManager;
	private Sensor mHumiditySensor;
	private Sensor mTemperatureSensor;
	private Sensor mPressureSensor;
	private boolean isHumiditySensorPresent;
	private boolean isTemperatureSensorPresent;
	private boolean isPressureSensorPresent;
	private TextView mRelativeHumiditySensorValue;
	private TextView mPressureSensorValue;
	private TextView mTemperatureSensorValue;
	private TextView mRelativeHumidityWSValue;
	private TextView mPressureWSValue;
	private TextView mTemperatureWSValue;
	protected GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comparesensor_layout);

		mRelativeHumiditySensorValue = (TextView)findViewById(R.id.relativehumiditytext);
		mTemperatureSensorValue = (TextView)findViewById(R.id.temperaturetext);
		mPressureSensorValue = (TextView)findViewById(R.id.pressuretext);
		
		mRelativeHumidityWSValue = (TextView)findViewById(R.id.relativehumiditywstext);
		mPressureWSValue = (TextView)findViewById(R.id.pressurewstext);
		mTemperatureWSValue = (TextView)findViewById(R.id.temperaturewstext);
		
		mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		if(mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
			mHumiditySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
			isHumiditySensorPresent = true;
		} else {
			mRelativeHumiditySensorValue.setText("Relative Humidity Sensor is not available!");
            isHumiditySensorPresent = false;
		}
		if(mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
			mTemperatureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
			isTemperatureSensorPresent = true;
		} else {
			isTemperatureSensorPresent = false;
			mTemperatureSensorValue.setText("Temperature Sensor is not available!");
		}
		if(mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
			mPressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
			isPressureSensorPresent = true;
		} else {
			isPressureSensorPresent = false;
			mPressureSensorValue.setText("Pressure Sensor is not available!");
		}
        buildGoogleClient();
	}

	public void buildGoogleClient()
	{
		mGoogleApiClient = new GoogleApiClient.Builder(this).
                                addConnectionCallbacks(this).
                                addOnConnectionFailedListener(this).
                                addApi(LocationServices.API).
                                build();
    }


	@Override
	public void onConnected(Bundle connectionHint) {

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		new WeatherAsyncTask().execute(mLastLocation);
		
	}

	@Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

	@Override
	protected void onResume() {
		super.onResume();
		if(isHumiditySensorPresent) {
			mSensorManager.registerListener(this, mHumiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		if(isTemperatureSensorPresent) {
			mSensorManager.registerListener(this, mTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		if(isPressureSensorPresent) {
			mSensorManager.registerListener(this, mPressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(isHumiditySensorPresent || isTemperatureSensorPresent || isPressureSensorPresent) {
			mSensorManager.unregisterListener(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSensorManager = null;
		mHumiditySensor = null;
		mTemperatureSensor = null;
		mPressureSensor = null;
	}

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType()==Sensor.TYPE_RELATIVE_HUMIDITY) {
			mRelativeHumiditySensorValue.setText("Relative Humidity from Phone Sensor in % is " + event.values[0]);
		} else if(event.sensor.getType()==Sensor.TYPE_AMBIENT_TEMPERATURE) {
			mTemperatureSensorValue.setText("Temperature from Phone Sensor in degree Celsius is " + event.values[0]);
		} else if(event.sensor.getType()==Sensor.TYPE_PRESSURE) {
			mPressureSensorValue.setText("Pressure from Phone Sensor in mbar is " + event.values[0]);
		}
	}

	public class WeatherAsyncTask extends AsyncTask<Object, Void, String>{

		private float mTemperature;
		private float mPressure;
		private float mRelativeHumidity;
		private String mUrlString = "http://api.openweathermap.org/data/2.5/weather?";
		private Location mLastKnownLocation;
		private boolean isResponseSuccessful = false;
        private String AppId = "5bcc10ceaffa83dfb77056b5470b1e46";//Replace with your own AppId
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(isResponseSuccessful)
			{
				runOnUiThread(new Runnable() {

	                @Override
	                public void run() {
	                	
	                	mRelativeHumidityWSValue.setText("Relative humidity from Web service in % is " + mRelativeHumidity);
	        			mPressureWSValue.setText("Pressure from Web service in mbar is " + mPressure);
	        			mTemperatureWSValue.setText("Temperature from Web service in Celsius is " + mTemperature);
	                }
	            });
			}
		}

		@Override
		protected String doInBackground(Object... params) {
			mLastKnownLocation = (Location)params[0];
			String urlparams = mUrlString + "lat="+mLastKnownLocation.getLatitude()+"&lon="+mLastKnownLocation.getLongitude()+"&units=metric&APPID="+AppId;
			try {
				URL url = new URL(urlparams);
				HttpURLConnection mHttpURLConnection = (HttpURLConnection) url.openConnection();
                mHttpURLConnection.setRequestMethod("GET");
                mHttpURLConnection.connect();
				BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mHttpURLConnection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = mBufferedReader.readLine()) != null)
				{
					response.append(inputLine);
				}
                mBufferedReader.close();
                mHttpURLConnection.disconnect();
				JSONObject responseObject = new JSONObject(response.toString());
				if(!responseObject.isNull("main"))
				{
					JSONObject mainJsonObject = responseObject.getJSONObject("main");
					mTemperature = (float) mainJsonObject.getDouble("temp");
					mPressure = (float) mainJsonObject.getDouble("pressure");
					mRelativeHumidity = (float) mainJsonObject.getDouble("humidity");
					isResponseSuccessful = true;
				}
				
			} catch (Exception e) {
			}
			return null;
		}
	}
}
