package com.yingshixiezuovip.yingshi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import com.baidu.platform.comapi.map.E;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.BaseFragment;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.fragment.DepartFragment;
import com.yingshixiezuovip.yingshi.fragment.HomeFragment;
import com.yingshixiezuovip.yingshi.fragment.NoticeFragment;
import com.yingshixiezuovip.yingshi.fragment.ProfileFragment;
import com.yingshixiezuovip.yingshi.fragment.RecruitFragment;
import com.yingshixiezuovip.yingshi.model.PushObject;
import com.yingshixiezuovip.yingshi.model.VersionModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.LoginUtils;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Resmic on 2017/5/2.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class MainActivity extends BaseActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BaseFragment mContent;
    private int mPageMax = 5;
    private BaseFragment[] baseFragments;
    private AlertWindow mVersionWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        needAuth = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, -1, false);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0x01);
        }
        /*123是权限设置回调*/
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.CALL_PHONE, android.Manifest.permission.READ_LOGS, android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.SET_DEBUG_APP, android.Manifest.permission.SYSTEM_ALERT_WINDOW, android.Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
        initView();
    }

    private void initView() {
        fragmentManager = getSupportFragmentManager();
        int resid;

        findViewById(R.id.main_btn_know).setOnClickListener(this);
        for (int i = 0; i < mPageMax; i++) {
            resid = getResources().getIdentifier("main_btn_tab_" + i, "id", getPackageName());
            findViewById(resid).setOnClickListener(this);
        }

        baseFragments = new BaseFragment[mPageMax];
        baseFragments[0] = new HomeFragment();
        baseFragments[1] = new DepartFragment();
        baseFragments[2] = new RecruitFragment();
        baseFragments[3] = new NoticeFragment();
        baseFragments[4] = new ProfileFragment();

        if (mUserInfo != null) {
            LoginUtils.doLogin(mUserInfo.token);
        }
        String flag = SPUtils.getString(false, this, "startup_flag_guide", "0");
        findViewById(R.id.main_guide_layout).setVisibility("0".equalsIgnoreCase(flag) ? View.VISIBLE : View.GONE);
        switchTab(0);
        Set<String> tagSet = new LinkedHashSet<String>();
        tagSet.add(mUserInfo.token);
        JPushInterface.setTags(this, 0, tagSet);//5aa47198fcc64f2da5c7ced205b022e0


        if (Intent.ACTION_VIEW.equals("android.intent.action.VIEW")) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                int mainType = getParams(uri, "type");
                int mainUserid = getParams(uri, "userid");
                int mainId = getParams(uri, "id");
                L.d("mainActiviy mainType = " + mainType + " , mainUserid = " + mainUserid + " , mainId = " + mainId);
                if (mainType > 0 && (mainUserid > 0 || mainId > 0)) {
                    Intent intent;
                    if (mainType == 2) {
                        intent = new Intent(this, UserInfo2Activity.class);
                        intent.putExtra("user_id", mainUserid);
                    } else if(mainType==10){
                        intent = new Intent(this, HomeShopDetailActvity.class);
                        intent.putExtra("id", ""+mainId);
                        intent.putExtra("type", "1");
                    } else {
                        intent = new Intent(this, MainDetailsActivity.class);
                        intent.putExtra("item_id", mainId);
                        intent.putExtra("item_name", "详情");
                        intent.putExtra("buttom_show", true);
                    }
                    startActivity(intent);
                }
            }
        }

        checkVersion();
    }

    private void checkVersion() {
        mLoadWindow.show();

        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("os", 2);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_VERSION, params, this);
    }

    private int getParams(Uri uri, String key) {
        try {
            return Integer.parseInt(uri.getQueryParameter(key));
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int resid;
        for (int i = 0; i < mPageMax; i++) {
            resid = getResources().getIdentifier("main_btn_tab_" + i, "id", getPackageName());
            if (resid == v.getId()) {
                switchTab(i);
                break;
            }
        }
        if (v.getId() == R.id.main_btn_know) {
            SPUtils.putString(false, this, "startup_flag_guide", "1");
            findViewById(R.id.main_guide_layout).setVisibility(View.GONE);
        } else if (v.getId() == R.id.alert_btn_submit) {
            mVersionWindow.cancel();
            YingApplication.getInstance().getVersionModel().toUpdate(this);
        }
    }

    @Override
    public void onEventMainThread(final BaseEvent event) {
        switch (event.getEventType()) {
            case EVENT_TYPE_RECIVER_NOTICE:
                L.d("YingZhePush", "收到跳转信息");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PushObject object = (PushObject) event.getData();
                        Intent intent = new Intent(MainActivity.this, NoticeMangerActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("push_object", object);
                        intent.putExtras(bundle);
                        superStartActivity(intent);
                        overridePendingTransition(R.anim.activity_nomove_anim, R.anim.activity_nomove_anim);
                    }
                });
                break;
        }
    }

    private void switchTab(int index) {
        int resid;
        CheckBox tempCheckBox;
        for (int i = 0; i < mPageMax; i++) {
            resid = getResources().getIdentifier("main_cb_" + i, "id", getPackageName());
            tempCheckBox = (CheckBox) findViewById(resid);
            tempCheckBox.setChecked(i == index);
        }
        switchFragment(baseFragments[index]);
    }

    private void switchFragment(BaseFragment fragment) {
        if (mContent != fragment) {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (mContent != null) {
                fragmentTransaction.hide(mContent);
            }
            if (fragment.isAdded()) {
                fragmentTransaction.show(fragment).commit();
            } else {
                fragmentTransaction.add(R.id.mainLayout, fragment).commit();
            }
            mContent = fragment;
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            return;
        } else {
            String versionStr = ((JSONObject) result).optString("data");

            if (!TextUtils.isEmpty(versionStr) && !"null".equalsIgnoreCase(versionStr)) {
                VersionModel versionModel = GsonUtil.fromJson(versionStr, VersionModel.class);
                if (versionModel.needUpdate()) {
                    YingApplication.getInstance().setVersionModel(versionModel);
                    mVersionWindow = new AlertWindow(this, true);
                    mVersionWindow.findViewById(R.id.alert_btn_submit).setOnClickListener(this);
                    mVersionWindow.show("版本更新", "检测到新的版本，是否立即更新？", "下次", "更新");
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (BaseFragment baseFragment : baseFragments) {
            baseFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
