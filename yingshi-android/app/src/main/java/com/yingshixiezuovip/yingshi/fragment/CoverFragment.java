package com.yingshixiezuovip.yingshi.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Trace;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.UserAuthenticationSelectActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.BaseFragment;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.AuthWindow;
import com.yingshixiezuovip.yingshi.custom.PhonePayWindow;
import com.yingshixiezuovip.yingshi.custom.PhoneWindow;
import com.yingshixiezuovip.yingshi.model.UserWorks;
import com.yingshixiezuovip.yingshi.utils.PictureManager;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

/**
 * Created by Resmic on 2017/9/14.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class CoverFragment extends BaseFragment {
    private UserWorks mUserWorks;
    private PhoneWindow mPhoneWindow;
    private PhonePayWindow mPhonePayWindow;
    private AuthWindow mAuthWindow;
    private int mUserid;

    public static CoverFragment getInstance(UserWorks userWorks, int userid) {
        CoverFragment coverFragment = new CoverFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user_works", userWorks);
        bundle.putInt("userid", userid);
        coverFragment.setArguments(bundle);
        return coverFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_cover_layout, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        initView();

        mUserWorks = (UserWorks) getArguments().getSerializable("user_works");
        mUserid = getArguments().getInt("userid");

        inflateData();
    }

    private void initView() {
        findViewById(R.id.cover_btn_call).setOnClickListener(this);
        mPhonePayWindow = new PhonePayWindow(getContext());
        mPhonePayWindow.findViewById(R.id.phonepay_btn_vip).setOnClickListener(this);

        mPhoneWindow = new PhoneWindow(getContext(), 1);
        mPhoneWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.dialog_item2) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mUserWorks.phone));
                    startActivity(intent);
                }
                mPhoneWindow.cancel();
            }
        });

        mAuthWindow = new AuthWindow(getContext());
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getEventType() == EventType.EVENT_TYPE_COVER_REFRESH) {
            mUserWorks = (UserWorks) event.getData();
        }
    }

    @Override
    protected void onSingleClick(View v) {
        if (v.getId() == R.id.cover_btn_call) {
            int userType = mUserWorks.usertype;
            if ((userType == 1 || userType == 3 || userType == 5 || userType == 6) && mUserWorks.ispay == 0) {
                mPhonePayWindow.show(userType, mUserWorks.lookphonemoney, mUserWorks.phone, mUserWorks.id);
            } else {
                mPhoneWindow.show("用户联系电话 " + mUserWorks.phone);
            }
        } else if (v.getId() == R.id.phonepay_btn_vip) {
            mPhonePayWindow.cancel();
            if (mUserInfo.type == 1 || mUserInfo.type == 3) {
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
                    }
                } else {
                    startActivity(new Intent(getContext(), UserAuthenticationSelectActivity.class));
                }
            }
        }
    }

    private void inflateData() {
        mUserInfo = SPUtils.getUserInfo(getContext());

        PictureManager.displayImage(mUserWorks.samplereels_fm, (ImageView) findViewById(R.id.cover_iv_image));
        PictureManager.displayHead(mUserWorks.head, (ImageView) findViewById(R.id.cover_iv_head));
        ((TextView) findViewById(R.id.cover_tv_name)).setText(mUserWorks.nickname);
        ((TextView) findViewById(R.id.cover_tv_position)).setText(mUserWorks.position + " / " + mUserWorks.city);
        if(mUserInfo.usertype==7){
            ((TextView) findViewById(R.id.cover_tv_invite)).setText("商铺会员已认证");
        }else {
            ((TextView) findViewById(R.id.cover_tv_invite)).setText("推荐人：" + mUserWorks.invite);
        }

        findViewById(R.id.cover_tv_invite).setVisibility(TextUtils.isEmpty(mUserWorks.invite) ? View.GONE : View.VISIBLE);

        findViewById(R.id.cover_btn_call).setVisibility(mUserid == mUserInfo.id ? View.GONE : View.VISIBLE);

        String idcardStr;
        if (mUserWorks.productionType == 2) {
            idcardStr = "身份证已认证";
        } else if (mUserWorks.productionType == 4) {
            idcardStr = "营业执照已认证";
        } else if(mUserWorks.productionType == 6){
            idcardStr = "学生证已认证";
        } else {
            idcardStr = null;
        }
        ((TextView) findViewById(R.id.cover_tv_idcard)).setText(idcardStr);
        findViewById(R.id.cover_tv_idcard).setVisibility(TextUtils.isEmpty(idcardStr) ? View.GONE : View.VISIBLE);
    }

    
}
