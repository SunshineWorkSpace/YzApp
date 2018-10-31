package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.BuyerModel;
import com.yingshixiezuovip.yingshi.utils.EventUtils;

import java.util.HashMap;

public class OrderDetailsResetPriceActivity extends BaseActivity {
    private BuyerModel.BuyerItem mBuyerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_reset_price, R.string.title_activity_order_details_reset_price);

        mBuyerItem = (BuyerModel.BuyerItem) getIntent().getSerializableExtra("item_data");
        if (mBuyerItem == null) {
            showMessage("参数异常，请稍后重试");
            return;
        }
        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.orderprice_tv_info)).setText("订单号：" + mBuyerItem.orderno + "\n" + "下单账号:" + mBuyerItem.nickname);
        String bookTime = "";
        for (String time : mBuyerItem.list) {
            bookTime += time + "  ";
        }
        ((TextView) findViewById(R.id.orderprice_book_time)).setText("预定档期：" + bookTime);
        ((EditText) findViewById(R.id.price_et_input)).setText(mBuyerItem.total);
        findViewById(R.id.orderprice_btn_set).setOnClickListener(this);
        findViewById(R.id.orderprice_btn_submit).setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.orderprice_btn_set:
                findViewById(R.id.price_et_input).setEnabled(true);
                findViewById(R.id.price_et_input).requestFocus();
                break;
            case R.id.orderprice_btn_submit:
                doSubmit();
                break;
        }
    }

    private void doSubmit() {
        String priceStr = ((EditText) findViewById(R.id.price_et_input)).getText().toString();
        if (TextUtils.isEmpty(priceStr)) {
            showMessage("请输入修改的价格");
            return;
        }
        if (priceStr.startsWith(".")) {
            showMessage("请输入正确的价格");
            return;
        }
        if (priceStr.equalsIgnoreCase(mBuyerItem.total)) {
            showMessage("没有修改价格");
            return;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", mBuyerItem.id);
        params.put("token", mUserInfo.token);
        params.put("total", priceStr);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_UPDATE_ORDER_PRICE, params, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_UPDATE_ORDER_PRICE:
                showMessage("修改成功");
                EventUtils.doPostEvent(EventType.EVENT_TYPE_REFRESH_PRICE);
                onBackPressed();
                break;
        }
    }
}
