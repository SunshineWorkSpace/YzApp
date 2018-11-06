package com.yingshixiezuovip.yingshi.custom;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.adapter.BirthdayAdapter;
import com.yingshixiezuovip.yingshi.adapter.CityAdapter;
import com.yingshixiezuovip.yingshi.adapter.NewOldAdapter;
import com.yingshixiezuovip.yingshi.adapter.ProvinceAdapter;
import com.yingshixiezuovip.yingshi.adapter.SexStatusAdapter;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;
import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.quote.dropview.OnItemSelectedListener;
import com.yingshixiezuovip.yingshi.quote.dropview.WheelView;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.ToastUtils;

import java.util.Calendar;

/**
 * Created by Resmic on 2017/5/17.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class SelectWindow extends BasePopupWindow {
    private WheelView mWheelViewYear;
    private WheelView mWheelViewMouth;
    private WheelView mWheelViewDay;
    private int minYear, maxYear;
    private int mCurrentY, mCurrentM, mCurrentD;
    private String[] mSexStatus;
    private PlaceModel mPlaceModel;
    private int mType;
    private String [] mNewOldStatus;
    public SelectWindow(Context mContext, int type) {
        super(mContext);
        initView(type);
        mSexStatus = mContext.getResources().getStringArray(R.array.text_feel_status);
        mNewOldStatus= mContext.getResources().getStringArray(R.array.text_new_old);
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_wheel_view_layout, null);
    }


    private void initView(int type) {
        mType = type;
        mWheelViewYear = (WheelView) getView().findViewById(R.id.wheelview_year);
        mWheelViewMouth = (WheelView) getView().findViewById(R.id.wheelview_mouth);
        mWheelViewDay = (WheelView) getView().findViewById(R.id.wheelview_day);
        getView().findViewById(R.id.btn_cancel).setOnClickListener(this);
        getView().findViewById(R.id.btn_submit).setOnClickListener(this);
        if (type == 1 || type == 4) {
            initBirth();
        } else if (type == 2) {
            initFeelStatus();
        }
        if(type==5){
            initNewOld();
        }
    }


    public void show(int feelStatus) {
        ((TextView) findViewById(R.id.select_tv_title)).setText("请选择性别");
        mWheelViewYear.setCurrentItem(feelStatus - 1);
        ((TextView) findViewById(R.id.select_tv_title)).setText(mSexStatus[feelStatus - 1]);
        super.show();
    }

    public void show() {
        ((TextView) findViewById(R.id.select_tv_title)).setText("请选择生日");
        mCurrentY = Calendar.getInstance().get(Calendar.YEAR);
        mCurrentM = Calendar.getInstance().get(Calendar.MONTH);
        mCurrentD = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        mWheelViewYear.setCurrentItem(50 - 1);
        mWheelViewMouth.setCurrentItem(mCurrentM);
        mWheelViewDay.setCurrentItem(mCurrentD - 1);
        ((TextView) findViewById(R.id.select_tv_title)).setText(mCurrentY + "年" + CommUtils.formatNumber(mCurrentM + 1) + "月" + CommUtils.formatNumber(mCurrentD) + "日");
        super.show();
    }

    public void show(PlaceModel placeModel, String city) {
        this.mPlaceModel = placeModel;
        ((TextView) findViewById(R.id.select_tv_title)).setText(city);
        initCitySelect();
        super.show();
    }

    private void initBirth() {
        if (mType == 1) {
            maxYear = Calendar.getInstance().get(Calendar.YEAR);
            minYear = maxYear - 50;
        } else {
            maxYear = Calendar.getInstance().get(Calendar.YEAR) + 20;
            minYear = Calendar.getInstance().get(Calendar.YEAR) - 50;
        }
        mWheelViewMouth.setVisibility(View.VISIBLE);
        mWheelViewDay.setVisibility(View.VISIBLE);

        mWheelViewYear.setAdapter(new BirthdayAdapter(minYear, maxYear, "%4d"));
        mWheelViewYear.setGravity(Gravity.CENTER);
        mWheelViewYear.setTextSize(24F, 14F);
        mWheelViewYear.setLineSpacingMultiplier(1.5F);
        mWheelViewYear.setCyclic(true);
        mWheelViewYear.setOnItemSelectedListener(itemSelectedListener);

        mWheelViewMouth.setAdapter(new BirthdayAdapter(1, 12, "%02d"));
        mWheelViewMouth.setGravity(Gravity.CENTER);
        mWheelViewMouth.setTextSize(24F, 14F);
        mWheelViewMouth.setLineSpacingMultiplier(1.5F);
        mWheelViewMouth.setCyclic(true);
        mWheelViewMouth.setOnItemSelectedListener(itemSelectedListener);

        mWheelViewDay.setAdapter(new BirthdayAdapter(1, 31, "%02d"));
        mWheelViewDay.setGravity(Gravity.CENTER);
        mWheelViewDay.setTextSize(24F, 14F);
        mWheelViewDay.setLineSpacingMultiplier(1.5F);
        mWheelViewDay.setCyclic(true);
        mWheelViewDay.setOnItemSelectedListener(itemSelectedListener);
    }

    private void initCitySelect() {
        mWheelViewMouth.setVisibility(View.VISIBLE);
        mWheelViewYear.setGravity(Gravity.CENTER);
        mWheelViewYear.setTextSize(24F, 14F);
        mWheelViewYear.setLineSpacingMultiplier(1.5F);
        mWheelViewYear.setCyclic(false);
        mWheelViewYear.setAdapter(new ProvinceAdapter(mPlaceModel.data));
        mWheelViewYear.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                L.d("onItemSelected::" + index);
                mWheelViewMouth.setAdapter(new CityAdapter(mPlaceModel.data.get(index).list));
                mWheelViewMouth.setCurrentItem(0);
                ((TextView) findViewById(R.id.select_tv_title)).setText(mPlaceModel.data.get(index).provinceName + " " + mPlaceModel.data.get(index).list.get(0).cityName);
            }
        });
        mWheelViewYear.setCurrentItem(0);
        mWheelViewMouth.setAdapter(new CityAdapter(mPlaceModel.data.get(0).list));
        mWheelViewMouth.setGravity(Gravity.CENTER);
        mWheelViewMouth.setTextSize(24F, 14F);
        mWheelViewMouth.setLineSpacingMultiplier(1.5F);
        mWheelViewMouth.setCyclic(false);
        mWheelViewMouth.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                ((TextView) findViewById(R.id.select_tv_title)).setText(mPlaceModel.data.get(mWheelViewYear.getCurrentItem()).provinceName + " " + mPlaceModel.data.get(mWheelViewYear.getCurrentItem()).list.get(mWheelViewMouth.getCurrentItem()).cityName);
            }
        });

    }


    private void initFeelStatus() {
        mWheelViewYear.setAdapter(new SexStatusAdapter(mContext));
        mWheelViewYear.setGravity(Gravity.CENTER);
        mWheelViewYear.setTextSize(24F, 14F);
        mWheelViewYear.setLineSpacingMultiplier(1.5F);
        mWheelViewYear.setCyclic(false);
        mWheelViewYear.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                ((TextView) findViewById(R.id.select_tv_title)).setText(mSexStatus[index]);
            }
        });
    }

    private void initNewOld() {
        mWheelViewYear.setAdapter(new NewOldAdapter(mContext));
        mWheelViewYear.setGravity(Gravity.CENTER);
        mWheelViewYear.setTextSize(24F, 14F);
        mWheelViewYear.setLineSpacingMultiplier(1.5F);
        mWheelViewYear.setCyclic(false);
        mWheelViewYear.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                ((TextView) findViewById(R.id.select_tv_title)).setText(mNewOldStatus[index]);
            }
        });
    }
    public void show(int newold,String type) {
        ((TextView) findViewById(R.id.select_tv_title)).setText("新旧程度");
        mWheelViewYear.setCurrentItem(newold - 1);
        ((TextView) findViewById(R.id.select_tv_title)).setText(mNewOldStatus[newold - 1]);
        super.show();
    }

    OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(int index) {
            int maxDay;
            switch (mWheelViewMouth.getCurrentItem()) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    maxDay = 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    maxDay = 30;
                    break;
                default:
                    if (leapYear(mWheelViewYear.getCurrentItem())) {
                        maxDay = 29;
                    } else {
                        maxDay = 28;
                    }
                    break;
            }
            ((TextView) findViewById(R.id.select_tv_title)).setText(mWheelViewYear.getCurrentItem() + "年" + CommUtils.formatNumber(mWheelViewMouth.getCurrentItem()) + "月" + CommUtils.formatNumber(mWheelViewDay.getCurrentItem()) + "日");
            mWheelViewDay.setAdapter(new BirthdayAdapter(1, maxDay, "%02d"));
        }
    };

    public boolean leapYear(int year) {
        if (((year % 100 == 0) && (year % 400 == 0))
                || ((year % 100 != 0) && (year % 4 == 0)))
            return true;
        else return false;
    }

    public interface OnWVItemSelectedListener {
        void onItemSelected(String selectContent);

    }

    public void setOnItemSelectedListener(OnWVItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    private OnWVItemSelectedListener onItemSelectedListener;


    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.btn_submit:
                if (onItemSelectedListener == null) {
                    return;
                }
                if (mType == 1) {
                    if (mWheelViewYear.getCurrentItem() >= mCurrentY && mWheelViewMouth.getCurrentItem() >= mCurrentM + 1 && mWheelViewDay.getCurrentItem() >= mCurrentD) {
                        ToastUtils.showMessage((Activity) mContext, "出生日期不能晚于当前日期");
                        return;
                    }
                    onItemSelectedListener.onItemSelected(mWheelViewYear.getCurrentItem() + "/" + CommUtils.formatNumber(mWheelViewMouth.getCurrentItem()) + "/" + CommUtils.formatNumber(mWheelViewDay.getCurrentItem()));
                } else if (mType == 4) {
                    onItemSelectedListener.onItemSelected(mWheelViewYear.getCurrentItem() + "/" + CommUtils.formatNumber(mWheelViewMouth.getCurrentItem()) + "/" + CommUtils.formatNumber(mWheelViewDay.getCurrentItem()));
                } else if (mType == 2) {
                    onItemSelectedListener.onItemSelected(mSexStatus[mWheelViewYear.getCurrentItem()]);
                } else if (mType == 3) {
                    String proviceName = mPlaceModel.data.get(mWheelViewYear.getCurrentItem()).provinceName;
                    String cityName = mPlaceModel.data.get(mWheelViewYear.getCurrentItem()).list.get(mWheelViewMouth.getCurrentItem()).cityName;
                    String result = proviceName.equalsIgnoreCase(cityName) ? proviceName : (proviceName + " " + cityName);
                    onItemSelectedListener.onItemSelected(result);
                }else if(mType==5){
                    onItemSelectedListener.onItemSelected(mNewOldStatus[mWheelViewYear.getCurrentItem()]);
                }
                break;
        }
        cancel();
    }
}

