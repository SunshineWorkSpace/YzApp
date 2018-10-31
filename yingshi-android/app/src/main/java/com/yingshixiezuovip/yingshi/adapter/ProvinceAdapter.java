package com.yingshixiezuovip.yingshi.adapter;

import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.quote.dropview.WheelAdapter;

import java.util.List;

/**
 * Created by Resmic on 2017/5/17.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class ProvinceAdapter implements WheelAdapter<String> {
    List<PlaceModel.ProviceItem> mProviceItems;

    public ProvinceAdapter(List<PlaceModel.ProviceItem> proviceItems) {
        this.mProviceItems = proviceItems;
    }

    @Override
    public int getItemsCount() {
        return mProviceItems.size();
    }

    @Override
    public String getItem(int index) {
        return mProviceItems.get(index).provinceName;
    }

    @Override
    public int indexOf(String o) {
        for (int i = 0; i < getItemsCount(); i++) {
            if (o.equals(getItem(i)))
                return i;
        }
        return -1;
    }
}
