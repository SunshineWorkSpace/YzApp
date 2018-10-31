package com.yingshixiezuovip.yingshi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.adapter.DetailsImageAdapter;
import com.yingshixiezuovip.yingshi.adapter.HomeListAdapter;
import com.yingshixiezuovip.yingshi.adapter.ReviewAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.custom.AuthWindow;
import com.yingshixiezuovip.yingshi.custom.PhonePayWindow;
import com.yingshixiezuovip.yingshi.custom.PhoneWindow;
import com.yingshixiezuovip.yingshi.custom.PhotoSingleWindow;
import com.yingshixiezuovip.yingshi.custom.PhotoViewWindow;
import com.yingshixiezuovip.yingshi.custom.ShareWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.minterface.OnAdapterClickListener;
import com.yingshixiezuovip.yingshi.minterface.WebInterface;
import com.yingshixiezuovip.yingshi.model.BaseEaseUser;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.model.ListDetailsModel;
import com.yingshixiezuovip.yingshi.model.ReviewModel;
import com.yingshixiezuovip.yingshi.model.ShareModel;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshBase;
import com.yingshixiezuovip.yingshi.quote.pulltorefresh.PullToRefreshListView;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.quote.video.JCVideoPlayer;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.TimeUtils;
import com.yingshixiezuovip.yingshi.utils.WebUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Resmic on 2017/5/7.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class MainDetailsActivity extends BaseActivity implements OnAdapterClickListener {
    private int mItemID;
    private String mItemName;
    private boolean isShowButtom = true;
    private ListDetailsModel mListDetailsModel;
    private AlertWindow mEditWindow;
    private ShareWindow mShareWindow;
    private PullToRefreshListView mImageListView;
    private ShareModel.ShareItem mShareItem;
    private int mTabIndex = -1;
    private HomeListAdapter mListAdapter;
    private ReviewAdapter mReviewAdapter;
    private ListView mHotListView;
    private View mMoreLayout;
    private View headTempView, footerTempView;
    private PhoneWindow mPhoneWindow;
    private PhonePayWindow mPhonePayWindow;
    private AuthWindow mAuthWindow;
    private WebView mWebView;

    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private View customView;
    private FrameLayout fullscreenContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_details);

        initView();
        mItemID = getIntent().getIntExtra("item_id", -1);
        if (mItemID < 0) {
            showMessage("参数异常，请稍后重试");
            return;
        }
        mItemName = getIntent().getStringExtra("item_name");
        isShowButtom = getIntent().getBooleanExtra("buttom_show", true);
        setActivityTitle(mItemName);
        initCallWindow();
        showLoadLayout();
        loadData();
    }

    private void initView() {
        findViewById(R.id.details_btn_consult).setOnClickListener(this);
        findViewById(R.id.details_btn_contact).setOnClickListener(this);
        findViewById(R.id.details_btn_book).setOnClickListener(this);
        findViewById(R.id.right_btn_name).setVisibility(View.GONE);
        findViewById(R.id.right_iv_more).setVisibility(View.VISIBLE);


        mListAdapter = new HomeListAdapter(this, 3);
        mListAdapter.setDetails(true);
        mListAdapter.setOnAdapterClickListener(this);
        mReviewAdapter = new ReviewAdapter(this);
        mReviewAdapter.setOnAdapterClickListener(this);
        mImageListView = (PullToRefreshListView) findViewById(R.id.detalis_image_listview);
        mImageListView.setOnRefreshListener(onRefreshListener);
        mImageListView.setShowIndicator(false);
        mImageListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mImageListView.setAdapter(new DetailsImageAdapter(this, new ArrayList<ListDetailsModel.DetailsBaseItem>()));
    }

    private PullToRefreshBase.OnRefreshListener2<ListView> onRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            long currentTime = System.currentTimeMillis();
            mImageListView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新：" + TimeUtils.formatRefreshDate(currentTime));
            SPUtils.saveRefreshTime(MainDetailsActivity.this, "details_" + mItemID, currentTime);
            loadData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        }
    };

    private void initCallWindow() {
        mPhoneWindow = new PhoneWindow(this, 1);
        mPhoneWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.dialog_item2) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mListDetailsModel.data.phone));
                    startActivity(intent);
                }
                mPhoneWindow.cancel();
            }
        });
        mEditWindow = new AlertWindow(this, false);
        mEditWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    mShareWindow.show(mShareItem, MainDetailsActivity.this);
                } else {
                    Intent intent = new Intent(MainDetailsActivity.this, MainDetailsChangeActivity.class);
                    intent.putExtra("works_id", mListDetailsModel.data.id);
                    intent.putExtra("works_price", mListDetailsModel.data.price);
                    intent.putExtra("works_unit", mListDetailsModel.data.unit);
                    startActivityForResult(intent, 1000);
                }
            }
        });
        mPhonePayWindow = new PhonePayWindow(this);
        mPhonePayWindow.findViewById(R.id.phonepay_btn_vip).setOnClickListener(this);
        mShareWindow = new ShareWindow(this);
        mAuthWindow = new AuthWindow(this);
        mAuthWindow.findViewById(R.id.auth_btn_cancel).setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        Intent intent = null;
        switch (v.getId()) {
            case R.id.details_btn_consult:
                intent = new Intent(this, MainChatListDetailsActivity.class);
                intent.putExtra("target_token", mListDetailsModel.data.easemob_token);
                intent.putExtra("target_name", mListDetailsModel.data.nickname);
                break;
            case R.id.auth_btn_cancel:
                mAuthWindow.cancel();
                break;
            case R.id.details_btn_contact:
                int userType = mListDetailsModel.data.usertype;
                if ((userType == 1 || userType == 3 || userType == 5 || userType == 6) && mListDetailsModel.data.ispay == 0) {
                    mPhonePayWindow.show(userType, mListDetailsModel.data.lookphonemoney, mListDetailsModel.data.phone, mListDetailsModel.data.userid);
                } else {
                    mPhoneWindow.show("用户联系电话 " + mListDetailsModel.data.phone);
                }
                break;
            case R.id.details_btn_book:
                intent = new Intent(this, MainDetailsBookActivity.class);
                intent.putExtra("item_id", mListDetailsModel.data.id);
                break;
            case R.id.right_btn_submit:
                if (mListDetailsModel.data.easemob_token.equalsIgnoreCase(mUserInfo.token)) {
                    mEditWindow.show("", "请选择对作品的操作", "修改作品", "分享");
                } else {
                    mShareWindow.show(mShareItem, this);
                }
                break;
            case R.id.details_btn_tuijian:
                switchTab(1);
                break;
            case R.id.details_btn_comment:
                switchTab(2);
                break;
            case R.id.phonepay_btn_vip:
                mPhonePayWindow.cancel();
            case R.id.details_tv_salary:
                if (mUserInfo.type == 1 || mUserInfo.type == 3 || mUserInfo.type == 5) {
                    if (mUserInfo.iswanshan != 0) {
                        if (mUserInfo.type == 3) {
                            if (mUserInfo.isbzj == 0) {
                                mAuthWindow.show("填写过认证资料，还需要支付会员费用才可以使用成为会员哦!", "算了", 4);
                            } else {
                                showMessage("正在认证中，请耐心等待...");
                            }
                        } else if (mUserInfo.type == 1) {
                            if (!TextUtils.isEmpty(mUserInfo.invite)) {
                                if (mUserInfo.isrenzhen == 0) {
                                    mAuthWindow.show("填写认证资料并通过审核，才可以使用发布价格等信息哦", "算了", 2);
                                } else {
                                    if (mUserInfo.isbzj == 0) {
                                        mAuthWindow.show("填写过认证资料，还需要支付会员费用才可以使用成为会员哦", "算了", 3);
                                    } else {
                                        showMessage("认证资料审核中，等待后台审核，请稍后...");
                                    }
                                }
                            } else {
                                showMessage("用户必须有推荐人才能认证");
                            }
                        } else {
                            showMessage("等待后台认证通过，谢谢！");
                        }
                    } else {
                        intent = new Intent(this, UserAuthenticationSelectActivity.class);
                    }
                }
                break;
            case R.id.details_iv_headImage:
            case R.id.details_btn_more:
                onHeadClick(mListDetailsModel.data.userid, mListDetailsModel.data.nickname);
                break;
            case R.id.details_invite_layout:
                if (!TextUtils.isEmpty(mListDetailsModel.data.invite)) {
                    onHeadClick(mListDetailsModel.data.inviteUserid, mListDetailsModel.data.invite);
                }
                break;
            case R.id.details_btn_mecomemnt:
            case R.id.details_btn_allcomemnt:
                intent = new Intent(this, MainDetailsCommentActivity.class);
                intent.putExtra("item_id", mListDetailsModel.data.id);
                intent.putExtra("item_type", MainDetailsCommentActivity.TYPE_LIST);
                intent.putExtra("item_rid", mItemID);
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void inflateData() {
        final ListDetailsModel.DetailsModel detailsModel = mListDetailsModel.data;
        SPUtils.saveBaseEaseUser(this, new BaseEaseUser(detailsModel.easemob_token, detailsModel.head, detailsModel.nickname));
        mShareItem = new ShareModel.ShareItem();
        mShareItem.photo = detailsModel.share_photo;
        mShareItem.title = detailsModel.share_title;
        mShareItem.url = detailsModel.share_url;
        mShareItem.content = detailsModel.share_content;

        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        setTitle(detailsModel.typename);

        View headView = View.inflate(this, R.layout.activity_main_details_head, null);
        mWebView = (WebView) headView.findViewById(R.id.details_webview);
        WebUtils.initWebView(mWebView);
        mWebView.addJavascriptInterface(new WebInterface(this), "WebInterface");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                String js = loadJS();
                L.d("loadJs ==== " + js);
                mWebView.loadUrl("javascript:" + js);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            /*** 视频播放相关的方法 **/

            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(MainDetailsActivity.this);
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                showCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
            }
        });


        mWebView.loadUrl(detailsModel.detailsUrl);

        mImageListView.getRefreshableView().addHeaderView(headView);

        View footerView = View.inflate(this, R.layout.activity_main_details_footer, null);
        footerView.findViewById(R.id.details_btn_tuijian).setOnClickListener(this);
        footerView.findViewById(R.id.details_btn_comment).setOnClickListener(this);
        footerView.findViewById(R.id.details_btn_more).setOnClickListener(this);
        footerView.findViewById(R.id.details_btn_mecomemnt).setOnClickListener(this);
        footerView.findViewById(R.id.details_btn_allcomemnt).setOnClickListener(this);
        footerView.findViewById(R.id.details_iv_headImage).setOnClickListener(this);
        footerView.findViewById(R.id.details_tv_salary).setOnClickListener(this);

        final TextView tvFollow = (TextView) footerView.findViewById(R.id.details_tv_follow);
        tvFollow.setText(detailsModel.isfollow == 0 ? "+关注" : "已关注");
        tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsModel.isfollow = detailsModel.isfollow == 0 ? 1 : 0;
                tvFollow.setText(detailsModel.isfollow == 0 ? "+关注" : "已关注");
                onFollowClick(detailsModel.userid, detailsModel.isfollow);
            }
        });
        ((TextView) footerView.findViewById(R.id.details_tv_nickName)).setText(detailsModel.nickname);
        ((TextView) footerView.findViewById(R.id.details_tv_position)).setText(detailsModel.city + " " + detailsModel.userposition);
        footerView.findViewById(R.id.details_invite_layout).setVisibility(TextUtils.isEmpty(detailsModel.invite) ? View.GONE : View.VISIBLE);
        footerView.findViewById(R.id.details_invite_layout).setOnClickListener(this);
        if (detailsModel.productionType == 1 || detailsModel.productionType == 3 || detailsModel.productionType == 5) {
            ((ImageView) footerView.findViewById(R.id.details_tv_authStatus)).setImageResource(R.mipmap.icon_unauth);
        } else {
            ((ImageView) footerView.findViewById(R.id.details_tv_authStatus)).setImageResource(R.mipmap.icon_authed);
        }

        footerView.findViewById(R.id.details_is_vip).setVisibility(View.VISIBLE);
        if (detailsModel.productionType == 2) {
            ((ImageView) footerView.findViewById(R.id.details_is_vip)).setImageResource(R.mipmap.auth_personal);
        } else if (detailsModel.productionType == 4) {
            ((ImageView) footerView.findViewById(R.id.details_is_vip)).setImageResource(R.mipmap.auth_company);
        } else if (detailsModel.productionType == 6) {
            ((ImageView) footerView.findViewById(R.id.details_is_vip)).setImageResource(R.mipmap.auth_student);
        } else {
            footerView.findViewById(R.id.details_is_vip).setVisibility(View.GONE);
        }

        if (mUserInfo.type == 1 || mUserInfo.type == 3 || mUserInfo.type == 5) {
            ((TextView) footerView.findViewById(R.id.details_tv_salary)).setText("酬劳：认证后方可查看!(点击认证)");
        } else if (detailsModel.usertype == 1 || detailsModel.usertype == 3 || detailsModel.usertype == 5) {
            ((TextView) footerView.findViewById(R.id.details_tv_salary)).setText("酬劳：该用户未认证");
        } else {
            ((TextView) footerView.findViewById(R.id.details_tv_salary)).setText("薪酬：" + detailsModel.price + "元/" + detailsModel.unit);
        }

        ((TextView) footerView.findViewById(R.id.details_tv_invite)).setText("推荐人：" + detailsModel.invite);


        PictureManager.displayHead(detailsModel.head, (RoundedImageView) footerView.findViewById(R.id.details_iv_headImage));

        mMoreLayout = footerView.findViewById(R.id.details_comment_layout);

        mHotListView = (ListView) footerView.findViewById(R.id.details_listview);
        mListAdapter.setDatas(detailsModel.otherList);
        mReviewAdapter.setDatas(detailsModel.reviewList);
        if (footerTempView != null) {
            mImageListView.getRefreshableView().removeFooterView(footerTempView);
        }
        footerTempView = footerView;
        mImageListView.getRefreshableView().addFooterView(footerTempView);
        findViewById(R.id.lin_controller_layout).setVisibility(isShowButtom ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(detailsModel.phone)) {
            findViewById(R.id.details_btn_contact).setVisibility(View.VISIBLE);
        }
        switchTab(1);
    }

    /**
     * 视频播放全屏
     **/
    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        getWindow().getDecorView();

        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        customViewCallback = callback;
    }

    /**
     * 隐藏视频全屏
     */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }

        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        mWebView.setVisibility(View.VISIBLE);
    }

    /**
     * 全屏容器界面
     */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    private String loadJS() {
        try {
            InputStream inStream = getAssets().open("js.txt");
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inStream.read(bytes)) > 0) {
                outStream.write(bytes, 0, len);
            }
            return outStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void switchTab(int index) {
        if (mTabIndex == index) {
            return;
        }
        mTabIndex = index;
        ((TextView) findViewById(R.id.details_btn_tuijian)).setTextColor(getWColor(index == 1 ? R.color.colorWhite : R.color.colorGray));
        ((TextView) findViewById(R.id.details_btn_comment)).setTextColor(getWColor(index == 1 ? R.color.colorGray : R.color.colorWhite));
        findViewById(R.id.details_btn_tuijian).setBackgroundColor(getWColor(index == 1 ? R.color.colorLanse : R.color.colorWhite));
        findViewById(R.id.details_btn_comment).setBackgroundColor(getWColor(index == 1 ? R.color.colorWhite : R.color.colorLanse));
        if (mHotListView == null) {
            return;
        }
        if (mTabIndex == 1) {
            mHotListView.setAdapter(mListAdapter);
            mMoreLayout.setVisibility(View.GONE);
        } else {
            mHotListView.setAdapter(mReviewAdapter);
            mMoreLayout.setVisibility(View.VISIBLE);
        }
        CommUtils.setListViewHeightBasedOnChildren(mHotListView);
    }

    public void onFollowClick(int userid, int follow) {
        HashMap localHashMap = new HashMap();
        if (mTabIndex == 1) {
            localHashMap.put("followid", userid);
            localHashMap.put("token", mUserInfo.token);
            HttpUtils.doPost(follow == 1 ? TaskType.TASK_TYPE_HOME_FOLLOW : TaskType.TASK_TYPE_HOME_CLEAR_FOLLOW, localHashMap, this);
        } else {
            localHashMap.put("rid", userid);
            localHashMap.put("token", mUserInfo.token);
            HttpUtils.doPost(follow == 1 ? TaskType.TASK_TYPE_REVIEW_ZAN : TaskType.TASK_TYPE_REVIEW_CANCELZAN, localHashMap, this);
        }
    }

    @Override
    public void onZanClick(int id) {
        Intent intent = new Intent(this, MainCommentActivity.class);
        intent.putExtra("list_id", id);
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = null;
        if (mTabIndex == 1) {
            intent = new Intent(this, MainDetailsActivity.class);
            HomeListModel.HomeListItem listItem = mListAdapter.getItem(position);
            intent.putExtra("item_id", listItem.id);
            intent.putExtra("item_name", listItem.typename);
        } else {
            intent = new Intent(this, MainDetailsCommentActivity.class);
            ReviewModel.ReviewItem reviewItem = mReviewAdapter.getItem(position);
            intent.putExtra("item_id", mItemID);
            intent.putExtra("item_rid", reviewItem.rid);
            intent.putExtra("item_type", MainDetailsCommentActivity.TYPE_DETAILS);
            intent.putExtra("item_name", reviewItem.nickname);
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onBookClick(int id) {
        Intent intent = new Intent(this, MainDetailsBookActivity.class);
        intent.putExtra("item_id", id);
        startActivity(intent);
    }

    @Override
    public void onHeadClick(int userid, String username) {
        Intent intent = new Intent(this, UserInfo2Activity.class);
        intent.putExtra("user_id", userid);
        intent.putExtra("user_name", username);
        startActivity(intent);
    }


    private void loadData() {
        mUserInfo = SPUtils.getUserInfo(this);
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("id", mItemID);
        HttpUtils.doPost(TaskType.TASK_TYPE_LIST_ITEM_DETAILS, params, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (!isActivityRun) {
            return;
        }
        closeLoadLayout();
        mImageListView.onRefreshComplete();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_LIST_ITEM_DETAILS:
                mListDetailsModel = GsonUtil.fromJson(result.toString(), ListDetailsModel.class);
                if (mListDetailsModel != null && mListDetailsModel.data != null) {
                    inflateData();
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;
        }
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        switch (event.getEventType()) {
            case EVENT_TYPE_CLOSE_DETAILS:
            case EVENT_TYPE_REFRESH_PRICE:
                finish();
                break;
            case EVENT_TYPE_PAY_SUCCESS:
                loadData();
                break;
            case EVENT_TYPE_REFRESH_ARTICLE:
                setActivityTitle((String) event.getData());
                mWebView.reload();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            hideCustomView();
            return;
        }

        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListAdapter != null && mListAdapter.getDatas() != null) {
            mListAdapter.getDatas().clear();
        }
        if (mReviewAdapter != null && mReviewAdapter.getDatas() != null) {
            mReviewAdapter.getDatas().clear();
        }

        if (mWebView != null) {
            mWebView.destroy();
        }

        if (mListDetailsModel != null && mListDetailsModel.data != null) {
            if (mListDetailsModel.data.bodyList != null) {
                mListDetailsModel.data.bodyList.clear();
                mListDetailsModel.data.bodyList = null;
            }
            if (mListDetailsModel.data.videoList != null) {
                mListDetailsModel.data.videoList.clear();
                mListDetailsModel.data.videoList = null;
            }
            if (mListDetailsModel.data.reviewList != null) {
                mListDetailsModel.data.reviewList.clear();
                mListDetailsModel.data.reviewList = null;
            }
            if (mListDetailsModel.data.otherList != null) {
                mListDetailsModel.data.otherList.clear();
                mListDetailsModel.data.otherList = null;
            }
        }

        if (mListAdapter != null)
            mListAdapter.onDestroy();
        if (headTempView != null) {
            mImageListView.getRefreshableView().removeHeaderView(headTempView);
            headTempView = null;
        }
        if (footerTempView != null) {
            mImageListView.getRefreshableView().removeFooterView(footerTempView);
            footerTempView = null;
        }
        mImageListView.removeAllViews();
        Runtime.getRuntime().gc();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

}
