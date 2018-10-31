package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.adapter.PublishNewAdapter;
import com.yingshixiezuovip.yingshi.adapter.PublishTypeAdater;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.custom.ChoiceVideoWindow;
import com.yingshixiezuovip.yingshi.custom.FontWindow;
import com.yingshixiezuovip.yingshi.custom.TextInputWindow;
import com.yingshixiezuovip.yingshi.custom.VideoLinkWindow;
import com.yingshixiezuovip.yingshi.datautils.CommonThreadPool;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskInfo;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.datautils.ThrowableUtils;
import com.yingshixiezuovip.yingshi.minterface.ProgressCallback;
import com.yingshixiezuovip.yingshi.model.DetailsEditModel;
import com.yingshixiezuovip.yingshi.model.FontModel;
import com.yingshixiezuovip.yingshi.model.PublishModel;
import com.yingshixiezuovip.yingshi.model.VideoModel;
import com.yingshixiezuovip.yingshi.publish.MyItemTouchHelperCallback;
import com.yingshixiezuovip.yingshi.quote.media.MediaItem;
import com.yingshixiezuovip.yingshi.quote.media.MediaOptions;
import com.yingshixiezuovip.yingshi.quote.media.activities.MediaPickerActivity;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.L;
import com.yingshixiezuovip.yingshi.utils.LoginUtils;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainCompanyResumeEditActivity extends BaseActivity {
    private boolean isEdit;
    public static final int REQUEST_MEDIA = 0x1003;
    private ChoiceVideoWindow mChoiceVideoWindow;
    private AlertWindow mExitWindow;
    private VideoLinkWindow mVideoLinkWindow;
    private FontWindow mFontWindow;
    private int mPosition;
    private PublishModel.PublishMediaItem mPublishMediaItem;
    private TextInputWindow mTextInputWindow;
    private PublishNewAdapter newAdapter;
    private RecyclerView recyclerView;
    private PublishModel mPublishModel;
    private List<MediaItem> mMediaSelectedList;
    private String lable = "";
    private int videoPosition;
    private HashMap<String, VideoModel> mVideoModels;
    private List<String> mVideoPaths;
    private DetailsEditModel editModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_company_resume, R.string.title_activity_main_company_resume);

        initView();
    }

    private void initView() {
        mPublishModel = new PublishModel();

        findViewById(R.id.resume_btn_album).setOnClickListener(this);
        findViewById(R.id.resume_btn_video).setOnClickListener(this);
        findViewById(R.id.resume_btn_lable).setOnClickListener(this);
        findViewById(R.id.resume_btn_font).setOnClickListener(this);

        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.right_btn_name)).setText("提交");

        newAdapter = new PublishNewAdapter(this);
        newAdapter.setOnEditClickListener(onEditClickListener);
        recyclerView = (RecyclerView) findViewById(R.id.resume_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newAdapter);

        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(newAdapter);// create MyItemTouchHelperCallback
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback); // Create ItemTouchHelper and pass with parameter the MyItemTouchHelperCallback
        touchHelper.attachToRecyclerView(recyclerView); // Attach ItemTouchHelper to RecyclerView

        initWindow();

        loadData();
    }

    private void loadData() {
        mLoadWindow.show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        HttpUtils.doPost(TaskType.TASK_TYPE_DETAILS_RESUME, params, this);
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
                    MediaPickerActivity.open(MainCompanyResumeEditActivity.this, REQUEST_MEDIA, new MediaOptions.Builder().selectPhoto().canSelectMultiPhoto(false).build());
                    break;
                case 2:
                    MediaPickerActivity.open(MainCompanyResumeEditActivity.this, REQUEST_MEDIA, new MediaOptions.Builder().selectVideo().build());
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

        mTextInputWindow = new TextInputWindow(this, onTextInputFinishListener);
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
    public void onClick(View v) {

        if (mChoiceVideoWindow.isShowing()) {
            mChoiceVideoWindow.dismiss();
        }

        switch (v.getId()) {
            case R.id.resume_btn_album:
                isEdit = false;
                MediaPickerActivity.open(this, REQUEST_MEDIA, new MediaOptions.Builder().selectPhoto().canSelectMultiPhoto(true).build());
                break;
            case R.id.resume_btn_video:
                mChoiceVideoWindow.showAsDropDown(v, 0,
                        -1 * v.getHeight() - CommUtils.dip2px(100));
                break;
            case R.id.alert_btn_submit:
                super.onBackPressed();
                break;
            case R.id.right_btn_submit:
                doSubmit();
                break;
            case R.id.font_btn_submit:
                mFontWindow.cancel();
                mPublishMediaItem.fontModel = mFontWindow.getFontModel();
                newAdapter.update(mPosition, mPublishMediaItem);
                break;
            case R.id.resume_btn_lable:
                Intent intent = new Intent(this, MainPublishLableActivity.class);
                intent.putExtra("lables", lable);
                startActivityForResult(intent, 100);
                break;
            case R.id.resume_btn_font:
                addEditText(-1, new FontModel());
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
        }
    }

    private void doSubmit() {
        if (newAdapter.getItemCount() <= 0) {
            showMessage("没有任何输入");
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
            doUploadResume();
        }
    }

    private void doUploadVideo() {
        mLoadWindow.show();
        HashMap<String, Object> uploadParams = new HashMap<>();
        uploadParams.put("token", mUserInfo.token);
        uploadParams.put("uploadfile", new File(mVideoPaths.get(videoPosition)));
        HttpUtils.doUpload(new TaskInfo(TaskType.TASK_TYPE_UPLOAD_RESUME_VIDEO, uploadParams, this), new ProgressCallback() {
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

    private void doUploadResume() {
        CommonThreadPool.getThreadPool().addCachedTask(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("token", mUserInfo.token);
                    if (TextUtils.isEmpty(lable)) {
                        params.put("label", "");
                    } else {
                        params.put("label", lable);
                    }
                    params.put("id", editModel.getData().getId());
                    List<HashMap<String, Object>> mediaParams = new ArrayList<>();
                    VideoModel mVideoModel;
                    if (mPublishModel.medias != null && mPublishModel.medias.size() > 0) {
                        HashMap<String, Object> mediaParam;

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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadWindow.show();
                        }
                    });

                    params.put("list", mediaParams);
                    HttpUtils.doPost(TaskType.TASK_TYPE_UPDATE_RESUME, params, MainCompanyResumeEditActivity.this, true);
                } catch (Exception e) {
                    L.d("Exception == " + ThrowableUtils.getThrowableDetailsMessage(e));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMessage("图片解析失败，请重新选择");
                            mLoadWindow.cancel();
                        }
                    });
                }
            }
        });
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
            doUploadResume();
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
            case TASK_TYPE_UPLOAD_RESUME_VIDEO:
                JSONObject uploadObject = (JSONObject) result;
                if (uploadObject.has("data") && uploadObject.optJSONObject("data").has("filename")) {
                    dealMediaItemData(uploadObject.optJSONObject("data"));
                } else {
                    showMessage("视频上传失败");
                }
                break;
            case TASK_TYPE_UPDATE_RESUME:
                mLoadWindow.cancel();
                showMessage("简历保存成功");
                super.onBackPressed();
                break;
            case TASK_TYPE_DETAILS_RESUME:
                editModel = GsonUtil.fromJson(result.toString(), DetailsEditModel.class);
                inflateData();
                break;
        }
    }

    private void inflateData() {
        lable = editModel.getData().getLabel();


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
    public void onBackPressed() {
        if (mChoiceVideoWindow.isShowing()) {
            mChoiceVideoWindow.dismiss();
        } else {
            mExitWindow.show("", "确定退出简历编辑？", "取消", "确定");
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
                    if (isEdit) {
                        mPublishMediaItem.mediaPath = mediaItem.getPathOrigin(this);
                        newAdapter.update(mPosition, mPublishMediaItem);
                    } else {
                        addMediaItem(mediaItem);
                    }
                }
            } else {
            }
        } else if (requestCode == 100) {
            lable = data.getStringExtra("lables");
            int num = TextUtils.isEmpty(lable) ? 0 : lable.split(",").length;

            if (num > 0) {
                ((TextView) findViewById(R.id.resume_tv_lable)).setText(String.valueOf(num));
                findViewById(R.id.resume_tv_lable).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.resume_tv_lable).setVisibility(View.GONE);
            }
        }
    }
}