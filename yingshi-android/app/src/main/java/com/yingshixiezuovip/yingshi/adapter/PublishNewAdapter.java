package com.yingshixiezuovip.yingshi.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.custom.MediaLayout;
import com.yingshixiezuovip.yingshi.custom.TextLayout;
import com.yingshixiezuovip.yingshi.model.PublishModel;
import com.yingshixiezuovip.yingshi.publish.CallbackItemTouch;
import com.yingshixiezuovip.yingshi.quote.media.MediaItem;
import com.yingshixiezuovip.yingshi.utils.CommUtils;
import com.yingshixiezuovip.yingshi.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Resmic on 18/1/31.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class PublishNewAdapter extends RecyclerView.Adapter<PublishNewAdapter.PublishNewHolder> implements CallbackItemTouch {
    private List<PublishModel.PublishMediaItem> datas;
    private Context context;
    private int margin = 10;
    private int selectItem;
    private View headView;

    public PublishNewAdapter(Context context) {
        this.context = context;
        this.datas = new ArrayList<>();
        this.margin = CommUtils.dip2px(margin);
    }

    @Override
    public PublishNewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.LayoutParams layoutParams;

        if (headView != null && viewType == 0) {
            return new PublishNewHolder(headView);
        } else if (viewType == 4) {
            layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(margin, margin, margin, margin);
            TextLayout textLayout = new TextLayout(context);
            textLayout.setLayoutParams(layoutParams);
            return new PublishNewHolder(textLayout);
        } else {
            layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            MediaLayout mediaLayout = new MediaLayout(context);
            mediaLayout.setLayoutParams(layoutParams);
            return new PublishNewHolder(mediaLayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (headView != null) {
            if (position == 0) {
                return 0;
            } else {
                return datas.get(position - 1).type;
            }
        } else {
            return datas.get(position).type;
        }
    }

    public void addHeaderView(View mHeadView) {
        this.headView = mHeadView;
    }

    @Override
    public void onBindViewHolder(final PublishNewHolder holder, final int position) {

        if (headView != null && position == 0) {
            return;
        }

        final PublishModel.PublishMediaItem mediaItem = datas.get(headView != null ? position - 1 : position);

        holder.itemView.findViewById(R.id.text_btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.remove(headView != null ? position - 1 : position);
                notifyDataSetChanged();
            }
        });

        holder.itemView.findViewById(R.id.text_btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(headView != null ? position - 1 : position, mediaItem, false);
                }
            }
        });

        holder.itemView.findViewById(R.id.text_et_input).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(headView != null ? position - 1 : position, mediaItem, true);
                }
            }
        });

        if (holder.itemView instanceof MediaLayout) {
            ((MediaLayout) holder.itemView).setMediaItem(mediaItem.mediaItem);
        } else {
            ((TextLayout) holder.itemView).setFontModel(mediaItem.fontModel);
            ((TextLayout) holder.itemView).setText(mediaItem.desc);
        }
    }

    public boolean add(int index, PublishModel.PublishMediaItem mediaItem) {
        for (PublishModel.PublishMediaItem item : getDatas()) {
            if (item.mediaPath != null && item.mediaPath.equals(mediaItem.mediaPath)) {
                ToastUtils.showMessage((Activity) context,
                        "该" + (mediaItem.type == MediaItem.PHOTO ? "图片" : "视频") + "已经存在，请勿重复添加");
                return false;
            }
        }

        datas.add(mediaItem);
        notifyDataSetChanged();
        return true;
    }

    public void update(int position, PublishModel.PublishMediaItem publishMediaItem) {
        publishMediaItem.listBean = null;
        datas.set(position, publishMediaItem);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datas.size() + (headView != null ? 1 : 0);
    }

    @Override
    public void itemTouchOnMove(int oldPosition, int newPosition) {
        if (oldPosition >= 0 && newPosition >= 0) {
            if (headView != null) {
                datas.add(newPosition - 1, datas.remove(oldPosition - 1));// change position
            } else {
                datas.add(newPosition, datas.remove(oldPosition));// change position
            }
            notifyItemMoved(oldPosition, newPosition); //notifies changes in
        }
    }

    @Override
    public void itemTouchOnFinish() {
        notifyDataSetChanged();
    }

    public List<PublishModel.PublishMediaItem> getDatas() {
        return datas;
    }

    public boolean hasVideo() {
        int videoSum = 0;
        for (PublishModel.PublishMediaItem mediaItem : datas) {
            if (mediaItem.type == PublishModel.TYPE_VIDEO) {
                videoSum++;
            }
        }

        return videoSum >= 3;
    }

    public class PublishNewHolder extends RecyclerView.ViewHolder {

        public PublishNewHolder(View itemView) {
            super(itemView);
            setIsRecyclable(false);
        }
    }

    public interface OnEditClickListener {
        void onEditClick(int position, PublishModel.PublishMediaItem mediaItem, boolean text);

    }

    private OnEditClickListener onEditClickListener;

    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

}
