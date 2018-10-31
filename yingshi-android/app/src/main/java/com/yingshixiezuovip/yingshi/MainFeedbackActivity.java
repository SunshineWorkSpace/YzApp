package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;

import java.util.HashMap;

/**
 * Created by Resmic on 2017/8/11.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class MainFeedbackActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback, R.string.activity_feedback_title);

        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.right_btn_name)).setText("提交");
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn_submit:
                doSendFeedBack();
                break;
        }
    }

    private void doSendFeedBack() {
        String feedStr = ((EditText) findViewById(R.id.feedback_et_input)).getText().toString();
        if (TextUtils.isEmpty(feedStr)) {
            showMessage("请先输入需要提交的问题");
            return;
        }
        HashMap<String, Object> feedbackParams = new HashMap<>();
        feedbackParams.put("token", mUserInfo.token);
        feedbackParams.put("msg", feedStr);
        mLoadWindow.show(R.string.waiting);
        HttpUtils.doPost(TaskType.TASK_TYPE_SUBMIT_FEEDBACK, feedbackParams, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        showMessage("谢谢您的反馈！客服会在1-3个工作日内联系您");
        onBackPressed();
    }
}
