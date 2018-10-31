package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.ShareWindow;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.model.ShareModel;

public class MainInsuranceDetailsActivity extends BaseActivity {
    private int mType;
    private int[] mImageIds = {
            R.mipmap.baoxian1,
            R.mipmap.baoxian2,
            R.mipmap.baoxian3,
    };
    private String[] mTitleStr = {
            "影者短期意外险20万元版",
            "影者短期意外险30万元版",
            "影者短期意外险50万元版",
    };

    private double[] mPrice = {10.0, 15.0, 50.0};
    private int[] mMaxPrice = {20, 30, 50};
    private int[] mMinPrice = {2, 3, 5};

    private String[] mMainURL = {
            "https://epcis-ptp.pingan.com.cn/epcis.ptp.internet.salesOfdimensionalCode.do?um_code=JCJJ-00004&product_code=02000458&policy_Holder_Type=2WMP01CD",
            "https://epcis-ptp.pingan.com.cn/epcis.ptp.internet.salesOfdimensionalCode.do?um_code=JCJJ-00004&product_code=02000459&policy_Holder_Type=2WMP01CD",
            "https://epcis-ptp.pingan.com.cn/epcis.ptp.internet.salesOfdimensionalCode.do?um_code=JCJJ-00004&product_code=02000460&policy_Holder_Type=2WMP01CD",
    };
    private ShareWindow mShareWindow;
    private ShareModel.ShareItem mShareItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_insurance_details);


        mType = getIntent().getIntExtra("insurance_type", 1);

        initView();
    }

    private void initView() {
        findViewById(R.id.right_btn_name).setVisibility(View.GONE);
        findViewById(R.id.right_iv_more).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        setActivityTitle(mTitleStr[mType - 1]);
        ((ImageView) findViewById(R.id.insurance_iv_background)).setImageResource(mImageIds[mType - 1]);
        ((TextView) findViewById(R.id.insurance_tv_price)).setText("保费：" + mPrice[mType - 1] + "元/份");
        ((TextView) findViewById(R.id.insurance_tv_maxprice)).setText("赔偿限额" + mMaxPrice[mType - 1] + "万元");
        ((TextView) findViewById(R.id.insurance_tv_minprice)).setText("赔偿限额" + mMinPrice[mType - 1] + "万元");
        findViewById(R.id.insurance_btn_gobuy).setOnClickListener(this);
        findViewById(R.id.insurance_btn_gobuy_sub).setOnClickListener(this);
        mShareItem = new ShareModel.ShareItem();
        mShareItem.url = Configs.ServerUrl + "/share/insuranceShare?page=" + mType;
        mShareItem.content = "《影视保》是【影者APP联合平安保险专门为各类演艺影视工作者研发的保险.适合所有非危险戏份参演相关的工作人员.参加人员 0-50周岁均可投保,极大扩展了演职人员的保障范围。";
        mShareItem.title = mTitleStr[mType - 1];
        mShareWindow = new ShareWindow(this);

    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.insurance_btn_gobuy:
            case R.id.insurance_btn_gobuy_sub:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mMainURL[mType - 1]));
                startActivity(intent);
                break;
            case R.id.right_btn_submit:
                mShareWindow.show(mShareItem, this);
                break;
        }
    }
}
