package com.css.fitnesstracker;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SensorActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, OnItemSelectedListener, OnItemClickListener{

    private GoogleApiClient mClient = null;
    private String TAG = "SensorActivity";
    private static final int REQUEST_OAUTH = 1;
    private ArrayList<DataSource> mDataSourceList = new ArrayList<DataSource>();
    private TextView mLiveDataText;
    private ListView mListView;
    private ListAdapter mListAdapter;
    private boolean isDataListenerAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensordata_layout);
        mLiveDataText = (TextView)findViewById(R.id.livedata);

        setUpSpinnerDropDown();
        setUpListView();

        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void setUpListView() {

        mListView = (ListView)findViewById(R.id.datasource_list);
        mListAdapter = new ListAdapter();
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mListAdapter);
    }

    public void setUpSpinnerDropDown() {

        Spinner spinnerDropDown = (Spinner) findViewById(R.id.spinner);
        spinnerDropDown.setOnItemSelectedListener(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DataHelper.getInstance().getDataTypeReadableValues());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDropDown.setAdapter(arrayAdapter);
    }

    public void listDataSources(DataType mDataType)
    {
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                .setDataTypes(mDataType)
                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        mListAdapter.notifyDataSetChanged();
                        if (dataSourcesResult.getDataSources().size() > 0) {
                            mDataSourceList.addAll(dataSourcesResult.getDataSources());
                            mLiveDataText.setText("Please select from following data source to get the live data");
                        } else {
                            mLiveDataText.setText("No data source found for selected data type");
                        }
                    }
                });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (mClient.isConnected() && position!=0) {
            listDataSources(DataHelper.getInstance().getDataTypeRawValues().get(position));
            if(mDataSourceList.size()>0) {
                mDataSourceList.clear();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //remove any existing data listener, if previously added.
        if(isDataListenerAdded) {
            removeDataListener();
        }
        addDataListener(mDataSourceList.get(position));
    }

    public void addDataListener(DataSource mDataSource)
    {
        Fitness.SensorsApi.add(mClient,
                new SensorRequest.Builder()
                        .setDataSource(mDataSource)
                        .setDataType(mDataSource.getDataType())
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build(), mOnDataPointListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            mLiveDataText.setText("Listener registered successfully, waiting for live data");
                            isDataListenerAdded = true;
                        } else {
                            mLiveDataText.setText("Listener registration failed");
                        }
                    }
                });
    }

    OnDataPointListener mOnDataPointListener = new OnDataPointListener() {
        @Override
        public void onDataPoint(DataPoint dataPoint) {
            final StringBuilder dataValue = new StringBuilder();
            for (Field field : dataPoint.getDataType().getFields())
            {
                Value val = dataPoint.getValue(field);
                dataValue.append("Name:" + field.getName() + "  Value:" + val.toString());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLiveDataText.setText(dataValue.toString());

                }
            });
        }
    };

    public void removeDataListener()
    {
        Fitness.SensorsApi.remove(mClient, mOnDataPointListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            isDataListenerAdded = false;
                            Log.i(TAG, "Listener was remove successfully");
                        } else {
                            Log.i(TAG, "Listener was not removed");
                        }
                    }
                });
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
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if(connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(SensorActivity.this, REQUEST_OAUTH);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    private class ListAdapter extends BaseAdapter {

        private TextView mTextView1;
        private TextView mTextView2;
        private TextView mTextView3;

        @Override
        public int getCount() {
            return mDataSourceList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataSourceList.get(position).getName();
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
            mTextView1 = (TextView)convertView.findViewById(R.id.text1);
            mTextView1.setText("Data Type: "+ mDataSourceList.get(position).getDataType().getName());
            mTextView2 = (TextView)convertView.findViewById(R.id.text2);
            mTextView2.setText("App Package Name: "+mDataSourceList.get(position).getAppPackageName());
            mTextView3 = (TextView)convertView.findViewById(R.id.text3);
            mTextView3.setText("Device Name: "+mDataSourceList.get(position).getDevice().getModel().toString());
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
