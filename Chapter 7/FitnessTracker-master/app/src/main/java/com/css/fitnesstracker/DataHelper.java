package com.css.fitnesstracker;

import com.google.android.gms.fitness.data.DataType;

import java.util.ArrayList;

public class DataHelper {

    private static DataHelper INSTANCE;

        public static DataHelper getInstance()
        {
            if (INSTANCE == null) {
                INSTANCE = new DataHelper();
                populateArrayData();
            }
            return INSTANCE;
        }

    private static ArrayList<DataType> mDataTypeList = new ArrayList<DataType>();
    private static ArrayList<String> mSpinnerList = new ArrayList<String>();

        private static void populateArrayData()
        {
            mDataTypeList.add(null);
            mDataTypeList.add(DataType.TYPE_STEP_COUNT_DELTA);
            mDataTypeList.add(DataType.TYPE_STEP_COUNT_CUMULATIVE);
            mDataTypeList.add(DataType.TYPE_STEP_COUNT_CADENCE);
            mDataTypeList.add(DataType.TYPE_ACTIVITY_SEGMENT);
            mDataTypeList.add(DataType.TYPE_CALORIES_CONSUMED);
            mDataTypeList.add(DataType.TYPE_CALORIES_EXPENDED);
            mDataTypeList.add(DataType.TYPE_BASAL_METABOLIC_RATE);
            mDataTypeList.add(DataType.TYPE_POWER_SAMPLE);
            mDataTypeList.add(DataType.TYPE_ACTIVITY_SAMPLE);
            mDataTypeList.add(DataType.TYPE_HEART_RATE_BPM);
            mDataTypeList.add(DataType.TYPE_LOCATION_SAMPLE);
            mDataTypeList.add(DataType.TYPE_LOCATION_TRACK);
            mDataTypeList.add(DataType.TYPE_DISTANCE_DELTA);
            mDataTypeList.add(DataType.TYPE_DISTANCE_CUMULATIVE);
            mDataTypeList.add(DataType.TYPE_SPEED);
            mDataTypeList.add(DataType.TYPE_CYCLING_WHEEL_REVOLUTION);
            mDataTypeList.add(DataType.TYPE_CYCLING_WHEEL_RPM);
            mDataTypeList.add(DataType.TYPE_CYCLING_PEDALING_CUMULATIVE);
            mDataTypeList.add(DataType.TYPE_CYCLING_PEDALING_CADENCE);
            mDataTypeList.add(DataType.TYPE_HEIGHT);
            mDataTypeList.add(DataType.TYPE_WEIGHT);
            mDataTypeList.add(DataType.TYPE_BODY_FAT_PERCENTAGE);
            mDataTypeList.add(DataType.TYPE_NUTRITION);
            mDataTypeList.add(DataType.TYPE_WORKOUT_EXERCISE);

            mDataTypeList.add(DataType.AGGREGATE_ACTIVITY_SUMMARY);
            mDataTypeList.add(DataType.AGGREGATE_BASAL_METABOLIC_RATE_SUMMARY);
            mDataTypeList.add(DataType.AGGREGATE_STEP_COUNT_DELTA);
            mDataTypeList.add(DataType.AGGREGATE_DISTANCE_DELTA);
            mDataTypeList.add(DataType.AGGREGATE_CALORIES_CONSUMED);
            mDataTypeList.add(DataType.AGGREGATE_CALORIES_EXPENDED);
            mDataTypeList.add(DataType.AGGREGATE_HEART_RATE_SUMMARY);
            mDataTypeList.add(DataType.AGGREGATE_LOCATION_BOUNDING_BOX);
            mDataTypeList.add(DataType.AGGREGATE_POWER_SUMMARY);
            mDataTypeList.add(DataType.AGGREGATE_SPEED_SUMMARY);
            mDataTypeList.add(DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY);
            mDataTypeList.add(DataType.AGGREGATE_WEIGHT_SUMMARY);
            mDataTypeList.add(DataType.AGGREGATE_NUTRITION_SUMMARY);

            mSpinnerList.add("Please select");
            mSpinnerList.add("STEP COUNT DELTA");
            mSpinnerList.add("STEP COUNT CUMULATIVE");
            mSpinnerList.add("STEP COUNT CADENCE");
            mSpinnerList.add("ACTIVITY SEGMENT");
            mSpinnerList.add("CALORIES CONSUMED");
            mSpinnerList.add("CALORIES EXPENDED");
            mSpinnerList.add("BASAL METABOLIC RATE");
            mSpinnerList.add("POWER SAMPLE");
            mSpinnerList.add("ACTIVITY SAMPLE");
            mSpinnerList.add("HEART RATE BPM");
            mSpinnerList.add("LOCATION SAMPLE");
            mSpinnerList.add("LOCATION TRACK");
            mSpinnerList.add("DISTANCE DELTA");
            mSpinnerList.add("DISTANCE CUMULATIVE");
            mSpinnerList.add("SPEED");
            mSpinnerList.add("CYCLING WHEEL REVOLUTION");
            mSpinnerList.add("CYCLING WHEEL RPM");
            mSpinnerList.add("CYCLING PEDALING CUMULATIVE");
            mSpinnerList.add("CYCLING PEDALING CADENCE");
            mSpinnerList.add("HEIGHT");
            mSpinnerList.add("WEIGHT");
            mSpinnerList.add("BODY FAT PERCENTAGE");
            mSpinnerList.add("NUTRITION");
            mSpinnerList.add("WORKOUT EXERCISE");

            mSpinnerList.add("AGGREGATE ACTIVITY SUMMARY");
            mSpinnerList.add("AGGREGATE BASAL METABOLIC RATE SUMMARY");
            mSpinnerList.add("AGGREGATE STEP COUNT DELTA");
            mSpinnerList.add("AGGREGATE DISTANCE DELTA");
            mSpinnerList.add("AGGREGATE CALORIES CONSUMED");
            mSpinnerList.add("AGGREGATE CALORIES EXPENDED");
            mSpinnerList.add("AGGREGATE HEART RATE SUMMARY");
            mSpinnerList.add("AGGREGATE LOCATION BOUNDING BOX");
            mSpinnerList.add("AGGREGATE POWER SUMMARY");
            mSpinnerList.add("AGGREGATE SPEED SUMMARY");
            mSpinnerList.add("AGGREGATE BODY FAT PERCENTAGE SUMMARY");
            mSpinnerList.add("AGGREGATE WEIGHT SUMMARY");
            mSpinnerList.add("AGGREGATE NUTRITION SUMMARY");
        }

        public ArrayList<String> getDataTypeReadableValues()
        {
            return mSpinnerList;
        }

        public ArrayList<DataType> getDataTypeRawValues()
        {
            return mDataTypeList;
        }
}
