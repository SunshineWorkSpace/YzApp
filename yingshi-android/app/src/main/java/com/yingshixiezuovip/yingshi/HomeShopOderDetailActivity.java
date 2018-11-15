package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.SelectWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.model.HomeTypeModel;
import com.yingshixiezuovip.yingshi.model.PublishIsOkModel;
import com.yingshixiezuovip.yingshi.model.ShopDetailTypeModel;
import com.yingshixiezuovip.yingshi.model.ShopOrderNoModel;
import com.yingshixiezuovip.yingshi.quote.media.utils.Utils;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.MoneyUtil;

import java.util.HashMap;

/**
 * 订单详情
 * Created by yuhua.gou on 2018/11/10.
 */

public class HomeShopOderDetailActivity extends BaseActivity {
    private TextView tv_shop_owner,tv_total_num,tv_price,
            tv_name_newsdegree,tv_buy_num,tv_freight_info,
            tv_freight,tv_total_fee,tv_total_info;
    private TextView tv_add,tv_add_address;
    private LinearLayout ll_add_address;
    private ShopDetailTypeModel.ShopDetailType mShopDetail;
    private final static int REQUEST_ADD_ADDRESS=110;
    private String cityName,addressName,address_id,telphone,revcname;
    private SelectWindow mNumberWindow;
    private int mChoiceNumber=1;
    private LinearLayout ll_choice_number;
    private String totoalMoney="0.00";
    private TextView tv_submmit;
    private String pro_id;
    private ShopOrderNoModel.ShopOrderNo shopOrderNo;
    private final static int REQUEST_TYPE_ODER_PAY=11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_shop_oderdetail,
                R.string.title_activity_home_shop_orderdetail);
        initView();
    }

    private void initView(){
        tv_shop_owner=(TextView) findViewById(R.id.tv_shop_owner);
        tv_total_num=(TextView) findViewById(R.id.tv_total_num);
        tv_price=(TextView) findViewById(R.id.tv_price);
        tv_name_newsdegree= (TextView) findViewById(R.id.tv_name_newsdegree);
        tv_buy_num= (TextView) findViewById(R.id.tv_buy_num);
        tv_freight_info= (TextView) findViewById(R.id.tv_freight_info);
        tv_freight=(TextView) findViewById(R.id.tv_freight);
        tv_total_fee= (TextView) findViewById(R.id.tv_total_fee);
        tv_total_info= (TextView) findViewById(R.id.tv_total_info);
        tv_add= (TextView) findViewById(R.id.tv_add);
        tv_add.setOnClickListener(this);
        tv_add_address= (TextView) findViewById(R.id.tv_add_address);
        ll_add_address=(LinearLayout) findViewById(R.id.ll_add_address);
        ll_add_address.setOnClickListener(this);
        ll_choice_number= (LinearLayout) findViewById(R.id.ll_choice_number);
        ll_choice_number.setOnClickListener(this);
        tv_submmit= (TextView) findViewById(R.id.tv_submmit);
        tv_submmit.setOnClickListener(this);
        initDataView();
    }

    private void initDataView(){
        mShopDetail=(ShopDetailTypeModel.ShopDetailType)
                getIntent().getSerializableExtra("orderDetail");
        if(null!=mShopDetail){
            pro_id=mShopDetail.id;
            tv_shop_owner.setText(mShopDetail.nickname);
            tv_total_num.setText("总共"+mShopDetail.num+"件商品");
            tv_name_newsdegree.setText(mShopDetail.title+"("+mShopDetail.isnew+")");
            tv_buy_num.setText("X"+mChoiceNumber);
            tv_price.setText("¥"+mShopDetail.price);
        }
        mNumberWindow=new SelectWindow(this,6,
                CommUtils.parseInt(mShopDetail.num,
                0));
        mNumberWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
            @Override
            public void onItemSelected(String selectContent) {
                mChoiceNumber=CommUtils.parseInt(selectContent,
                        0);
                tv_buy_num.setText("X"+selectContent);
                setTotalMoney();
            }
        });
        setTotalMoney();
    }

    private void setTotalMoney(){
        totoalMoney= MoneyUtil.moneyMul2(mShopDetail.price,mChoiceNumber+"");
        tv_total_fee.setText("¥"+totoalMoney);
        tv_total_info.setText("合计: ¥"+totoalMoney);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.ll_add_address:
                Intent addAddress=new Intent(HomeShopOderDetailActivity.this,
                        UserAddressListActivity.class);
                addAddress.putExtra("type",1);
                startActivityForResult(addAddress,REQUEST_ADD_ADDRESS);
                break;
            case R.id.ll_choice_number:
                if(mChoiceNumber==0) {
                    mChoiceNumber=1;
                }
                    mNumberWindow.showNumber(mChoiceNumber, "");
                break;
            case R.id.tv_submmit:
                if(tv_submmit.getText().toString().trim().equals("提交订单")) {
                    submmiteOrder();
                }
                break;
        }
    }

    private void submmiteOrder(){
        if(TextUtils.isEmpty(address_id)){
            showMessage("请先添加地址");
            return;
        }

        if(mChoiceNumber==0){
            showMessage("请先选择数量");
            return;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("address_id", address_id);
        params.put("pro_id", pro_id);
        params.put("pay_manner", "1");
        params.put("num", mChoiceNumber+"");
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_SHOP_SUBMMITE_ORDER, params, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            switch (requestCode){
                case REQUEST_ADD_ADDRESS:
                    cityName=data.getStringExtra("city");
                    addressName=data.getStringExtra("address");
                    address_id=data.getStringExtra("id");
                    telphone=data.getStringExtra("telphone");
                    revcname=data.getStringExtra("revcname");
                    tv_add_address.setText(cityName+" "+addressName);
                    break;
                case REQUEST_TYPE_ODER_PAY://付款回调
                    tv_submmit.setText("已付款");
                    ll_add_address.setEnabled(false);
                    ll_choice_number.setEnabled(false);
                    break;
            }
        }
    }


    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_SHOP_SUBMMITE_ORDER:
                System.out.println("订单信息:"+result.toString());
                ShopOrderNoModel  shopOrderNoModel = GsonUtil.fromJson(result.toString(), ShopOrderNoModel.class);
                if (shopOrderNoModel != null) {
                    if (shopOrderNoModel.data != null) {
                       shopOrderNo=shopOrderNoModel.data;
                       Intent orderPay=new Intent(HomeShopOderDetailActivity.this,
                               HomeShopOrderPayActivity.class);
                        orderPay.putExtra("flow_trade_no",shopOrderNo.flow_trade_no);
                        orderPay.putExtra("money",totoalMoney);
                        orderPay.putExtra("name",tv_name_newsdegree.getText().toString());
                        orderPay.putExtra("num",mChoiceNumber+"");
                        startActivityForResult(orderPay,REQUEST_TYPE_ODER_PAY);
                    }
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;
        }
    }

}
