package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.custom.CircleView;
import com.yingshixiezuovip.yingshi.utils.CommUtils;

/**
 * Created by Resmic on 18/1/23.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class FontColorAdapter extends RecyclerView.Adapter<FontColorAdapter.FontSizeViewHolder> {
    private Context context;
    private String[] datas;

    public FontColorAdapter(Context context, String[] datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public FontSizeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CircleView textView = new CircleView(context);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(CommUtils.dip2px(40),
                CommUtils.dip2px(25));
        textView.setLayoutParams(layoutParams);
        return new FontSizeViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(FontSizeViewHolder holder, int position) {
        final String dataItem = datas[position];
        ((CircleView) holder.itemView).setColor(dataItem);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onColorSelectedListener != null) {
                    onColorSelectedListener.onColorSelected(dataItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.length;
    }

    public static class FontSizeViewHolder extends RecyclerView.ViewHolder {

        public FontSizeViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnColorSelectedListener {
        void onColorSelected(String color);
    }

    private OnColorSelectedListener onColorSelectedListener;

    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        this.onColorSelectedListener = onColorSelectedListener;
    }
}
