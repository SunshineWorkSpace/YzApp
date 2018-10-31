package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.BookModel;
import com.yingshixiezuovip.yingshi.quote.calendarview.CalendarDay;
import com.yingshixiezuovip.yingshi.quote.calendarview.DayViewDecorator;
import com.yingshixiezuovip.yingshi.quote.calendarview.DayViewFacade;

/**
 * Created by Resmic on 2017/5/8.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class BookDecorator implements DayViewDecorator {
    private BookModel.BookItem mBookItem;
    private Context mContext;

    public BookDecorator(Context ctx, BookModel.BookItem bookItem) {
        this.mContext = ctx;
        this.mBookItem = bookItem;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return mBookItem.getYear() == day.getYear() && mBookItem.getMonth() - 1 == day.getMonth() && mBookItem.getDay() == day.getDay();
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.calendar_disable_shape));
        view.setDaysDisabled(true);
    }
}
