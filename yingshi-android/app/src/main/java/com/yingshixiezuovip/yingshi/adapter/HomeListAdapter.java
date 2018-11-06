package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.custom.MainImageView;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Resmic on 2017/5/3.
 */

public class HomeListAdapter extends MBaseAdapter<HomeListModel.HomeListItem> implements ImageLoadingListener {
    private int mType = 1;//1为home，2为招聘,3为个人作品，4作品集,5 搜索
    private UserInfo mUserInfo;
    private List<Bitmap> mBitmaps;

    private boolean isDetails = false;

    public HomeListAdapter(Context mContext) {
        super(mContext);
        mUserInfo = SPUtils.getUserInfo(YingApplication.getInstance());
        mBitmaps = new ArrayList<>();
    }

    public HomeListAdapter(Context mContext, int type) {
        super(mContext);
        mType = type;
        mUserInfo = SPUtils.getUserInfo(YingApplication.getInstance());
        mBitmaps = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolder viewHolder) {
        final HomeListModel.HomeListItem listItem = getItem(position);
        MainImageView fmImageView;
        TextView titleTextView;
        final TextView followCheckBox;
        viewHolder.findViewById(R.id.homeitem_type_1_line).setVisibility(View.GONE);
        viewHolder.findViewById(R.id.homeitem_type_2_line).setVisibility(View.GONE);

        if (mType == 1 && position < 6) {
            if(position < 5) {
                viewHolder.findViewById(R.id.homeitem_type_1_line).setVisibility(View.VISIBLE);
            }

            viewHolder.findViewById(R.id.homeitem_type_1).setVisibility(View.VISIBLE);
            viewHolder.findViewById(R.id.homeitem_type_2).setVisibility(View.GONE);
            fmImageView = (MainImageView) viewHolder.findViewById(R.id.homeitem_iv_image_1);
            titleTextView = (TextView) viewHolder.findViewById(R.id.homeitem_tv_title_1);
            ((TextView) viewHolder.findViewById(R.id.homeitem_tv_city)).setText(!TextUtils.isEmpty(listItem.city) ? (listItem.city.split(" ")[0]) : "暂无");
            ((TextView) viewHolder.findViewById(R.id.homeitem_tv_position)).setText(listItem.position);
            ((TextView) viewHolder.findViewById(R.id.homeitem_tv_nickname)).setText(listItem.nickname);
            ((TextView) viewHolder.findViewById(R.id.homeitem_tv_type)).setText(listItem.typename);
        } else {
            if(position == 0){
                viewHolder.findViewById(R.id.homeitem_type_2_line).setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                viewHolder.findViewById(R.id.homeitem_type_2_line).setBackgroundColor(Color.parseColor("#f5f5f5"));
            }

            viewHolder.findViewById(R.id.homeitem_type_2_line).setVisibility(View.VISIBLE);

            viewHolder.findViewById(R.id.homeitem_type_1).setVisibility(View.GONE);
            viewHolder.findViewById(R.id.homeitem_type_2).setVisibility(View.VISIBLE);
            if (isDetails) {
                PictureManager.displayHead(listItem.head, (ImageView) viewHolder.findViewById(R.id.homeitem_iv_head), this);
            } else {
                PictureManager.displayHead(listItem.head, (ImageView) viewHolder.findViewById(R.id.homeitem_iv_head));
            }
            fmImageView = (MainImageView) viewHolder.findViewById(R.id.homeitem_iv_image_2);
            titleTextView = (TextView) viewHolder.findViewById(R.id.homeitem_tv_title_2);
            ((TextView) viewHolder.findViewById(R.id.homeitem_tv_author)).setText(listItem.nickname);
            ((TextView) viewHolder.findViewById(R.id.homeitem_tv_invite)).setText("推荐人：" + (TextUtils.isEmpty(listItem.invite) ? "暂无" : listItem.invite));
            followCheckBox = (TextView) viewHolder.findViewById(R.id.homeitem_cb_follow);
            followCheckBox.setText(listItem.isfollow == 0 ? "+关注" : "已关注");
            followCheckBox.setTextColor(mContext.getResources().getColor(listItem.isfollow == 1 ? R.color.colorWhite : R.color.colorGray));
            followCheckBox.setBackgroundResource(listItem.isfollow == 1 ? R.drawable.home_follow_shape : R.drawable.home_unfollow_shape);
            followCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listItem.isfollow = listItem.isfollow == 0 ? 1 : 0;
                    followCheckBox.setText(listItem.isfollow == 0 ? "+关注" : "已关注");
                    followCheckBox.setTextColor(mContext.getResources().getColor(listItem.isfollow == 1 ? R.color.colorWhite : R.color.colorGray));
                    followCheckBox.setBackgroundResource(listItem.isfollow == 1 ? R.drawable.home_follow_shape : R.drawable.home_unfollow_shape);
                    if (onAdapterClickListener != null) {
                        onAdapterClickListener.onFollowClick(listItem.userid, listItem.isfollow);
                    }
                    L.d("listItem.isfollow = " + listItem.isfollow + " , " + listItem.isfollow);
                }
            });
            followCheckBox.setVisibility(mType == 3 ? View.GONE : (mUserInfo.id != listItem.userid ? View.VISIBLE : View.GONE));
            ((TextView) viewHolder.findViewById(R.id.homeitem_tv_zan)).setText(listItem.zan + "");

            ((TextView) viewHolder.findViewById(R.id.homeitem_tv_price)).setText(listItem.usertype == 1 ? "账号未认证" : ("0".equalsIgnoreCase(listItem.price) ? "商家未认证" : (listItem.price.equals("0")?"价格面议":"￥" + listItem.price + "/" + listItem.unit)));

            viewHolder.findViewById(R.id.homeitem_iv_auth).setVisibility(View.VISIBLE);
            if (listItem.productionType == 2) {
                ((ImageView) viewHolder.findViewById(R.id.homeitem_iv_auth)).setImageResource(R.mipmap.auth_personal);
            } else if (listItem.productionType == 4) {
                ((ImageView) viewHolder.findViewById(R.id.homeitem_iv_auth)).setImageResource(R.mipmap.auth_company);
            } else if (listItem.productionType == 6) {
                ((ImageView) viewHolder.findViewById(R.id.homeitem_iv_auth)).setImageResource(R.mipmap.auth_student);
            } else {
                viewHolder.findViewById(R.id.homeitem_iv_auth).setVisibility(View.GONE);
            }
        }

        if (isDetails) {
            PictureManager.displayImage(listItem.fmphoto, fmImageView, this);
        } else {
            PictureManager.displayImage(listItem.fmphoto, fmImageView);
        }

        titleTextView.setText(listItem.title + "");
        viewHolder.findViewById(R.id.homeitem_btn_zan).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onZanClick(listItem.id);
                }
            }
        });
        viewHolder.findViewById(R.id.homeitem_tv_invite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    if (!TextUtils.isEmpty(listItem.invite)) {
                        onAdapterClickListener.onHeadClick(listItem.inviteUserid, listItem.invite);
                    }
                }
            }
        });
        viewHolder.findViewById(R.id.homeitem_tv_book).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onBookClick(listItem.id);
                }
            }
        });
        viewHolder.findViewById(R.id.homeitem_iv_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onHeadClick(listItem.userid, listItem.nickname);
                }
            }
        });
        viewHolder.getCovertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdapterClickListener != null) {
                    onAdapterClickListener.onItemClick(v, position);
                }
            }
        });
        ((ImageView) viewHolder.findViewById(R.id.homeitem_iv_zan)).setImageResource(listItem.iszan == 1 ? R.mipmap.notice_icon_heart_check : R.mipmap.notice_icon_heart);
    }

    public void setDetails(boolean details) {
        isDetails = details;
    }

    @Override
    public int getConvertViewLayoutID() {
        return R.layout.fm_home_item_layout;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        mBitmaps.add(loadedImage);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
    }

    public void onDestroy() {
        for (Bitmap bitmap : mBitmaps) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }
}
