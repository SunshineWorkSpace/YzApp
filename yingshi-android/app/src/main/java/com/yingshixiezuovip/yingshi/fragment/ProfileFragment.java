package com.yingshixiezuovip.yingshi.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.AccountPriceActivity;
import com.yingshixiezuovip.yingshi.HomeShopDetailActvity;
import com.yingshixiezuovip.yingshi.HomeShopPublishDetailActivity;
import com.yingshixiezuovip.yingshi.HomeShopUserActivity;
import com.yingshixiezuovip.yingshi.MainAboutActivity;
import com.yingshixiezuovip.yingshi.MainAuthenticationInfoActivity;
import com.yingshixiezuovip.yingshi.MainAuthenticationMoneyActivity;
import com.yingshixiezuovip.yingshi.MainChatListActivity;
import com.yingshixiezuovip.yingshi.MainCommentActivity;
import com.yingshixiezuovip.yingshi.MainCommonActivity;
import com.yingshixiezuovip.yingshi.MainCompanyInfoSetActivity;
import com.yingshixiezuovip.yingshi.MainFeedbackActivity;
import com.yingshixiezuovip.yingshi.MainPublishActivity;
import com.yingshixiezuovip.yingshi.MallOrderActivity;
import com.yingshixiezuovip.yingshi.ProfileConsumerPriceActivity;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.StartupLoginActivity;
import com.yingshixiezuovip.yingshi.UserAddressListActivity;
import com.yingshixiezuovip.yingshi.UserAuthenticationSelectActivity;
import com.yingshixiezuovip.yingshi.UserInfo2Activity;
import com.yingshixiezuovip.yingshi.UserInfoSettingsActivity;
import com.yingshixiezuovip.yingshi.WebViewActivity;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.BaseFragment;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.custom.AuthWindow;
import com.yingshixiezuovip.yingshi.custom.PhoneWindow;
import com.yingshixiezuovip.yingshi.custom.ShareWindow;
import com.yingshixiezuovip.yingshi.custom.VersionWindow;
import com.yingshixiezuovip.yingshi.datautils.CommonThreadPool;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.ShareModel;
import com.yingshixiezuovip.yingshi.model.UserModel;
import com.yingshixiezuovip.yingshi.model.VersionModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.FileUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.LoginUtils;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Resmic on 2017/5/3.
 */

public class ProfileFragment extends BaseFragment implements PictureManager.OnPictureCallbackListener {
    private File mCacheFile;
    private PictureManager mPictureManager;
    private boolean isHead = false;
    private AuthWindow mAuthWindow;
    private AlertWindow mCacheWindow;
    private ShareWindow mShareWindow;
    private AlertWindow mExitWindow;
    private String mCacheSize;
    private PhoneWindow mPhoneWindow;

    private String mBackgroundUrl = "";
    private VersionModel mVersionModel;
    private VersionWindow mVersionWindow;

    private AlertWindow mStatusWindow;
    private AlertWindow mChoiceWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_profile, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        inflatUserData();
        loadData();
        initCache();
    }

    private void initView() {
        mPictureManager = new PictureManager((BaseActivity) getActivity(), this);
        findViewById(R.id.profile_iv_head).setOnClickListener(this);
        findViewById(R.id.profile_btn_logout).setOnClickListener(this);
        findViewById(R.id.profile_btn_work).setOnClickListener(this);
        findViewById(R.id.profile_btn_comment).setOnClickListener(this);
        findViewById(R.id.profile_btn_follow).setOnClickListener(this);
        findViewById(R.id.profile_btn_fans).setOnClickListener(this);

        findViewById(R.id.profile_btn_info).setOnClickListener(this);
        findViewById(R.id.profile_btn_workshare).setOnClickListener(this);
        findViewById(R.id.profile_btn_account).setOnClickListener(this);
        findViewById(R.id.profile_btn_message).setOnClickListener(this);
        findViewById(R.id.profile_btn_order).setOnClickListener(this);
        findViewById(R.id.profile_btn_seller).setOnClickListener(this);
        findViewById(R.id.profile_btn_salary).setOnClickListener(this);
        findViewById(R.id.profile_btn_invateShare).setOnClickListener(this);
        findViewById(R.id.profile_btn_invate).setOnClickListener(this);
        findViewById(R.id.profile_btn_cache).setOnClickListener(this);
        findViewById(R.id.profile_btn_service).setOnClickListener(this);
        findViewById(R.id.profile_btn_agreement).setOnClickListener(this);
        findViewById(R.id.profile_btn_about).setOnClickListener(this);
        findViewById(R.id.profile_btn_update).setOnClickListener(this);
        findViewById(R.id.profile_btn_reaccount).setOnClickListener(this);
        findViewById(R.id.profile_btn_desc).setOnClickListener(this);
        findViewById(R.id.profile_btn_pubaddress).setOnClickListener(this);
        findViewById(R.id.lin_xbj).setOnClickListener(this);
        findViewById(R.id.lin_shop_order).setOnClickListener(this);
        findViewById(R.id.lin_account_price).setOnClickListener(this);
        findViewById(R.id.lin_shipping_address).setOnClickListener(this);
        initAlertWindow();
    }

    private void initAlertWindow() {
        mCacheWindow = new AlertWindow(getContext(), true);
        mCacheWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    clearCache();
                }
            }
        });
        mShareWindow = new ShareWindow(getContext());
        mExitWindow = new AlertWindow(getContext(), false);
        mExitWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    SPUtils.saveUserInfo(null);
                    LoginUtils.doLogout();
                    Intent intent = new Intent(getContext(), StartupLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    YingApplication.getInstance().startActivity(intent);
                }
            }
        });
        mPhoneWindow = new PhoneWindow(getContext(), 2);
        mPhoneWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneWindow.cancel();
                if (v.getId() == R.id.dialog_item1) {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Configs.SERVICE_PHONE)));
                } else if (v.getId() == R.id.dialog_item2) {
                    startActivity(new Intent(getContext(), MainFeedbackActivity.class));
                }
            }
        });

        mVersionWindow = new VersionWindow(getContext());
        mVersionWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVersionWindow.cancel();
                if (v.getId() == R.id.dialog_item2) {
                    mVersionModel.toUpdate(getActivity());
                }
            }
        });

        mStatusWindow = new AlertWindow(getContext(), false);
        mStatusWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    startActivity(new Intent(getContext(), UserAuthenticationSelectActivity.class));
                }
            }
        });

        mChoiceWindow = new AlertWindow(getContext(), false);
        mChoiceWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_cancel) {
                    isHead = false;
                } else {
                    isHead = true;
                }
                mPictureManager.showWindow(isHead ? PictureManager.WINDOW_TYPE_HEAD : PictureManager.WINDOW_TYPE_BACKGROUND);
            }
        });
    }

    private void initCache() {
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                long tempSize = 0;
                if (Configs.CacheDirectory != null && Configs.CacheDirectory.length > 0) {
                    for (String tempFileName : Configs.CacheDirectory) {
                        mCacheFile = FileUtils.getPath(tempFileName);
                        tempSize += getTotalSizeOfFilesInDir(mCacheFile);
                    }
                }
                long fileSize = tempSize;
                float fSize = fileSize * 1.0f / 1024 / 1024;
                mCacheSize = String.format("%.2fMB", fSize);
            }
        };
        CommonThreadPool.getThreadPool().addCachedTask(mRunnable);
    }

    private long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }

    public void clearCache() {
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (Configs.CacheDirectory != null && Configs.CacheDirectory.length > 0) {
                    for (String tempFileName : Configs.CacheDirectory) {
                        mCacheFile = FileUtils.getPath(tempFileName);
                        clearFiles(mCacheFile);
                    }
                }
                initCache();
                showMessage("清理完成");
            }
        };
        CommonThreadPool.getThreadPool().addCachedTask(mRunnable);
    }

    private void clearFiles(File file) {
        if (file.exists()) {
            deleteFile(file);
        }
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }

    private void initWindow() {
        mUserInfo = SPUtils.getUserInfo(YingApplication.getInstance());
        if (mAuthWindow == null) {
            mAuthWindow = new AuthWindow(getActivity());
        }
        if (mUserInfo.type == 2) {
            return;
        }
        if (mUserInfo.iswanshan == 0 || mUserInfo.isrenzhen == 0 || mUserInfo.isbzj == 0) {
            if (mUserInfo.iswanshan == 0) {
                mAuthWindow.show("完善基本资料才可以使用发布哦", "算了", "完善资料", 1);
            }
//            else if (mUserInfo.isrenzhen == 0) {
//                mAuthWindow.show("填写认证资料并通过审核，才可以使用发布价格等信息哦", "下次认证", 2);
//            } else {
//                mAuthWindow.show("填写过认证资料，还需要支付会员费用才可以使用成为会员哦", "下次认证", 3);
//            }
        }
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        Intent intent = null;
        switch (v.getId()) {
            case R.id.profile_iv_head:
                mChoiceWindow.show("", "修改背景图或者头像", "背景图", "头像");
                break;
            case R.id.profile_btn_work:
                intentPic();
                break;
            case R.id.profile_btn_comment:
                intent = new Intent(getActivity(), MainCommentActivity.class);
                intent.putExtra("item_type", MainCommentActivity.TYPE_NOTICE);
                break;
            case R.id.profile_btn_follow:
                intent = new Intent(getActivity(), MainCommonActivity.class);
                intent.putExtra("item_type", MainCommonActivity.TYPE_MINE_FOLLOW);
                intent.putExtra("item_title", "关注");
                break;
            case R.id.profile_btn_fans:
                intent = new Intent(getActivity(), MainCommonActivity.class);
                intent.putExtra("item_type", MainCommonActivity.TYPE_FOLLOW);
                intent.putExtra("item_title", "粉丝");
                intent.putExtra("user_id", mUserInfo.id);
                break;
            case R.id.profile_btn_info:
                if (mUserInfo.iswanshan == 0) {
                    intent = new Intent(getContext(), UserAuthenticationSelectActivity.class);
                } else {
                    startActivity(new Intent(getContext(), MainCompanyInfoSetActivity.class));
                }
                break;
            case R.id.profile_btn_account:
                if (mUserInfo.type == 2 || mUserInfo.type == 4 || mUserInfo.type == 6||mUserInfo.type==7) {
                    showMessage("已认证");
                } else {
                    if (mUserInfo.iswanshan == 0) {
                        //跳转个人/企业选择页面
                        intent = new Intent(getContext(), UserAuthenticationSelectActivity.class);
                    } else {
                        if (mUserInfo.type == 1) {
                            if (TextUtils.isEmpty(mUserInfo.invite)) {
                                showMessage("请先填写邀请码");
                            } else {
                                if (mUserInfo.isrenzhen == 1) {
                                    if (mUserInfo.isbzj == 1) {
                                        showMessage("正在认证中，请耐心等待结果");
                                    } else {
                                        //跳转支付299页面
                                        intent = new Intent(getContext(), MainAuthenticationMoneyActivity.class);
                                    }
                                } else {
                                    //跳转认证页面
                                    intent = new Intent(getContext(), MainAuthenticationInfoActivity.class);
                                }
                            }
                        } else {
                            if (mUserInfo.isbzj == 1) {
                                showMessage("正在认证中，请耐心等待结果");
                            } else {
                                //跳转支付499页面
                                intent = new Intent(getContext(), MainAuthenticationMoneyActivity.class);
                                intent.putExtra("company_auth", true);
                            }
                        }
                    }
                }
                break;
            case R.id.profile_btn_workshare:
                intent = new Intent(getActivity(), UserInfo2Activity.class);
                intent.putExtra("user_id", mUserInfo.id);
                intent.putExtra("user_name", mUserInfo.nickname);
                break;
            case R.id.profile_btn_message:
                intent = new Intent(getActivity(), MainChatListActivity.class);
                break;
            case R.id.profile_btn_order:
                intent = new Intent(getActivity(), MainCommonActivity.class);
                intent.putExtra("item_type", MainCommonActivity.TYPE_BUYER_ORDER);
                intent.putExtra("item_title", "订单");
                break;
            case R.id.profile_btn_seller:
                if (mUserInfo.type == 2 || mUserInfo.type == 4 || mUserInfo.type == 6||mUserInfo.type==7) {
                    intent = new Intent(getActivity(), MainCommonActivity.class);
                    intent.putExtra("item_type", MainCommonActivity.TYPE_SELLER_ORDER);
                    intent.putExtra("item_title", "卖家信息");
                } else {
                    showMessage("认证会员才能使用卖家权限！");
                }
                break;
            case R.id.profile_btn_salary:
                if (mUserInfo.type == 2 || mUserInfo.type == 4 || mUserInfo.type == 5 || mUserInfo.type == 6||mUserInfo.type==7) {
                    intent = new Intent(getActivity(), UserInfoSettingsActivity.class);
                    intent.putExtra("user_type", UserInfoSettingsActivity.TYPE_PRICE);
                } else {
                    showMessage("普通用户不能修该薪酬");
                }
                break;
            case R.id.profile_btn_invateShare:
                if (mUserInfo.usertype == 2 || mUserInfo.usertype == 4 || mUserInfo.usertype == 6|mUserInfo.usertype==7) {
                    ShareModel.ShareItem shareItem = new ShareModel.ShareItem();
                    shareItem.title = mUserInfo.share_title;
                    shareItem.photo = mUserInfo.share_photo;
                    shareItem.url = mUserInfo.share_url;
                    mShareWindow.show(shareItem, this);
                } else {
                    showMessage("普通用户不能分享邀请码");
                }
                break;
            case R.id.profile_btn_invate:
                if (mUserInfo.type == 1 || mUserInfo.type == 2) {
                    if (mUserInfo.type == 2) {
                        showMessage("已经认证通过");
                    } else {
                        intent = new Intent(getContext(), UserInfoSettingsActivity.class);
                        intent.putExtra("user_type", UserInfoSettingsActivity.TYPE_INVATE);
                    }
                } else if (mUserInfo.type == 5 || mUserInfo.type == 6) {
                    showMessage("学生用户不需要填写邀请码");
                } else {
                    showMessage("企业用户不需要填写邀请码");
                }
                break;
            case R.id.profile_btn_cache:
                mCacheWindow.show("缓存清理", "有" + mCacheSize + "缓存，确定清除吗？", "取消", "确定");
                break;
            case R.id.profile_btn_service:
                mPhoneWindow.show("直接拨打客服电话：" + Configs.SERVICE_PHONE, "提问客服");
                break;
            case R.id.profile_btn_agreement:
                intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(WebViewActivity.CURL, Configs.AgreementURL);
                break;
            case R.id.profile_btn_about:
                intent = new Intent(getActivity(), MainAboutActivity.class);
                break;
            case R.id.profile_btn_logout:
                mExitWindow.show("", "确定退出登录？", "取消", "确定");
                break;
            case R.id.profile_btn_update:
                mVersionModel = YingApplication.getInstance().getVersionModel();

                if (mVersionModel != null && mVersionModel.needUpdate()) {
                    mVersionWindow.showVersion(mVersionModel);
                } else {
                    showMessage("已是最新版本");
                }
                break;
            case R.id.profile_btn_reaccount:
                if(mUserInfo.usertype==7){
                    showMessage("商户会员用户无法修改成其他用户状态");
                    return;
                }
                mStatusWindow.show("", "是否要修改成其他用户状态?", "取消", "确认");
                break;
            case R.id.profile_btn_desc:
                intent = new Intent(getActivity(), MainCommonActivity.class);
                intent.putExtra("item_type", MainCommonActivity.TYPE_HOME);
                intent.putExtra("item_title", "操作指南");
                intent.putExtra("item_tid", 100);
                intent.putExtra("show_bottom", false);
                break;
            case R.id.profile_btn_pubaddress:
                String url = Configs.ServerUrl + "Web/login.html";
                ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("pcUrl", url);
                cmb.setPrimaryClip(clipData);
                showMessage("成功复制" + url + " 电脑端用户发布链接");
                break;
            case R.id.tv_publish:
                intent = new Intent(getActivity(), MainCommonActivity.class);
                intent.putExtra("item_type", MainCommonActivity.TYPE_MINE_WORK);
                intent.putExtra("item_title", mUserInfo.nickname + "的作品集");
                window.dismiss();
                break;
            case R.id.tv_shop:
                /*商城入口*/
                Intent userShop=new Intent(getActivity(),HomeShopUserActivity.class);
                userShop.putExtra("uid",mUserInfo.id+"");
                startActivity(userShop);
                window.dismiss();
                break;
            case R.id.pop_cancel:
                window.dismiss();
                break;
            case R.id.lin_shop_order:
                intent=new Intent(getActivity(),MallOrderActivity.class);
                break;
            case R.id.lin_account_price:
                intent=new Intent(getActivity(),AccountPriceActivity.class);
                break;
                /*收货地址入口*/
            case R.id.lin_shipping_address:
                intent=new Intent(getActivity(), UserAddressListActivity.class);
                break;
            case R.id.lin_xbj:
                intent=new Intent(getActivity(),ProfileConsumerPriceActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
    PopupWindow window;
    private void intentPic() {

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
        ((TextView) contentView.findViewById(R.id.tv_publish)).setText("作品");
            ((TextView) contentView.findViewById(R.id.tv_publish)).setOnClickListener(this);
            ((TextView) contentView.findViewById(R.id.tv_shop)).setOnClickListener(this);
            ((TextView) contentView.findViewById(R.id.pop_cancel)).setOnClickListener(this);

    }
    private void inflatUserData() {
        if (getActivity() == null) {
            return;
        }
        mUserInfo = SPUtils.getUserInfo(getActivity());
        if (mUserInfo == null) {
            return;
        }

        PictureManager.displayHead(mUserInfo.head, (RoundedImageView) findViewById(R.id.profile_iv_head));
        PictureManager.displayImage(mUserInfo.background, (ImageView) findViewById(R.id.profile_iv_background));

        ((TextView) findViewById(R.id.profile_tv_nickname)).setText(mUserInfo.nickname);
        ((TextView) findViewById(R.id.profile_tv_infoStatus)).setText(mUserInfo.iswanshan == 1 ? "已填写" : "未填写");
        ((TextView) findViewById(R.id.profile_tv_invite)).setText(TextUtils.isEmpty(mUserInfo.invite) ? getAuthStatus() : ("推荐人：" + mUserInfo.invite));
        ((TextView) findViewById(R.id.profile_tv_phone)).setText("电话：" + mUserInfo.phone);
        ((TextView) findViewById(R.id.profile_tv_authStatus)).setText(getAuthStatus2());
        ((TextView) findViewById(R.id.profile_tv_num_publish)).setText(String.valueOf(mUserInfo.zuopincount));
        ((TextView) findViewById(R.id.profile_tv_num_comment)).setText(String.valueOf(mUserInfo.otherzancount));
        ((TextView) findViewById(R.id.profile_tv_num_follow)).setText(String.valueOf(mUserInfo.followcount));
        ((TextView) findViewById(R.id.profile_tv_num_fans)).setText(String.valueOf(mUserInfo.forfollowcount));
    }


    private String getAuthStatus() {
        if ((mUserInfo.type == 1 || mUserInfo.type == 3) && mUserInfo.isbzj == 1) {
            return "认证中";
        } else if (mUserInfo.type == 2) {
            return "认证会员";
        } else if (mUserInfo.type == 4) {
            return "认证企业";
        } else if (mUserInfo.type == 5) {
            return "普通学生用户";
        } else if (mUserInfo.type == 6) {
            return "学生会员用户";
        } else if(mUserInfo.type == 7){
            return "商铺会员用户";
        }
            else {
            return "未认证";
        }
    }

    private String getAuthStatus2() {
        if ((mUserInfo.type == 1 || mUserInfo.type == 3) && mUserInfo.isbzj == 1) {
            return "认证中";
        } else if (mUserInfo.type == 2) {
            return "认证会员用户";
        } else if (mUserInfo.type == 4) {
            return "认证企业用户";
        } else if (mUserInfo.type == 5) {
            return "普通学生用户";
        } else if (mUserInfo.type == 6) {
            return "认证学生用户";
        } else if(mUserInfo.type == 7){
            return "商铺会员用户";
        }else {
            return "未认证";
        }
    }

    private void loadData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_USER_INFO, params, this);
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        mLoadWindow.cancel();
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_USER_INFO:
                UserModel userModel = GsonUtil.fromJson(result.toString(), UserModel.class);
                if (userModel != null && userModel.data != null) {
                    mUserInfo = SPUtils.getUserInfo(YingApplication.getInstance());
                    userModel.data.token = mUserInfo.token;
                    userModel.data.type = userModel.data.usertype;
                    SPUtils.saveUserInfo(userModel.data);
                    inflatUserData();
                }
                if (!isHidden()) {
                    initWindow();
                }
                break;
            case TASK_TYPE_MINE_BACKGROUND_UPLOAD:
            case TASK_TYPE_MINE_HEAD_UPLOAD:
            case TASK_TYPE_MINE_PHOTO:
                showMessage("信息更新成功");
                break;
            case TASK_TYPE_QRY_VERSION:

                break;
            default:
                break;
        }
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        switch (event.getEventType()) {
            case EVENT_TYPE_PAY_SUCCESS:
                loadData();
                break;
            case EVENT_TYPE_RECIVER_NOTICE:
                inflatUserData();
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadData();
            initCache();
        }
    }

    @Override
    public void onPictureCallback(Uri uri, Intent data) {
        mLoadWindow.show(R.string.text_pic_upload);
        if (isHead) {
            mPictureManager.doUploadPicture(mUserInfo.token, uri, 1, this, -1);
            ((RoundedImageView) findViewById(R.id.profile_iv_head)).setImageURI(uri);
        } else {
            mPictureManager.doUploadPicture(mUserInfo.token, uri, 2, this, -1);
            ((ImageView) findViewById(R.id.profile_iv_background)).setImageURI(uri);
        }
    }

    @Override
    public void onPictureUpload(int status, String message) {
        mLoadWindow.cancel();
        showMessage(message);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mPictureManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
