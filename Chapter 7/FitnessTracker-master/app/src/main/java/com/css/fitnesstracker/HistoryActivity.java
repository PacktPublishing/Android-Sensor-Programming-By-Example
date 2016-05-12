package com.css.fitnesstracker;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class HistoryActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener, OnItemSelectedListener{

    private String TAG = "HistoryActivity";
    private GoogleApiClient mClient;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MMM d, h:mm a");
    private static final int REQUEST_OAUTH = 1;
    private TextView mStartDateText;
    private TextView mEndDateText;
    private TextView mResultsText;
    private Calendar mStartDateCalendar;
    private Calendar mEndDateCalendar;
    private DataType mSelectedDataType;
    private CheckBox mAggregateCheckBox;
    private ListView mListView;
    private ListAdapter mListAdapter;
    private int bucketSize = 0;
    private boolean isDataAggregated;
    ArrayList<DataPoint> mDataPointList = new ArrayList<DataPoint>();
    ArrayList<DataType> mAggregateDataTypeList = new ArrayList<DataType>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historydata_layout);

        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mAggregateCheckBox = (CheckBox)findViewById(R.id.aggregatecheckbox);
        mStartDateText = (TextView)findViewById(R.id.startdate);
        mEndDateText = (TextView)findViewById(R.id.enddate);
        mResultsText = (TextView)findViewById(R.id.results);

        setUpSpinnerDropDown();
        setUpListView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_OAUTH && resultCode == RESULT_OK) {
            if (!mClient.isConnecting() && !mClient.isConnected()) {
                mClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if(connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(HistoryActivity.this, REQUEST_OAUTH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setUpSpinnerDropDown() {

        Spinner spinnerDropDown = (Spinner) findViewById(R.id.spinner);
        spinnerDropDown.setOnItemSelectedListener(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DataHelper.getInstance().getDataTypeReadableValues());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDropDown.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position!=0) {
            mSelectedDataType = DataHelper.getInstance().getDataTypeRawValues().get(position);
        }
    }

    public void setStartDate(View v) {
        showDateSelectorDialog(true);
    }

    public void setEndDate(View v) {
        showDateSelectorDialog(false);
    }

    public void showDateSelectorDialog(final boolean isStartDate) {

        final Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.date_time_layout, null, false);
        dialog.setContentView(view);
        dialog.setTitle("Select the Date and Time");
        Button submit = (Button) view.findViewById(R.id.submit);
        Button cancel = (Button) view.findViewById(R.id.cancel);

        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timepicker);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datepicker);

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                int month = datePicker.getMonth();
                int date = datePicker.getDayOfMonth();
                int year = datePicker.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, date, hour, min);
                if (isStartDate) {
                    mStartDateCalendar = calendar;
                    mStartDateText.setText(" Start Date: "+mSimpleDateFormat.format(mStartDateCalendar.getTime()));
                } else {
                    mEndDateCalendar = calendar;
                    mEndDateText.setText(" End Date: "+mSimpleDateFormat.format(mEndDateCalendar.getTime()));
                }
                dialog.dismiss();

                if (mStartDateCalendar != null && mEndDateCalendar != null && mClient.isConnected() && !isStartDate) {
                    if (mDataPointList.size() > 0) {
                        mDataPointList.clear();
                    }
                    if (mAggregateDataTypeList.size() > 0) {
                        mAggregateDataTypeList.clear();
                    }
                    isDataAggregated = mAggregateCheckBox.isChecked();
                    new ReadFromHistoryTask().execute();
                }
            }

        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }

        });
        dialog.show();
    }

    public void setUpListView() {

        mListView = (ListView)findViewById(R.id.history_list);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
    }

    public class ReadFromHistoryTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            long endTime = mEndDateCalendar.getTimeInMillis();
            long startTime = mStartDateCalendar.getTimeInMillis();
            DataReadResult dataReadResult = null;
            if(isDataAggregated) {
                mAggregateDataTypeList.addAll(DataType.getAggregatesForInput(mSelectedDataType));
                if(mAggregateDataTypeList.size()>0) {
                    DataReadRequest readRequest = new DataReadRequest.Builder()
                            .aggregate(mSelectedDataType, mAggregateDataTypeList.get(0))
                            .bucketByTime(1, TimeUnit.DAYS)
                            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                            .build();
                    dataReadResult = Fitness.HistoryApi.readData(mClient,readRequest).await();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mResultsText.setText("Aggregation of data not supported");
                            mListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } else {
                DataReadRequest readRequest = new DataReadRequest.Builder().read(mSelectedDataType)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS).build();
                dataReadResult = Fitness.HistoryApi.readData(mClient, readRequest).await();
            }
            if(isDataAggregated) {
                if(mAggregateDataTypeList.size()>0 && dataReadResult!=null) {
                    readBucketValues(dataReadResult);
                }
            } else {
                DataSet dataSet = dataReadResult.getDataSet(mSelectedDataType);
                readDataSetValues(dataSet, false);
            }
            return null;
        }
    }

    public void readBucketValues(DataReadResult dataReadResult) {

        bucketSize = 0;
        for (Bucket bucket : dataReadResult.getBuckets()) {

            List<DataSet> dataSets = bucket.getDataSets();

            for (DataSet dataSet : dataSets) {

                if(dataSet.getDataPoints().size()>0)
                {
                    bucketSize++;
                    readDataSetValues(dataSet, true);
                }
            }
        }
        updateUIThread(true);
    }

    public void readDataSetValues(DataSet dataSet, boolean isBucketData) {

        for (DataPoint mDataPoint : dataSet.getDataPoints()) {

            mDataPointList.add(mDataPoint);
        }

        if(!isBucketData) {

            updateUIThread(isBucketData);
        }
    }

    public void updateUIThread(final boolean isBucketData)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isBucketData) {
                    if (bucketSize > 0) {
                        mResultsText.setText("Total " + bucketSize + " number of buckets data found");
                    } else {
                        mResultsText.setText("No Aggregated History Data found");
                    }
                } else {
                    if (mDataPointList.size() > 0) {
                        mResultsText.setText("Total " + mDataPointList.size() + " Data points found");
                    } else {
                        mResultsText.setText("No History Data found");
                    }
                }
                mListAdapter.notifyDataSetChanged();
            }
        });
    }


    public class ListAdapter extends BaseAdapter {

        private TextView mTextView1;
        private TextView mTextView2;
        private TextView mTextView3;

        @Override
        public int getCount() {

            return mDataPointList.size();
        }

        @Override
        public Object getItem(int position) {

            return mDataPointList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null){

                convertView = getLayoutInflater().inflate(R.layout.list_rows, parent, false);
            }

            StringBuilder dataValue = new StringBuilder();
            for (Field field : mDataPointList.get(position).getDataType().getFields())
            {
                Value val = mDataPointList.get(position).getValue(field);
                dataValue.append("Name: " + field.getName() + "  Value: " + val.toString());

            }

            mTextView1 = (TextView)convertView.findViewById(R.id.text1);
            mTextView1.setText("Data Type: "+ mDataPointList.get(position).getDataType().getName());
            mTextView2 = (TextView)convertView.findViewById(R.id.text2);
            mTextView2.setText(dataValue.toString());
            mTextView3 = (TextView)convertView.findViewById(R.id.text3);
            mTextView3.setText("Start Time: " + mSimpleDateFormat.format(mDataPointList.get(position).getStartTime(TimeUnit.MILLISECONDS)) +
                    "\nEnd Time: " + mSimpleDateFormat.format(mDataPointList.get(position).getEndTime(TimeUnit.MILLISECONDS)));

            return convertView;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

}
