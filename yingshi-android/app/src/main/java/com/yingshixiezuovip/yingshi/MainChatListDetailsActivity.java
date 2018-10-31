package com.yingshixiezuovip.yingshi;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.utils.L;

public class MainChatListDetailsActivity extends BaseActivity {
    private EaseChatFragment mEaseChatFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private String mTargetToken;
    private String mTargetUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat_list);

        mTargetToken = getIntent().getStringExtra("target_token");
        mTargetUsername = getIntent().getStringExtra("target_name");
        L.d("MainChatListDetailsActivity::onCreate::mTargetToken = " + mTargetToken);
        setActivityTitle(mTargetUsername);
        initView();
    }

    private void initView() {
        mEaseChatFragment = new EaseChatFragment();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        args.putString(EaseConstant.EXTRA_USER_ID, mTargetToken);
        mEaseChatFragment.setArguments(args);
        mFragmentTransaction.add(R.id.chatlist_mainlayout, mEaseChatFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cleanMessageListener();
    }
}
