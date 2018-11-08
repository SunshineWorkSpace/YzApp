package com.yingshixiezuovip.yingshi.datautils;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BaseResp;
import com.yingshixiezuovip.yingshi.base.BaseThrowable;
import com.yingshixiezuovip.yingshi.base.YingApplication;
import com.yingshixiezuovip.yingshi.minterface.ProgressCallback;
import com.yingshixiezuovip.yingshi.model.BaseEaseUser;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.SPUtils;
import com.yingshixiezuovip.yingshi.utils.WakelockUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;


/**
 * Created by Resmic on 2016/11/14.
 */

public class HttpUtils {
    public static void doPost(TaskType mTaskType, HashMap<String, Object> mTaskParams, TaskListener mListener, boolean isJson) {
        new HttpTask(false, mTaskType, mTaskParams, mListener, isJson).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void doPost(TaskType mTaskType, HashMap<String, Object> mTaskParams, TaskListener mListener) {
        new HttpTask(false, mTaskType, mTaskParams, mListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void doPost(TaskInfo info) {
        new HttpTask(false, info.getTaskType(), info.getTaskParams(), info.getTaskListener()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void doUpload(TaskInfo info, ProgressCallback callback) {
        new HttpTask(true, info.getTaskType(), info.getTaskParams(), info.getTaskListener(), callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static boolean isJson(TaskType taskType) {
        return taskType == TaskType.TASK_TYPE_RELEASE ||
                taskType == TaskType.TASK_TYPE_MINE_BACKGROUND_UPLOAD ||
                taskType == TaskType.TASK_TYPE_MINE_HEAD_UPLOAD ||
                taskType == TaskType.TASK_TYPE_MINE_PHOTO ||
                taskType == TaskType.TASK_TYPE_UPDATE_USER_INFO ||
                taskType == TaskType.TASK_TYPE_SUBMIT_AUTHENTICATION ||
                taskType == TaskType.TASK_TYPE_COMPANY_INFO_UPLOAD ||
                taskType == TaskType.TASK_TYPE_UPLOAD_COVER ||
                taskType == TaskType.TASK_TYPE_UPLOAD_RESUME ||
                taskType == TaskType.TASK_TYPE_UPLOAD_BYZS ||
                taskType == TaskType.TASK_TYPE_STUDENT_AUTH;

    }

    static class HttpTask extends AsyncTask<Void, Void, Object> {
        private TaskType mTaskType;
        private HashMap<String, Object> mTaskParams;
        private TaskListener mListener;
        private Context mContext;
        private String mMethodName;
        private boolean isUpload = false;
        private OkHttpClient mHttpClient;
        private Call mTaskCall;
        private ProgressCallback mUploadCallback;
        private boolean isJson;

        public HttpTask(boolean isUpload, TaskType mTaskType, HashMap<String, Object> mTaskParams, TaskListener mListener) {
            this(isUpload, mTaskType, mTaskParams, mListener, isJson(mTaskType));
        }

        public HttpTask(boolean isUpload, TaskType mTaskType, HashMap<String, Object> mTaskParams, TaskListener mListener, boolean isJson) {
            this.isUpload = isUpload;
            this.mTaskType = mTaskType;
            this.mTaskParams = mTaskParams;
            this.mListener = mListener;
            this.mContext = YingApplication.getInstance();
            this.isJson = isJson;
        }

        public HttpTask(boolean isUpload, TaskType mTaskType, HashMap<String, Object> mTaskParams, TaskListener mListener, ProgressCallback callback) {
            this.mTaskType = mTaskType;
            this.mTaskParams = mTaskParams;
            this.mListener = mListener;
            this.mContext = YingApplication.getInstance();
            this.isUpload = isUpload;
            this.mUploadCallback = callback;
        }


        private void initHttpClient() {
            mHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(Configs.TimeOut, TimeUnit.SECONDS)
                    .readTimeout(Configs.TimeOut, TimeUnit.SECONDS)
                    .writeTimeout(Configs.TimeOut, TimeUnit.SECONDS)
                    .build();
        }

        @Override
        protected Object doInBackground(Void... params) {
            WakelockUtils.acquireWakeLock();
            if (!CommUtils.isNetworkAvailable()) {
                return new Error(mContext.getString(R.string.network_error));
            }
            Object mResultObject;
            initHttpClient();
            String urlStr = getMethodUrl(mTaskType);

            if (!isUpload) {
                mTaskCall = getStringByURL(isJson, urlStr, mTaskParams);
            } else {
                mTaskCall = doUploadFile(urlStr, mTaskParams);
            }
            try {
                mResultObject = doParseRespose(mTaskCall.execute());
            } catch (IOException e) {
                L.d("Http异常，" + ThrowableUtils.getThrowableDetailsMessage(e));
                return new Error(mContext.getString(R.string.text_server_connect_fail) + " , -1");
            }
            if (mResultObject.toString().contains("Failed to")) {
                return new Error(mContext.getString(R.string.text_server_connect_fail) + " , -4");
            }
            return mResultObject;
        }

        private Call doUploadFile(String urlStr, HashMap<String, Object> mTaskParams) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            for (String key : mTaskParams.keySet()) {
                Object object = mTaskParams.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                }
            }
            RequestBody uploadBody = builder.build();
            Request request = new Request.Builder().url(urlStr
            ).post(new ProgressRequestBody(uploadBody, mUploadCallback)).build();
            return mHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        }

        private Call getStringByURL(boolean isJson, String strUrl, HashMap<String, Object> taskParams) {
            Request.Builder builder = new Request.Builder().url(strUrl).cacheControl(CacheControl.FORCE_NETWORK);
            L.d("RequestUrl:" + strUrl + " , " + (isJson ? "JSON" : "表单"));
            String params = GsonUtil.toJson(taskParams);
            RequestBody requestBody;
            L.d("TaskParams:" + params);
            if (isJson) {
                requestBody = RequestBody.create(MediaType.parse("application/json"), params);
            } else {
                requestBody = getRequestBody(taskParams);
            }
            builder.post(requestBody);
            Request request = builder.build();
            L.d("TaskTimeout：" + Configs.TimeOut * 1000);
            return mHttpClient.newCall(request);
        }

        private Object doParseRespose(Response response) {
            InputStream inputStream = null;
            try {
                if (response.code() == 200) {
                    inputStream = response.body().byteStream();
                    String resultStr = StringUtils.istream2Str(inputStream);
                    L.d("TaskResult:" + resultStr);

                    BaseResp baseResp = GsonUtil.fromJson(resultStr, BaseResp.class);
                    if (baseResp.result.code != 200 && mTaskType != TaskType.TASK_TYPE_RELEASE_PASS) {
                        if (baseResp.result.code == 500) {
                            baseResp.result.detail = mContext.getString(R.string.text_server_connect_fail) + " , -2";
                        } else if (baseResp.result.code == 400) {
                            baseResp.result.detail = "参数传递有误";
                        }
                        return new BaseThrowable(baseResp.result.detail, baseResp.result.code);
                    } else {
                        return new JSONObject(resultStr);
                    }
                } else {
                    return new Error(mContext.getString(R.string.text_server_connect_fail) + " , -3");
                }
            } catch (Exception e) {
                return new Error(e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Object object) {
            WakelockUtils.releaseWakeLock();
            if (mTaskType == TaskType.TASK_TYPE_QRY_USERINFO_BY_TOKEN && !(object instanceof Throwable)) {
                if (((JSONObject) object).has("data")) {
                    JSONObject dataObject = ((JSONObject) object).optJSONObject("data");
                    SPUtils.saveBaseEaseUser(mContext, new BaseEaseUser((String) mTaskParams.get("token"), dataObject.optString("head"), dataObject.optString("nickname")));
                }
            }
            if (mListener != null) {
                mListener.taskFinished(mTaskType, object, false);
            }
            mTaskParams = null;
        }


        private String getMethodUrl(TaskType type) {
            String methodName = null;
            switch (type) {
                case TASK_TYPE_STARTUP:
                    methodName = "/startpage.spr";
                    break;
                case TASK_TYPE_SendSMS:
                    methodName = "/send.spr";
                    break;
                case TASK_TYPE_REGIST:
                    methodName = "/register.spr";
                    break;
                case TASK_TYPE_LOGIN:
                    methodName = "/login.spr";
                    break;
                case TASK_TYPE_HOME_TYPE:
                    methodName = "/types.spr";
                    break;
                case TASK_TYPE_HOME_LIST:
                    methodName = "/starlist.spr";
                    break;
                case TASK_TYPE_DO_COMMENT:
                    methodName = "/zan.spr";
                    break;
                case TASK_TYPE_COMMENT_LIST:
                    methodName = "/zanlist.spr";
                    break;
                case TASK_TYPE_HOME_CLEAR_FOLLOW:
                    methodName = "/cancelfollow.spr";
                    break;
                case TASK_TYPE_HOME_FOLLOW:
                    methodName = "/follow.spr";
                    break;
                case TASK_TYPE_LOGIN_SINA:
                    methodName = "/loginforweibo.spr";
                    break;
                case TASK_TYPE_LOGIN_WECHAT:
                    methodName = "/loginforweixin.spr";
                    break;
                case TASK_TYPE_USER_INFO:
                    methodName = "/user/info.spr";
                    break;
                case TASK_TYPE_RESET_SendSMS:
                    methodName = "/retsetSend.spr";
                    break;
                case TASK_TYPE_RESET_PASSWORD:
                    methodName = "/retsetpwd.spr";
                    break;
                case TASK_TYPE_LIST_ITEM_DETAILS:
                    methodName = "/v1/detailsofWorks.spr";
                    break;
                case TASK_TYPE_SUBSCRIBE_STAR:
                    methodName = "/subscribeStar.spr";
                    break;
                case TASK_TYPE_SUBSCRIBE_STAR_QRY:
                    methodName = "/getSubscribeStartime.spr";
                    break;
                case TASK_TYPE_QRY_OTHER_USERINFO:
                    methodName = "/user/oinfo.spr";
                    break;
                case TASK_TYPE_QRY_OTHER_WORKS:
                    methodName = "/user/ozuoping.spr";
                    break;
                case TASK_TYPE_QRY_OTHER_FOLLOW_LIST:
                    methodName = "/user/ofollowlist.spr";
                    break;
                case TASK_TYPE_QRY_SHARE_CONTENT:
                    methodName = "/ishare.spr";
                    break;
                case TASK_TYPE_QRY_HEART_VIDEO:
                    methodName = "/videolist.spr";
                    break;
                case TASK_TYPE_MSG_SELLER_ORDER:
                    methodName = "/msg/sellerOrderlist.spr";
                    break;
                case TASK_TYPE_MSG_BUYER_ORDER:
                    methodName = "/msg/transactionlist.spr";
                    break;
                case TASK_TYPE_MSG_COMMENT_LIST:
                    methodName = "/msg/zanlist.spr";
                    break;
                case TASK_TYPE_MSG_FANS_LIST:
                    methodName = "/user/fanslist.spr";
                    break;
                case TASK_TYPE_MINE_WORK_LIST:
                    methodName = "/user/workslist.spr";
                    break;
                case TASK_TYPE_MINE_COMMENT_LIST:
                    methodName = "/user/zanlist.spr";
                    break;
                case TASK_TYPE_MINE_FOLLOW_LIST:
                    methodName = "/user/followlist.spr";
                    break;
                case TASK_TYPE_MINE_HEAD_UPLOAD:
                    methodName = "/user/updatehead.spr";
                    break;
                case TASK_TYPE_MINE_BACKGROUND_UPLOAD:
                    methodName = "/user/updatebackground.spr";
                    break;
                case TASK_TYPE_MINE_PHOTO:
                    methodName = "/user/updatephoto.spr";
                    break;
                case TASK_TYPE_RELEASE:
                    methodName = "/v1/uploadworks_V1.spr";
                    break;
                case TASK_TYPE_VIDEO_UPLOAD:
                    methodName = "/uploadvideo.spr";
                    break;
                case TASK_TYPE_SEARCH_HOT:
                    methodName = "/search.spr";
                    break;
                case TASK_TYPE_SEARCH_RECOMMEND:
                    methodName = "/hotrecommend.spr";
                    break;
                case TASK_TYPE_DELETE_WORKS:
                    methodName = "/delstar.spr";
                    break;
                case TASK_TYPE_UPDATE_WORKS_PRICE:
                    methodName = "/updateStarPrice.spr";
                    break;
                case TASK_TYPE_UPDATE_USER_PRICE:
                    methodName = "/user/updateprice.spr";
                    break;
                case TASK_TYPE_UPDATE_USER_INFO:
                    methodName = "/user/update.spr";
                    break;
                case TASK_TYPE_QRY_CITY_INFO:
                    methodName = "/city.spr";
                    break;
                case TASK_TYPE_UPDATE_INVITE_CODE:
                    methodName = "/user/writeInvite.spr";
                    break;
                case TASK_TYPE_SUBMIT_AUTHENTICATION:
                    methodName = "/user/authsub.spr";
                    break;
                case TASK_TYPE_QRY_PAY_ORDER_WECHAT:
                    methodName = "/user/identificationforwxpay.spr";
                    break;
                case TASK_TYPE_QRY_PAY_ORDER_ALIPAY:
                    methodName = "/user/identificationforalipay.spr";
                    break;
                case TASK_TYPE_QRY_USERINFO_BY_TOKEN:
                    methodName = "/infoForToken.spr";
                    break;
                case TASK_TYPE_MSG_SELLER_ORDER_DETAILS:
                    methodName = "/msg/orderInfo.spr";
                    break;
                case TASK_TYPE_QRY_ORDER_PAYINFO_WHIT_WECHAT:
                    methodName = "/weixinpay.spr";
                    break;
                case TASK_TYPE_BUYER_CANCEL_ORDER:
                    methodName = "/msg/cancelOrder.spr";
                    break;
                case TASK_TYPE_QRY_ORDER_PAYINFO:
                    methodName = "/msg/payOnOrderList.spr";
                    break;
                case TASK_TYPE_UPDATE_ORDER_DATE:
                    methodName = "/msg/updateOrderData.spr";
                    break;
                case TASK_TYPE_UPDATE_ORDER_PRICE:
                    methodName = "/msg/updateOrderTotal.spr";
                    break;
                case TASK_TYPE_MAKE_SURE_ORDER:
                    methodName = "/msg/makesureOrder.spr";
                    break;
                case TASK_TYPE_ORDER_COMPLETE:
                    methodName = "/msg/completeOrder.spr";
                    break;
                case TASK_TYPE_QRY_COMMENT_LIST:
                    methodName = "/reviewList.spr";
                    break;
                case TASK_TYPE_QRY_COMMENT_DETAILS:
                    methodName = "/replyList.spr";
                    break;
                case TASK_TYPE_DO_REVIEW:
                    methodName = "/review.spr";
                    break;
                case TASK_TYPE_DO_REPLY:
                    methodName = "/replyreview.spr";
                    break;
                case TASK_TYPE_RELEASE_PASS:
                    methodName = "/releasePass.spr";
                    break;
                case TASK_TYPE_REVIEW_ZAN:
                    methodName = "/reviewzan.spr";
                    break;
                case TASK_TYPE_REVIEW_CANCELZAN:
                    methodName = "/reviewcancelzan.spr";
                    break;
                case TASK_TYPE_BIND_PHONE:
                    methodName = "/user/bindphone.spr";
                    break;
                case TASK_TYPE_COMPANY_CHECK_VERIFICODE:
                    methodName = "/v1/checkcompanycode.spr";
                    break;
                case TASK_TYPE_COMPANY_INFO_UPLOAD:
                    methodName = "/v1/uploadcompanyinfo.spr";
                    break;
                case TASK_TYPE_COMPANY_PAY_BY_ALIPAY:
                    methodName = "/v1/companyauthbondforalipay.spr";
                    break;
                case TASK_TYPE_COMPANY_PAY_BY_WECHAT:
                    methodName = "/v1/companyauthbondforwxpay.spr";
                    break;
                case TASK_TYPE_USERINFO_PAY_BY_ALIPAY:
                    methodName = "/v1/lookphoneforalipay.spr";
                    break;
                case TASK_TYPE_USERINFO_PAY_BY_WECHAT:
                    methodName = "/v1/lookphoneforweixin.spr";
                    break;
                case TASK_TYPE_SUBMIT_FEEDBACK:
                    methodName = "/v1/sendcustomermsg.spr";
                    break;
                case TASK_TYPE_QRY_COMPANY_INFO:
                    methodName = "/v1/user/getcompanyinfo.spr";
                    break;
                case TASK_TYPE_QRY_COMPANY_STATUS:
                    methodName = "/v1/user/infoupdatelist.spr";
                    break;
                case TASK_TYPE_UPDADE_COMPANY_INFO:
                    methodName = "/v1/user/updatecompanyinfo.spr";
                    break;
                case TASK_TYPE_QRY_SYS_COVER:
                    methodName = "/v1/user/zpjfmlist.spr";
                    break;
                case TASK_TYPE_UPLOAD_COVER:
                    methodName = "/v1/user/uploadzpjfm.spr";
                    break;
                case TASK_TYPE_UPLOAD_RESUME_VIDEO:
                    methodName = "/v1/user/v1uploadresumevideo.spr";
                    break;
                case TASK_TYPE_UPLOAD_RESUME:
                    methodName = "/v1/user/v2uploadresume.spr";
                    break;
                case TASK_TYPE_UPDATE_RESUME:
                    methodName = "/v1/user/v2updateresume.spr";
                    break;
                case TASK_TYPE_UPLOAD_BYZS:
                    methodName = "/v1/user/uploadbycertificate.spr";
                    break;
                case TASK_TYPE_CUSTOM_URL:
                    methodName = "/v1/user/updatesamplereelsurl.spr";
                    break;
                case TASK_TYPE_QRY_USER_WORKS:
                    methodName = "/v1/v1userzpj.spr";
                    break;
                case TASK_TYPE_QRY_VERSION:
                    methodName = "/sys/version.spr";
                    break;
                case TASK_TYPE_QRY_HOT_SEARCH:
                    methodName = "/v1/hotsearch.spr";
                    break;
                case TASK_TYPE_QRY_HOT_SEARCH_DETAILS:
                    methodName = "/v1/search.spr";
                    break;
                case TASK_TYPE_STUDENT_AUTH:
                    methodName = "/v1/user/studentAuth.spr";
                    break;
                case TASK_TYPE_QRY_STUDENT_INTO:
                    methodName = "/v1/user/getstudentinfo.spr";
                    break;
                case TASK_TYPE_UPDATE_STUDENT_INTO:
                    methodName = "/v1/user/updatestudentinfo.spr";
                    break;
                case TASK_TYPE_QRY_DETAILS_CHNAGE:
                    methodName = "/v1/worksUpdatePage.spr";
                    break;
                case TASK_TYPE_UPDATE_DETAILS_CHNAGE:
                    methodName = "/v1/worksUpdateAll_V1.spr";
                    break;
                case TASK_TYPE_DETAILS_RESUME:
                    methodName = "/v1/user/v1resumedetail.spr";
                    break;
                case TASK_TYPE_UPDATE_USER_INFO_NEW:
                    methodName = "/v1/user/personalAuthReset.spr";
                    break;
                case TASK_TYPE_ISPUBLISH:
                    methodName = "/v2/isuploadindencard.spr";
                    break;
                case TASK_TYPE_OSS_IMG:
                    methodName = "/sys/getOssDirByPhoto.spr";
                    break;
                case TASK_TYPE_OSS_VIDEO:
                    methodName = "/sys/getOssDirByVideo.spr";
                    break;
                case TASK_TYPE_ASIDE_SEND:
                    methodName="/v2/asideSend.spr";
                    break;
                default:
                    break;
            }
            mMethodName = methodName;
            return String.format("%s%s", Configs.ServerUrl, mMethodName);
        }

        private RequestBody getRequestBody(Map<String, Object> params) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (params != null) {
                Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
                String paramKey;
                Object paramValue;
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    paramKey = entry.getKey();
                    paramValue = entry.getValue();
                    formBodyBuilder.add(paramKey, String.valueOf(paramValue));
                }
            }
            return formBodyBuilder.build();

        }

        public static class ProgressRequestBody extends RequestBody {
            private RequestBody requestBody;
            private ProgressCallback mListener;
            private BufferedSink bufferedSink;

            public ProgressRequestBody(RequestBody requestBody, ProgressCallback listener) {
                this.requestBody = requestBody;
                this.mListener = listener;
            }

            @Override
            public long contentLength() throws IOException {
                return requestBody.contentLength();
            }

            @Override
            public MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                if (bufferedSink == null) {
                    bufferedSink = Okio.buffer(sink(sink));
                }
                requestBody.writeTo(bufferedSink);
                bufferedSink.flush();
            }

            private Sink sink(Sink sink) {
                return new ForwardingSink(sink) {
                    long byteWriteed = 0L;
                    long contentBytes = 0L;

                    @Override
                    public void write(Buffer source, long byteCount) throws IOException {
                        super.write(source, byteCount);
                        if (mListener != null) {
                            if (contentBytes == 0L) {
                                contentBytes = contentLength();
                            }
                            byteWriteed += byteCount;
                            mListener.onProgress(1, byteWriteed * 1.0 / contentBytes, "uploading");
                        }
                    }
                };
            }

        }
    }
}
