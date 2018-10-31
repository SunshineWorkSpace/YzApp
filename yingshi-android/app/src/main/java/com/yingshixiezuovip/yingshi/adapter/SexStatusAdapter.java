package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.quote.dropview.WheelAdapter;


/**
 * Created by Resmic on 2016/8/26.
 */

public class SexStatusAdapter implements WheelAdapter<String> {
    private Context mContext;
    private String[] mSexStatus;

    public SexStatusAdapter(Context context) {
        this.mContext = context;
        mSexStatus = mContext.getResources().getStringArray(R.array.text_feel_status);
    }

    @Override
    public int getItemsCount() {
        return mSexStatus.length;
    }

    @Override
    public String getItem(int index) {
        return mSexStatus[index];
    }

    @Override
    public int indexOf(String o) {
        for (int i = 0; i < mSexStatus.length; i++) {
            if (mSexStatus[i].equalsIgnoreCase(o))
                return i;
        }
        return -1;
    }
}
