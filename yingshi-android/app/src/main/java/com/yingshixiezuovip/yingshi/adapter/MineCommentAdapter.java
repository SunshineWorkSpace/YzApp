package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.model.CommentModel;

/**
 * Created by Resmic on 2017/5/11.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class MineCommentAdapter extends MBaseAdapter<CommentModel.CommentItem> {

    public MineCommentAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolder viewHolder) {
        CommentModel.CommentItem commentItem = getItem(position);
        ((TextView) viewHolder.findViewById(R.id.common_item_tv_time)).setText(commentItem.crtime);
        ((TextView) viewHolder.findViewById(R.id.common_item_tv_name)).setText("文章《" + commentItem.title + "》");
        ((TextView) viewHolder.findViewById(R.id.common_item_tv_content)).setText(commentItem.content);

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
        return R.layout.profile_comment_item_layout;
    }

}
