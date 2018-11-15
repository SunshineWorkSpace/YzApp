package com.yingshixiezuovip.yingshi;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.Qrutils;

/**
 * 类名称:ShopDetailShareActivity
 * 类描述:
 * 创建时间: 2018-11-15-10:45
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class ShopDetailShareActivity extends Activity implements View.OnClickListener {
    private TextView tv_title,tv_content,tv_price,tv_au;
    private ImageView iv_detail,iv_app;
    private LinearLayout lin_save, lin_weixin, lin_friend, lin_copy;
    private LinearLayout rel_share;
    Bitmap bitmap;
    private FrameLayout fl_no_data;
    RoundedImageView iv_au;
    String url="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shop_share);
        fl_no_data=(FrameLayout)findViewById(R.id.fl_no_data);
        fl_no_data.setOnClickListener(this);
        rel_share=(LinearLayout) findViewById(R.id.rel_share);
        lin_save = (LinearLayout) findViewById(R.id.lin_save);
        lin_save.setOnClickListener(this);
        lin_weixin = (LinearLayout) findViewById(R.id.lin_weixin);
        lin_weixin.setOnClickListener(this);
        lin_friend = (LinearLayout) findViewById(R.id.lin_friend);
        lin_friend.setOnClickListener(this);
        lin_copy = (LinearLayout) findViewById(R.id.lin_copy);
        lin_copy.setOnClickListener(this);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_content=(TextView)findViewById(R.id.tv_content);
        tv_price=(TextView)findViewById(R.id.tv_price);
        tv_au=(TextView)findViewById(R.id.tv_au);
        iv_detail=(ImageView)findViewById(R.id.iv_detail);
        iv_au=(RoundedImageView)findViewById(R.id.iv_au);
        iv_app=(ImageView)findViewById(R.id.iv_app);
        tv_title.setText(getIntent().getStringExtra("title"));
        tv_content.setText(getIntent().getStringExtra("content"));
        tv_au.setText(getIntent().getStringExtra("autv"));
        tv_price.setText("¥"+getIntent().getStringExtra("price"));
        Glide.with(this).load(getIntent().getStringExtra("img"))
//                .bitmapTransform(new GlideCircleTransform(MyApplication.getInstance()))
//                .placeholder(R.mipmap.ic_team_default)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error(R.mipmap.ic_team_default)
                .into(iv_detail);
        Glide.with(this).load(getIntent().getStringExtra("au"))
//                .bitmapTransform(new GlideCircleTransform(this))
//                .placeholder(R.mipmap.ic_team_default)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error(R.mipmap.ic_team_default)
                .into(iv_au);
        url=getIntent().getStringExtra("shareurl");
        Bitmap mBitmap = Qrutils.generateBitmap(url, 100, 100);

        Bitmap logoBitmap = Qrutils.getLogo(this);
        Bitmap mBitmaps = Qrutils.addLogo(mBitmap, logoBitmap);
        iv_app.setImageBitmap(mBitmaps);
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
            case R.id.tv_clean:
                finish();
                break;
            case R.id.lin_save:
                Toast.makeText(this,"保存成功",Toast.LENGTH_LONG).show();
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "YzApp", "YzApp");
                finish();
                break;
            case R.id.lin_weixin:
                UMImage images = new UMImage(this, bitmap);//bitmap文件
                ShareContent shareContents = new ShareContent();
                shareContents.mFollow = ("coinMerit");
                shareContents.mText = ("test1");
                new ShareAction(this).setShareContent(shareContents).setPlatform(SHARE_MEDIA.WEIXIN).withMedia(images).setCallback(shareListener).share();
                break;
            case R.id.lin_friend:
                UMImage image = new UMImage(this, bitmap);//bitmap文件
                ShareContent shareContent = new ShareContent();
                shareContent.mFollow = ("test2");
                shareContent.mText = ("test1");
                new ShareAction(this).setShareContent(shareContent).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).withMedia(image).setCallback(shareListener).share();
                break;
            case R.id.lin_copy:
                //举报
        /*        Toast.makeText(this,"保存成功",Toast.LENGTH_LONG).show();
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
// 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", Constant.URL_NEWS_SHARE+getIntent().getExtras().getString("id"));
// 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                finish();*/
                break;
        }
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
