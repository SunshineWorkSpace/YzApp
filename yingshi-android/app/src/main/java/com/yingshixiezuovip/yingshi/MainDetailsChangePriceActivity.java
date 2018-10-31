package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.utils.EventUtils;

import java.util.HashMap;

public class MainDetailsChangePriceActivity extends BaseActivity {
    private boolean isDay = true;
    private int mWorkId;
    private String mWorkPrice;
    private String mWorkUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_details_change_price, R.string.title_activity_main_details_change_price);
        mWorkId = getIntent().getIntExtra("works_id", -1);
        mWorkPrice = getIntent().getStringExtra("works_price");
        mWorkUnit = getIntent().getStringExtra("works_unit");
        if (mWorkId < 0) {
            showMessage("参数异常，请稍后重试");
            return;
        }
        initView();
    }

    private void initView() {
        findViewById(R.id.price_btn_day).setOnClickListener(this);
        findViewById(R.id.price_btn_project).setOnClickListener(this);
        findViewById(R.id.price_btn_submit).setOnClickListener(this);
        ((TextView) findViewById(R.id.price_tv_info)).setText("温馨提示：该作品原薪酬：" + mWorkPrice + " / " + mWorkUnit);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.price_btn_day:
                onClickCheckBox(1);
                break;
            case R.id.price_btn_project:
                onClickCheckBox(2);
                break;
            case R.id.price_btn_submit:
                doSubmit();
                break;
        }
    }

    private void doSubmit() {
        double price;
        String priceStr = ((EditText) findViewById(R.id.price_et_input)).getText().toString();
        if (TextUtils.isEmpty(priceStr)) {
            showMessage("请输入修改的价格");
            return;
        }
        if (priceStr.startsWith(".")) {
            showMessage("请输入正确的价格");
            return;
        }
        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception e) {
            showMessage("请输入正确的价格");
            return;
        }
        mLoadWindow.show(R.string.text_request);
        HashMap<String, Object> priceParams = new HashMap<>();
        priceParams.put("token", mUserInfo.token);
        priceParams.put("price", price);
        priceParams.put("unit", isDay ? "天" : "项目");
        priceParams.put("id", mWorkId);
        HttpUtils.doPost(TaskType.TASK_TYPE_UPDATE_WORKS_PRICE, priceParams, this);
    }

    private void onClickCheckBox(int index) {
        ((CheckBox) findViewById(R.id.price_cb_day)).setChecked(index == 1);
        ((CheckBox) findViewById(R.id.price_cb_project)).setChecked(index != 1);
        isDay = index == 1;
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_UPDATE_WORKS_PRICE:
                showMessage("修改成功");
                EventUtils.doPostEvent(EventType.EVENT_TYPE_REFRESH_PRICE);
                onBackPressed();
                break;
        }
    }
}
