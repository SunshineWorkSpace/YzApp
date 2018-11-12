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
import com.yingshixiezuovip.yingshi.datautils.Configs;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.OrderInfoUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 商城订单付款
 * Created by yuhua.gou on 2018/11/12.
 */

public class HomeShopOrderPayActivity extends BaseActivity {
    private IWXAPI mWXApi;
    private String flow_trade_no,mChoiceNumber;
    private String totalmoney,name;
    private boolean isWecahtPay = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conprice_submit);
        setActivityTitle("订单付款");
        initView();
    }

    private void initView(){
        findViewById(R.id.authmoney_btn_wechat).setOnClickListener(this);
        findViewById(R.id.authmoney_btn_alipay).setOnClickListener(this);
        findViewById(R.id.authmoney_btn_submit).setOnClickListener(this);

        flow_trade_no=getIntent().getStringExtra("flow_trade_no");
        totalmoney=getIntent().getStringExtra("money");
        name=getIntent().getStringExtra("name");
        mChoiceNumber=getIntent().getStringExtra("num");
        mWXApi = WXAPIFactory.createWXAPI(this, Configs.WECHAT_APPID);
        ((TextView) findViewById(R.id.authmoney_tv_money)).setText(totalmoney + "元");
        ((TextView)findViewById(R.id.authmoney_tv_title)).setText(name+" 共"+mChoiceNumber+"件");
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
                payMoneyDate();
                break;
        }
    }

    private void switchPayStyle(boolean isWechat) {
        isWecahtPay = isWechat;
        ((CheckBox) findViewById(R.id.authmoney_cb_wechat)).setChecked(isWecahtPay);
        ((CheckBox) findViewById(R.id.authmoney_cb_alipay)).setChecked(!isWecahtPay);
    }

    private void payMoneyDate() {
        HashMap<String, Object> orderParams = new HashMap<>();
        orderParams.put("token", mUserInfo.token);
        orderParams.put("flow_trade_no", flow_trade_no);
        orderParams.put("type", isWecahtPay?2:1);
        mLoadWindow.show(R.string.waiting);
        HttpUtils.doPost(TaskType.TASK_TYPE_SHOP_ORDER_PAY, orderParams, this);
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        switch (event.getEventType()) {
            case EVENT_TYPE_WECHAT_PAY_RESULT:
                mLoadWindow.cancel();
                BaseResp baseResp = (BaseResp) event.getData();
                if (baseResp.errCode == -2) {
                    showMessage("取消支付");
                } else if (baseResp.errCode == -1) {
                    showMessage("支付失败，请稍后重试");
                } else {
                    showMessage("支付成功");
                    EventUtils.doPostEvent(EventType.EVENT_TYPE_PAY_SUCCESS);
                }
                break;
            case EVENT_TYPE_PAY_SUCCESS:
                setResult(Activity.RESULT_OK);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            mLoadWindow.cancel();
            if (result instanceof BaseThrowable) {
                if (((BaseThrowable) result).getErrorCode() == 400) {
                    showMessage("参数传递有误");
                }
            } else {
                showMessage(((Throwable) result).getMessage());
            }
            return;
        }
        switch (type) {
            case TASK_TYPE_SHOP_ORDER_PAY:
                System.out.println("支付参数:"+result.toString());
                if (((JSONObject) result).has("data")) {
                    mLoadWindow.cancel();
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
        }
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
                        EventUtils.doPostEvent(EventType.EVENT_TYPE_PAY_SUCCESS);
                        setResult(RESULT_OK);
                        finish();
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
                PayTask alipay = new PayTask(HomeShopOrderPayActivity.this);
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

    public String getCallbackURL() {
        String callBack;
        callBack = "/asideSPaycallback/zhifubaocallback.spr";
        return Configs.ServerUrl + callBack;
    }
}
