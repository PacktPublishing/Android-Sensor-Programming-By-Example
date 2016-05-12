package com.css.pedometer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.css.pedometer.models.DateStepsModel;
import java.util.ArrayList;
import java.util.Calendar;

public class StepsDBHelper extends SQLiteOpenHelper
{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "StepsDatabase";
	private static final String TABLE_STEPS_SUMMARY = "StepsSummary";
	private static final String ID = "id";
	private static final String STEPS_COUNT = "stepscount";
	private static final String CREATION_DATE = "creationdate";

	private static final String CREATE_TABLE_STEPS_SUMMARY = "CREATE TABLE "
			+ TABLE_STEPS_SUMMARY + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CREATION_DATE + " TEXT,"+ STEPS_COUNT + " INTEGER"+")";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_STEPS_SUMMARY);
	}

	public boolean createStepsEntry() {

		boolean isDateAlreadyPresent = false;
		boolean createSuccessful = false;
		int currentDateStepCounts = 0;
		Calendar mCalendar = Calendar.getInstance();
		String todayDate = String.valueOf(mCalendar.get(Calendar.MONTH))+"/" +
                String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)+1)+"/"+String.valueOf(mCalendar.get(Calendar.YEAR));
		String selectQuery = "SELECT " + STEPS_COUNT + " FROM " +
                TABLE_STEPS_SUMMARY + " WHERE " + CREATION_DATE +" = '"+ todayDate+"'";
		try {
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor c = db.rawQuery(selectQuery, null);
	        if (c.moveToFirst()) {
	            do {
	            	isDateAlreadyPresent = true;
	            	currentDateStepCounts = c.getInt((c.getColumnIndex(STEPS_COUNT)));
	            } while (c.moveToNext());
	        }
	        db.close();
    	} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(CREATION_DATE, todayDate);
			if(isDateAlreadyPresent) {
				values.put(STEPS_COUNT, ++currentDateStepCounts);
				int row = db.update(TABLE_STEPS_SUMMARY, values,  CREATION_DATE +" = '"+ todayDate+"'", null);
				if(row == 1) {
					createSuccessful = true;
				}
				db.close();
			} else {
				values.put(STEPS_COUNT, 1);
				long row = db.insert(TABLE_STEPS_SUMMARY, null, values);
				if(row!=-1) {
					createSuccessful = true;
				}
				db.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return createSuccessful;
	}
	
	public ArrayList<DateStepsModel> readStepsEntries() {

		ArrayList<DateStepsModel> mStepCountList = new ArrayList<DateStepsModel>();
		String selectQuery = "SELECT * FROM " + TABLE_STEPS_SUMMARY;
		try {
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor c = db.rawQuery(selectQuery, null);
	        if (c.moveToFirst()) {
	            do {
	            	DateStepsModel mDateStepsModel = new DateStepsModel();
	            	mDateStepsModel.mDate = c.getString((c.getColumnIndex(CREATION_DATE)));
	            	mDateStepsModel.mStepCount = c.getInt((c.getColumnIndex(STEPS_COUNT)));
	            	mStepCountList.add(mDateStepsModel);
	            } while (c.moveToNext());
	        }
	        db.close();
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return mStepCountList;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_STEPS_SUMMARY);
		this.onCreate(db);
	}

    public StepsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
}
