package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.MainAuthenticationMoneyActivity;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;
import com.yingshixiezuovip.yingshi.model.DetailsPayModel;

/**
 * Created by Resmic on 2017/8/10.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class PhonePayWindow extends BasePopupWindow {
    private String phone;
    private String price;
    private int userID;

    public PhonePayWindow(Context mContext) {
        super(mContext, false, false);
        setWidthHeight(true, -1, -1);

        getView().setOnClickListener(this);
        findViewById(R.id.phonepay_btn_buy).setOnClickListener(this);
        findViewById(R.id.phonepay_btn_service).setOnClickListener(this);
        findViewById(R.id.phonepay_btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.phonepay_btn_buy:
                intent = new Intent(mContext, MainAuthenticationMoneyActivity.class);
                intent.putExtra("pay_model", new DetailsPayModel(price, userID));
                break;
            case R.id.phonepay_btn_service:
                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                break;
        }
        cancel();
        if (intent != null) {
            mContext.startActivity(intent);
        }
    }

    public void show(int type, String price, String phone, int userID) {
        if (type == 5 || type == 6) {
            ((TextView) findViewById(R.id.phonepay_btn_vip)).setText("学生用户无法直接看到用户电话");
        } else {
            ((TextView) findViewById(R.id.phonepay_btn_vip)).setText("去认证成为会员");
        }

        this.phone = phone;
        this.userID = userID;
        this.price = price;
        ((TextView) findViewById(R.id.phonepay_btn_buy)).setText("花费" + price + "元获取该用户电话");
        ((TextView) findViewById(R.id.phonepay_btn_service)).setText("拨打客服电话：" + phone);
        super.show();
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.view_phone_pay_layout, null);
    }
}
