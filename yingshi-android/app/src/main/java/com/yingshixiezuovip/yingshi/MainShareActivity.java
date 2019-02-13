package com.yingshixiezuovip.yingshi;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yingshixiezuovip.yingshi.model.ShareModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.Qrutils;

/**
 * 类名称:MainShareActivity
 * 类描述:
 * 创建时间: 2019-01-24-15:15
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class MainShareActivity extends Activity implements View.OnClickListener {
    private TextView tv_title, tv_content, tv_price, tv_au, tv_address;
    private ImageView iv_detail, iv_app,iv_video;
    private LinearLayout lin_save;
    private LinearLayout rel_share;
    Bitmap bitmap;
    private FrameLayout fl_no_data;
    RoundedImageView iv_au;
    String url = "";
    private ShareAction mShareAction;
    private ShareModel.ShareItem mShareItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_share);
        findViewById(R.id.share_btn_wechat).setOnClickListener(this);
        findViewById(R.id.share_btn_moments).setOnClickListener(this);
        findViewById(R.id.share_btn_weibo).setOnClickListener(this);
        findViewById(R.id.share_btn_copy).setOnClickListener(this);
        findViewById(R.id.share_btn_cancel).setOnClickListener(this);
        iv_video=(ImageView)findViewById(R.id.iv_video);
        fl_no_data = (FrameLayout) findViewById(R.id.fl_no_data);
        fl_no_data.setOnClickListener(this);
        rel_share = (LinearLayout) findViewById(R.id.rel_share);
        lin_save = (LinearLayout) findViewById(R.id.lin_save);
        lin_save.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_au = (TextView) findViewById(R.id.tv_au);
        iv_detail = (ImageView) findViewById(R.id.iv_detail);
        iv_au = (RoundedImageView) findViewById(R.id.iv_au);
        tv_address = (TextView) findViewById(R.id.tv_address);
        iv_app = (ImageView) findViewById(R.id.iv_app);
        mShareItem=(ShareModel.ShareItem)getIntent().getSerializableExtra("data");
        tv_title.setText(mShareItem.title);
        tv_content.setText(mShareItem.content);
        tv_address.setText(mShareItem.position);
        tv_au.setText(mShareItem.name);
        Glide.with(this).load(mShareItem.head)
//                .bitmapTransform(new GlideCircleTransform(MyApplication.getInstance()))
//                .placeholder(R.mipmap.ic_team_default)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error(R.mipmap.ic_team_default)
                .into(iv_video);
        Glide.with(this).load(mShareItem.photo)
//                .bitmapTransform(new GlideCircleTransform(MyApplication.getInstance()))
//                .placeholder(R.mipmap.ic_team_default)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error(R.mipmap.ic_team_default)
                .into(iv_detail);
        url = mShareItem.url;
        Bitmap mBitmap = Qrutils.generateBitmap(url, 150, 150);

        Bitmap logoBitmap = Qrutils.getLogoNew(this);
        Bitmap mBitmaps = Qrutils.addLogo(mBitmap, logoBitmap);
        iv_app.setImageBitmap(mBitmaps);
        mShareAction = new ShareAction((Activity) this);
    }

    @Override
    public void onClick(View v) {
        bitmap = Bitmap.createBitmap(
                rel_share.getWidth(),
                rel_share.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        rel_share.draw(c);
        switch (v.getId()) {
            case R.id.fl_no_data:
                finish();
                break;
            case R.id.lin_save:
                Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "YzApp", "YzApp");
                finish();
                break;
            case R.id.share_btn_wechat:
                mShareAction.setPlatform(SHARE_MEDIA.WEIXIN).share();
                show();
                break;
            case R.id.share_btn_moments:
                mShareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).share();
                show();
                break;
            case R.id.share_btn_weibo:
                mShareAction.setPlatform(SHARE_MEDIA.SINA).share();
                show();
                break;
            case R.id.share_btn_copy:
                    ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("shareUrl", mShareItem.url);
                    cmb.setPrimaryClip(clipData);
                    Toast.makeText(this,"复制成功",Toast.LENGTH_SHORT).show();
                break;
            case R.id.share_btn_cancel:
                finish();
                break;
        }
    }
    private void show(){
        UMWeb web = new UMWeb(mShareItem.url);
        web.setTitle(mShareItem.title);//标题
        if (!TextUtils.isEmpty(mShareItem.photo) && mShareItem.photo.startsWith("http")) {
            web.setThumb(new UMImage(this, mShareItem.photo));
        } else {
            web.setThumb(new UMImage(this, R.mipmap.ic_launcher));
        }
        web.setDescription(TextUtils.isEmpty(mShareItem.content) ? mShareItem.title : mShareItem.content);//描述
        mShareAction.withMedia(web);
        mShareAction.setCallback(shareListener);
    }
    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {

        }
    };
}
