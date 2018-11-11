package com.yingshixiezuovip.yingshi.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.AddressListModel;

/**
 * 用户地址
 * Created by yuhua.gou on 2018/11/11.
 */

public class AddressAdapter  extends
        BaseQuickAdapter<AddressListModel.AddressModel, BaseViewHolder> {
    public AddressAdapter() {
        super(R.layout.item_user_address);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddressListModel.AddressModel item) {
        TextView tv_user_name=helper.getView(R.id.tv_user_name);
        TextView tv_phone=helper.getView(R.id.tv_phone);
        TextView tv_address=helper.getView(R.id.tv_address);
        tv_user_name.setText(item.revcname);
        tv_phone.setText(item.telphone);
        tv_address.setText(item.city+""+item.address);

    }
}
