package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.TodayDecorator;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.BuyerModel;
import com.yingshixiezuovip.yingshi.quote.calendarview.CalendarDay;
import com.yingshixiezuovip.yingshi.quote.calendarview.MaterialCalendarView;
import com.yingshixiezuovip.yingshi.quote.calendarview.OnDateSelectedListener;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.L;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class OrderDetailsResetDateActivity extends BaseActivity {
    private BuyerModel.BuyerItem mBuyerItem;
    private MaterialCalendarView mCalendarView;
    private List<CalendarDay> mCalendarDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_reset_date, R.string.title_activity_order_details_reset_date);

        mBuyerItem = (BuyerModel.BuyerItem) getIntent().getSerializableExtra("item_data");
        if (mBuyerItem == null) {
            showMessage("参数异常，请稍后重试");
            return;
        }
        initView();
    }


    private void initView() {
        String bookTime = "";
        for (String time : mBuyerItem.list) {
            bookTime += time + "  ";
        }
        ((TextView) findViewById(R.id.orderbook_tv_time)).setText("订单日期：" + bookTime);
        findViewById(R.id.orderbook_btn_edit).setOnClickListener(this);
        findViewById(R.id.orderbook_btn_submit).setOnClickListener(this);
        ((TextView) findViewById(R.id.right_btn_name)).setText("提交修改");
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);

        mCalendarDays = new ArrayList<>();
        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        mCalendarView.setTitleAnimationOrientation(MaterialCalendarView.HORIZONTAL);
        mCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                L.d(date.toString());
                if (selected && !mCalendarDays.contains(date)) {
                    mCalendarDays.add(date);
                } else {
                    mCalendarDays.remove(date);
                }
                UpdateUI();
            }
        });
        mCalendarView.addDecorator(new TodayDecorator());
        mCalendarView.setCurrentDate(CalendarDay.from(Calendar.getInstance()));
    }

    private void UpdateUI() {
        String selectTime = "";
        for (CalendarDay day : mCalendarDays) {
            selectTime += day.toString() + "，";
        }
        ((TextView) findViewById(R.id.orderbook_tv_datainfo)).setText(TextUtils.isEmpty(selectTime) ? "请点击选择预定的档期" : selectTime);
    }

    public void submitBook() {
        String selectTime = "";
        for (CalendarDay day : mCalendarDays) {
            selectTime += day.toString() + ",";
        }
        if (TextUtils.isEmpty(selectTime)) {
            showMessage("没有选择修改预定档期");
            return;
        }
        selectTime = selectTime.substring(0, selectTime.lastIndexOf(","));
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", mBuyerItem.id);
        params.put("token", mUserInfo.token);
        params.put("time", selectTime);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_UPDATE_ORDER_DATE, params, this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.orderbook_btn_edit:
                findViewById(R.id.orderbook_edit_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.orderbook_info_layout).setVisibility(View.GONE);
                break;
            case R.id.orderbook_btn_submit:
                findViewById(R.id.orderbook_edit_layout).setVisibility(View.GONE);
                findViewById(R.id.orderbook_info_layout).setVisibility(View.VISIBLE);
                break;
            case R.id.right_btn_submit:
                submitBook();
                break;
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_UPDATE_ORDER_DATE:
                showMessage("修改订单预约成功");
                EventUtils.doPostEvent(EventType.EVENT_TYPE_REFRESH_PRICE);
                onBackPressed();
                break;
        }
    }
}
