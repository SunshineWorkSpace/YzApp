package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.custom.BookDecorator;
import com.yingshixiezuovip.yingshi.custom.TodayDecorator;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.BookModel;
import com.yingshixiezuovip.yingshi.quote.calendarview.CalendarDay;
import com.yingshixiezuovip.yingshi.quote.calendarview.DayViewDecorator;
import com.yingshixiezuovip.yingshi.quote.calendarview.MaterialCalendarView;
import com.yingshixiezuovip.yingshi.quote.calendarview.OnDateSelectedListener;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Resmic on 2017/5/8.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class MainDetailsBookActivity extends BaseActivity {
    private MaterialCalendarView mCalendarView;
    private List<CalendarDay> mCalendarDays;
    private int mItemID;
    private BookModel mBookModel;
    private boolean isEdit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_details_book, R.string.title_activity_main_details_book);
        mItemID = getIntent().getIntExtra("item_id", -1);
        if (mItemID < 0) {
            showMessage("参数异常，请稍后重试");
            return;
        }
        initView();
        initBookSureWindow();
        loadData();
    }

    private void loadData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", mItemID);
        showLoadLayout();
        HttpUtils.doPost(TaskType.TASK_TYPE_SUBSCRIBE_STAR_QRY, params, this);
    }

    public void submitBook() {
        String selectTime = "";
        for (CalendarDay day : mCalendarDays) {
            selectTime += day.toString() + ",";
        }
        selectTime = selectTime.substring(0, selectTime.lastIndexOf(","));
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", mItemID);
        params.put("token", mUserInfo.token);
        params.put("time", selectTime);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_SUBSCRIBE_STAR, params, this);
    }


    private void initBookSureWindow() {
        mAlertWindow = new AlertWindow(this, true);
        mAlertWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    submitBook();
                }
            }
        });
    }

    private void initView() {
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
        findViewById(R.id.detailsbook_btn_submit).setOnClickListener(this);
    }

    private void UpdateUI() {
        if (mCalendarDays.size() == 0) {
            findViewById(R.id.detailsbook_tv_datainfo).setVisibility(View.GONE);
        } else {
            findViewById(R.id.detailsbook_tv_datainfo).setVisibility(View.VISIBLE);
            String selectTime = "";
            for (CalendarDay day : mCalendarDays) {
                selectTime += day.toString() + "，";
            }
            ((TextView) findViewById(R.id.detailsbook_tv_datainfo)).setText(selectTime);
        }
        if (!isEdit && mCalendarDays.size() == 0) {
            findViewById(R.id.detailsbook_btn_submit).setVisibility(View.GONE);
        } else {
            findViewById(R.id.detailsbook_btn_submit).setVisibility(View.VISIBLE);
        }
    }

    private void initCalendarView() {
        List<DayViewDecorator> bookDecorators = new ArrayList<>();
        for (BookModel.BookItem bookItem : mBookModel.data) {
            bookDecorators.add(new BookDecorator(this, bookItem));
        }
        mCalendarView.addDecorators(bookDecorators);
        mCalendarView.invalidateDecorators();
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.detailsbook_btn_submit:
                if (isEdit) {
                    isEdit = false;
                    UpdateUI();
                } else {
                    mAlertWindow.show("温馨提示", "在正式下单前，请先沟通确认时间", "取消", "确定");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            if (result instanceof BaseThrowable) {
                switch (((BaseThrowable) result).getErrorCode()) {
                    case 402:
                        showMessage("商家非会员不能进行预订");
                        break;
                    case 403:
                        showMessage("时间已被其他人预约，请重新选择");
                        break;
                    case 406:
                        showMessage("您已经预约过该时间，请重新选择");
                        break;
                    default:
                        showMessage("未知异常预约失败，请稍后重试");
                        break;
                }
            } else {
                showMessage(((Throwable) result).getMessage());
            }
            return;
        }
        closeLoadLayout();
        switch (type) {
            case TASK_TYPE_SUBSCRIBE_STAR_QRY:
                mBookModel = GsonUtil.fromJson(result.toString(), BookModel.class);
                if (mBookModel != null && mBookModel.data != null && mBookModel.data.size() > 0) {
                    L.d("----初始化预订时间----");
                    initCalendarView();
                }
                break;
            case TASK_TYPE_SUBSCRIBE_STAR:
                showMessage("预约成功");
                onBackPressed();
                break;
        }
    }
}
