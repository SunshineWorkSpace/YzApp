package com.yingshixiezuovip.yingshi.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.ShopTypeModel;
import com.yingshixiezuovip.yingshi.quote.media.utils.Utils;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.ImageLoaderNew;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.widget.ScaleImageView;

/**
 * Created by yuhua.gou on 2018/11/8.
 */

public class ShopNewAdapter extends BaseQuickAdapter<ShopTypeModel.ShopType, BaseViewHolder> {

    public ShopNewAdapter(){
        super(R.layout.item_shop);
    }


    @Override
    protected void convert(BaseViewHolder helper, ShopTypeModel.ShopType item) {
        ScaleImageView imageView = helper.getView(R.id.iv_shop);
        RoundedImageView iv_user_head=helper.getView(R.id.iv_user_head);
        TextView tv_price=helper.getView(R.id.tv_price);
        imageView.setInitSize(CommUtils.parseInt(item.width,0),
                CommUtils.parseInt(item.height,0));
        Glide.with(mContext).load(item.photo).placeholder(android.R.color.transparent)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
   /*     ImageLoaderNew.load(mContext,
                item.photo, imageView);*/
        PictureManager.displayHead(item.head,
                iv_user_head);
       tv_price.setText("¥"+CommUtils.subZeroAndDot(item.price));
    }

}
