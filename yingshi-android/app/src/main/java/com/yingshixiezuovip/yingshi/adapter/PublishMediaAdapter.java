package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.MBaseAdapter;
import com.yingshixiezuovip.yingshi.model.PublishModel;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

/**

 * Created by Resmic on 2017/5/14.

 */

public class PublishMediaAdapter extends MBaseAdapter<PublishModel.PublishMediaItem> {
    private int mType;

    public PublishMediaAdapter(Context mContext) {
        this(mContext, 1);
    }

    public PublishMediaAdapter(Context mContext, int type) {
        super(mContext);
        this.mType = type;
    }

    @Override
    public void onBindViewHolder(final int position, ViewHolder viewHolder) {
        PublishModel.PublishMediaItem mediaIten = getItem(position);
        viewHolder.findViewById(R.id.media_iv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeData(position);
            }
        });
        viewHolder.findViewById(R.id.media_iv_delete).setSoundEffectsEnabled(false);
        if (mediaIten.type == PublishModel.TYPE_PICTYRE) {
            viewHolder.findViewById(R.id.media_et_position).setVisibility(View.GONE);
            ((EditText) viewHolder.findViewById(R.id.media_et_desc)).setHint("添加图片描述");
        } else {
            viewHolder.findViewById(R.id.media_et_position).setVisibility(View.VISIBLE);
            ((EditText) viewHolder.findViewById(R.id.media_et_desc)).setHint("添加视频描述");
        }
        if (!TextUtils.isEmpty(mediaIten.desc)) {
            ((EditText) viewHolder.findViewById(R.id.media_et_desc)).setText(mediaIten.desc);
        }
        if (!TextUtils.isEmpty(mediaIten.position)) {
            ((EditText) viewHolder.findViewById(R.id.media_et_position)).setText(mediaIten.position);
        }
        if (mType == 2) {
            viewHolder.findViewById(R.id.media_et_position).setVisibility(View.GONE);
        }
        PictureManager.displayImage(mediaIten.mediaPath, (ImageView) viewHolder.findViewById(R.id.media_iv_icon));
    }

    @Override
    public int getConvertViewLayoutID() {
        return R.layout.publish_media_item;
    }
}