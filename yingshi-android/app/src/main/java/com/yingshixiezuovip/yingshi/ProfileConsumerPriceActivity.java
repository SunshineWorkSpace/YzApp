package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yingshixiezuovip.yingshi.adapter.ConsumerPriceAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.AlOssImgModel;
import com.yingshixiezuovip.yingshi.custom.AlOssVideoModel;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.ConsumerPriceModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 类名称:ProfileConsumerPriceActivity
 * 类描述:我的-消保金页面
 * 创建时间: 2018-11-10-00:42
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class ProfileConsumerPriceActivity extends BaseActivity implements ConsumerPriceAdapter.OnItemClickListener, OnRefreshListener {
    private RecyclerView rv_list;
    private SmartRefreshLayout srl_main;
    private int pageSize = 10;
    private int page = 1;
    private ConsumerPriceAdapter marketDetailNewsAdapter;
    private List<ConsumerPriceModel.ConsumerPriceDetailModel> mList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_consumerprice);
        setActivityTitle("商铺会员升级消保金");

        ((TextView) findViewById(R.id.right_btn_name)).setText("提交");
        ((TextView) findViewById(R.id.right_btn_name)).setTextColor(ContextCompat.getColor(this,R.color.colorBlue));

        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        srl_main = (SmartRefreshLayout) findViewById(R.id.srl_main);
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_list.setLayoutManager(linearLayoutManager);
        marketDetailNewsAdapter = new ConsumerPriceAdapter(this, mList);
        rv_list.setAdapter(marketDetailNewsAdapter);
        marketDetailNewsAdapter.setOnItemClickListener(this);
        srl_main.autoRefresh();
        srl_main.setOnRefreshListener(this);
        srl_main.setEnableLoadMore(false);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.right_btn_submit:
                submitDate();
                break;
        }
    }

    private void submitDate() {
        boolean isGoNext=false;
        String vip="";
        String price="";
        for(int i=0;i<mList.size();i++){
            if(mList.get(i).isClick){
                isGoNext=true;
                vip=mList.get(i).vip;
                price=mList.get(i).price;
            }
        }
        if(isGoNext){
            Intent it=new Intent(this,ConsumerPriceSubmitActivity.class);
            it.putExtra("vip",vip);
            it.putExtra("price",price);
            startActivity(it);
        }else {
            showMessage("请选择消保金");
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        for(int i=0;i<mList.size();i++){
            ConsumerPriceModel.ConsumerPriceDetailModel model=new ConsumerPriceModel.ConsumerPriceDetailModel();
            if(i!=position){
                model.price=mList.get(i).price;
                model.vip=mList.get(i).vip;
                model.isClick=false;
            }else {
                model.price=mList.get(i).price;
                model.vip=mList.get(i).vip;
                model.isClick=true;
            }
            mList.set(i,model);
            marketDetailNewsAdapter.refreshItem(mList);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_VIPS, params, this);
    }
    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            srl_main.finishRefresh();
            showMessage(((Throwable) result).getMessage());
            return;
        }
        srl_main.finishRefresh();
        switch (type) {
            case TASK_TYPE_VIPS:
                ConsumerPriceModel alOssVideoModel = GsonUtil.fromJson(result.toString(), ConsumerPriceModel.class);
                if (alOssVideoModel != null) {
                    mList = alOssVideoModel.data;
                    marketDetailNewsAdapter.refreshItem(mList);
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;

        }
    }
}
