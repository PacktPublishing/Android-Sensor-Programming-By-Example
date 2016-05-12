package com.sensorplay;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;

public class SensorService extends Service implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private MediaPlayer mMediaPlayer;
	boolean isPlaying = false;
	private float mThreshold = 2.5f;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		 mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		 mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		 mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mario);
		 mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					
					isPlaying = false;
					
				}

			});
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
		
		return Service.START_NOT_STICKY;
		
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		
		 mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		double rateOfRotation = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
		if(rateOfRotation>mThreshold)
		{
			if(!isPlaying)
			{
				mMediaPlayer.start();
				isPlaying = true;
			}
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

}
