package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.custom.MainImageView;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

/**
 * Created by Resmic on 2017/5/3.
 */

public class WorksListAdapter extends MBaseAdapter<HomeListModel.HomeListItem> {

    public WorksListAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolder viewHolder) {
        final HomeListModel.HomeListItem listItem = getItem(position);
        MainImageView fmImageView;
        TextView titleTextView;
        PictureManager.displayHead(listItem.head, (ImageView) viewHolder.findViewById(R.id.homeitem_iv_head));
        fmImageView = (MainImageView) viewHolder.findViewById(R.id.homeitem_iv_image_2);
        titleTextView = (TextView) viewHolder.findViewById(R.id.homeitem_tv_title_2);
        ((TextView) viewHolder.findViewById(R.id.homeitem_tv_zan)).setText(listItem.zan + "");
        ((TextView) viewHolder.findViewById(R.id.homeitem_tv_price)).setText(listItem.usertype == 1 ? "账号未认证" : ("0".equalsIgnoreCase(listItem.price) ? "商家未认证" : ("￥" + listItem.price + "/" + listItem.unit)));
        PictureManager.displayImage(listItem.fmphoto, fmImageView);
        titleTextView.setText(listItem.title + "");
        viewHolder.findViewById(R.id.homeitem_btn_zan).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onZanClick(listItem.id);
                }
            }
        });
        viewHolder.findViewById(R.id.homeitem_tv_book).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onBookClick(listItem.id);
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
        ((ImageView) viewHolder.findViewById(R.id.homeitem_iv_zan)).setImageResource(listItem.iszan == 1 ? R.mipmap.notice_icon_heart_check : R.mipmap.notice_icon_heart);
    }

    @Override
    public int getConvertViewLayoutID() {
        return R.layout.fm_cover_work_item;
    }

}
