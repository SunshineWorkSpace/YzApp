package com.yingshixiezuovip.yingshi.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.HomeShopMainActivity;
import com.yingshixiezuovip.yingshi.HomeShopPublishDetailActivity;
import com.yingshixiezuovip.yingshi.MainCommentActivity;
import com.yingshixiezuovip.yingshi.MainCommonActivity;
import com.yingshixiezuovip.yingshi.MainDetailsActivity;
import com.yingshixiezuovip.yingshi.MainDetailsBookActivity;
import com.yingshixiezuovip.yingshi.MainInsuranceActivity;
import com.yingshixiezuovip.yingshi.MainInviteActivity;
import com.yingshixiezuovip.yingshi.MainPublishActivity;
import com.yingshixiezuovip.yingshi.MainSearchHistoryActivity;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.UserInfo2Activity;
import com.yingshixiezuovip.yingshi.adapter.HomeListAdapter;
import com.yingshixiezuovip.yingshi.base.BaseFragment;
import com.yingshixiezuovip.yingshi.base.BaseResp;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.custom.AuthWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.minterface.OnAdapterClickListener;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.model.HomeTypeModel;
import com.yingshixiezuovip.yingshi.model.PublishIsOkModel;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshBase;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshListView;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.TimeUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Resmic on 2017/5/3.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class HomeFragment extends BaseFragment implements OnAdapterClickListener {
    private HomeTypeModel mHomeTypeModel;
    private List<HomeTypeModel.HomeType> mHomeTypes;
    private HorizontalScrollView mScrollView;
    private RadioGroup mTypeRadioGroup;
    private PullToRefreshListView mListView;
    private HomeListModel mHomeListModel;
    private int mTypeId = -1;
    private HomeListAdapter mHomeListAdapter;
    private boolean isMore = false;
    private int mPage = 0;
    private AuthWindow mAuthWindow;
    private AuthWindow mAuthInfoWindow;
    private boolean isFirstShow = true;
    private PublishIsOkModel mPublishIsOkModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        loadData();

    }



    private void initView() {
        mAuthWindow = new AuthWindow(getActivity());
        mAuthWindow.setOnClickListener(this);
        mScrollView = (HorizontalScrollView) findViewById(R.id.home_sc_type);
        mListView = (PullToRefreshListView) findViewById(R.id.common_listview);

        findViewById(R.id.home_btn_invite).setOnClickListener(this);
        findViewById(R.id.home_btn_search).setOnClickListener(this);
        findViewById(R.id.home_btn_publish).setOnClickListener(this);
        mListView.setShowIndicator(false);
        mListView.setOnRefreshListener(onRefreshListener);
        mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(SPUtils.getRefreshTime(getActivity(), "home")));
        mHomeTypes = new ArrayList<>();
        HomeTypeModel.HomeType homeType = new HomeTypeModel.HomeType();
        homeType.id = 0;
        homeType.name = "全部";
        mHomeTypes.add(homeType);
        mHomeListAdapter = new HomeListAdapter(getActivity());
        mHomeListAdapter.setOnAdapterClickListener(this);

        View headView = View.inflate(getContext(), R.layout.fragment_home_head, null);
        mListView.getRefreshableView().addHeaderView(headView);

        mListView.setAdapter(mHomeListAdapter);
        initSecondTab();
    }

    private void initWindow() {
        if (mAuthInfoWindow == null) {
            mAuthInfoWindow = new AuthWindow(getActivity());
        }
        if (mUserInfo.type == 2) {
            return;
        }
        if (isFirstShow && (mUserInfo.iswanshan == 0 || mUserInfo.isrenzhen == 0 || mUserInfo.isbzj == 0)) {
            isFirstShow = false;
            if (mUserInfo.iswanshan == 0) {
                mAuthInfoWindow.show("完善基本资料才可以使用发布哦", "算了", "完善资料", 1);
            } else if (!TextUtils.isEmpty(mUserInfo.invite)) {
            }
        }
    }

    private void initSecondTab() {
        findViewById(R.id.home_btn_secure).setOnClickListener(this);
        findViewById(R.id.home_btn_assist).setOnClickListener(this);
        findViewById(R.id.home_btn_project).setOnClickListener(this);
        findViewById(R.id.home_btn_iptrade).setOnClickListener(this);
        findViewById(R.id.home_btn_video).setOnClickListener(this);
        findViewById(R.id.home_btn_train).setOnClickListener(this);
    }

    private PullToRefreshBase.OnRefreshListener2<ListView> onRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(getActivity(), "home", currentTime);
            if (mHomeTypeModel == null || mHomeTypeModel.data == null || mHomeTypeModel.data.size() == 0) {
                loadData();
            } else {
                mPage = 0;
                loadListData();
            }
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            isMore = true;
            long currentTime = System.currentTimeMillis();
            mListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(getActivity(), "home", currentTime);
            if (mHomeTypeModel == null || mHomeTypeModel.data == null || mHomeTypeModel.data.size() == 0) {
                loadData();
            } else {
                mPage++;
                loadListData();
            }
        }
    };

    private void loadData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", 2);
        mLoadWindow.show(R.string.text_request);
        HttpUtils.doPost(TaskType.TASK_TYPE_HOME_TYPE, params, this);
    }
    private void loadPublish() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_ISPUBLISH, params, this);
    }
    private void loadListData() {
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("tid", mTypeId);
        params.put("start", mPage);
        params.put("qb", 1);
        HttpUtils.doPost(TaskType.TASK_TYPE_HOME_LIST, params, this);
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            if (checkedId == mTypeId) {
                return;
            }
            mTypeId = checkedId;
            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListView.setRefreshing();
                }
            }, 100L);
        }
    };

    private void inflateData() {
        mScrollView.setVisibility(View.VISIBLE);
        mScrollView.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(CommUtils.dip2px(50), CommUtils.dip2px(35));
        layoutParams.gravity = Gravity.CENTER;
        RadioButton tempRadioButtom;
        mTypeRadioGroup = new RadioGroup(getActivity());
        mTypeRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        mTypeRadioGroup.setOrientation(LinearLayout.HORIZONTAL);
        int i = 0;
        for (HomeTypeModel.HomeType homeType : mHomeTypes) {
            tempRadioButtom = (RadioButton) View.inflate(getActivity(), R.layout.view_radiobuttom, null);
            tempRadioButtom.setId(homeType.id);
            tempRadioButtom.setText(homeType.name);
            tempRadioButtom.setLayoutParams(layoutParams);
            mTypeRadioGroup.addView(tempRadioButtom);
            if (i++ == 0) {
                tempRadioButtom.setChecked(true);
            }
        }
        mTypeRadioGroup.check(0);
        mScrollView.addView(mTypeRadioGroup);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        Intent intent = null;
        mUserInfo = SPUtils.getUserInfo(getContext());
        switch (v.getId()) {
            case R.id.home_btn_invite:
                intent = new Intent(getActivity(), MainInviteActivity.class);
                break;
            case R.id.home_btn_search:
                intent = new Intent(getActivity(), MainSearchHistoryActivity.class);
                break;
            case R.id.home_btn_publish:

                if (mUserInfo.iswanshan == 0) {
                    mAuthInfoWindow.show("完善基本资料才可以使用发布哦", "取消", "完善资料", 1);
                } else {
                    if (mUserInfo.type == 1 || mUserInfo.type == 3) {
                        if (mUserInfo.type == 1) {
                            if (TextUtils.isEmpty(mUserInfo.invite)) {
//                                intent = new Intent(getActivity(), MainPublishActivity.class);
                                intentPic();
                            } else {
                                if (mUserInfo.isrenzhen == 0) {
                                    mAuthWindow.show("填写认证资料并通过审核，才可以使用发布价格等信息哦", "继续发布", 2);
                                } else {
                                    if (mUserInfo.isbzj == 0) {
                                        mAuthWindow.show("填写过认证资料，还需要支付会员费用才可以使用成为会员哦", "继续发布", 3);
                                    } else {
//                                        intent = new Intent(getActivity(), MainPublishActivity.class);
                                        intentPic();
                                    }
                                }
                            }
                        } else {
                            if (mUserInfo.isbzj == 0) {
                                mAuthWindow.show("填写过认证资料，还需要支付会员费用才可以使用成为会员哦", "继续发布", 4);
                            } else {
                                intentPic();
//                                intent = new Intent(getActivity(), MainPublishActivity.class);
                            }
                        }
                    } else {
                        intentPic();
//                        intent = new Intent(getActivity(), MainPublishActivity.class);
                    }
                }
                break;
            case R.id.home_btn_secure:
                intent = new Intent(getActivity(), MainInsuranceActivity.class);
                break;
            case R.id.home_btn_assist:
                intent = new Intent(getActivity(), MainCommonActivity.class);
                intent.putExtra("item_title", "协拍");
                intent.putExtra("item_tid", 17);
                intent.putExtra("item_type", MainCommonActivity.TYPE_HOME);
                break;
            case R.id.home_btn_project:
                intent = new Intent(getActivity(), HomeShopMainActivity.class);
                intent.putExtra("item_title", "商城");
                intent.putExtra("item_type", MainCommonActivity.TYPE_VIDEO);
                break;
            case R.id.home_btn_iptrade:
                intent = new Intent(getActivity(), MainCommonActivity.class);
                intent.putExtra("item_title", "电影项目");
                intent.putExtra("item_tid", 18);
                intent.putExtra("item_type", MainCommonActivity.TYPE_HOME);
                break;
            case R.id.home_btn_video:
                intent = new Intent(getActivity(), MainCommonActivity.class);
                intent.putExtra("item_title", "IP交易");
                intent.putExtra("item_tid", 19);
                intent.putExtra("item_type", MainCommonActivity.TYPE_HOME);
                break;
            case R.id.home_btn_train:
                intent = new Intent(getActivity(), MainCommonActivity.class);
                intent.putExtra("item_title", "培训");
                intent.putExtra("item_tid", 20);
                intent.putExtra("item_type", MainCommonActivity.TYPE_HOME);
                break;
            case R.id.auth_btn_cancel:
                if (mAuthWindow.getType() != 1) {
                    intentPic();
//                    intent = new Intent(getContext(), MainPublishActivity.class);
                }
                break;
            case R.id.tv_publish:
                intent = new Intent(getActivity(), MainPublishActivity.class);
                window.dismiss();
                break;
            case R.id.tv_shop:
                intent = new Intent(getActivity(), HomeShopPublishDetailActivity.class);
                window.dismiss();
                break;
            case R.id.pop_cancel:
                window.dismiss();
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }

    }
    PopupWindow window;
    private void intentPic() {
        if(mPublishIsOkModel==null){
            loadPublish();
            return;
        }
        if(mPublishIsOkModel.data.state.equals("1")){
            // 用于PopupWindow的View
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_home_publish, null, false);
            // 创建PopupWindow对象，其中：
            // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
            // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
            window = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            // 设置PopupWindow的背景
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // 设置PopupWindow是否能响应外部点击事件
            window.setOutsideTouchable(true);
            // 设置PopupWindow是否能响应点击事件
            window.setTouchable(true);
            // 显示PopupWindow，其中：
            // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
//        window.showAsDropDown(anchor, xoff, yoff);
            // 或者也可以调用此方法显示PopupWindow，其中：
            // 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
            // 第三和第四个参数分别是PopupWindow相对父View的x、y偏移
            // window.showAtLocation(parent, gravity, x, y);
            window.setAnimationStyle(R.style.animTranslate);
            window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getActivity(). getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    getActivity().getWindow().setAttributes(lp);
                }
            });

            window.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
            WindowManager.LayoutParams lp =  getActivity().getWindow().getAttributes();
            lp.alpha = 0.3f;
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            getActivity().getWindow().setAttributes(lp);
            ((TextView) contentView.findViewById(R.id.tv_publish)).setOnClickListener(this);
            ((TextView) contentView.findViewById(R.id.tv_shop)).setOnClickListener(this);
            ((TextView) contentView.findViewById(R.id.pop_cancel)).setOnClickListener(this);
        }else {

        }

    }
    @Override
    public void onFollowClick(int userid, int follow) {
        HashMap localHashMap = new HashMap();
        localHashMap.put("followid", userid);
        localHashMap.put("token", mUserInfo.token);
        HttpUtils.doPost(follow == 1 ? TaskType.TASK_TYPE_HOME_FOLLOW : TaskType.TASK_TYPE_HOME_CLEAR_FOLLOW, localHashMap, this);
    }

    @Override
    public void onZanClick(int id) {
        Intent intent = new Intent(getActivity(), MainCommentActivity.class);
        intent.putExtra("list_id", id);
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), MainDetailsActivity.class);
        HomeListModel.HomeListItem listItem = mHomeListAdapter.getItem(position);
        intent.putExtra("item_id", listItem.id);
        intent.putExtra("item_name", listItem.typename);
        startActivity(intent);
    }

    @Override
    public void onBookClick(int id) {
        Intent intent = new Intent(getActivity(), MainDetailsBookActivity.class);
        intent.putExtra("item_id", id);
        startActivity(intent);
    }

    @Override
    public void onHeadClick(int userid, String username) {
        Intent intent = new Intent(getActivity(), UserInfo2Activity.class);
        intent.putExtra("user_id", userid);
        intent.putExtra("user_name", username);
        startActivity(intent);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        mListView.onRefreshComplete();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_HOME_TYPE:
                mHomeTypeModel = GsonUtil.fromJson(result.toString(), HomeTypeModel.class);
                if (mHomeTypeModel != null) {
                    if (mHomeTypeModel.data != null) {
                        mHomeTypes.addAll(mHomeTypeModel.data);
                        inflateData();
                    }
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;
            case TASK_TYPE_HOME_LIST:
                mHomeListModel = GsonUtil.fromJson(result.toString(), HomeListModel.class);
                if (mHomeListModel != null) {
                    if (!isMore) {
                        mHomeListAdapter.setDatas(mHomeListModel.data);
                    } else {
                        isMore = false;
                        mHomeListAdapter.addDatas(mHomeListModel.data);
                    }
                    findViewById(R.id.notdata_layout).setVisibility(mHomeListAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                } else {
                    showMessage(R.string.data_load_failed);
                    findViewById(R.id.notdata_layout).setVisibility(View.VISIBLE);
                }
                break;
            case TASK_TYPE_HOME_CLEAR_FOLLOW:
            case TASK_TYPE_HOME_FOLLOW:
                break;
            case TASK_TYPE_ISPUBLISH:
                mPublishIsOkModel=GsonUtil.fromJson(result.toString(),PublishIsOkModel.class);
                if (mPublishIsOkModel != null) {
                    if (mPublishIsOkModel.data.state.equals("1")) {
                    }
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initWindow();
    }
}