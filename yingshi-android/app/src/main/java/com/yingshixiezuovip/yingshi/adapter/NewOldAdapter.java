package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.quote.dropview.WheelAdapter;

/**
 * 类名称:NewOldAdapter
 * 类描述:
 * 创建时间: 2018-11-05-14:38
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class NewOldAdapter implements WheelAdapter<String> {
    private Context mContext;
    private String[] mSexStatus;

    public NewOldAdapter(Context context) {
        this.mContext = context;
        mSexStatus = mContext.getResources().getStringArray(R.array.text_new_old);
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
