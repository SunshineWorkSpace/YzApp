package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.quote.media.MediaItem;
import com.yingshixiezuovip.yingshi.quote.media.MediaOptions;
import com.yingshixiezuovip.yingshi.quote.media.activities.MediaPickerActivity;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import java.io.File;
import java.util.List;

/**
 * 类名称:HomeShopPublishDetailEndActivity
 * 类描述:第三步
 * 创建时间: 2018-11-06-16:24
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class HomeShopPublishDetailEndActivity extends BaseActivity {
    private ImageView iv_video;
    public static final int REQUEST_MEDIA = 0x1003;
    private List<MediaItem> mMediaSelectedList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_detail_end);
        setActivityTitle("发布页(3/3)");

        ((TextView) findViewById(R.id.right_btn_name)).setText("下一步");
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        iv_video=(ImageView) findViewById(R.id.iv_video);
        iv_video.setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.right_btn_submit:
                break;
            case R.id.iv_video:
                MediaPickerActivity.open(this, REQUEST_MEDIA, new MediaOptions.Builder().selectVideo().build());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_MEDIA) {
            mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
            if (mMediaSelectedList != null) {
                for (MediaItem mediaItem : mMediaSelectedList) {
//                    PictureManager.displayImage(mediaItem.getUriOrigin()+"",iv_video);
                    iv_video.setBackground(null);
                    Matrix matrix = new Matrix();
                    matrix.setScale(1f, 1f);
                   Bitmap bitmap = Bitmap.createBitmap( getVideoThumbnail( getPath(mediaItem.getUriOrigin().toString())+""), 0, 0,  1000, 1000, matrix, false);
                        iv_video.setImageBitmap(bitmap );

                }
            } else {
            }
        }
    }
    public File getPath(String uris){
        Uri uri = Uri.parse(uris);


        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor actualimagecursor = this.managedQuery(uri,proj,null,null,null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();


        String img_path = actualimagecursor.getString(actual_image_column_index);
        File file = new File(img_path);
        return  file;
//        Uri fileUri = Uri.fromFile(file);
    }
    // 获取视频缩略图
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap b=null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            b=retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();

        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return b;
    }
}
