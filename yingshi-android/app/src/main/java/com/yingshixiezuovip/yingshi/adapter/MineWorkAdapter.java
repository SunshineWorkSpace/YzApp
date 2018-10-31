package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.minterface.OnAdapterClickListener;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

/**
 * Created by Resmic on 2017/5/11.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class MineWorkAdapter extends MBaseAdapter<HomeListModel.HomeListItem> {
    public MineWorkAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolder viewHolder) {
        final HomeListModel.HomeListItem listItem = getItem(position);
        ((TextView) viewHolder.findViewById(R.id.works_tv_author)).setText(listItem.title);
        PictureManager.displayImage(listItem.photo, (ImageView) viewHolder.findViewById(R.id.works_iv_background));
        ((TextView) viewHolder.findViewById(R.id.works_tv_zan)).setText(String.valueOf(listItem.zan));
        viewHolder.findViewById(R.id.works_btn_zan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onZanClick(listItem.id);
                }
            }
        });
        viewHolder.getCovertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getConvertViewLayoutID() {
        return R.layout.profile_work_item_layout;
    }
}
