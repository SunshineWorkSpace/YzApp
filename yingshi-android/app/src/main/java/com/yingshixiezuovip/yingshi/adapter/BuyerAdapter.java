package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.MainCommonActivity;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.model.BuyerModel;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

/**
 * Created by Resmic on 2017/5/10.
 */

public class BuyerAdapter extends MBaseAdapter<BuyerModel.BuyerItem> {
    private int mType;

    public BuyerAdapter(Context mContext, int type) {
        super(mContext);
        this.mType = type;
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolder viewHolder) {
        final BuyerModel.BuyerItem buyerItem = getItem(position);
        PictureManager.displayImage(buyerItem.fmphoto, (ImageView) viewHolder.findViewById(R.id.buyer_iv_head));
        if (mType == MainCommonActivity.TYPE_BUYER_ORDER) {
            ((TextView) viewHolder.findViewById(R.id.buyer_tv_name)).setText(buyerItem.nickname);
            viewHolder.findViewById(R.id.buyer_tv_ordername).setVisibility(View.GONE);
        } else {
            ((TextView) viewHolder.findViewById(R.id.buyer_tv_name)).setText(null);
            viewHolder.findViewById(R.id.buyer_tv_ordername).setVisibility(View.VISIBLE);
            ((TextView) viewHolder.findViewById(R.id.buyer_tv_ordername)).setText("客户昵称：" + buyerItem.nickname);
        }
        ((TextView) viewHolder.findViewById(R.id.buyer_tv_crtime)).setText(buyerItem.crtime);
        String bookTime = "";
        for (String time : buyerItem.list) {
            bookTime += time + "  ";
        }
        if (!TextUtils.isEmpty(bookTime)) {
            viewHolder.findViewById(R.id.buyer_tv_booktime).setVisibility(View.VISIBLE);
            ((TextView) viewHolder.findViewById(R.id.buyer_tv_booktime)).setText("预定时间：" + bookTime);
        } else {
            viewHolder.findViewById(R.id.buyer_tv_booktime).setVisibility(View.GONE);
        }
        viewHolder.findViewById(R.id.buyer_iv_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onHeadClick(buyerItem.userid, buyerItem.nickname);
                }
            }
        });
        ((TextView) viewHolder.findViewById(R.id.buyer_tv_price)).setText("交易金额：" + buyerItem.total + "元");
        ((TextView) viewHolder.findViewById(R.id.buyer_tv_ordernum)).setText("订单编号：" + buyerItem.orderno);
        ((TextView) viewHolder.findViewById(R.id.buyer_tv_orderstatus)).setText(buyerItem.statusName);
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
        return R.layout.common_buyer_item_layout;
    }
}
