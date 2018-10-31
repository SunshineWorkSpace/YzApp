package com.yingshixiezuovip.yingshi.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yingshixiezuovip.yingshi.minterface.OnAdapterClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Resmic on 2016/7/18.
 */

public abstract class MBaseAdapter<T> extends BaseAdapter {
    protected List<T> mData;
    protected Context mContext;

    public MBaseAdapter(Context mContext, List<T> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public MBaseAdapter(Context mContext) {
        this.mContext = mContext;
        this.mData = new ArrayList<>();
    }

    public void setDatas(List<T> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        mData = data;
        notifyDataSetChanged();
    }

    public void setData(T data) {
        if (mData != null) {
            mData.clear();
        } else {
            mData = new ArrayList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    public void addDatas(List<T> datas) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.addAll(datas);
        notifyDataSetChanged();
    }

    public void addData(T data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        if (mData != null && mData.size() > position) {
            mData.remove(position);
            notifyDataSetChanged();
        }
    }

    public List<T> getDatas() {
        if (mData == null) {
            mData = new ArrayList<T>();
        }
        return mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position % mData.size());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, getConvertViewLayoutID(), null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        onBindViewHolder(position, viewHolder);
        return viewHolder.getCovertView();
    }

    public abstract void onBindViewHolder(int position, ViewHolder viewHolder);

    protected OnAdapterClickListener onAdapterClickListener;

    public void setOnAdapterClickListener(OnAdapterClickListener onAdapterClickListener) {
        this.onAdapterClickListener = onAdapterClickListener;
    }

    public abstract int getConvertViewLayoutID();

    public static class ViewHolder {
        private View mParentView;
        HashMap<Integer, View> viewHashMap;

        ViewHolder(View convertView) {
            viewHashMap = new HashMap<>();
            this.mParentView = convertView;
        }

        public View findViewById(int id) {
            View tempView = viewHashMap.get(id);
            if (tempView == null) {
                tempView = mParentView.findViewById(id);
                viewHashMap.put(id, tempView);
            }
            return tempView;
        }

        public View getCovertView() {
            return mParentView;
        }


    }
}
