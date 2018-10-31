package com.yingshixiezuovip.yingshi.custom;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;
import com.yingshixiezuovip.yingshi.model.ShareModel;

/**
 * Created by Resmic on 2017/5/9.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class ShareWindow extends BasePopupWindow {
    private ShareModel.ShareItem mShareItem;
    private ShareAction mShareAction;

    public ShareWindow(Context mContext) {
        super(mContext);
        mShareAction = new ShareAction((Activity) mContext);
        initView();
    }

    private void initView() {
        findViewById(R.id.share_btn_wechat).setOnClickListener(this);
        findViewById(R.id.share_btn_moments).setOnClickListener(this);
        findViewById(R.id.share_btn_weibo).setOnClickListener(this);
        findViewById(R.id.share_btn_copy).setOnClickListener(this);
        findViewById(R.id.share_btn_cancel).setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.share_btn_wechat:
                mShareAction.setPlatform(SHARE_MEDIA.WEIXIN).share();
                break;
            case R.id.share_btn_moments:
                mShareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).share();
                break;
            case R.id.share_btn_weibo:
                mShareAction.setPlatform(SHARE_MEDIA.SINA).share();
                break;
            case R.id.share_btn_copy:
                ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("shareUrl", mShareItem.url);
                cmb.setPrimaryClip(clipData);
                showMessage("复制成功");
                break;
            case R.id.share_btn_cancel:
                break;
        }
        cancel();
    }

    public void show(ShareModel.ShareItem data, UMShareListener listener) {
        this.mShareItem = data;
        UMWeb web = new UMWeb(data.url);
        web.setTitle(data.title);//标题
        if (!TextUtils.isEmpty(data.photo) && data.photo.startsWith("http")) {
            web.setThumb(new UMImage(mContext, data.photo));
        } else {
            web.setThumb(new UMImage(mContext, R.mipmap.ic_launcher));
        }
        web.setDescription(TextUtils.isEmpty(data.content) ? data.title : data.content);//描述
        mShareAction.withMedia(web);
        mShareAction.setCallback(listener);
        show();
    }


    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.share_window_layout, null);
    }
}
