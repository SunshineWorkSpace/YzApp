package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.BuyerModel;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import org.json.JSONObject;

import java.util.HashMap;

public class OrderDetailsActivity extends BaseActivity {
    public static final int TYPE_USER_SELLER = 1, TYPE_USER_BUYER = 2;//卖家，买家
    private BuyerModel.BuyerItem mBuyerItem;
    private int mType = TYPE_USER_SELLER;
    private AlertWindow mMakeSureWindow;
    private AlertWindow mCompleteWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details, R.string.title_activity_order_details);

        mType = getIntent().getIntExtra("item_user_type", TYPE_USER_SELLER);
        mBuyerItem = (BuyerModel.BuyerItem) getIntent().getSerializableExtra("item_data");
        if (mBuyerItem == null) {
            showMessage("参数异常，请稍后重试");
            return;
        }
        initView();
    }

    private void initView() {
        setActivityTitle("订单资料");
        if (mType == TYPE_USER_SELLER) {
            inflateData();
        } else {
            inflateBuyerData();
        }
    }


    private void inflateData() {
        switch (mBuyerItem.status) {
            case 1:
                findViewById(R.id.orderdetails_status_1_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.detailsbook_btn_choiceDate).setOnClickListener(this);
                findViewById(R.id.detailsbook_btn_choicePrice).setOnClickListener(this);
                break;
            case 2:
            case 3:
            case 4:
                findViewById(R.id.orderdetails_status_2_layout).setVisibility(View.VISIBLE);
                setActivityTitle(mBuyerItem.status == 2 ? "订单资料-正在接单中..." : (mBuyerItem.status == 3) ? "订单资料-等待对方支付订单" : "订单资料-已完成");
                ((TextView) findViewById(R.id.orderdetails_tv_status)).setText(mBuyerItem.status == 2 ? "正在接单中..." : (mBuyerItem.status == 3 ? "等待对方支付订单中..." : "对方已确认完成订单支付"));
                ((TextView) findViewById(R.id.orderdetails_tv_tips)).setText(mBuyerItem.status == 2 ? "温馨提示：完成指定任务，请联系对方，让对方确认订单完成" : (mBuyerItem.status == 3) ? "温馨提示：如对方在24小时内未完成，将自动取消此订单" : "客服会在1-5个工作日内联系你，支付金额。");
                break;
        }
        findViewById(R.id.orderdetails_btn_submit).setOnClickListener(this);

        String bookTime = "";
        for (String time : mBuyerItem.list) {
            bookTime += time + "  ";
        }
        ((TextView) findViewById(R.id.orderdetails_book_time)).setText("预订档期：" + bookTime);
        ((TextView) findViewById(R.id.orderdetails_tv_telephone)).setText("买家联系电话：" + mBuyerItem.telphone + "  支付酬薪：" + mBuyerItem.total + "元");

        mMakeSureWindow = new AlertWindow(this, false);
        mMakeSureWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    doSubmit();
                }
            }
        });
    }

    private void inflateBuyerData() {
        switch (mBuyerItem.status) {
            case 1:
                findViewById(R.id.orderdetails_status_2_layout).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.orderdetails_tv_status)).setText("等待对方接受订单");
                findViewById(R.id.orderdetails_status_2_lin_layout).setBackgroundColor(Color.TRANSPARENT);
                ((TextView) findViewById(R.id.orderdetails_tv_telephone)).setText("卖家联系电话：" + mBuyerItem.telphone + "  酬薪：" + mBuyerItem.total + "元");
                ((TextView) findViewById(R.id.orderdetails_tv_tips)).setText("温馨提示：如超过24小时未接受订单，将自动取消订单");
                ((TextView) findViewById(R.id.orderdetails_tv_tips)).setTextColor(getWColor(R.color.colorRed));
                break;
            case 2:
                findViewById(R.id.orderdetails_btn_finish).setVisibility(View.VISIBLE);
                findViewById(R.id.orderdetails_btn_finish).setOnClickListener(this);
            case 3:
            case 4:
                findViewById(R.id.orderdetails_status_2_layout).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.orderdetails_tv_telephone)).setText("卖家联系电话：" + mBuyerItem.telphone + "  支付酬薪：" + mBuyerItem.total + "元");
                ((TextView) findViewById(R.id.orderdetails_tv_status)).setText(mBuyerItem.status == 2 ? "对方接受了订单" : "已完成订单");
                ((TextView) findViewById(R.id.orderdetails_tv_tips)).setText(mBuyerItem.status == 2 ? "如已完成订单，请在订单规定时间内点击完成订单" : "如果有所疑问，可联系客服人员。");
                break;
        }
        String bookTime = "";
        for (String time : mBuyerItem.list) {
            bookTime += time + "  ";
        }
        ((TextView) findViewById(R.id.orderdetails_book_time)).setText("预订档期：" + bookTime);
        ((TextView) findViewById(R.id.orderdetails_tv_info)).setText("订单号：" + mBuyerItem.orderno + "\n" + "商家账号::" + mBuyerItem.nickname);
        mCompleteWindow = new AlertWindow(this, false);
        mCompleteWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    HashMap<String, Object> orderParams = new HashMap<>();
                    orderParams.put("token", mUserInfo.token);
                    orderParams.put("id", mBuyerItem.id);
                    mLoadWindow.show(R.string.waiting);
                    HttpUtils.doPost(TaskType.TASK_TYPE_ORDER_COMPLETE, orderParams, OrderDetailsActivity.this);
                }
            }
        });
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        switch (event.getEventType()) {
            case EVENT_TYPE_REFRESH_PRICE:
                finish();
                break;
        }
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        Intent intent = null;
        switch (v.getId()) {
            case R.id.detailsbook_btn_choiceDate:
                intent = new Intent(this, OrderDetailsResetDateActivity.class);
                break;
            case R.id.detailsbook_btn_choicePrice:
                intent = new Intent(this, OrderDetailsResetPriceActivity.class);
                break;
            case R.id.orderdetails_btn_submit:
                mMakeSureWindow.show("", "确定接受订单？", "取消", "确定");
                break;
            case R.id.orderdetails_btn_finish:
                mCompleteWindow.show("", "确认支付订单？", "取消", "确定");
                break;
        }
        if (intent != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("item_data", mBuyerItem);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void doSubmit() {
        HashMap<String, Object> orderParams = new HashMap<>();
        orderParams.put("token", mUserInfo.token);
        orderParams.put("id", mBuyerItem.id);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_MAKE_SURE_ORDER, orderParams, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        closeLoadLayout();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_MSG_SELLER_ORDER_DETAILS:
                try {
                    mBuyerItem = GsonUtil.fromJson(((JSONObject) result).optString("data"), BuyerModel.BuyerItem.class);
                } catch (Exception e) {
                    mBuyerItem = null;
                }
                if (mBuyerItem == null) {
                    showMessage("订单获取失败，请稍后重试");
                    return;
                }
                inflateData();
                break;
            case TASK_TYPE_MAKE_SURE_ORDER:
                showMessage("接单成功");
                EventUtils.doPostEvent(EventType.EVENT_TYPE_CLOSE_DETAILS);
                onBackPressed();
                break;
            case TASK_TYPE_ORDER_COMPLETE:
                showMessage("交易成功");
                EventUtils.doPostEvent(EventType.EVENT_TYPE_CLOSE_DETAILS);
                onBackPressed();
                break;
        }
    }


}
