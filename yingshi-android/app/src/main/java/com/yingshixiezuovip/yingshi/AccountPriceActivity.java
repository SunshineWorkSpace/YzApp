package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.BuyerOrderModel;
import com.yingshixiezuovip.yingshi.model.MyBalanceModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.util.HashMap;

/**
 * 类名称:AccountPriceActivity
 * 类描述:账户余额
 * 创建时间: 2018-11-13-01:17
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class AccountPriceActivity extends BaseActivity {
    private TextView tv_ac_price,tv_ac_other,tv_shop_price,tv_ac_get,tv_price_get;
    private MyBalanceModel myBalanceModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_price);
        setActivityTitle("账户余额");
        tv_ac_price=(TextView)findViewById(R.id.tv_ac_price);
        tv_ac_other=(TextView)findViewById(R.id.tv_ac_other);
        tv_shop_price=(TextView)findViewById(R.id.tv_shop_price);
        tv_ac_get=(TextView)findViewById(R.id.tv_ac_get);
        tv_ac_get.setOnClickListener(this);
        tv_price_get=(TextView)findViewById(R.id.tv_price_get);
        tv_price_get.setOnClickListener(this);
        initDate();
    }

    private void initDate() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_MY_BALANCE, params, this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.tv_ac_get:
                AcGet();
                break;
            case R.id.tv_price_get:
                PricieGet();
                break;
        }
    }


    private void AcGet() {
        if(tv_shop_price.getText().toString().equals("0.0")){
            showMessage("消保金要大于0元，谢谢");
            return;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_XBJ_GET, params, this);

    }
    private void PricieGet() {
        if(tv_ac_price.getText().toString().equals("0.0")){
            showMessage("提现金额要大于0元，谢谢");
            return;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("money",myBalanceModel.data.balance);
        HttpUtils.doPost(TaskType.TASK_TYPE_TX_GET, params, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type){
            case TASK_TYPE_MY_BALANCE:
                myBalanceModel = GsonUtil.fromJson(result.toString(), MyBalanceModel.class);
                if (myBalanceModel != null) {
                    tv_ac_price.setText(myBalanceModel.data.balance);
                    tv_ac_other.setText(myBalanceModel.data.txz_balance);
                    tv_shop_price.setText(myBalanceModel.data.xbj_balance);
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;
            case TASK_TYPE_XBJ_GET:
                showMessage("提现成功");
                initDate();
                break;
            case TASK_TYPE_TX_GET:
                showMessage("提现成功");
                initDate();
                break;
        }
    }

}
