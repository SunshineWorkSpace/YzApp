package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.BuyerDetailModel;
import com.yingshixiezuovip.yingshi.model.BuyerOrderModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.util.HashMap;

/**
 * 类名称:BuyPlayerProfileActivity
 * 类描述:买家信息
 * 创建时间: 2018-11-12-23:55
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class BuyPlayerProfileActivity extends BaseActivity {
    private TextView tv_area_detail,tv_area,tv_phone,tv_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_message);
        setActivityTitle("买家资料");
        tv_name=(TextView)findViewById(R.id.tv_name);
        tv_phone=(TextView)findViewById(R.id.tv_phone);
        tv_area=(TextView)findViewById(R.id.tv_area);
        tv_area_detail=(TextView)findViewById(R.id.tv_area_detail);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("id", getIntent().getStringExtra("id"));
        HttpUtils.doPost(TaskType.TASK_TYPE_MALL_BUYER_MESSAGE, params, this);

    }
    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type){
            case TASK_TYPE_MALL_BUYER_MESSAGE:
                BuyerDetailModel buyerDetailModel=GsonUtil.fromJson(result.toString(), BuyerDetailModel.class);
                tv_name.setText(buyerDetailModel.data.revcname);
                tv_phone.setText(buyerDetailModel.data.telphone);
                tv_area.setText(buyerDetailModel.data.city);
                tv_area_detail.setText(buyerDetailModel.data.address);
                break;
        }
    }
}
