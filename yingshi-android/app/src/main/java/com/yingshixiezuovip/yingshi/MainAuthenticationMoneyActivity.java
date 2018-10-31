package com.yingshixiezuovip.yingshi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.BuyerModel;
import com.yingshixiezuovip.yingshi.model.DetailsPayModel;
import com.yingshixiezuovip.yingshi.model.PayModel;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.OrderInfoUtils;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainAuthenticationMoneyActivity extends BaseActivity {
    private boolean isWecahtPay = true;
    private IWXAPI mWXApi;
    private BuyerModel.BuyerItem mBuyerItem;
    private AlertWindow mCancelOrderWindow;
    private JSONObject mOrderObjectInfo;
    private boolean isCancel = false;
    private boolean isCompanyAuth = false;
    private PayModel mPayModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_authentication_money, R.string.title_activity_main_authentication_money);

        mWXApi = WXAPIFactory.createWXAPI(this, Configs.WECHAT_APPID);
        try {
            isCompanyAuth = getIntent().getBooleanExtra("company_auth", false);
            mPayModel = (PayModel) getIntent().getSerializableExtra("pay_model");
            mBuyerItem = (BuyerModel.BuyerItem) getIntent().getSerializableExtra("item_data");
        } catch (Exception e) {
            mBuyerItem = null;
        }
        initView();
    }

    private void initView() {
        findViewById(R.id.authmoney_btn_wechat).setOnClickListener(this);
        findViewById(R.id.authmoney_btn_alipay).setOnClickListener(this);
        findViewById(R.id.authmoney_btn_submit).setOnClickListener(this);
        if (mPayModel instanceof DetailsPayModel) {
            ((TextView) findViewById(R.id.authmoney_tv_title)).setText("此次支付可获取到该用户的联系方式");
            ((TextView) findViewById(R.id.authmoney_tv_money)).setText(mPayModel.payPrice + "元");
        } else if (isCompanyAuth) {
            setActivityTitle("企业认证年费");
            ((TextView) findViewById(R.id.authmoney_tv_title)).setText("你需要缴纳的企业认证年费");
            ((TextView) findViewById(R.id.authmoney_tv_money)).setText(Configs.COMPANY_AUTH_MONEY + "元");
        } else if (mBuyerItem == null) {
            ((TextView) findViewById(R.id.authmoney_tv_money)).setText(Configs.AUTH_MONEY + "元");
            ((TextView) findViewById(R.id.authmoney_tv_title)).setText("你需要缴纳的认证年费");
        } else {
            setActivityTitle("提交订单");
            ((TextView) findViewById(R.id.authmoney_tv_money)).setText(mBuyerItem.total + "元");
            ((TextView) findViewById(R.id.authmoney_tv_title)).setText("此次交易您需要支付");
            ((TextView) findViewById(R.id.right_btn_name)).setText("取消订单");
            findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
            showLoadLayout();
            getPayInfo();
        }
        mCancelOrderWindow = new AlertWindow(this, false);
        mCancelOrderWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    HashMap<String, Object> orderParams = new HashMap<>();
                    orderParams.put("token", mUserInfo.token);
                    orderParams.put("id", mBuyerItem.id);
                    mLoadWindow.show(R.string.waiting);
                    HttpUtils.doPost(TaskType.TASK_TYPE_BUYER_CANCEL_ORDER, orderParams, MainAuthenticationMoneyActivity.this);
                }
            }
        });
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.authmoney_btn_wechat:
                switchPayStyle(true);
                break;
            case R.id.authmoney_btn_alipay:
                switchPayStyle(false);
                break;
            case R.id.authmoney_btn_submit:
                if (isCompanyAuth) {
                    doGetCompanyPayInfo();
                } else if (mPayModel instanceof DetailsPayModel) {
                    doGetUserInfoOrder();
                } else if (mBuyerItem == null) {
                    doGetOrderInfo();
                } else {
                    doGetOrderPayInfo();
                }
                break;
            case R.id.right_btn_submit:
                mCancelOrderWindow.show("", "确认取消订单？", "取消", "确定");
                break;
        }
    }

    private void doGetUserInfoOrder() {
        HashMap<String, Object> orderParams = new HashMap<>();
        orderParams.put("token", mUserInfo.token);
        orderParams.put("userid", ((DetailsPayModel) mPayModel).userID);
        mLoadWindow.show(R.string.waiting);
        HttpUtils.doPost(isWecahtPay ? TaskType.TASK_TYPE_USERINFO_PAY_BY_WECHAT : TaskType.TASK_TYPE_USERINFO_PAY_BY_ALIPAY, orderParams, this);
    }

    private void doGetCompanyPayInfo() {
        HashMap<String, Object> orderParams = new HashMap<>();
        orderParams.put("token", mUserInfo.token);
        mLoadWindow.show(R.string.waiting);
        HttpUtils.doPost(isWecahtPay ? TaskType.TASK_TYPE_COMPANY_PAY_BY_WECHAT : TaskType.TASK_TYPE_COMPANY_PAY_BY_ALIPAY, orderParams, this);
    }

    private void getPayInfo() {
        HashMap<String, Object> orderParams = new HashMap<>();
        orderParams.put("token", mUserInfo.token);
        orderParams.put("id", mBuyerItem.id);
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_ORDER_PAYINFO, orderParams, this);
    }

    private void doGetOrderPayInfo() {
        if (isCancel) {
            mLoadWindow.show(R.string.get_order_info);
            getPayInfo();
            return;
        }
        mLoadWindow.show(R.string.waiting);
        if (isWecahtPay) {
            HashMap<String, Object> orderParams = new HashMap<>();
            orderParams.put("token", mUserInfo.token);
            orderParams.put("flowingorderno", mOrderObjectInfo.optString("flowingorderno"));
            HttpUtils.doPost(TaskType.TASK_TYPE_QRY_ORDER_PAYINFO_WHIT_WECHAT, orderParams, this);
        } else {
            doAlipay(mOrderObjectInfo);
        }
    }

    private void doGetOrderInfo() {
        mLoadWindow.show(R.string.waiting);
        HashMap<String, Object> orderParams = new HashMap<>();
        orderParams.put("token", mUserInfo.token);
        HttpUtils.doPost(isWecahtPay ? TaskType.TASK_TYPE_QRY_PAY_ORDER_WECHAT : TaskType.TASK_TYPE_QRY_PAY_ORDER_ALIPAY, orderParams, this);
    }


    private void switchPayStyle(boolean isWechat) {
        isWecahtPay = isWechat;
        ((CheckBox) findViewById(R.id.authmoney_cb_wechat)).setChecked(isWecahtPay);
        ((CheckBox) findViewById(R.id.authmoney_cb_alipay)).setChecked(!isWecahtPay);
    }

    private void doWXPay(JSONObject wxObject) {
        mLoadWindow.showMessage("正在支付...");
        PayReq request = new PayReq();
        request.appId = wxObject.optString("appid");
        request.partnerId = wxObject.optString("partnerid");
        request.prepayId = wxObject.optString("prepayid");
        request.packageValue = wxObject.optString("package");
        request.nonceStr = wxObject.optString("noncestr");
        request.timeStamp = wxObject.optString("timestamp");
        request.sign = wxObject.optString("sign");
        mWXApi.sendReq(request);
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        switch (event.getEventType()) {
            case EVENT_TYPE_WECHAT_PAY_RESULT:
                mLoadWindow.cancel();
                BaseResp baseResp = (BaseResp) event.getData();
                if (baseResp.errCode == -2) {
                    isCancel = true;
                    showMessage("取消支付");
                } else if (baseResp.errCode == -1) {
                    showMessage("支付失败，请稍后重试");
                } else {
                    showMessage("支付成功");
                    if (isCompanyAuth || (mPayModel == null && mBuyerItem == null)) {
                        mUserInfo.isbzj = 1;
                    }
                    SPUtils.saveUserInfo(mUserInfo);
                    EventUtils.doPostEvent(EventType.EVENT_TYPE_PAY_SUCCESS);
                }
                break;
            case EVENT_TYPE_PAY_SUCCESS:
                if (mPayModel != null && mPayModel.getType() == 1) {
                    startActivity(new Intent(this, MainCompanyResumeActivity.class));
                }
                setResult(Activity.RESULT_OK);
                finish();
                break;
            default:
                break;
        }
    }

    private void doAlipay(final JSONObject aliObject) {
        String backUrl = getCallbackURL();
        Map<String, String> params = OrderInfoUtils.buildOrderParamMap(Configs.ALIPAY_APPID, aliObject.optString("flowingorderno"), aliObject.optString("title"), aliObject.optString("price"), true, backUrl);
        String orderParam = OrderInfoUtils.buildOrderParam(params);
        String sign = OrderInfoUtils.getSign(params, Configs.ALIPAY_PRIVATE_RSA, true);
        final String orderInfo = orderParam + "&" + sign;
        L.d("doAlipay:OrderInfo => " + orderInfo);
        final Handler mHandler = new Handler(getMainLooper()) {
            public void handleMessage(Message msg) {
                mLoadWindow.cancel();
                Map<String, String> result = (Map<String, String>) msg.obj;
                L.d("doAlipay::" + GsonUtil.toJson(result));
                switch (result.get("resultStatus")) {
                    case "9000":
                        if (isCompanyAuth || (mPayModel == null && mBuyerItem == null)) {
                            mUserInfo.isbzj = 1;
                        }
                        SPUtils.saveUserInfo(mUserInfo);
                        EventUtils.doPostEvent(EventType.EVENT_TYPE_PAY_SUCCESS);
                        break;
                    case "8000":
                        showMessage("支付结果确认中");
                        break;
                    case "4000":
                        showMessage("订单支付失败");
                        break;
                    case "5000":
                        showMessage("订单重复请求");
                        break;
                    case "6001":
                        isCancel = true;
                        showMessage("取消支付");
                        break;
                    case "6002":
                        showMessage("网络连接出错，请稍后重试");
                        break;
                    default:
                        showMessage("未知错误");
                        break;

                }
            }
        };
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(MainAuthenticationMoneyActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, false);
                Message msg = new Message();
                msg.what = 100;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            mLoadWindow.cancel();
            if (result instanceof BaseThrowable) {
                if (((BaseThrowable) result).getErrorCode() == 201) {
                    showMessage("已缴纳保证金，无需重复缴纳");
                } else if (((BaseThrowable) result).getErrorCode() == 404) {
                    showMessage("参数传递有误");
                }
            } else {
                showMessage(((Throwable) result).getMessage());
            }
            return;
        }
        switch (type) {
            case TASK_TYPE_QRY_PAY_ORDER_WECHAT:
            case TASK_TYPE_QRY_PAY_ORDER_ALIPAY:
            case TASK_TYPE_COMPANY_PAY_BY_ALIPAY:
            case TASK_TYPE_QRY_ORDER_PAYINFO_WHIT_WECHAT:
            case TASK_TYPE_COMPANY_PAY_BY_WECHAT:
            case TASK_TYPE_USERINFO_PAY_BY_WECHAT:
            case TASK_TYPE_USERINFO_PAY_BY_ALIPAY:
                if (((JSONObject) result).has("data")) {
                    if (isWecahtPay) {
                        doWXPay(((JSONObject) result).optJSONObject("data"));
                    } else {
                        doAlipay(((JSONObject) result).optJSONObject("data"));
                    }
                } else {
                    mLoadWindow.cancel();
                    showMessage("支付订单获取失败，请稍后重试");
                }
                break;
            case TASK_TYPE_QRY_ORDER_PAYINFO:
                if (((JSONObject) result).has("data")) {
                    mOrderObjectInfo = ((JSONObject) result).optJSONObject("data");
                    if (mOrderObjectInfo == null) {
                        showMessage("支付订单获取失败，请稍后重试");
                        return;
                    }
                    mLoadWindow.cancel();
                    if (isCancel) {
                        isCancel = false;
                        doGetOrderPayInfo();
                    }
                    closeLoadLayout();
                } else {
                    showMessage("支付订单获取失败，请稍后重试");
                }
                break;
            case TASK_TYPE_BUYER_CANCEL_ORDER:
                setResult(Activity.RESULT_OK);
                showMessage("订单取消成功");
                onBackPressed();
                break;
        }
    }

    public String getCallbackURL() {
        String callBack;
        if (mPayModel instanceof DetailsPayModel) {
            callBack = "/lookphonepaycallback/zhifubaocallback.spr";
        } else if (isCompanyAuth) {
            callBack = "/companyrzjcallback/zhifubaocallback.spr";
        } else if (mBuyerItem != null) {
            callBack = "/paycallback/zhifubaocallback.spr";
        } else {
            callBack = "/rzjcallback/zhifubaocallback.spr";
        }
        return Configs.ServerUrl + callBack;
    }
}
