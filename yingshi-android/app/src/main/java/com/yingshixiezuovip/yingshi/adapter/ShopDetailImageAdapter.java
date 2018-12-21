package com.yingshixiezuovip.yingshi.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.ShopDetailTypeModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.ImageLoaderNew;
import com.yingshixiezuovip.yingshi.widget.ScaleImageNewView;
import com.yingshixiezuovip.yingshi.widget.ScaleImageView;

/**
 * 显示图片
 * Created by yuhua.gou on 2018/11/9.
 */

public class ShopDetailImageAdapter extends BaseQuickAdapter<ShopDetailTypeModel.PhotoImageItem, BaseViewHolder> {
    public ShopDetailImageAdapter(){
        super(R.layout.item_image);
    }

    @Override
    protected void convert(BaseViewHolder helper,ShopDetailTypeModel.PhotoImageItem item) {
        ScaleImageNewView imageView=helper.getView(R.id.iv_imge);
        TextView tv_price=helper.getView(R.id.tv_price);
     /*   imageView.setInitSize(CommUtils.parseInt(item.width,0),
                CommUtils.parseInt(item.height,0));*/
        Glide.with(mContext).load(item.photo).placeholder(android.R.color.transparent)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

    }
}
