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
import com.yingshixiezuovip.yingshi.adapter.VipChoosePayListAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.ConsumerPriceModel;
import com.yingshixiezuovip.yingshi.model.VipChoosePayModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 类名称:VipChoosePayTypeActivity
 * 类描述:请选择支付方式
 * 创建时间: 2018-11-13-11:28
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class VipChoosePayTypeActivity extends BaseActivity implements OnRefreshListener, VipChoosePayListAdapter.OnItemClickListener {
    private RecyclerView rv_list;
    private SmartRefreshLayout srl_main;
    private VipChoosePayListAdapter marketDetailNewsAdapter;
    private List<VipChoosePayModel.VipChoosePayBeanModel> mList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_choose);
        setActivityTitle("请选择支付方式");

        ((TextView) findViewById(R.id.right_btn_name)).setText("提交");
        ((TextView) findViewById(R.id.right_btn_name)).setTextColor(ContextCompat.getColor(this,R.color.colorBlue));

        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        srl_main = (SmartRefreshLayout) findViewById(R.id.srl_main);
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_list.setLayoutManager(linearLayoutManager);
        marketDetailNewsAdapter = new VipChoosePayListAdapter(this, mList);
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
        String id="";
        for(int i=0;i<mList.size();i++){
            if(mList.get(i).isClick){
                isGoNext=true;
//                vip=mList.get(i).vip;
                id=mList.get(i).id;
            }
        }
        if(isGoNext){
            Intent it=new Intent(this,VipChoosePayActivity.class);
            it.putExtra("id",id);
            startActivity(it);
        }else {
            showMessage("请选择会员价");
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        for(int i=0;i<mList.size();i++){
            VipChoosePayModel.VipChoosePayBeanModel model=new VipChoosePayModel.VipChoosePayBeanModel();
            if(i!=position){
                model.price=mList.get(i).price;
                model.year=mList.get(i).year;
                model.oprice=mList.get(i).oprice;
                model.id=mList.get(i).id;
                model.rate=mList.get(i).rate;
//                model.vip=mList.get(i).vip;
                model.isClick=false;
            }else {
                model.price=mList.get(i).price;
                model.year=mList.get(i).year;
                model.oprice=mList.get(i).oprice;
                model.id=mList.get(i).id;
                model.rate=mList.get(i).rate;
//                model.vip=mList.get(i).vip;
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
        HttpUtils.doPost(TaskType.TASK_TYPE_VIP_LIST, params, this);
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
            case TASK_TYPE_VIP_LIST:
                VipChoosePayModel alOssVideoModel = GsonUtil.fromJson(result.toString(), VipChoosePayModel.class);
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
