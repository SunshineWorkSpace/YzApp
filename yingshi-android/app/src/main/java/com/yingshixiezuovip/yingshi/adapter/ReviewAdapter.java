package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.model.ReviewModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

/**
 * Created by Resmic on 2017/5/25.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class ReviewAdapter extends MBaseAdapter<ReviewModel.ReviewItem> {
    public ReviewAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public void onBindViewHolder(final int position, final ViewHolder viewHolder) {
        final ReviewModel.ReviewItem reviewItem = getItem(position);
        PictureManager.displayImage(reviewItem.head, (RoundedImageView) viewHolder.findViewById(R.id.review_iv_head));
        ((TextView) viewHolder.findViewById(R.id.review_tv_nickname)).setText(reviewItem.nickname);
        ((TextView) viewHolder.findViewById(R.id.review_tv_position)).setText((TextUtils.isEmpty(reviewItem.position) ? "暂无" : reviewItem.position) + "  |  " + (TextUtils.isEmpty(reviewItem.city) ? "暂无地区" : reviewItem.city));
        ((TextView) viewHolder.findViewById(R.id.review_tv_content)).setText(reviewItem.content);
        ((ImageView) viewHolder.findViewById(R.id.review_iv_review)).setImageResource(reviewItem.iszan == 1 ? R.mipmap.icon_review : R.mipmap.icon_unreview);
        ((TextView) viewHolder.findViewById(R.id.review_tv_renum)).setText(String.valueOf(reviewItem.zan));
        viewHolder.findViewById(R.id.review_iv_vip).setVisibility(reviewItem.usertype == 1 ? View.GONE : View.VISIBLE);
        ((TextView) viewHolder.findViewById(R.id.review_tv_date)).setText(reviewItem.crtime + " * " + reviewItem.count + " 回复");
        viewHolder.getCovertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onItemClick(v, position);
                }
            }
        });
        viewHolder.findViewById(R.id.review_iv_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onHeadClick(reviewItem.userid, reviewItem.nickname);
                }
            }
        });
        ((ImageView) viewHolder.findViewById(R.id.review_iv_review)).setImageResource(reviewItem.iszan == 1 ? R.mipmap.icon_review : R.mipmap.icon_unreview);
        viewHolder.findViewById(R.id.review_btn_zan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewItem.iszan = reviewItem.iszan == 1 ? 0 : 1;
                reviewItem.zan += reviewItem.iszan == 0 ? (-1) : 1;
                ((TextView) viewHolder.findViewById(R.id.review_tv_renum)).setText(String.valueOf(reviewItem.zan));
                ((ImageView) viewHolder.findViewById(R.id.review_iv_review)).setImageResource(reviewItem.iszan == 1 ? R.mipmap.icon_review : R.mipmap.icon_unreview);
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onFollowClick(reviewItem.rid, reviewItem.iszan);
                }
            }
        });
        ListView listView = (ListView) viewHolder.findViewById(R.id.review_listview);
        if (reviewItem.list != null && reviewItem.list.size() > 0) {
            listView.setVisibility(View.VISIBLE);
            ReplyAdapter replyAdapter = new ReplyAdapter(mContext, reviewItem.list, reviewItem.nickname);
            replyAdapter.setOnAdapterClickListener(onAdapterClickListener);
            listView.setAdapter(replyAdapter);
            CommUtils.setListViewHeightBasedOnChildren(listView);
        } else {
            listView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getConvertViewLayoutID() {
        return R.layout.review_item_layout;
    }
}
