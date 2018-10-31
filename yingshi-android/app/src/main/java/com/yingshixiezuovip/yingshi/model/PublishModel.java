package com.yingshixiezuovip.yingshi.model;

import com.yingshixiezuovip.yingshi.quote.media.MediaItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Resmic on 2017/5/10.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class PublishModel implements Serializable {
    public static final int TYPE_PICTYRE = 1, TYPE_VIDEO = 2, TYPE_LINK = 3;
    public int tid;
    public String position = "";
    public String cover;
    public String coverName;
    public String title;
    public String content;
    public List<PublishMediaItem> medias;

    public PublishModel() {
        this.medias = new ArrayList<>();
    }

    public static class PublishMediaItem implements Serializable {

        /**
         * type:1为图片，2为视频
         */

        public int type;
        public String mediaPath;
        public String desc;
        public String position = "";
        public String cover;
        public FontModel fontModel = new FontModel();
        public MediaItem mediaItem;
        public DetailsEditModel.DataBean.BodylistBean listBean;

        public PublishMediaItem() {
        }

        public PublishMediaItem(int type, String mediaPath) {
            this.type = type;
            this.mediaPath = mediaPath;
        }

    }
}
