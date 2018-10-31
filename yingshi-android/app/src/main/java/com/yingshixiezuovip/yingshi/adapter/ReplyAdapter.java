package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.model.ReviewModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import java.util.List;


/**
 * Created by Resmic on 2017/5/26.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class ReplyAdapter extends MBaseAdapter<ReviewModel.ReviewChildItem> {
    private String authorName;

    public ReplyAdapter(Context mContext, List<ReviewModel.ReviewChildItem> mData, String authorName) {
        super(mContext, mData);
        this.authorName = authorName;
    }

    @Override
    public void onBindViewHolder(int position, ViewHolder viewHolder) {
        final ReviewModel.ReviewChildItem childItem = getItem(position);
        PictureManager.displayImage(childItem.reply_head, (RoundedImageView) viewHolder.findViewById(R.id.reply_iv_head));
        ((TextView) viewHolder.findViewById(R.id.reply_tv_nickname)).setText(childItem.reply_nickname);
        ((TextView) viewHolder.findViewById(R.id.reply_tv_authorname)).setText(authorName);
        ((TextView) viewHolder.findViewById(R.id.reply_tv_content)).setText(childItem.reply_content);
        ((TextView) viewHolder.findViewById(R.id.reply_tv_date)).setText(childItem.reply_crtime);
        viewHolder.findViewById(R.id.reply_iv_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onHeadClick(childItem.reply_userid, childItem.reply_nickname);
                }
            }
        });
    }

    @Override
    public int getConvertViewLayoutID() {
        return R.layout.review_reply_item_layout;
    }
}
