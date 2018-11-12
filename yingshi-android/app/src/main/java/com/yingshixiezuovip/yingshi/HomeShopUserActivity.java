package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseResp;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.fragment.ShopUserFragment;
import com.yingshixiezuovip.yingshi.model.ShopUserModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.ImageLoaderNew;

import java.util.HashMap;

/**
 * Created by yuhua.gou on 2018/11/12.
 */

public class HomeShopUserActivity extends BaseActivity implements View.OnClickListener{
    private String mUid;
    private ShopUserModel.ShopUser mShopUser;
    private TextView tv_user_name,tv_address,tv_shop_type,tvFollow;
    private RoundedImageView iv_user_head;
    private int isFllow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_shop_user,
                R.string.title_activity_shop_user);
        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState){
        tv_user_name= (TextView) findViewById(R.id.tv_user_name);
        tv_address= (TextView) findViewById(R.id.tv_address);
        tv_shop_type= (TextView) findViewById(R.id.tv_shop_type);
        tvFollow= (TextView) findViewById(R.id.tv_follow_new);
        iv_user_head= (RoundedImageView) findViewById(R.id.iv_user_head);
        tvFollow.setOnClickListener(this);
        mUid=getIntent().getStringExtra("uid");
        loadData();

        if (savedInstanceState == null && getIntent() != null) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            ShopUserFragment kc = new ShopUserFragment();
            bundle.putString("uid",mUid);
            kc.setArguments(bundle);
            ft.add(R.id.fragment_shop_new, kc);
            ft.commitAllowingStateLoss();
        }
    }

    private void loadData(){
        mLoadWindow.show(R.string.text_request);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("uid", mUid);
        HttpUtils.doPost(TaskType.TASK_TYPE_SHOP_INFO, params, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        super.taskFinished(type, result, isHistory);
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type){
            case TASK_TYPE_SHOP_INFO:
                mLoadWindow.cancel();
                System.out.println("商铺:"+result.toString());
                ShopUserModel shopUserModel= GsonUtil.fromJson(result.toString(), ShopUserModel.class);
                if(null!=shopUserModel){
                    mShopUser=shopUserModel.data;
                    isFllow=mShopUser.isguanzhu;
                    initShopInfoView();
                }
                break;
            case TASK_TYPE_HOME_FOLLOW:
                System.out.println("关注:"+result.toString());
                BaseResp baseResp=GsonUtil.fromJson(result.toString(), BaseResp.class);
                isFllow=1;
                if(200==baseResp.result.code) {
                    tvFollow.setText("已关注");
                }
                break;
            case TASK_TYPE_HOME_CLEAR_FOLLOW:
                System.out.println("关注:"+result.toString());
                BaseResp resp=GsonUtil.fromJson(result.toString(), BaseResp.class);
                if(200==resp.result.code) {
                    isFllow = 0;
                    tvFollow.setText("+关注");
                }
                break;
        }
    }

    private void initShopInfoView(){
        ImageLoaderNew.load(this,
                mShopUser.head, iv_user_head);
        tv_user_name.setText(mShopUser.shopName+"  "+"消保金: "+mShopUser.vipMoney+"元");
        tv_address.setText(mShopUser.city);
        tv_shop_type.setText(mShopUser.shoptype);
        tvFollow.setText(mShopUser.isguanzhu == 0 ? "+关注" : "已关注");
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.tv_follow_new:
                onFollowClick(mUid,isFllow);
                break;
        }
    }


    public void onFollowClick(String userid, int follow) {
        HashMap localHashMap = new HashMap();
            localHashMap.put("followid", userid);
            localHashMap.put("token", mUserInfo.token);
            HttpUtils.doPost(follow == 1 ? TaskType.TASK_TYPE_HOME_CLEAR_FOLLOW:
                            TaskType.TASK_TYPE_HOME_FOLLOW
                    , localHashMap, this);
    }
}
