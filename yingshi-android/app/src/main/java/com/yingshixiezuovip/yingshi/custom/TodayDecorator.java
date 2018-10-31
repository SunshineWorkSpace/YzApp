package com.yingshixiezuovip.yingshi.custom;

import com.yingshixiezuovip.yingshi.quote.calendarview.CalendarDay;
import com.yingshixiezuovip.yingshi.quote.calendarview.DayViewDecorator;
import com.yingshixiezuovip.yingshi.quote.calendarview.DayViewFacade;

import java.util.Calendar;

/**
 * Created by Resmic on 2017/5/24.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class TodayDecorator implements DayViewDecorator {
    @Override
    public boolean shouldDecorate(CalendarDay calendarDay) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        boolean noSelected;
        if (calendarDay.getYear() < year) {
            noSelected = true;
        } else if (calendarDay.getYear() == year && calendarDay.getMonth() < month) {
            noSelected = true;
        } else if (calendarDay.getYear() == year && calendarDay.getMonth() == month && calendarDay.getDay() <= day) {
            noSelected = true;
        } else {
            noSelected = false;
        }
        return noSelected;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(true);
    }
}
