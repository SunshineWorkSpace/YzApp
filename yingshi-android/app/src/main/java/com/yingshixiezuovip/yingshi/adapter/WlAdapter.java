package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;

import com.yingshixiezuovip.yingshi.model.WLOrderModel;
import com.yingshixiezuovip.yingshi.quote.dropview.WheelAdapter;

import java.util.List;

/**
 * 类名称:WlAdapter
 * 类描述:
 * 创建时间: 2018-11-13-00:53
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class WlAdapter implements WheelAdapter<String> {
    private Context mContext;
    private List<WLOrderModel.WLOrderBeanModel> mSexStatus;

    public WlAdapter(Context context, List<WLOrderModel.WLOrderBeanModel> list) {
        this.mContext = context;
        mSexStatus = list;
    }

    @Override
    public int getItemsCount() {
        return mSexStatus.size();
    }

    @Override
    public String getItem(int index) {
        return mSexStatus.get(index).invoice_name;
    }

    @Override
    public int indexOf(String o) {
        for (int i = 0; i < mSexStatus.size(); i++) {
            if (mSexStatus.get(i).invoice_name.equals(o))
                return i;
        }
        return -1;
    }
}
