package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.MainCommentActivity;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.minterface.OnAdapterClickListener;
import com.yingshixiezuovip.yingshi.model.CommentModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

/**
 * Created by Resmic on 2017/5/5.
 */

public class CommentListAdapter extends MBaseAdapter<CommentModel.CommentItem> {
    private int mType;

    public CommentListAdapter(Context mContext, int type) {
        super(mContext);
        this.mType = type;
    }

    public int getConvertViewLayoutID() {
        return R.layout.comment_item_layout;
    }

    public void onBindViewHolder(final int position, ViewHolder viewHolder) {
        final CommentModel.CommentItem localCommentItem = getItem(position);
        if (mType == MainCommentActivity.TYPE_COMMON) {
            viewHolder.findViewById(R.id.comment_common_layout).setVisibility(View.VISIBLE);
            viewHolder.findViewById(R.id.comment_notice_layout).setVisibility(View.GONE);
            PictureManager.displayHead(localCommentItem.head, (RoundedImageView) viewHolder.findViewById(R.id.common_item_iv_head));
            ((TextView) viewHolder.findViewById(R.id.common_item_tv_name)).setText(localCommentItem.nickname + "   " + localCommentItem.position);
            ((TextView) viewHolder.findViewById(R.id.common_item_tv_crtime)).setText(localCommentItem.crtime);
            ((TextView) viewHolder.findViewById(R.id.common_item_tv_content)).setText(localCommentItem.content);

            viewHolder.findViewById(R.id.common_item_iv_head).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAdapterClickListener != null) {
                        onAdapterClickListener.onHeadClick(localCommentItem.userid, localCommentItem.nickname);
                    }
                }
            });
        } else {
            viewHolder.findViewById(R.id.comment_common_layout).setVisibility(View.GONE);
            viewHolder.findViewById(R.id.comment_notice_layout).setVisibility(View.VISIBLE);
            PictureManager.displayHead(localCommentItem.head, (ImageView) viewHolder.findViewById(R.id.comment_iv_head));
            ((TextView) viewHolder.findViewById(R.id.comment_tv_info)).setText(localCommentItem.nickname + "   背书时间：" + localCommentItem.crtime);
            ((TextView) viewHolder.findViewById(R.id.comment_tv_city)).setText(localCommentItem.city);
            ((TextView) viewHolder.findViewById(R.id.comment_tv_title)).setText("背书了你的《" + localCommentItem.title + "》文章");
            ((TextView) viewHolder.findViewById(R.id.comment_tv_content)).setText("背书语句：" + localCommentItem.content);

            viewHolder.findViewById(R.id.comment_iv_head).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAdapterClickListener != null) {
                        onAdapterClickListener.onHeadClick(localCommentItem.userid, localCommentItem.nickname);
                    }
                }
            });
            viewHolder.getCovertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAdapterClickListener != null) {
                        onAdapterClickListener.onItemClick(v, localCommentItem.id);
                    }
                }
            });
        }
    }
}