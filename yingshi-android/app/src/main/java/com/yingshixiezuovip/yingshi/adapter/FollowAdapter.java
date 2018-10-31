package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.minterface.OnAdapterClickListener;
import com.yingshixiezuovip.yingshi.model.FollowModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

/**
 * Created by Resmic on 2017/5/9.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class FollowAdapter extends MBaseAdapter<FollowModel.FollowItem> {
    public FollowAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolder viewHolder) {
        final FollowModel.FollowItem followItem = getItem(position);
        PictureManager.displayHead(followItem.head, (RoundedImageView) viewHolder.findViewById(R.id.follow_iv_head));
        ((TextView) viewHolder.findViewById(R.id.follow_tv_name)).setText(followItem.nickname);
        ((TextView) viewHolder.findViewById(R.id.follow_tv_position)).setText("职位：" + followItem.position);
        ((TextView) viewHolder.findViewById(R.id.follow_tv_city)).setText("出生：" + followItem.born);
        ((TextView) viewHolder.findViewById(R.id.follow_tv_crtime)).setText("关注时间：" + followItem.crtime);
        final CheckBox followCheckBox = (CheckBox) viewHolder.findViewById(R.id.follow_cb_follow);
        followCheckBox.setChecked(followItem.isfollow == 0);
        followCheckBox.setText(followItem.isfollow == 0 ? "+关注" : "已关注");
        followCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                followItem.isfollow = followItem.isfollow == 0 ? 1 : 0;
                followCheckBox.setText(isChecked ? "+关注" : "已关注");
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onFollowClick(followItem.userid, followItem.isfollow);
                }
            }
        });
        viewHolder.findViewById(R.id.follow_iv_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onHeadClick(followItem.userid, followItem.nickname);
                }
            }
        });
    }

    @Override
    public int getConvertViewLayoutID() {
        return R.layout.fm_userinfo_follow_item;
    }

}
