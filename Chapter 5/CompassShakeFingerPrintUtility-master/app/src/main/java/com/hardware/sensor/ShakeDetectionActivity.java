package com.hardware.sensor;


import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;


public class ShakeDetectionActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private boolean isAccelerometerPresent;
    private float x, y, z, last_x, last_y, last_z;
    private boolean isFirstValue;
    private float shakeThreshold = 3f;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shakedetection_layout);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerPresent = true;
        } else {
            isAccelerometerPresent = false;
        }
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mario);
    }

    protected void onResume() {
        super.onResume();
        if(isAccelerometerPresent) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    protected void onPause() {
        super.onPause();
        if(isAccelerometerPresent) {
            mSensorManager.unregisterListener(this, mAccelerometer);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        if(isFirstValue) {
            float deltaX = Math.abs(last_x - x);
            float deltaY = Math.abs(last_y - y);
            float deltaZ = Math.abs(last_z - z);
             // If the values of acceleration have changed on at least two axises, then we assume that we are in a shake motion
            if((deltaX > shakeThreshold && deltaY > shakeThreshold) || (deltaX > shakeThreshold && deltaZ > shakeThreshold)
                    || (deltaY > shakeThreshold && deltaZ > shakeThreshold)) {
                //Don't play sound, if it is already being played
                if(!mMediaPlayer.isPlaying()) {
                    //Play the sound, when Phone is Shaking
                    mMediaPlayer.start();
                }
            }
        }
        last_x = x;
        last_y = y;
        last_z = z;
        isFirstValue = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
