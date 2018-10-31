package com.yingshixiezuovip.yingshi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.yingshixiezuovip.yingshi.adapter.SearchKeywordAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.SimpleTextWatcher;
import com.yingshixiezuovip.yingshi.custom.CateWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.HotModel;
import com.yingshixiezuovip.yingshi.quote.flowlayout.TagFlowLayout;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * * Created by Resmic on 2017/5/4.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class MainSearchHistoryActivity extends BaseActivity {
    private EditText mSearchEdit;
    private boolean isSearch = false;
    private TagFlowLayout mHistoryLayout;
    private TagFlowLayout mHotLayout;
    private SearchKeywordAdapter mHotAdapter;
    private SearchKeywordAdapter mHistoryAdpter;
    private CateWindow mCateWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search_history, -1, false);

        initView();
    }


    private void initView() {
        mSearchEdit = (EditText) findViewById(R.id.search_et_input);
        mSearchEdit.addTextChangedListener(textWatcher);
        findViewById(R.id.search_btn_start).setOnClickListener(this);
        findViewById(R.id.search_btn_clear).setOnClickListener(this);

        mHistoryAdpter = new SearchKeywordAdapter(this, 1);
        mHistoryAdpter.setOnDeleteClickListener(onKeywordClickListener);
        mHistoryLayout = (TagFlowLayout) findViewById(R.id.history_flowlayout);
        mHistoryLayout.setAdapter(mHistoryAdpter);

        mHotAdapter = new SearchKeywordAdapter(this, 2);
        mHotAdapter.setOnDeleteClickListener(onKeywordClickListener);
        mHotLayout = (TagFlowLayout) findViewById(R.id.hot_flowlayout);
        mHotLayout.setAdapter(mHotAdapter);

        loadHotSearch();
    }

    private void loadHotSearch() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_HOT_SEARCH, params, this);
    }

    private TextWatcher textWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ((TextView) findViewById(R.id.search_btn_start)).setText(s.length() == 0 ? "取消" : "搜索");
            isSearch = s.length() != 0;
        }
    };

    private SearchKeywordAdapter.OnKeywordClickListener onKeywordClickListener = new SearchKeywordAdapter.OnKeywordClickListener() {
        @Override
        public void onKeywordClick(int position, HotModel hotModel) {
            goSearch(hotModel);
        }
    };

    private void startSearch(HotModel hotModel) {
        mCateWindow.dismiss();

        mHistoryAdpter.add(hotModel);
        goSearch(hotModel);
    }


    private void initListView() {
        String historyStr = SPUtils.getString(true, this, "search_history", null);
        List<HotModel> mHistoryLists;

        if (!TextUtils.isEmpty(historyStr)) {
            try {
                mHistoryLists = GsonUtil.fromJson(historyStr, new TypeToken<List<HotModel>>() {
                }.getType());
            } catch (Exception e) {
                mHistoryLists = new ArrayList<>();
            }
        } else {
            mHistoryLists = new ArrayList<>();
        }

        mHistoryAdpter.setDatas(mHistoryLists);

        findViewById(R.id.search_not_history).setVisibility(mHistoryAdpter.getCount() > 0 ? View.GONE : View.VISIBLE);
        mHistoryLayout.setVisibility(mHistoryAdpter.getCount() > 0 ? View.VISIBLE : View.GONE);
        findViewById(R.id.search_btn_clear).setVisibility(mHistoryAdpter.getCount() > 0 ? View.VISIBLE : View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SPUtils.putString(true, this, "search_history", GsonUtil.toJson(mHistoryAdpter.getDatas()));
    }

    private void goSearch(HotModel hotModel) {
        Intent intent = new Intent(this, MainSearchActivity.class);
        intent.putExtra("serial_params", hotModel);
        startActivity(intent);
    }

    @Override
    protected void onSingleClick(View v) {
        String keyword = (mSearchEdit.getText().toString() + "").trim();

        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.search_btn_start:
                if (isSearch) {
                    showCateWindow(v);
                } else {
                    onBackPressed();
                }
                break;
            case R.id.cate_btn_area:
                startSearch(new HotModel(4, keyword));
                break;
            case R.id.cate_btn_works:
                startSearch(new HotModel(3, keyword));
                break;
            case R.id.cate_btn_position:
                startSearch(new HotModel(1, keyword));
                break;
            case R.id.cate_btn_user:
                startSearch(new HotModel(2, keyword));
                break;
            case R.id.search_btn_clear:
                SPUtils.putString(true, this, "search_history", null);
                initListView();
                break;
            default:
                break;
        }
    }

    private void showCateWindow(View view) {
        if (mCateWindow == null) {
            mCateWindow = new CateWindow(this);
            mCateWindow.getContentView().findViewById(R.id.cate_btn_area).setOnClickListener(this);
            mCateWindow.getContentView().findViewById(R.id.cate_btn_works).setOnClickListener(this);
            mCateWindow.getContentView().findViewById(R.id.cate_btn_position).setOnClickListener(this);
            mCateWindow.getContentView().findViewById(R.id.cate_btn_user).setOnClickListener(this);
        }

        mCateWindow.showAsDropDown(view, -150, 0);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        } else {
            String keysStr = ((JSONObject) result).optString("data");

            if (!TextUtils.isEmpty(keysStr) && !"null".equalsIgnoreCase(keysStr)) {
                List<HotModel> hotModels = GsonUtil.fromJson(keysStr, new TypeToken<List<HotModel>>() {
                }.getType());

                mHotAdapter.setDatas(hotModels);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListView();
    }

    private static class SearchHistoryAdapter extends BaseAdapter {
        private List<String> mSearchHistory;
        private Context mCtx;

        public SearchHistoryAdapter(Context ctx) {
            this.mCtx = ctx;
            mSearchHistory = new ArrayList<>();
        }

        @Override
        public int getCount() {
            if (mSearchHistory.size() > 0) {
                return mSearchHistory.size() + 1;
            }
            return mSearchHistory.size();
        }

        @Override
        public Object getItem(int position) {
            if (mSearchHistory.size() > 0) {
                return mSearchHistory.get(position % mSearchHistory.size());
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(mCtx);
                ((TextView) convertView).setTextSize(16);
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommUtils.dip2px(45)));
                ((TextView) convertView).setGravity(Gravity.CENTER_VERTICAL);
            }
            TextView textView = (TextView) convertView;
            if (getCount() > 1 && position == getCount() - 1) {
                ((TextView) convertView).setGravity(Gravity.CENTER);
                textView.setTextColor(mCtx.getResources().getColor(R.color.line_color));
                textView.setText("清除历史搜索");
            } else {
                ((TextView) convertView).setGravity(Gravity.CENTER_VERTICAL);
                textView.setTextColor(mCtx.getResources().getColor(R.color.colorGray));
                textView.setText((String) getItem(position));
            }
            return convertView;
        }

        public void setData(List<String> data) {
            if (data == null) {
                data = new ArrayList<>();
            }
            mSearchHistory.clear();
            mSearchHistory.addAll(data);
            notifyDataSetChanged();
        }

        public List<String> getData() {
            return mSearchHistory;
        }
    }
}
