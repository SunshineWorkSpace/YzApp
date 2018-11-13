package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.yingshixiezuovip.yingshi.adapter.ShopDetailMoreAdapter;
import com.yingshixiezuovip.yingshi.animator.NoAlphaItemAnimator;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.model.ShopMoreModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 更多信息
 * Created by yuhua.gou on 2018/11/13.
 */

public class HomeShopDetailMoreActivity extends BaseActivity implements ShopDetailMoreAdapter.ItemClickListener {
    private List<ShopMoreModel> modelList=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ShopDetailMoreAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_shopdetail_more,
                R.string.title_activity_shop_detail_more);
        initView();
    }

    private void initView(){
        mRecyclerView= (RecyclerView) findViewById(R.id.rv);
        initData();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new ShopDetailMoreAdapter(this,modelList);
        mAdapter.setItemClickListener(this);
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData(){
        modelList.add(new ShopMoreModel("*如何保证钱款安全",
                "支付的货款由平台承担,当确定收到货后,货款才会打给商家。"));
        modelList.add(new ShopMoreModel("*收到货不满意，可以退货吗？",
                "1,商铺付款后，若卖家72小时内未发货，可申请退款。"));
        modelList.add(new ShopMoreModel("*在哪里可以查看收益?",
                "进入我的账户,点击我的余额后，即可看到查看收益明细。"));
        modelList.add(new ShopMoreModel("*商品交易成功账户余额却没有增加？",
                "金额会在1-3个工作日之内把钱退回您的账户，请耐心等待，也可以拨打我们客服电话咨询。"));
        modelList.add(new ShopMoreModel("*提现什么时候到账？",
                "您好，我们的业务流程是这样的，工作日（周一到周五)财务每天10:30和15:30集中处理两次," +
                        "周六15:30处理一次，在处理时间前的申请都会当日处理完毕。周末及节假日的提现申请，顺延至下一" +
                        "工作日处理；所用银行均为T+1到账，具体到账以各自银行系统运作为准。"));
    }

    @Override
    public void onItemClick(View view, int position) {
        ShopMoreModel shopMoreModel=modelList.get(position);
        if (shopMoreModel.showDetails == View.GONE) {
            shopMoreModel.showDetails =View.VISIBLE;
        } else {
            shopMoreModel.showDetails =View.GONE;
        }
        mAdapter.notifyItemChanged(position,shopMoreModel);
    }
}
