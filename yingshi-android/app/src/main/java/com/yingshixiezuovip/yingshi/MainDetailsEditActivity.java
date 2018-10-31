package com.yingshixiezuovip.yingshi;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.adapter.PublishNewAdapter;
import com.yingshixiezuovip.yingshi.adapter.PublishTypeAdater;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.EventType;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.custom.ChoiceVideoWindow;
import com.yingshixiezuovip.yingshi.custom.EditWindow;
import com.yingshixiezuovip.yingshi.custom.FontWindow;
import com.yingshixiezuovip.yingshi.custom.TextInputWindow;
import com.yingshixiezuovip.yingshi.custom.VideoLinkWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskInfo;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.datautils.ThrowableUtils;
import com.yingshixiezuovip.yingshi.minterface.ProgressCallback;
import com.yingshixiezuovip.yingshi.model.DetailsEditModel;
import com.yingshixiezuovip.yingshi.model.FontModel;
import com.yingshixiezuovip.yingshi.model.HomeTypeModel;
import com.yingshixiezuovip.yingshi.model.PublishModel;
import com.yingshixiezuovip.yingshi.model.VideoModel;
import com.yingshixiezuovip.yingshi.publish.MyItemTouchHelperCallback;
import com.yingshixiezuovip.yingshi.quote.media.MediaItem;
import com.yingshixiezuovip.yingshi.quote.media.MediaOptions;
import com.yingshixiezuovip.yingshi.quote.media.activities.MediaPickerActivity;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.EventUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Resmic on 18/1/23.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class MainDetailsEditActivity extends BaseActivity implements PictureManager.OnPictureCallbackListener {
    private int mWorkId;
    private DetailsEditModel editModel;
    public static final int REQUEST_MEDIA = 0x1003;
    private HomeTypeModel mHomeTypeModel;
    private Spinner mSpinner;
    private List<MediaItem> mMediaSelectedList;
    private PictureManager mPictureManager;
    private PublishModel mPublishModel;
    private AlertWindow mExitWindow;
    private EditWindow mEditWindow;
    private boolean isEdit;
    private ChoiceVideoWindow mChoiceVideoWindow;
    private VideoLinkWindow mVideoLinkWindow;
    private FontWindow mFontWindow;
    private String lable = "";

    private PublishNewAdapter newAdapter;
    private View mHeadView;
    private int mPosition;
    private PublishModel.PublishMediaItem mPublishMediaItem;
    private TextInputWindow mTextInputWindow;
    private RecyclerView recyclerView;

    private int videoPosition;
    private HashMap<String, VideoModel> mVideoModels;
    private List<String> mVideoPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_publish, R.string.title_activity_main_publish_edit);

        initView();
        loadData();
    }

    private void initView() {
        mWorkId = getIntent().getIntExtra("works_id", -1);
        if (mWorkId <= 0) {
            showMessage("服务器异常，请稍后重试");
            super.onBackPressed();
            return;
        }

        newAdapter = new PublishNewAdapter(this);
        newAdapter.setOnEditClickListener(onEditClickListener);
        recyclerView = (RecyclerView) findViewById(R.id.publish_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mHeadView = LayoutInflater.from(this).inflate(R.layout.activity_main_pulish_head, null);
        newAdapter.addHeaderView(mHeadView);
        recyclerView.setAdapter(newAdapter);

        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(newAdapter);// create MyItemTouchHelperCallback
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback); // Create ItemTouchHelper and pass with parameter the MyItemTouchHelperCallback
        touchHelper.attachToRecyclerView(recyclerView); // Attach ItemTouchHelper to RecyclerView

        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.right_btn_name)).setText("提交修改");
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        mSpinner = (Spinner) mHeadView.findViewById(R.id.publish_spinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setTextColor(Color.BLACK);    //设置颜色
                tv.setTextSize(16.0f);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mTextInputWindow = new TextInputWindow(this, onTextInputFinishListener);

        mPublishModel = new PublishModel();
        mPictureManager = new PictureManager(this, this);
        mHeadView.findViewById(R.id.publish_iv_cover).setOnClickListener(this);
        findViewById(R.id.publish_btn_album).setOnClickListener(this);
        findViewById(R.id.publish_btn_video).setOnClickListener(this);
        findViewById(R.id.publish_btn_lable).setOnClickListener(this);
        findViewById(R.id.publish_btn_font).setOnClickListener(this);
        findViewById(R.id.publish_btn_share).setOnClickListener(this);

        initWindow();
    }

    private TextInputWindow.OnTextInputFinishListener onTextInputFinishListener = new TextInputWindow.OnTextInputFinishListener() {
        @Override
        public void onTextInputFinish(String text) {
            mPublishMediaItem.desc = text;
            newAdapter.update(mPosition, mPublishMediaItem);
        }
    };

    private PublishNewAdapter.OnEditClickListener onEditClickListener = new PublishNewAdapter.OnEditClickListener() {
        @Override
        public void onEditClick(int position, PublishModel.PublishMediaItem mediaItem, boolean text) {
            L.d("onEditClick = " + position);
            isEdit = true;
            mPosition = position;
            mPublishMediaItem = mediaItem;

            if (text) {
                mTextInputWindow.show(mediaItem.desc);
                return;
            }

            switch (mediaItem.type) {
                case 1:
                    MediaPickerActivity.open(MainDetailsEditActivity.this, REQUEST_MEDIA, new MediaOptions.Builder().selectPhoto().canSelectMultiPhoto(false).build());
                    break;
                case 2:
                    MediaPickerActivity.open(MainDetailsEditActivity.this, REQUEST_MEDIA, new MediaOptions.Builder().selectVideo().build());
                    break;
                case 3:
                    mVideoLinkWindow.show(mediaItem.mediaItem.getUriOrigin().toString());
                    break;
                case 4:
                    mFontWindow.show(mediaItem.fontModel);
                    break;
                default:
                    break;
            }
        }

    };

    private void initWindow() {
        mExitWindow = new AlertWindow(this, false);
        mExitWindow.setOnClickListener(this);

        mEditWindow = new EditWindow(this);
        mEditWindow.getContentView().findViewById(R.id.edit_btn_replace).setOnClickListener(this);
        mEditWindow.getContentView().findViewById(R.id.edit_btn_delete).setOnClickListener(this);
        mEditWindow.getContentView().findViewById(R.id.edit_btn_edit).setOnClickListener(this);

        mChoiceVideoWindow = new ChoiceVideoWindow(this);
        mChoiceVideoWindow.getContentView().findViewById(R.id.video_btn_local).setOnClickListener(this);
        mChoiceVideoWindow.getContentView().findViewById(R.id.video_btn_link).setOnClickListener(this);

        mVideoLinkWindow = new VideoLinkWindow(this);
        mVideoLinkWindow.setOnVideoLinkFinishCallback(new VideoLinkWindow.OnVideoLinkFinishCallback() {
            @Override
            public void onVideoLink(String linkStr) {
                MediaItem mediaItem = new MediaItem(MediaItem.LINK, Uri.parse(linkStr));

                if (isEdit) {
                    mPublishMediaItem.mediaItem = mediaItem;
                    newAdapter.update(mPosition, mPublishMediaItem);
                } else {
                    addMediaItem(mediaItem);
                }
            }
        });

        mFontWindow = new FontWindow(this);
        mFontWindow.findViewById(R.id.font_btn_submit).setOnClickListener(this);
    }

    private void addMediaItem(MediaItem mediaItem) {
        PublishModel.PublishMediaItem publishMediaItem = new PublishModel.PublishMediaItem(mediaItem.getType(), mediaItem.getPathOrigin(this));
        publishMediaItem.mediaItem = mediaItem;
        publishMediaItem.listBean = mediaItem.getListBean();
        newAdapter.add(-1, publishMediaItem);
    }

    private void addEditText(int index, FontModel fontModel) {
        PublishModel.PublishMediaItem mediaItem = new PublishModel.PublishMediaItem(4, null);
        mediaItem.fontModel = fontModel;
        mediaItem.desc = fontModel.getText();
        newAdapter.add(index, mediaItem);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);

        if (mChoiceVideoWindow.isShowing()) {
            mChoiceVideoWindow.dismiss();
        }

        switch (v.getId()) {
            case R.id.publish_iv_cover:
                mPictureManager.showWindow(PictureManager.WINDOW_TYPE_COMMON);
                break;
            case R.id.publish_btn_album:
                isEdit = false;
                MediaPickerActivity.open(this, REQUEST_MEDIA, new MediaOptions.Builder().selectPhoto().canSelectMultiPhoto(true).build());
                break;
            case R.id.publish_btn_video:
                mChoiceVideoWindow.showAsDropDown(v, 0,
                        -1 * v.getHeight() - CommUtils.dip2px(100));
                break;
            case R.id.alert_btn_submit:
                super.onBackPressed();
                break;
            case R.id.right_btn_submit:
                doPublish();
                break;
            case R.id.video_btn_local:
                isEdit = false;

                if (newAdapter.hasVideo()) {
                    showMessage("最多只能添加3个视频，请先删除已添加的视频");
                    return;
                }
                MediaPickerActivity.open(this, REQUEST_MEDIA, new MediaOptions.Builder().selectVideo().build());
                break;
            case R.id.video_btn_link:
                isEdit = false;
                mVideoLinkWindow.show(null);
                break;
            case R.id.publish_btn_lable:
                Intent intent = new Intent(this, MainPublishLableActivity.class);
                intent.putExtra("lables", lable);
                startActivityForResult(intent, 100);
                break;
            case R.id.publish_btn_font:
                addEditText(-1, new FontModel());
                break;
            case R.id.publish_btn_share:
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("shareUrl", "124");
                cmb.setPrimaryClip(clipData);
                showMessage("成功复制电脑端用户发布链接");
                break;

            case R.id.font_btn_submit:
                mFontWindow.cancel();
                mPublishMediaItem.fontModel = mFontWindow.getFontModel();
                newAdapter.update(mPosition, mPublishMediaItem);
                break;
            default:
                break;
        }
    }

    private boolean checkFormParams() {
        if (mSpinner.getSelectedItemPosition() < 0) {
            showMessage("职位不能为空");
            return false;
        }

        mPublishModel.tid = mHomeTypeModel.data.get(mSpinner.getSelectedItemPosition()).id;

        if (TextUtils.isEmpty(mPublishModel.cover)) {
            showMessage("你还没有设计封面哦");
            return false;
        }
        String title = ((TextView) mHeadView.findViewById(R.id.publish_et_title)).getText().toString();
        if (TextUtils.isEmpty(title)) {
            showMessage("你还没有添加标题哦");
            return false;
        }
        mPublishModel.title = title;

        return true;
    }

    private void doPublish() {
        if (!checkFormParams()) {
            return;
        }

        mPublishModel.medias.clear();
        mPublishModel.medias.addAll(newAdapter.getDatas());
        mVideoModels = new HashMap<>();
        mVideoPaths = new ArrayList<>();

        for (PublishModel.PublishMediaItem mediaItem : mPublishModel.medias) {
            if (mediaItem.type == PublishModel.TYPE_VIDEO && !mediaItem.mediaPath.startsWith("http")) {
                mVideoPaths.add(mediaItem.mediaPath);
            }
        }

        if (mVideoPaths.size() > 0) {
            videoPosition = 0;
            doUploadVideo();
        } else {
            doUploadPhoto();
        }
    }

    public void doUploadVideo() {
        mLoadWindow.show(R.string.text_is_upload_video);
        HashMap<String, Object> uploadParams = new HashMap<>();
        uploadParams.put("token", mUserInfo.token);
        uploadParams.put("uploadfile", new File(mVideoPaths.get(videoPosition)));
        HttpUtils.doUpload(new TaskInfo(TaskType.TASK_TYPE_VIDEO_UPLOAD, uploadParams, this), new ProgressCallback() {
            @Override
            public void onProgress(int status, final double progress, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progress == 1) {
                            mLoadWindow.showMessage("请稍后...");
                        } else {
                            mLoadWindow.showMessage("进度: " + (videoPosition + 1) + "/" + mVideoPaths.size() + ", " + (int) (progress * 100) + "%...");
                        }
                    }
                });
                L.d("status = " + status + " , progress = " + progress + " , message =" + message);
            }
        });
    }

    public void doUploadPhoto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.show(R.string.text_is_upload);
                        }
                    });
                    final HashMap<String, Object> params = new HashMap<>();
                    params.put("id", mWorkId);
                    params.put("token", mUserInfo.token);
                    params.put("tid", mPublishModel.tid);
                    params.put("title", mPublishModel.title + "");
                    if (mPublishModel.cover.startsWith("http")) {
                        params.put("fmphoto_type", 2);
                        params.put("fmphoto", mPublishModel.cover);
                    } else {
                        params.put("fmphoto_type", 1);
                        params.put("fmphoto", PictureManager.getBase64(mPublishModel.cover));
                    }
                    params.put("label", lable + "");
                    params.put("position", "");

                    List<HashMap<String, Object>> mediaParams = new ArrayList<>();
                    if (mPublishModel.medias != null && mPublishModel.medias.size() > 0) {
                        HashMap<String, Object> mediaParam;
                        VideoModel mVideoModel;
                        for (PublishModel.PublishMediaItem mediaIten : mPublishModel.medias) {
                            mediaParam = new HashMap<>();

                            if (mediaIten.type == PublishModel.TYPE_PICTYRE) {
                                if (mediaIten.mediaPath.startsWith("http")) {
                                    mediaParam.put("isupdate", 2);
                                    mediaParam.put("photo", mediaIten.listBean.getList_photo_filename());
                                    mediaParam.put("width", mediaIten.listBean.getList_width());
                                    mediaParam.put("height", mediaIten.listBean.getList_height());
                                    mediaParam.put("ispic", mediaIten.listBean.getList_ispic());
                                } else {
                                    boolean isGif = mediaIten.mediaPath.toLowerCase().endsWith(".gif");
                                    mediaParam.put("ispic", isGif ? 2 : 1);
                                    mediaParam.put("photo", isGif ? PictureManager.getFileBase64(mediaIten.mediaPath) : PictureManager.getBase64(mediaIten.mediaPath));
                                    mediaParam.put("isupdate", 1);
                                    mediaParam.put("width", 0);
                                    mediaParam.put("height", 0);
                                }

                                mediaParam.put("type", 1);
                            } else if (mediaIten.type == MediaItem.VIDEO) {
                                mediaParam.put("type", 3);
                                mVideoModel = mVideoModels.get(mediaIten.mediaPath);

                                if (mVideoModel != null) {
                                    mediaParam.put("isupdate", 1);
                                    mediaParam.put("video", mVideoModel.videoPath);
                                    mediaParam.put("videofm", PictureManager.getVideoThumbnail(mVideoModel.localPath));
                                    mediaParam.put("videotimer", mVideoModel.videoLong);
                                    mediaParam.put("width", 0);
                                    mediaParam.put("height", 0);
                                } else {
                                    mediaParam.put("isupdate", 2);
                                    mediaParam.put("video", mediaIten.listBean.getList_video_filename());
                                    mediaParam.put("videofm", mediaIten.listBean.getList_photo_filename());
                                    mediaParam.put("videotimer", mediaIten.listBean.getList_videotime());
                                    mediaParam.put("width", mediaIten.listBean.getList_width());
                                    mediaParam.put("height", mediaIten.listBean.getList_height());
                                }

                            } else if (mediaIten.type == MediaItem.LINK) {
                                mediaParam.put("type", 4);
                                mediaParam.put("video", mediaIten.mediaPath);
                            } else {
                                if (TextUtils.isEmpty(mediaIten.desc)) {
                                    continue;
                                }

                                mediaParam.put("type", 2);
                                mediaParam.put("isupdate", 1);
                                mediaParam.put("content", mediaIten.desc);
                                mediaParam.put("isbold", String.valueOf(mediaIten.fontModel.isBlod() ? 1 : 0));
                                mediaParam.put("color", mediaIten.fontModel.getRGBColor());
                                mediaParam.put("fontsize", String.valueOf(mediaIten.fontModel.getFont()));
                                mediaParam.put("textalign", String.valueOf(mediaIten.fontModel.isCenter() ? 1 : 0));

                            }

                            mediaParams.add(mediaParam);
                        }

                    }
                    params.put("list", mediaParams);
                    HttpUtils.doPost(TaskType.TASK_TYPE_UPDATE_DETAILS_CHNAGE, params, MainDetailsEditActivity.this, true);
                } catch (Exception e) {
                    L.d(ThrowableUtils.getThrowableDetailsMessage(e));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.cancel();
                        }
                    });
                    showMessage("封面编码失败，请换张封面再试");
                }
            }
        }).start();
    }

    private void dealMediaItemData(JSONObject uploadObject) {
        L.d("dealMediaItemData:" + uploadObject);

        VideoModel mVideoModel = new VideoModel();
        mVideoModel.localPath = mVideoPaths.get(videoPosition);
        mVideoModel.videoPath = uploadObject.optString("filename");
        mVideoModel.videoLong = CommUtils.getDuration(mVideoModel.localPath);

        mVideoModels.put(mVideoModel.localPath, mVideoModel);

        videoPosition++;
        if (videoPosition < mVideoPaths.size()) {
            doUploadVideo();
        } else {
            doUploadPhoto();
        }
    }

    private void loadData() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", mUserInfo.token);
        hashMap.put("id", mWorkId);
        mLoadWindow.show();
        HttpUtils.doPost(TaskType.TASK_TYPE_QRY_DETAILS_CHNAGE, hashMap, this);
    }

    private void initMediaData() {
        if (TextUtils.isEmpty(mPublishModel.cover)) {
            ((ImageView) mHeadView.findViewById(R.id.publish_iv_cover)).setImageResource(R.mipmap.mine_bg);
            mHeadView.findViewById(R.id.publish_cover_layout).setVisibility(View.VISIBLE);
        } else {
            PictureManager.displayImage(mPublishModel.cover, (ImageView) mHeadView.findViewById(R.id.publish_iv_cover));
            mHeadView.findViewById(R.id.publish_cover_layout).setVisibility(View.GONE);
        }
    }

    private void inflateData() {
        mSpinner.setSelection(((PublishTypeAdater) mSpinner.getAdapter()).getIdByName(editModel.getData().getTypename()));

        mPublishModel.cover = editModel.getData().getFmphoto();
        lable = editModel.getData().getLabel();

        initMediaData();

        ((TextView) mHeadView.findViewById(R.id.publish_et_title)).setText(editModel.getData().getTitle());

        int type;
        for (DetailsEditModel.DataBean.BodylistBean bodylistBean : editModel.getData().getBodylist()) {
            type = Integer.parseInt(bodylistBean.getList_type());
            if (type == 1) {
                MediaItem mediaItem = new MediaItem(MediaItem.PHOTO, Uri.parse(bodylistBean.getList_photo()));
                mediaItem.setListBean(bodylistBean);
                addMediaItem(mediaItem);
            } else if (type == 2) {
                FontModel fontModel = new FontModel();
                fontModel.setCenter(Integer.parseInt(bodylistBean.getList_textalign()) == 1);
                fontModel.setBlod(Integer.parseInt(bodylistBean.getList_isbold()) == 1);
                fontModel.setRGBColor(bodylistBean.getList_color());
                fontModel.setFont(Integer.parseInt(bodylistBean.getList_fontsize()));
                fontModel.setText(bodylistBean.getList_content());

                addEditText(-1, fontModel);
            } else if (type == 3) {
                MediaItem mediaItem = new MediaItem(MediaItem.VIDEO, Uri.parse(bodylistBean.getList_video()));
                mediaItem.setListBean(bodylistBean);
                addMediaItem(mediaItem);
            } else {
                addMediaItem(new MediaItem(MediaItem.LINK, Uri.parse(bodylistBean.getList_video())));
            }
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            mLoadWindow.cancel();
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type) {
            case TASK_TYPE_HOME_TYPE:
                mLoadWindow.cancel();

                mHomeTypeModel = GsonUtil.fromJson(result.toString(), HomeTypeModel.class);
                if (mHomeTypeModel != null) {
                    mSpinner.setAdapter(new PublishTypeAdater(MainDetailsEditActivity.this, mHomeTypeModel.data));
                    inflateData();
                } else {
                    showMessage(R.string.data_load_failed);
                }
                break;
            case TASK_TYPE_UPDATE_DETAILS_CHNAGE:
                mLoadWindow.cancel();

                showMessage("保存成功");
                EventUtils.doPostEvent(EventType.EVENT_TYPE_REFRESH_ARTICLE, ((HomeTypeModel.HomeType) mSpinner.getSelectedItem()).name);
                super.onBackPressed();
                break;
            case TASK_TYPE_VIDEO_UPLOAD:
                JSONObject uploadObject = (JSONObject) result;
                if (uploadObject.has("data") && uploadObject.optJSONObject("data").has("filename")) {
                    dealMediaItemData(uploadObject.optJSONObject("data"));
                } else {
                    mLoadWindow.cancel();
                    showMessage("视频上传失败");
                }
                break;
            case TASK_TYPE_QRY_DETAILS_CHNAGE:
                editModel = GsonUtil.fromJson(result.toString(), DetailsEditModel.class);

                HashMap<String, Object> params = new HashMap<>();
                params.put("type", 1);
                mLoadWindow.show(R.string.text_request);
                HttpUtils.doPost(TaskType.TASK_TYPE_HOME_TYPE, params, this);
                break;
        }
    }


    @Override
    public void onPictureCallback(Uri uri, Intent data) {
        mPublishModel.cover = uri.getPath();
        initMediaData();
    }

    @Override
    public void onPictureUpload(int status, String message) {

    }

    @Override
    public void onBackPressed() {
        if (mChoiceVideoWindow.isShowing()) {
            mChoiceVideoWindow.dismiss();
        } else {
            mExitWindow.show("", "确定退出修改？", "取消", "确定");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == PictureManager.PM_REQUESTCODE_CROP || requestCode == PictureManager.PM_REQUESTCODE_CAMERA || requestCode == PictureManager.PM_REQUESTCODE_MEDIA) {
            mPictureManager.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == REQUEST_MEDIA) {
            mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
            if (mMediaSelectedList != null) {
                for (MediaItem mediaItem : mMediaSelectedList) {
                    if (isEdit) {
                        mPublishMediaItem.mediaPath = mediaItem.getPathOrigin(this);
                        mPublishMediaItem.mediaItem = mediaItem;
                        newAdapter.update(mPosition, mPublishMediaItem);
                    } else {
                        addMediaItem(mediaItem);
                    }
                }
            } else {
            }

            initMediaData();
        } else if (requestCode == 100) {
            lable = data.getStringExtra("lables");
            int num = TextUtils.isEmpty(lable) ? 0 : lable.split(",").length;

            if (num > 0) {
                ((TextView) findViewById(R.id.publish_tv_lable)).setText(String.valueOf(num));
                findViewById(R.id.publish_tv_lable).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.publish_tv_lable).setVisibility(View.GONE);
            }
        }
    }

}
