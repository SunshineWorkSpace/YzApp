package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.CoverModel;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import java.util.List;

/**
 * Created by Resmic on 2017/9/13.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class SysCoverAdapter extends RecyclerView.Adapter<SysCoverAdapter.CoverHolder> {
    private Context mContext;
    private List<CoverModel.CoverItem> mDatas;

    public SysCoverAdapter(Context mContext, List<CoverModel.CoverItem> data) {
        this.mContext = mContext;
        this.mDatas = data;
    }

    @Override
    public CoverHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CoverHolder(View.inflate(mContext, R.layout.view_syscover_item_layout, null));
    }

    @Override
    public void onBindViewHolder(CoverHolder holder, int position) {
        final CoverModel.CoverItem coverItem = mDatas.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCoverSelectedListener != null) {
                    onCoverSelectedListener.onCoverSelected(coverItem);
                }
            }
        });
        holder.textView.setText(coverItem.name);
        PictureManager.displayImage(coverItem.photononame, holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class CoverHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public CoverHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.cover_item_img);
            textView = (TextView) itemView.findViewById(R.id.cover_item_cover);
        }
    }

    public interface OnCoverSelectedListener {
        void onCoverSelected(CoverModel.CoverItem coverItem);
    }

    private OnCoverSelectedListener onCoverSelectedListener;

    public void setOnCoverSelectedListener(OnCoverSelectedListener onCoverSelectedListener) {
        this.onCoverSelectedListener = onCoverSelectedListener;
    }
}
