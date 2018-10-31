package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.HomeTypeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Resmic on 2017/5/4.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class PublishTypeAdater extends BaseAdapter implements SpinnerAdapter {
    private List<HomeTypeModel.HomeType> mData;
    private Context mContext;

    public PublishTypeAdater(Context context, List<HomeTypeModel.HomeType> data) {
        this.mContext = context;
        if (data == null) {
            data = new ArrayList<>();
        }
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HomeTypeModel.HomeType homeType = (HomeTypeModel.HomeType) getItem(position);
        if (convertView == null) {
            convertView = new TextView(mContext);
            ((TextView) convertView).setTextColor(mContext.getResources().getColor(R.color.colorGray));
            ((TextView) convertView).setTextSize(15);
        }
        ((TextView) convertView).setText(homeType.name);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        HomeTypeModel.HomeType homeType = (HomeTypeModel.HomeType) getItem(position);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.view_textview_layout, null);
        }
        ((TextView) convertView).setText(homeType.name);
        return convertView;
    }

    public int getIdByName(String name) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).name.equals(name)) {
                return i;
            }
        }
        return 0;
    }
}
