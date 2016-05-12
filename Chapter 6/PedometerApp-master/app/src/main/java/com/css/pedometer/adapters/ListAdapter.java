package com.css.pedometer.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.css.pedometer.R;
import com.css.pedometer.models.DateStepsModel;
import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    TextView mDateStepCountText;
    ArrayList<DateStepsModel> mStepCountList;
    Context mContext;
    LayoutInflater mLayoutInflater;

    public ListAdapter(ArrayList<DateStepsModel> mStepCountList, Context mContext) {
        this.mStepCountList = mStepCountList;
        this.mContext = mContext;
        this.mLayoutInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return mStepCountList.size();
    }

    @Override
    public Object getItem(int position) {

        return mStepCountList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){

            convertView = mLayoutInflater.inflate(R.layout.list_rows, parent, false);
        }

        mDateStepCountText = (TextView)convertView.findViewById(R.id.sensor_name);
        mDateStepCountText.setText(mStepCountList.get(position).mDate + " - Total Steps: " + String.valueOf(mStepCountList.get(position).mStepCount));

        return convertView;
    }
}
