package com.yingshixiezuovip.yingshi;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yingshixiezuovip.yingshi.adapter.ShopDetailImageAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.fragment.ShopTabFragment;
import com.yingshixiezuovip.yingshi.model.ShopDetailTypeModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.ImageLoaderNew;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.widget.ScaleImageView;

import java.util.HashMap;

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
    private String mPhone;
    private  GridLayoutManager layoutManager;
    private ShopDetailImageAdapter shopDetailImageAdapter;

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
        id=getIntent().getStringExtra("id");
        loadData();

        //设置item之间的间隔
        SpacesItemDecoration decoration=new SpacesItemDecoration(16);
        mRecyclerView.addItemDecoration(decoration);
        shopDetailImageAdapter=new ShopDetailImageAdapter();
        shopDetailImageAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        shopDetailImageAdapter.setPreLoadNumber(2);
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
                ShopDetailTypeModel  shopDetailType = GsonUtil.fromJson(result.toString(), ShopDetailTypeModel.class);
                if (shopDetailType != null) {
                    if (shopDetailType.data != null) {
                        mShopDetail=shopDetailType.data;
                        mPhone=mShopDetail.phone;
                        inflateView();
                    }
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;
        }
    }

    public void inflateView(){
        if(null!=mShopDetail) {
            tv_user_name.setText(mShopDetail.nickname);
            tv_num.setText(mShopDetail.num);
            tv_degree.setText(mShopDetail.isnew);
            tv_fllow.setText(mShopDetail.isfollow == 0 ? "+关注" : "已关注");
            tv_fllow.setTextColor(getResources().getColor(mShopDetail.isfollow == 1
                    ? R.color.colorWhite : R.color.colorLanse));
            tv_fllow.setBackgroundResource(mShopDetail.isfollow == 1 ?
                    R.drawable.concern_alread_shape : R.drawable.concern_shape);
            tv_price.setText("¥"+mShopDetail.price);
            tv_premium_money.setText("¥"+mShopDetail.vipMoney);
            tv_title.setText(mShopDetail.title);
            tv_content.setText(mShopDetail.content);
            tv_address.setText(mShopDetail.city);
            ImageLoaderNew.load(this,
                    mShopDetail.head, iv_user_head);
            if(null!=mShopDetail.photoList){
                if(mShopDetail.photoList.size()==1){
                    mScaleImageView.setVisibility(View.VISIBLE);
                    PictureManager.displayHead(mShopDetail.photoList.get(0).photo,
                            mScaleImageView);
                }else {

                    mScaleImageView.setVisibility(View.GONE);
                    // 如果我们想要一个GridView形式的RecyclerView，那么在LayoutManager上我们就要使用GridLayoutManager
                    // 实例化一个GridLayoutManager，列数为3
                    if (mShopDetail.photoList.size() < 5 && mShopDetail.photoList.size() > 1) {
                        layoutManager = new GridLayoutManager(this,
                                2);
                    }else{
                        layoutManager = new GridLayoutManager(this,
                                3);
                    }
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(shopDetailImageAdapter);
                    shopDetailImageAdapter.setNewData(mShopDetail.photoList);
                }
            }

        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space=space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.left=space;
            outRect.right=space;
            outRect.bottom=space;
            if(parent.getChildAdapterPosition(view)==0){
                outRect.top=space;
            }
        }
    }
    @Override
    public void taskIsCanceled(TaskType type) {
        super.taskIsCanceled(type);
    }
}
