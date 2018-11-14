package com.yingshixiezuovip.yingshi;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.custom.UrlWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.datautils.ThrowableUtils;
import com.yingshixiezuovip.yingshi.model.CompanyStatusModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Resmic on 2017/9/12.
 */

public class MainCompanyInfoSetActivity extends BaseActivity {
    private CompanyStatusModel mCompanyStatusModel;
    private AlertWindow mResumeWindow;
    private UrlWindow mUrlWindow;
    private LinearLayout lin_vip;
    private String[] mSchoolCardStatus = {
            "未上传", "审核中", "已认证", "未通过",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info_set, R.string.activity_company_info_set_title);

        initView();
        initWindow();
    }

    private void initView() {
        findViewById(R.id.company_btn_info).setOnClickListener(this);
        findViewById(R.id.company_btn_cover).setOnClickListener(this);
        findViewById(R.id.company_btn_resume).setOnClickListener(this);
        findViewById(R.id.company_btn_schoolcard).setOnClickListener(this);
        findViewById(R.id.company_btn_shareurl).setOnClickListener(this);
        lin_vip=(LinearLayout)findViewById(R.id.lin_vip);
        lin_vip.setOnClickListener(this);
        if(mUserInfo.usertype==1||mUserInfo.usertype==2||mUserInfo.usertype==3||mUserInfo.usertype==4){
            lin_vip.setVisibility(View.VISIBLE);
        }
    }

    private void initWindow() {
        mResumeWindow = new AlertWindow(this, true);
        mResumeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    startActivity(new Intent(MainCompanyInfoSetActivity.this, MainCompanyResumeEditActivity.class));
                }
            }
        });
        mUrlWindow = new UrlWindow(this);
        mUrlWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUrlWindow.cancel();
                if (v.getId() == R.id.dialog_item2) {
                    startCopy(mCompanyStatusModel.qianzhui + mCompanyStatusModel.samplereels_url);
                }
            }
        });
    }

    private void loadData() {
        mLoadWindow.show(R.string.text_loading);
        HashMap<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_COMPANY_STATUS, tokenParams, this);
    }

    private void inflateData() {
        ((TextView) findViewById(R.id.company_tv_schoolcardStatus)).setText(mSchoolCardStatus[mCompanyStatusModel.checkbycer]);
        ((TextView) findViewById(R.id.company_tv_resumeStatus)).setText(mCompanyStatusModel.isresume == 1 ? "已填写" : "未填写");
        ((TextView) findViewById(R.id.company_tv_shareurlStatus)).setText(mCompanyStatusModel.iswriteurl == 1 ? "已修改" : "未修改");
    }

    @Override
    protected void onSingleClick(View v) {
        if (mCompanyStatusModel == null) {
            showMessage("企业信息加载失败，请稍后重试");
            return;
        }
        switch (v.getId()) {
            case R.id.company_btn_info:
                if (mUserInfo.type == 1 || mUserInfo.type == 2) {
                    Intent intent = new Intent(this, UserInfoSettingsActivity.class);
                    intent.putExtra("user_type", UserInfoSettingsActivity.TYPE_INFO);
                    startActivity(intent);
                } else if (mUserInfo.type == 5 || mUserInfo.type == 6) {
                    Intent intent = new Intent(this, MainSchoolInfoSetActivity.class);
                    startActivity(intent);
                } else if(mUserInfo.type==7){
                    Intent intent = new Intent(this, MainShopInfoSetActivity.class);
                    startActivity(intent);
                } else{
                    startActivity(new Intent(this, MainCompanyBaseInfoActivity.class));
                }
                break;
            case R.id.company_btn_cover:
                Intent intent = new Intent(this, MainCompanyCoverActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("company_status", mCompanyStatusModel);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.company_btn_resume:
                if (mCompanyStatusModel.isresume == 1) {
                    mResumeWindow.show("修改简历", "您已经填写过简历，确定重新填写？", "取消", "确定");
                } else {
                    startActivity(new Intent(this, MainCompanyResumeActivity.class));
                }
                break;
            case R.id.company_btn_schoolcard:
                if (mCompanyStatusModel.checkbycer == 0 || mCompanyStatusModel.checkbycer == 3) {
                    startActivity(new Intent(this, MainCompanyGraduationActivity.class));
                } else if (mCompanyStatusModel.checkbycer == 1) {
                    showMessage("正在审核中，请耐心等待...");
                } else {
                    showMessage("审核已通过");
                }
                break;
            case R.id.company_btn_shareurl:
                if (mCompanyStatusModel.iswriteurl == 0) {
                    intent = new Intent(this, MainCompanyCustomUrlActivity.class);
                    bundle = new Bundle();
                    bundle.putSerializable("company_status", mCompanyStatusModel);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    mUrlWindow.show("作品集地址为：" + mCompanyStatusModel.qianzhui + mCompanyStatusModel.samplereels_url + "\n你的作品分享集地址复制链接吗？");
                }
                break;
            case R.id.lin_vip:
                intent=new Intent(this,VipChoosePayTypeActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void startCopy(String url) {
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("shareUrl", url);
        cmb.setPrimaryClip(clipData);
        showMessage("链接复制成功");
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_QRY_COMPANY_STATUS:
                if (((JSONObject) result).has("data")) {
                    try {
                        mCompanyStatusModel = GsonUtil.fromJson(((JSONObject) result).optString("data"), CompanyStatusModel.class);
                        inflateData();
                    } catch (Exception e) {
                        L.d("Exception = " + ThrowableUtils.getThrowableDetailsMessage(e));
                        mCompanyStatusModel = null;
                    }
                } else {
                    mCompanyStatusModel = null;
                }
                if (mCompanyStatusModel == null) {
                    showMessage("企业信息加载失败，请稍后重试");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
