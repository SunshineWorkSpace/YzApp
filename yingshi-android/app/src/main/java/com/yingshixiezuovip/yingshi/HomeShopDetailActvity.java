package com.yingshixiezuovip.yingshi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yingshixiezuovip.yingshi.adapter.ShopDetailImageAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseResp;
import com.yingshixiezuovip.yingshi.custom.PhoneWindow;
import com.yingshixiezuovip.yingshi.custom.ShareWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.ShareModel;
import com.yingshixiezuovip.yingshi.model.ShopDetailTypeModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.quote.video.JCVideoPlayerStandard;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.ImageLoaderNew;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.widget.ScaleImageNewView;
import com.yingshixiezuovip.yingshi.widget.ScaleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuhua.gou on 2018/11/9.
 */

public class HomeShopDetailActvity extends BaseActivity {

    private String id;
    private ShopDetailTypeModel.ShopDetailType mShopDetail;
    private RoundedImageView iv_user_head;
    private TextView tv_user_name,tv_num;
    private TextView tv_degree,tv_address,tv_price,tv_fllow,tv_premium_money,
            tv_title,tv_content;
    private RecyclerView mRecyclerView;
    private ScaleImageView mScaleImageView;
    private JCVideoPlayerStandard video_videoplayer;
    private LinearLayout details_btn_shops;
    private String mPhone;
    private  GridLayoutManager layoutManager;
    private ShopDetailImageAdapter shopDetailImageAdapter;
    private PhoneWindow mPhoneWindow;
    private ShareWindow mShareWindow;
    private ShareModel.ShareItem mShareItem;
    private int mIsfllow;
    final ArrayList<String> pictureList = new ArrayList<>();
    private Bundle mReenterState;
    ViewGroup parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_shop_detail,
                R.string.title_activity_home_shop_detail_buy);
        initView();
    }

    private void initView(){

        iv_user_head=(RoundedImageView) findViewById(R.id.iv_user_head);
        tv_user_name=(TextView) findViewById(R.id.tv_user_name);
        tv_num=(TextView) findViewById(R.id.tv_num);
        tv_degree=(TextView) findViewById(R.id.tv_degree);
        tv_address=(TextView) findViewById(R.id.tv_address);
        tv_price=(TextView) findViewById(R.id.tv_price);
        tv_fllow=(TextView) findViewById(R.id.tv_fllow);
        tv_premium_money=(TextView) findViewById(R.id.tv_premium_money);
        tv_title=(TextView) findViewById(R.id.tv_title);
        tv_content=(TextView) findViewById(R.id.tv_content);
        mRecyclerView=(RecyclerView) findViewById(R.id.rv);
        mScaleImageView=(ScaleImageView) findViewById(R.id.iv_one);
        video_videoplayer=(JCVideoPlayerStandard)findViewById(R.id.video_videoplayer);
        details_btn_shops= (LinearLayout) findViewById(R.id.details_btn_shops);
        parent = (ViewGroup) findViewById(R.id.rv);

        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_name).setVisibility(View.GONE);
        findViewById(R.id.right_iv_more).setVisibility(View.VISIBLE);

        findViewById(R.id.details_btn_contact).setOnClickListener(this);
        findViewById(R.id.btn_buy).setOnClickListener(this);
        findViewById(R.id.ll_shop_more).setOnClickListener(this);
        id=getIntent().getStringExtra("id");
        loadData();

        shopDetailImageAdapter=new ShopDetailImageAdapter();
        shopDetailImageAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        shopDetailImageAdapter.setPreLoadNumber(2);
        initShareWindow();
        details_btn_shops.setOnClickListener(this);
        shopDetailImageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ScaleImageNewView imageView=(ScaleImageNewView)view.findViewById(R.id.iv_imge);
                PhotoBrowseActivity.startWithElement(HomeShopDetailActvity.this, pictureList, position, imageView);

            }
        });
        mScaleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoBrowseActivity.startWithElement(HomeShopDetailActvity.this, pictureList, 0, mScaleImageView);

            }
        });
    }

    private void initShareWindow(){
        mShareItem=new ShareModel.ShareItem();
        mShareWindow = new ShareWindow(this,1,id);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.details_btn_contact:
                mPhoneWindow.show("用户联系电话 " + mPhone);
                break;
            case R.id.btn_buy://订单详情页
                Intent orderDetail=new Intent(HomeShopDetailActvity.this,
                        HomeShopOderDetailActivity.class);
                orderDetail.putExtra("orderDetail",mShopDetail);
                startActivity(orderDetail);
                break;
            case R.id.right_btn_submit:
                if(null!=shopDetailType){
                    Intent it=new Intent(this,ShopDetailShareActivity.class);
                    it.putExtra("title",shopDetailType.data.title);
                    it.putExtra("content",shopDetailType.data.content);
                    it.putExtra("img",shopDetailType.data.photoList.get(0).photo);
                    it.putExtra("au",shopDetailType.data.head);
                    it.putExtra("autv",shopDetailType.data.nickname);
                    it.putExtra("price",shopDetailType.data.price);
                    it.putExtra("shareurl",shopDetailType.data.shareurl);
                    startActivity(it);
                }

//                mShareWindow.show(mShareItem, this);
                break;
            case R.id.details_btn_shops://商铺
                Intent shops=new Intent(HomeShopDetailActvity.this,HomeShopUserActivity.class);
                shops.putExtra("uid",mShopDetail.uid+"");
                startActivity(shops);
                break;
            case R.id.tv_fllow:
                onFollowClick(mShopDetail.uid,mIsfllow);
                break;
            case R.id.ll_shop_more:
                Intent more=new Intent(HomeShopDetailActvity.this,HomeShopDetailMoreActivity.class);
                startActivity(more);
                break;
        }
    }

    public void onFollowClick(int userid, int follow) {
        HashMap localHashMap = new HashMap();
        localHashMap.put("followid", userid);
        localHashMap.put("token", mUserInfo.token);
        HttpUtils.doPost(follow == 1 ? TaskType.TASK_TYPE_HOME_CLEAR_FOLLOW:TaskType.TASK_TYPE_HOME_FOLLOW , localHashMap, this);
    }


    private void initCallWindow() {
        mPhoneWindow = new PhoneWindow(this, 1);
        mPhoneWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.dialog_item2) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhone));
                    startActivity(intent);
                }
                mPhoneWindow.cancel();
            }
        });
    }

    private void loadData() {
        mUserInfo = SPUtils.getUserInfo(this);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("id", id);
        HttpUtils.doPost(TaskType.TASK_TYPE_SHOPDETAIL, params, this);
    }

    @Override
    public void taskStarted(TaskType type) {
        super.taskStarted(type);
    }
    ShopDetailTypeModel  shopDetailType;
    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        super.taskFinished(type, result, isHistory);
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_SHOPDETAIL:
                System.out.println("商品详情:" + result.toString());
                shopDetailType = GsonUtil.fromJson(result.toString(), ShopDetailTypeModel.class);
                if (shopDetailType != null) {
                    if (shopDetailType.data != null) {
                        mShopDetail=shopDetailType.data;
                        mPhone=mShopDetail.phone;
                        inflateView();
                        initCallWindow();
                    }
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;
            case TASK_TYPE_HOME_FOLLOW ://关注
                BaseResp baseResp=GsonUtil.fromJson(result.toString(), BaseResp.class);
            if(200==baseResp.result.code) {
                mIsfllow = 1;
                tv_fllow.setText(mIsfllow == 0 ? "+关注" : "已关注");
                tv_fllow.setTextColor(getResources().getColor(mIsfllow == 1
                        ? R.color.colorWhite : R.color.colorLanse));
                tv_fllow.setBackgroundResource(mIsfllow == 1 ?
                        R.drawable.concern_alread_shape : R.drawable.concern_shape);
            }
                break;

            case TASK_TYPE_HOME_CLEAR_FOLLOW://取消关注
                BaseResp clearfollow=GsonUtil.fromJson(result.toString(), BaseResp.class);
                if(200==clearfollow.result.code) {
                    mIsfllow = 0;
                    tv_fllow.setText(mIsfllow == 0 ? "+关注" : "已关注");
                    tv_fllow.setTextColor(getResources().getColor(mIsfllow == 1
                            ? R.color.colorWhite : R.color.colorLanse));
                    tv_fllow.setBackgroundResource(mIsfllow == 1 ?
                            R.drawable.concern_alread_shape : R.drawable.concern_shape);
                }
                break;
        }
    }

    public void inflateView(){
        if(null!=mShopDetail) {
            mShareItem.content=mShopDetail.sharecontent;
            mShareItem.photo=mShopDetail.sharephoto;
            mShareItem.title=mShopDetail.sharetitle;
            mShareItem.url=mShopDetail.shareurl;
            tv_user_name.setText(mShopDetail.nickname);
            tv_num.setText(mShopDetail.num);
            tv_degree.setText(mShopDetail.isnew);
            mIsfllow=mShopDetail.isfollow;
            tv_fllow.setText(mIsfllow== 0 ? "+关注" : "已关注");
            tv_fllow.setTextColor(getResources().getColor(mIsfllow == 1
                    ? R.color.colorWhite : R.color.colorLanse));
            tv_fllow.setBackgroundResource(mIsfllow == 1 ?
                    R.drawable.concern_alread_shape : R.drawable.concern_shape);
            tv_fllow.setOnClickListener(this);
            tv_price.setText("¥"+mShopDetail.price);
            tv_premium_money.setText("¥"+mShopDetail.vipMoney);
            tv_title.setText(mShopDetail.title);
            tv_content.setText(mShopDetail.content);
            tv_address.setText(mShopDetail.city);
            ImageLoaderNew.load(this,
                    mShopDetail.head, iv_user_head);


            if(null!=mShopDetail.photoList){
                setImageList();
                if(mShopDetail.photoList.size()==1){
                    mScaleImageView.setVisibility(View.VISIBLE);
                    PictureManager.displayHead(mShopDetail.photoList.get(0).photo,
                            mScaleImageView);
                }else {

                    mScaleImageView.setVisibility(View.GONE);
                    int type=0;
                    // 如果我们想要一个GridView形式的RecyclerView，那么在LayoutManager上我们就要使用GridLayoutManager
                    // 实例化一个GridLayoutManager，列数为3
                    if (mShopDetail.photoList.size() < 5 && mShopDetail.photoList.size() > 1) {
                        if(mShopDetail.photoList.size()==3){
                            layoutManager = new GridLayoutManager(this,
                                    3);
                            type=3;
                        }else{
                            layoutManager = new GridLayoutManager(this,
                                    2);
                            type=2;
                        }
                    }else{
                        layoutManager = new GridLayoutManager(this,
                                3);
                        type=3;
                    }
                    //设置item之间的间隔
                    SpacesItemDecoration decoration=new SpacesItemDecoration(16,type);
                    mRecyclerView.addItemDecoration(decoration);
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(shopDetailImageAdapter);
                    shopDetailImageAdapter.setNewData(mShopDetail.photoList);
                    setSharedElementCallback(this);
                }
            }


            if(TextUtils.isEmpty(mShopDetail.video)){
                video_videoplayer.setVisibility(View.GONE);
            }else{
                video_videoplayer.setVisibility(View.VISIBLE);
                video_videoplayer.setUp(mShopDetail.video, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,
                        "");
                PictureManager.displayImage(mShopDetail.videofm, video_videoplayer.thumbImageView);

            }
        }
    }
    /**
     * 接管Activity的setExitSharedElementCallback
     * @param activity
     */
    public void setSharedElementCallback(Activity activity){
        ActivityCompat.setExitSharedElementCallback(activity, new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (mReenterState!=null){
                    int index = mReenterState.getInt("index",0);
                    sharedElements.clear();
                    sharedElements.put("tansition_view",parent.getChildAt(index));
                    mReenterState = null;
                }
            }
        });

    }
    private void setImageList(){
        for(int i=0;i<mShopDetail.photoList.size();i++){
            pictureList.add(mShopDetail.photoList.get(i).photo);
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;
        private int type;

        public SpacesItemDecoration(int space,int type) {
            this.space=space;
            this.type=type;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.left=space;
            outRect.right=space;
            outRect.bottom=space;
            if(parent.getChildAdapterPosition(view)==0||parent.getChildAdapterPosition(view)==1){
                outRect.top=space;
            }

            if(type==3){
                if(parent.getChildAdapterPosition(view)==2){
                    outRect.top=space;
                }
            }
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mReenterState = new Bundle(data.getExtras());
    }
    @Override
    public void taskIsCanceled(TaskType type) {
        super.taskIsCanceled(type);
    }
}
