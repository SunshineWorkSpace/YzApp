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

public class CityAdapter implements WheelAdapter<String> {
    List<PlaceModel.CityItem> mCityItems;

    public CityAdapter(List<PlaceModel.CityItem> cityItems) {
        this.mCityItems = cityItems;
    }

    @Override
    public int getItemsCount() {
        return mCityItems.size();
    }

    @Override
    public String getItem(int index) {
        return mCityItems.get(index).cityName;
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
