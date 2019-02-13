package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 类名称:ConsumerPriceSubmitWebActivity
 * 类描述:网银付款
 * 创建时间: 2019-01-18-10:38
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class ConsumerPriceSubmitWebActivity extends BaseActivity {
    String vip="";
    String price="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conprice_submit_new);
        setActivityTitle("商铺用户升级认证金");
//        mWXApi = WXAPIFactory.createWXAPI(this, Configs.WECHAT_APPID);
        try {
            vip = getIntent().getStringExtra("vip");
            price = getIntent().getStringExtra("price");
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.authmoney_btn_submit).setOnClickListener(this);
        ((TextView) findViewById(R.id.authmoney_tv_money)).setText(price + "元");

    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.authmoney_btn_submit:
                payMoneyDate();
                break;
        }
    }
    private void payMoneyDate() {
        HashMap<String, Object> orderParams = new HashMap<>();
        orderParams.put("token", mUserInfo.token);
        orderParams.put("vip", vip);
        orderParams.put("type", 3);
        mLoadWindow.show(R.string.waiting);
        HttpUtils.doPost(TaskType.TASK_TYPE_VIPS_SUBMIT, orderParams, this);
    }
    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            mLoadWindow.cancel();
            if (result instanceof BaseThrowable) {
                if (((BaseThrowable) result).getErrorCode() == 201) {
                    showMessage("已缴纳保证金，无需重复缴纳");
                } else if (((BaseThrowable) result).getErrorCode() == 404) {
                    showMessage("参数传递有误");
                }
            } else {
                showMessage(((Throwable) result).getMessage());
            }
            return;
        }
        switch (type) {
            case TASK_TYPE_VIPS_SUBMIT:
                if (((JSONObject) result).has("data")) {
                    mLoadWindow.cancel();
                   String url= ((JSONObject) result).optJSONObject("data").optString("url");
                   Intent it=new Intent(this,PayWebActivity.class);
                   it.putExtra("url",url);
                   startActivity(it);
                } else {
                    mLoadWindow.cancel();
                    showMessage("支付订单获取失败，请稍后重试");
                }
                break;
        }
    }
}
