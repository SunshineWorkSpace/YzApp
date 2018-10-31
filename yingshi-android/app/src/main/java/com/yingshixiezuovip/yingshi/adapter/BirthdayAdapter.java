package com.yingshixiezuovip.yingshi.adapter;


import com.yingshixiezuovip.yingshi.quote.dropview.WheelAdapter;

/**
 * Created by Resmic on 2016/8/26.
 */

public class BirthdayAdapter implements WheelAdapter {
    private int minValue;
    private int maxValue;
    private String format;

    public BirthdayAdapter(int minValue, int maxValue, String format) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
    }


    @Override
    public int getItemsCount() {
        return maxValue - minValue + 1;
    }

    @Override
    public Object getItem(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index;
            return format != null ? String.format(format, value) : Integer
                    .toString(value);
        }
        return 0;
    }

    @Override
    public int indexOf(Object o) {
        return Integer.parseInt(o.toString());
    }
}
