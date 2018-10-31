package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.model.BaseEaseUser;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

public class MainChatListActivity extends BaseActivity {
    private EaseConversationListFragment mListFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat_list, R.string.title_activity_main_chat_list);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cleanMessageListener();
    }


    private void initView() {
        mListFragment = new EaseConversationListFragment();
        mListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(MainChatListActivity.this, MainChatListDetailsActivity.class);
                intent.putExtra("target_token", conversation.conversationId());
                BaseEaseUser easeUser = SPUtils.getBaseEaseUser(MainChatListActivity.this, conversation.conversationId());
                if (easeUser != null) {
                    intent.putExtra("target_name", easeUser.nickname);
                } else {
                    intent.putExtra("target_name", "");
                }
                startActivity(intent);
            }
        });
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.chatlist_mainlayout, mListFragment).commit();
    }
}
