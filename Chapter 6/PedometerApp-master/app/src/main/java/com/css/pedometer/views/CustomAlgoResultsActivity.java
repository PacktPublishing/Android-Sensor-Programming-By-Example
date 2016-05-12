package com.css.pedometer.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.css.pedometer.R;
import com.css.pedometer.database.StepsTrackerDBHelper;
import com.css.pedometer.services.StepsTrackerService;
import java.util.Calendar;


public class CustomAlgoResultsActivity extends Activity{

	private TextView mTotalStepsTextView;
	private TextView mTotalDistanceTextView;
	private TextView mTotalDurationTextView;
	private TextView mAverageSpeedTextView;
	private TextView mAveragFrequencyTextView;
	private TextView mTotalCalorieBurnedTextView;
	private TextView mPhysicalActivityTypeTextView;
	StepsTrackerDBHelper mStepsTrackerDBHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capability_layout);

		mStepsTrackerDBHelper = new StepsTrackerDBHelper(this);

		mTotalStepsTextView = (TextView)findViewById(R.id.total_steps);
		mTotalDistanceTextView = (TextView)findViewById(R.id.total_distance);
		mTotalDurationTextView = (TextView)findViewById(R.id.total_duration);
		mAverageSpeedTextView = (TextView)findViewById(R.id.average_speed);
		mAveragFrequencyTextView = (TextView)findViewById(R.id.average_frequency);
		mTotalCalorieBurnedTextView = (TextView)findViewById(R.id.calories_burned);
		mPhysicalActivityTypeTextView = (TextView)findViewById(R.id.physical_activitytype);

		Intent stepsAnalysisIntent = new Intent(getApplicationContext(), StepsTrackerService.class);
		startService(stepsAnalysisIntent);
		
		calculateDataMatrix();
	}

	public void calculateDataMatrix() {

		Calendar calendar = Calendar.getInstance();
		String todayDate = String.valueOf(calendar.get(Calendar.MONTH)+1)+"/" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.YEAR));
		int stepType[] = mStepsTrackerDBHelper.getStepsByDate(todayDate);
		int walkingSteps = stepType[0];
		int joggingSteps = stepType[1];
		int runningSteps = stepType[2];

		//Calculating total steps
		int totalStepTaken = walkingSteps + joggingSteps + runningSteps;
		mTotalStepsTextView.setText(String.valueOf(totalStepTaken)+ " Steps");

		//Calculating total distance traveled
		float totalDistance = walkingSteps*0.5f + joggingSteps * 1.0f + runningSteps * 1.5f;
		mTotalDistanceTextView.setText(String.valueOf(totalDistance)+" meters");

		//Calculating total duration
		float totalDuration = walkingSteps*1.0f + joggingSteps * 0.7f + runningSteps * 0.4f;
		float hours = totalDuration / 3600;
		float minutes = (totalDuration % 3600) / 60;
		float seconds = totalDuration % 60;
		mTotalDurationTextView.setText(String.format("%.0f",hours) + " hrs " +  String.format("%.0f",minutes) + " mins " +  String.format("%.0f",seconds)+ " secs");

		//Calculating average speed
		if(totalDistance>0) {
			mAverageSpeedTextView.setText(String.format("%.2f", totalDistance/totalDuration)+" meter per seconds");
		} else {
			mAverageSpeedTextView.setText("0 meter per seconds");
		}

		//Calculating average step frequency
		if(totalStepTaken>0) {
			mAveragFrequencyTextView.setText(String.format("%.0f",totalStepTaken/minutes)+" steps per minute");
		} else {
			mAveragFrequencyTextView.setText("0 steps per minute");
		}

		//Calculating total calories burned
		float totalCaloriesBurned = walkingSteps * 0.05f + joggingSteps * 0.1f + runningSteps * 0.2f;
		mTotalCalorieBurnedTextView.setText(String.format("%.0f",totalCaloriesBurned)+" Calories");

		//Calculating type of physical activity
		mPhysicalActivityTypeTextView.setText(String.valueOf(walkingSteps) + " Walking Steps " +  "\n"+String.valueOf(joggingSteps) + " Jogging Steps " + 
				"\n"+String.valueOf(runningSteps)+ " Running Steps");
	}
}
