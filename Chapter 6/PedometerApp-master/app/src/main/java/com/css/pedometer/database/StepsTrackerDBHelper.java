package com.css.pedometer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class StepsTrackerDBHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "StepsTrackerDatabase";
	private static final String TABLE_STEPS_SUMMARY = "StepsTrackerSummary";
	private static final String ID = "id";
	private static final String STEP_TYPE = "steptype";
	private static final String STEP_TIME = "steptime";//time is in milliseconds Epoch Time
	private static final String STEP_DATE = "stepdate";//Date format is mm/dd/yyyy
	private static final String SESSION_ID = "sessionid";

	private static final String CREATE_TABLE_STEPS_SUMMARY = "CREATE TABLE "
			+ TABLE_STEPS_SUMMARY + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + STEP_DATE + " TEXT,"+ SESSION_ID + " TEXT,"+ STEP_TIME + " INTEGER,"+ STEP_TYPE + " TEXT"+")";

	public boolean createStepsEntry(long timeStamp, int stepType, String sessionId)
	{

		boolean createSuccessful = false;
		Calendar mCalendar = Calendar.getInstance(); 
		String todayDate = String.valueOf(mCalendar.get(Calendar.MONTH)+1)+"/" + String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(mCalendar.get(Calendar.YEAR));
        try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(STEP_TIME, timeStamp);
			values.put(STEP_DATE, todayDate);
			values.put(STEP_TYPE, stepType);
			values.put(SESSION_ID, sessionId);
			long row = db.insert(TABLE_STEPS_SUMMARY, null, values);
			if(row!=-1)
			{
				createSuccessful = true;
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return createSuccessful;
	}

	public int [] getStepsByDate(String date)
	{
		int stepType[] = new int[3];
		String selectQuery = "SELECT " + STEP_TYPE + " FROM " + TABLE_STEPS_SUMMARY +" WHERE " + STEP_DATE +" = '"+ date + "'";
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(selectQuery, null);
			if (c.moveToFirst()) {
				do {
					switch(c.getInt((c.getColumnIndex(STEP_TYPE))))
					{
					case WALKING: ++stepType[0];
					break;
					case JOGGING: ++stepType[1];
					break;
					case RUNNING: ++stepType[2];
					break;
					}
				} while (c.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stepType;
	}

    //For Debug Purposes
	public long getTotalStepsDuration()
	{
		long totalDuration = 0;
		ArrayList<String> sessionNameList = new ArrayList<String>();
		ArrayList<Integer> stepTimeSessionList = new ArrayList<Integer>();
		try {
			String selectQuery = "SELECT DISTINCT"+ SESSION_ID +" FROM " + TABLE_STEPS_SUMMARY;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(selectQuery, null);
			if (c.moveToFirst()) {
				do {
					sessionNameList.add(c.getString((c.getColumnIndex(SESSION_ID))));
				} while (c.moveToNext());
			}

			int sizeSessionNameList = sessionNameList.size();
			for (int i = 0; i < sizeSessionNameList; i++) {

				String selectTimeQuery = "SELECT "+ STEP_TIME +" FROM " + TABLE_STEPS_SUMMARY +" WHERE " + SESSION_ID +" = '"+ sessionNameList.get(i) + "'";
				Cursor cTime = db.rawQuery(selectTimeQuery, null);
				if (cTime.moveToFirst()) {
					do {
						stepTimeSessionList.add(cTime.getInt((cTime.getColumnIndex(STEP_TIME))));
					} while (cTime.moveToNext());
				}
				int sizeStepTimeSessionList = stepTimeSessionList.size();
				for (int j = sizeStepTimeSessionList-1; j == 1; j--) {
					totalDuration = totalDuration + stepTimeSessionList.get(j) - stepTimeSessionList.get(j-1);
				}
			}

			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalDuration;
	}

    //For Debug Purposes
	public ArrayList<String> getAvailableDates()
	{
		ArrayList<String> dateList = new ArrayList<String>();
		try {
			String selectQuery = "SELECT DISTINCT"+ STEP_DATE +" FROM " + TABLE_STEPS_SUMMARY;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(selectQuery, null);
			if (c.moveToFirst()) {
				do {
					dateList.add(c.getString((c.getColumnIndex(STEP_DATE))));
				} while (c.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dateList;
	}
	
	private static final int DATABASE_VERSION = 1;
	private static final int RUNNING = 3;
	private static final int JOGGING = 2;
	private static final int WALKING = 1;
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_STEPS_SUMMARY);
		this.onCreate(db);
	}
	
	public StepsTrackerDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_STEPS_SUMMARY);

	}

}
