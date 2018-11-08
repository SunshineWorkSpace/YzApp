package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.CoverModel;
import com.yingshixiezuovip.yingshi.model.ShopTypeModel;
import com.yingshixiezuovip.yingshi.quote.media.utils.Utils;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品列表
 * Created by yuhua.gou on 2018/11/8.
 */

public class ShopAdapter extends BaseAdapter<ShopTypeModel.ShopType, RecyclerView.ViewHolder> implements ImageLoadingListener {
    public ShopAdapter(Context context,  List<ShopTypeModel.ShopType> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemDefaultHolder(mInflater.inflate(R.layout.item_shop, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (null != this.mItems && this.mItems.size() > 0){
            ShopTypeModel.ShopType item=mItems.get(position);
            if (item != null) {
                ItemDefaultHolder itemDefaultHolder = (ItemDefaultHolder) holder;
                itemDefaultHolder.tv_price.setText("￥"+item.price);
                PictureManager.displayHead(item.head,
                        itemDefaultHolder.iv_user_head);
                PictureManager.displayImage(item.photo, itemDefaultHolder.iv_shop, this);

//                Glide.with(mContext)
//                        .load(item.photo)
//                        .into(itemDefaultHolder.iv_shop);

            }
        }

    }

    @Override
    public void onLoadingStarted(String s, View view) {

    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
    }

    @Override
    public void onLoadingCancelled(String s, View view) {

    }


    public static class ItemDefaultHolder extends RecyclerView.ViewHolder {
        public final TextView tv_price;
        public final RoundedImageView iv_user_head;
        public final ImageView iv_shop;


        public ItemDefaultHolder(View itemView) {
            super(itemView);
            tv_price=(TextView) itemView.findViewById(R.id.tv_price);
            iv_user_head=(RoundedImageView) itemView.findViewById(R.id.iv_user_head);
            iv_shop=(ImageView) itemView.findViewById(R.id.iv_shop);
        }
    }

}
