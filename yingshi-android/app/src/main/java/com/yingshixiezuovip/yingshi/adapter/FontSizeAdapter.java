package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.utils.CommUtils;

/**
 * Created by Resmic on 18/1/23.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class FontSizeAdapter extends RecyclerView.Adapter<FontSizeAdapter.FontSizeViewHolder> {
    private Context context;
    private int[] datas;

    public FontSizeAdapter(Context context, int[] datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public FontSizeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.colorBlack));
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(CommUtils.dip2px(40), CommUtils.dip2px(25));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(14);
        textView.setLayoutParams(layoutParams);
        return new FontSizeViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(FontSizeViewHolder holder, int position) {
        final int dataItem = datas[position];
        ((TextView) holder.itemView).setText(String.valueOf(dataItem));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSizeSelectedListener != null) {
                    onSizeSelectedListener.onSizeSelected(dataItem);
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

    public interface OnSizeSelectedListener {
        void onSizeSelected(int font);
    }

    private OnSizeSelectedListener onSizeSelectedListener;

    public void setOnSizeSelectedListener(OnSizeSelectedListener onSizeSelectedListener) {
        this.onSizeSelectedListener = onSizeSelectedListener;
    }
}
