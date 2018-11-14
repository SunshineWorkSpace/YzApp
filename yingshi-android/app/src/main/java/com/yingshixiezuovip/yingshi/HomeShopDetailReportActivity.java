package com.yingshixiezuovip.yingshi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.adapter.ShopDetailReportAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseResp;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.ShopDetailTypeModel;
import com.yingshixiezuovip.yingshi.model.ShopMoreModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuhua.gou on 2018/11/14.
 */

public class HomeShopDetailReportActivity extends BaseActivity implements ShopDetailReportAdapter.ItemClickListener {
    private List<ShopMoreModel> modelList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ShopDetailReportAdapter mAdapter;
    private int mChoicePostion;
    private String id="";
    private EditText edt_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_shop_detial_report,
                R.string.title_activity_shop_detail_report);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        edt_report= (EditText) findViewById(R.id.edt_report);
        setActivityTitle("举报内容","#1B834F");
        ((TextView) findViewById(R.id.right_btn_name)).setText("举报");
        ((TextView) findViewById(R.id.right_btn_name)).setTextColor(Color.parseColor("#1B834F"));
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        id=getIntent().getStringExtra("id");
        initData();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ShopDetailReportAdapter(this, modelList);
        mAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }



    private void initData(){
        modelList.add(new ShopMoreModel("色情低俗",true));
        modelList.add(new ShopMoreModel("广告骚扰",false));
        modelList.add(new ShopMoreModel("诱导分享",false));
        modelList.add(new ShopMoreModel("商品虚假",false));
        modelList.add(new ShopMoreModel("违法 (暴力恐怖、违禁品等)",false));
        modelList.add(new ShopMoreModel("侵权",false));
        modelList.add(new ShopMoreModel("其他",false));
    }


    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.right_btn_submit:
                reportData();
                break;
        }
    }

    /**
     * 举报
     */
    public void reportData(){
        String content=modelList.get(mChoicePostion).title;
        if(!TextUtils.isEmpty(edt_report.getText().toString().trim())){
           content+=" "+edt_report.getText().toString().trim();
        }
        mUserInfo = SPUtils.getUserInfo(this);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("id", id);
        params.put("content",content);
        mLoadWindow.show(R.string.waiting);
        HttpUtils.doPost(TaskType.TASK_TYPE_SHOP_REPORT, params, this);
    }

    @Override
    public void onItemClick(View view, int position) {
        mChoicePostion=position;
       for(int i=0;i<modelList.size();i++){
           ShopMoreModel shopMoreModel=modelList.get(i);
           if(i==mChoicePostion){
               shopMoreModel.isCheck=true;
           }else{
               shopMoreModel.isCheck=false;
           }
       }
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        super.taskFinished(type, result, isHistory);
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_SHOP_REPORT:
                mLoadWindow.cancel();
                BaseResp baseResp = GsonUtil.fromJson(result.toString(), BaseResp.class);
                if(null!=baseResp){
                    if(baseResp.result.code==200) {
                        showMessage("举报成功");
                        finish();
                    }else {
                        showMessage("举报失败");
                    }
                }else{
                    showMessage(R.string.data_load_failed);
                }

                break;
        }
    }
}
