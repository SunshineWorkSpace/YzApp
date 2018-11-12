package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yingshixiezuovip.yingshi.adapter.ConsumerPriceAdapter;
import com.yingshixiezuovip.yingshi.adapter.LookLogisticsAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.ConsumerPriceModel;
import com.yingshixiezuovip.yingshi.model.LogisticsDetailModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 类名称:LookLogisticsActivity
 * 类描述:查看物流信息
 * 创建时间: 2018-11-12-17:37
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class LookLogisticsActivity extends BaseActivity {
    private RecyclerView rv_list;
    private LookLogisticsAdapter marketDetailNewsAdapter;
    private List<LogisticsDetailModel.LogisticsDetailBeanModel> mList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wl);
        setActivityTitle("物流信息");
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_list.setLayoutManager(linearLayoutManager);
        marketDetailNewsAdapter = new LookLogisticsAdapter(this, mList);
        rv_list.setAdapter(marketDetailNewsAdapter);
        initDate();
    }

    private void initDate() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("id",getIntent().getStringExtra("id"));
        HttpUtils.doPost(TaskType.TASK_TYPE_MALL_WL_DETAIL, params, this);
    }
    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_MALL_WL_DETAIL:
                LogisticsDetailModel alOssVideoModel = GsonUtil.fromJson(result.toString(), LogisticsDetailModel.class);
                if (alOssVideoModel.data != null) {
                    mList = alOssVideoModel.data;
                    marketDetailNewsAdapter.refreshItem(mList);
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;

        }
    }
}
