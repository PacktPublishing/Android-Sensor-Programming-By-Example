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
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.result.ListSubscriptionsResult;
import java.util.ArrayList;

public class SubscriptionActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener, OnItemSelectedListener, OnItemClickListener {

    private GoogleApiClient mClient = null;
    private String TAG = "SubscriptionActivity";
    private static final int REQUEST_OAUTH = 1;
    private ListView mListView;
    private ListAdapter mListAdapter;
    private int lastRemovedPosition;
    private ArrayList<DataType> mDataTypeList = new ArrayList<DataType>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscriptiondata_layout);

        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setUpSpinnerDropDown();
        setUpListView();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if(connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(SubscriptionActivity.this, REQUEST_OAUTH);
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
    public void onConnected(Bundle bundle) {

        listExistingSubscription();
    }

    public void listExistingSubscription() {

        Fitness.RecordingApi.listSubscriptions(mClient)
                .setResultCallback(new ResultCallback<ListSubscriptionsResult>() {
                    @Override
                    public void onResult(ListSubscriptionsResult listSubscriptionsResult) {
                        mDataTypeList.clear();
                        for (Subscription sc : listSubscriptionsResult.getSubscriptions()) {
                            mDataTypeList.add(sc.getDataType());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mListAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
    }

    public void setUpListView() {

        mListView = (ListView)findViewById(R.id.subscription_list);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position!=0 && mClient.isConnected()) {
            addSubscription(DataHelper.getInstance().getDataTypeRawValues().get(position));
        }
    }

    public void addSubscription(DataType mDataType) {

        Fitness.RecordingApi.subscribe(mClient, mDataType)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            listExistingSubscription();
                        } else {
                            Log.i(TAG, "There was a problem subscribing.");
                        }
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        removeSubscription(mDataTypeList.get(position));
    }

    public void removeSubscription(DataType mDataType) {

        Fitness.RecordingApi.unsubscribe(mClient, mDataType)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDataTypeList.remove(lastRemovedPosition);
                                    mListAdapter.notifyDataSetChanged();
                                }
                            });
                        } else {
                            Log.i(TAG, "Failed to unsubscribe ");
                        }
                    }
                });
    }

    private class ListAdapter extends BaseAdapter {

        private TextView mTextView1;
        private TextView mTextView2;

        @Override
        public int getCount() {

            return mDataTypeList.size();
        }

        @Override
        public Object getItem(int position) {

            return mDataTypeList.get(position);
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
            mTextView1.setText("Data Type: "+ mDataTypeList.get(position).getName());
            mTextView2 = (TextView)convertView.findViewById(R.id.text2);
            mTextView2.setText("Field Name: "+mDataTypeList.get(position).getFields().get(0).getName());
            return convertView;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
