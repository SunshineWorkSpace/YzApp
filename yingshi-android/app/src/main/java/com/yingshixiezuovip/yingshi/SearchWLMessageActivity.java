package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.socialize.media.Base;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.SelectWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.BuyerOrderModel;
import com.yingshixiezuovip.yingshi.model.WLOrderModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.util.HashMap;

/**
 * 类名称:SearchWLMessageActivity
 * 类描述:查询物流信息
 * 创建时间: 2018-11-13-00:27
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class SearchWLMessageActivity extends BaseActivity {
    private EditText et_order;
    private TextView tv_order;
    private SelectWindow mNewOldWindow;
    private WLOrderModel wlOrderModel;
    private int clickPostion=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_wl);
        setActivityTitle("填写物流资料");
        ((TextView) findViewById(R.id.right_btn_name)).setText("下一步");
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        tv_order=(TextView)findViewById(R.id.tv_order);
        tv_order.setOnClickListener(this);
        et_order=(EditText)findViewById(R.id.et_order);

        initData();
    }

    private void initData() {
        HashMap<String, Object> params = new HashMap<>();
        HttpUtils.doPost(TaskType.TASK_TYPE_WL_LIST, params, this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.right_btn_submit:
                checkDate();
                break;
            case R.id.tv_order:
                if(null!=wlOrderModel){
                    mNewOldWindow.showWL(1);
                }
                break;
        }
    }
    private void checkDate() {
        if (tv_order.getText().toString().equals("请选择你的物流名称")) {
            showMessage("请选择你的物流名称");
            return;
        }
        if (TextUtils.isEmpty(et_order.getText().toString())) {
            showMessage("请输入物流单号");
            return;
        }
        for (int i=0;i<wlOrderModel.data.size();i++){
            if(wlOrderModel.data.get(i).invoice_name.equals(tv_order.getText().toString())){
                clickPostion=i;
            }
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("id", getIntent().getStringExtra("id"));
        params.put("invoice_id",wlOrderModel.data.get(clickPostion).invoice_id);
        params.put("invoice_num",wlOrderModel.data.get(clickPostion).invoice_name);
        HttpUtils.doPost(TaskType.TASK_TYPE_SEND_ORDER, params, this);
    }
    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type){
            case TASK_TYPE_WL_LIST:
                wlOrderModel=GsonUtil.fromJson(result.toString(),WLOrderModel.class);
                mNewOldWindow=new SelectWindow(this,10,wlOrderModel.data);
                mNewOldWindow.setOnItemSelectedListener(new SelectWindow.OnWVItemSelectedListener() {
                    @Override
                    public void onItemSelected(String selectContent) {
                        tv_order.setText(selectContent);
                    }
                });
                break;
            case TASK_TYPE_SEND_ORDER:
                finish();
                break;
        }
    }

}
