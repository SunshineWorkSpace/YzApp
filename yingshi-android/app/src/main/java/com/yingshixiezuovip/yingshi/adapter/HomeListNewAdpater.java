/*
package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.custom.MainImageView;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * 类名称:HomeListNewAdpater
 * 类描述:
 * 创建时间: 2018-11-28-14:47
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 *//*

public class HomeListNewAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //获取从Activity中传递过来每个item的数据集合
    private int mType = 1;//1为home，2为招聘,3为个人作品，4作品集,5 搜索
    private UserInfo mUserInfo;
    private List<Bitmap> mBitmaps;

    private boolean isDetails = false;
    private Context context;
    private List<HomeListModel.HomeListItem>mDatas;
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private HomeListNewAdpater.OnItemClickListener mOnItemClickListener;
    private HomeListNewAdpater.OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(HomeListNewAdpater.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(HomeListNewAdpater.OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
    public HomeListNewAdpater(Context mContext,List<HomeListModel.HomeListItem> list) {
        mUserInfo = SPUtils.getUserInfo(YingApplication.getInstance());
        mBitmaps = new ArrayList<>();
        mDatas=list;
    }

    public HomeListNewAdpater(Context mContext, int type) {
        this.context=mContext;
        mType = type;
        mUserInfo = SPUtils.getUserInfo(YingApplication.getInstance());
        mBitmaps = new ArrayList<>();
    }


    //创建View，如果是HeaderView或者是FooterView，直接在Holder中返回
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.fm_home_item_layout, parent, false);

        return new HomeListNewAdpater.ListHolder(layout);
    }


    //绑定View，这里是根据返回的这个position的类型，从而进行绑定的，   HeaderView和FooterView, 就不同绑定了
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        try {
            //判断是否设置了监听器
            if (mOnItemClickListener != null) {
                //为ItemView设置监听器
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.onItemClick(holder.itemView, position); // 2
                    }
                });
            }
            if (mOnItemLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        mOnItemLongClickListener.onItemLongClick(holder.itemView, position);
                        //返回true 表示消耗了事件 事件不会继续传递
                        return true;
                    }
                });
            }
            final HomeListModel.HomeListItem listItem =mDatas.get(position) ;
            MainImageView fmImageView;
            TextView titleTextView;
            final TextView followCheckBox;
            holder.findViewById(R.id.homeitem_type_1_line).setVisibility(View.GONE);
            holder.findViewById(R.id.homeitem_type_2_line).setVisibility(View.GONE);

            if (mType == 1 && position < 6) {
                if(position < 5) {
                    holder.findViewById(R.id.homeitem_type_1_line).setVisibility(View.VISIBLE);
                }

                holder.findViewById(R.id.homeitem_type_1).setVisibility(View.VISIBLE);
                holder.findViewById(R.id.homeitem_type_2).setVisibility(View.GONE);
                fmImageView = (MainImageView) holder.findViewById(R.id.homeitem_iv_image_1);
                titleTextView = (TextView) holder.findViewById(R.id.homeitem_tv_title_1);
                ((TextView) holder.findViewById(R.id.homeitem_tv_city)).setText(!TextUtils.isEmpty(listItem.city) ? (listItem.city.split(" ")[0]) : "暂无");
                ((TextView) holder.findViewById(R.id.homeitem_tv_position)).setText(listItem.position);
                ((TextView) holder.findViewById(R.id.homeitem_tv_nickname)).setText(listItem.nickname);
                ((TextView) holder.findViewById(R.id.homeitem_tv_type)).setText(listItem.typename);
            } else {
                if(position == 0){
                    holder.findViewById(R.id.homeitem_type_2_line).setBackgroundColor(Color.parseColor("#ffffff"));
                } else {
                    holder.findViewById(R.id.homeitem_type_2_line).setBackgroundColor(Color.parseColor("#f5f5f5"));
                }

                holder.findViewById(R.id.homeitem_type_2_line).setVisibility(View.VISIBLE);

                holder.findViewById(R.id.homeitem_type_1).setVisibility(View.GONE);
                holder.findViewById(R.id.homeitem_type_2).setVisibility(View.VISIBLE);
                if (isDetails) {
                    PictureManager.displayHead(listItem.head, (ImageView) holder.findViewById(R.id.homeitem_iv_head), this);
                } else {
                    PictureManager.displayHead(listItem.head, (ImageView) holder.findViewById(R.id.homeitem_iv_head));
                }
                fmImageView = (MainImageView) holder.findViewById(R.id.homeitem_iv_image_2);
                titleTextView = (TextView) holder.findViewById(R.id.homeitem_tv_title_2);
                ((TextView) holder.findViewById(R.id.homeitem_tv_author)).setText(listItem.nickname);
                ((TextView) holder.findViewById(R.id.homeitem_tv_invite)).setText("推荐人：" + (TextUtils.isEmpty(listItem.invite) ? "暂无" : listItem.invite));
                followCheckBox = (TextView) holder.findViewById(R.id.homeitem_cb_follow);
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
                ((TextView) holder.findViewById(R.id.homeitem_tv_zan)).setText(listItem.zan + "");

                ((TextView) holder.findViewById(R.id.homeitem_tv_price)).setText(listItem.usertype == 1 ? "账号未认证" : ("0".equalsIgnoreCase(listItem.price) ? "商家未认证" : (listItem.price.equals("0")?"价格面议":"￥" + listItem.price + "/" + listItem.unit)));

                holder.findViewById(R.id.homeitem_iv_auth).setVisibility(View.VISIBLE);
                if (listItem.productionType == 2) {
                    ((ImageView) holder.findViewById(R.id.homeitem_iv_auth)).setImageResource(R.mipmap.auth_personal);
                } else if (listItem.productionType == 4) {
                    ((ImageView) holder.findViewById(R.id.homeitem_iv_auth)).setImageResource(R.mipmap.auth_company);
                } else if (listItem.productionType == 6) {
                    ((ImageView) holder.findViewById(R.id.homeitem_iv_auth)).setImageResource(R.mipmap.auth_student);
                } else if(listItem.productionType == 7){
                    ((ImageView) holder.findViewById(R.id.homeitem_iv_auth)).setImageResource(R.mipmap.ic_shop_user);
                }else {
                    holder.findViewById(R.id.homeitem_iv_auth).setVisibility(View.GONE);
                }
            }

            if (isDetails) {
                PictureManager.displayImage(listItem.fmphoto, fmImageView, this);
            } else {
                PictureManager.displayImage(listItem.fmphoto, fmImageView);
            }

            titleTextView.setText(listItem.title + "");
            holder.findViewById(R.id.homeitem_btn_zan).setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramAnonymousView) {
                    if (onAdapterClickListener != null) {
                        onAdapterClickListener.onZanClick(listItem.id);
                    }
                }
            });
            holder.findViewById(R.id.homeitem_tv_invite).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAdapterClickListener != null) {
                        if (!TextUtils.isEmpty(listItem.invite)) {
                            onAdapterClickListener.onHeadClick(listItem.inviteUserid, listItem.invite);
                        }
                    }
                }
            });
            holder.findViewById(R.id.homeitem_tv_book).setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramAnonymousView) {
                    if (onAdapterClickListener != null) {
                        onAdapterClickListener.onBookClick(listItem.id);
                    }
                }
            });
            holder.findViewById(R.id.homeitem_iv_head).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAdapterClickListener != null) {
                        onAdapterClickListener.onHeadClick(listItem.userid, listItem.nickname);
                    }
                }
            });
            holder.getCovertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAdapterClickListener != null) {
                        onAdapterClickListener.onItemClick(v, position);
                    }
                }
            });
            ((ImageView) holder.findViewById(R.id.homeitem_iv_zan)).setImageResource(listItem.iszan == 1 ? R.mipmap.notice_icon_heart_check : R.mipmap.notice_icon_heart);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //在这里面加载ListView中的每个item的布局
   public class ListHolder extends RecyclerView.ViewHolder {
        TextView tv_market_title, tv_market_from, tv_market_time, tv_zd, tv_dj;
        ImageView iv_market_news;

        public ListHolder(View itemView) {
            super(itemView);
            //如果是headerview或者是footerview,直接返回
            tv_dj = itemView.findViewById(R.id.tv_dj);
            tv_zd = itemView.findViewById(R.id.tv_zd);
            tv_market_title = (TextView) itemView.findViewById(R.id.tv_market_title);
            tv_market_from = (TextView) itemView.findViewById(R.id.tv_market_from);
            tv_market_time = (TextView) itemView.findViewById(R.id.tv_market_time);
            iv_market_news = (ImageView) itemView.findViewById(R.id.iv_market_news);

        }
    }

    //返回View中Item的个数，这个时候，总的个数应该是ListView中Item的个数加上HeaderView和FooterView
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    */
/**
     * 刷新数据
     *//*

    public void refreshItem(List<CurrencyNewsListBean> newDatas) {
        mDatas.removeAll(mDatas);
        mDatas.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addData(int position, List<CurrencyNewsListBean> newDatas) {
//        mDatas.add(position, "Insert One");
        notifyItemChanged(position);
  */
/*      mDatas.removeAll(mDatas);
        mDatas.addAll(newDatas);*//*

//        notifyItemChanged(position);
    }

}
*/
