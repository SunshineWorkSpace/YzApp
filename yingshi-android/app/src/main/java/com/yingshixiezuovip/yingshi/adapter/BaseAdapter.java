package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shu.xinghu on 2015/10/29.
 */
public abstract class BaseAdapter<E, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected Context mContext;
    protected List<E> mItems;
    protected LayoutInflater mInflater;

    protected boolean mDebug = false;

    public BaseAdapter(Context context, List<E> list) {
        mContext = context;
        mItems = list;
        mInflater = LayoutInflater.from(context);
    }
    public void setDatas(List<E> datas){
        if(null!=datas) {
            this.mItems = datas;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mItems != null) {
            return mItems.size();
        } else {
            return 0;
        }
    }





    public void addDatas(List<E> datas) {
        if(null!=datas) {
            mItems.addAll(datas);
        }
        notifyDataSetChanged();
    }


    public E getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 刷新列表
     * refreshList
     * @param list
     * @since 1.0
     */
    public void refreshList(List<E> list) {
        mItems = list;
        notifyDataSetChanged();
    }

    /**
     * 删除一条数据
     * remove
     * @param e
     * @since 1.0
     */
    public boolean remove(E e) {
        int pos = mItems.indexOf(e);
        if (pos >= 0) {
            mItems.remove(e);
            notifyItemRemoved(pos);
            return true;
        }
        return false;
    }

    /**
     * 添加一条数据
     * add
     * @param e
     * @since 1.0
     */
    public void add(E e) {
        int size = mItems.size();
        mItems.add(e);
        notifyItemInserted(size - 1);
    }

    public void addAll(List<E> list) {
        int size = mItems.size();
        mItems.addAll(list);
        notifyItemRangeInserted(size - 1, list.size());
    }
}
