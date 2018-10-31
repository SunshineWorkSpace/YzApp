package com.yingshixiezuovip.yingshi.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.model.RecommendModel;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

/**
 * Created by Resmic on 2017/5/16.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class SearchRecommendAdapter extends MBaseAdapter<RecommendModel.RecommendItem> {
    private UserInfo mUserInfo;

    public SearchRecommendAdapter(Context mContext) {
        super(mContext);
        mUserInfo = SPUtils.getUserInfo(YingApplication.getInstance());

    }

    @Override
    public void onBindViewHolder(int position, ViewHolder viewHolder) {
        final RecommendModel.RecommendItem recommendItem = getItem(position);
        PictureManager.displayHead(recommendItem.head, (RoundedImageView) viewHolder.findViewById(R.id.search_iv_head));
        ((TextView) viewHolder.findViewById(R.id.search_tv_name)).setText(recommendItem.name);
        ((TextView) viewHolder.findViewById(R.id.search_tv_position)).setText(recommendItem.position);
        ((TextView) viewHolder.findViewById(R.id.search_tv_city)).setText(recommendItem.city);

        final CheckBox followCheckBox = (CheckBox) viewHolder.findViewById(R.id.search_cb_follow);
        followCheckBox.setChecked(recommendItem.isfollow == 0);
        followCheckBox.setText(recommendItem.isfollow == 0 ? "+关注" : "已关注");
        followCheckBox.setBackgroundResource(recommendItem.isfollow == 1 ? R.drawable.home_follow_shape
                : R.drawable.home_unfollow_shape);

        followCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                recommendItem.isfollow = recommendItem.isfollow == 0 ? 1 : 0;
                followCheckBox.setText(isChecked ? "+关注" : "已关注");
                followCheckBox.setBackgroundResource(recommendItem.isfollow == 1 ? R.drawable.home_follow_shape
                        : R.drawable.home_unfollow_shape);

                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onFollowClick(recommendItem.userid, recommendItem.isfollow);
                }
                L.d("listItem.isfollow = " + recommendItem.isfollow + " , " + recommendItem.isfollow);
            }
        });
        followCheckBox.setVisibility(mUserInfo.id != recommendItem.userid ? View.VISIBLE : View.GONE);

        viewHolder.findViewById(R.id.search_iv_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onHeadClick(recommendItem.userid, recommendItem.name);
                }
            }
        });
    }

    @Override
    public int getConvertViewLayoutID() {
        return R.layout.search_recommend_item_layout;
    }
}
