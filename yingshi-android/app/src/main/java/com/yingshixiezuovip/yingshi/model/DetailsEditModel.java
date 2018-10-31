package com.yingshixiezuovip.yingshi.model;

import java.util.List;

/**
 * Created by Resmic on 18/1/23.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class DetailsEditModel {

    /**
     * result : {"code":"200","detail":"OK"}
     * data : {"id":"531","tid":"1","typename":"片场","title":"混编","fmphoto":"http://www.cftvc.com:8888/yz_dir/star/photo/20180123/thumb/459ab49522c1db34696e741a957d90ca.jpg","label":"标签1,标签二","bodylist":[{"list_id":"1426","list_type":"1","list_photo":"http://www.cftvc.com:8888/yz_dir/star/body/20180123/thumb/c9ba74894ae521a497e499c4d765998e.jpg","list_photo_filename":"star/body/20180123/c9ba74894ae521a497e499c4d765998e.jpg","list_width":"180","list_height":"180","list_position":"1"},{"list_id":"1427","list_type":"2","list_content":"累死","list_isbold":"1","list_color":"rgb(103, 18, 124)","list_fontsize":"18","list_textalign":"1","list_position":"2"},{"list_id":"314","list_width":"1088","list_height":"1920","list_type":"3","list_video":"http://www.cftvc.com:8888/yz_dir/star/video/20180123/d74d32a10b1f8b60cf6640ab7b9adfaa.mp4","list_videofm":"http://www.cftvc.com:8888/yz_dir/star/video/fm/20180123/thumb/ae5887f8bc7eb4fb08f29901eef53285.jpg","list_video_filename":"star/video/20180123/d74d32a10b1f8b60cf6640ab7b9adfaa.mp4","list_photo_filename":"star/video/fm/20180123/ae5887f8bc7eb4fb08f29901eef53285.jpg","list_position":"3","list_videotime":"00:12"},{"list_id":"1428","list_type":"2","list_content":"本地视频","list_isbold":"0","list_color":"rgb(163, 125, 207)","list_fontsize":"30","list_textalign":"0","list_position":"4"},{"list_id":"315","list_type":"4","list_video":"http://www.baidu.com","list_position":"5"},{"list_id":"1429","list_type":"2","list_content":"在线视频","list_isbold":"0","list_color":"rgb(0, 0, 0)","list_fontsize":"14","list_textalign":"0","list_position":"6"}]}
     */

    private ResultBean result;
    private DataBean data;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class ResultBean {
        /**
         * code : 200
         * detail : OK
         */

        private String code;
        private String detail;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }

    public static class DataBean {
        /**
         * id : 531
         * tid : 1
         * typename : 片场
         * title : 混编
         * fmphoto : http://www.cftvc.com:8888/yz_dir/star/photo/20180123/thumb/459ab49522c1db34696e741a957d90ca.jpg
         * label : 标签1,标签二
         * bodylist : [{"list_id":"1426","list_type":"1","list_photo":"http://www.cftvc.com:8888/yz_dir/star/body/20180123/thumb/c9ba74894ae521a497e499c4d765998e.jpg","list_photo_filename":"star/body/20180123/c9ba74894ae521a497e499c4d765998e.jpg","list_width":"180","list_height":"180","list_position":"1"},{"list_id":"1427","list_type":"2","list_content":"累死","list_isbold":"1","list_color":"rgb(103, 18, 124)","list_fontsize":"18","list_textalign":"1","list_position":"2"},{"list_id":"314","list_width":"1088","list_height":"1920","list_type":"3","list_video":"http://www.cftvc.com:8888/yz_dir/star/video/20180123/d74d32a10b1f8b60cf6640ab7b9adfaa.mp4","list_videofm":"http://www.cftvc.com:8888/yz_dir/star/video/fm/20180123/thumb/ae5887f8bc7eb4fb08f29901eef53285.jpg","list_video_filename":"star/video/20180123/d74d32a10b1f8b60cf6640ab7b9adfaa.mp4","list_photo_filename":"star/video/fm/20180123/ae5887f8bc7eb4fb08f29901eef53285.jpg","list_position":"3","list_videotime":"00:12"},{"list_id":"1428","list_type":"2","list_content":"本地视频","list_isbold":"0","list_color":"rgb(163, 125, 207)","list_fontsize":"30","list_textalign":"0","list_position":"4"},{"list_id":"315","list_type":"4","list_video":"http://www.baidu.com","list_position":"5"},{"list_id":"1429","list_type":"2","list_content":"在线视频","list_isbold":"0","list_color":"rgb(0, 0, 0)","list_fontsize":"14","list_textalign":"0","list_position":"6"}]
         */

        private String id;
        private String tid;
        private String typename;
        private String title;
        private String fmphoto;
        private String label;
        private List<BodylistBean> bodylist;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTid() {
            return tid;
        }

        public void setTid(String tid) {
            this.tid = tid;
        }

        public String getTypename() {
            return typename;
        }

        public void setTypename(String typename) {
            this.typename = typename;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFmphoto() {
            return fmphoto;
        }

        public void setFmphoto(String fmphoto) {
            this.fmphoto = fmphoto;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<BodylistBean> getBodylist() {
            return bodylist;
        }

        public void setBodylist(List<BodylistBean> bodylist) {
            this.bodylist = bodylist;
        }

        public static class BodylistBean {
            /**
             * list_id : 1426
             * list_type : 1
             * list_photo : http://www.cftvc.com:8888/yz_dir/star/body/20180123/thumb/c9ba74894ae521a497e499c4d765998e.jpg
             * list_photo_filename : star/body/20180123/c9ba74894ae521a497e499c4d765998e.jpg
             * list_width : 180
             * list_height : 180
             * list_position : 1
             * list_content : 累死
             * list_isbold : 1
             * list_color : rgb(103, 18, 124)
             * list_fontsize : 18
             * list_textalign : 1
             * list_video : http://www.cftvc.com:8888/yz_dir/star/video/20180123/d74d32a10b1f8b60cf6640ab7b9adfaa.mp4
             * list_videofm : http://www.cftvc.com:8888/yz_dir/star/video/fm/20180123/thumb/ae5887f8bc7eb4fb08f29901eef53285.jpg
             * list_video_filename : star/video/20180123/d74d32a10b1f8b60cf6640ab7b9adfaa.mp4
             * list_videotime : 00:12
             */

            private String list_id;
            private String list_type;
            private String list_photo;
            private String list_photo_filename;
            private String list_width;
            private String list_height;
            private String list_position;
            private String list_content;
            private String list_isbold;
            private String list_color;
            private String list_fontsize;
            private String list_textalign;
            private String list_video;
            private String list_videofm;
            private String list_video_filename;
            private String list_videotime;
            private String list_ispic;

            public String getList_id() {
                return list_id;
            }

            public void setList_id(String list_id) {
                this.list_id = list_id;
            }

            public String getList_type() {
                return list_type;
            }

            public void setList_type(String list_type) {
                this.list_type = list_type;
            }

            public String getList_photo() {
                return list_photo;
            }

            public void setList_photo(String list_photo) {
                this.list_photo = list_photo;
            }

            public String getList_photo_filename() {
                return list_photo_filename;
            }

            public void setList_photo_filename(String list_photo_filename) {
                this.list_photo_filename = list_photo_filename;
            }

            public String getList_width() {
                return list_width;
            }

            public void setList_width(String list_width) {
                this.list_width = list_width;
            }

            public String getList_height() {
                return list_height;
            }

            public void setList_height(String list_height) {
                this.list_height = list_height;
            }

            public String getList_position() {
                return list_position;
            }

            public void setList_position(String list_position) {
                this.list_position = list_position;
            }

            public String getList_content() {
                return list_content;
            }

            public void setList_content(String list_content) {
                this.list_content = list_content;
            }

            public String getList_isbold() {
                return list_isbold;
            }

            public void setList_isbold(String list_isbold) {
                this.list_isbold = list_isbold;
            }

            public String getList_color() {
                return list_color;
            }

            public void setList_color(String list_color) {
                this.list_color = list_color;
            }

            public String getList_fontsize() {
                return list_fontsize;
            }

            public void setList_fontsize(String list_fontsize) {
                this.list_fontsize = list_fontsize;
            }

            public String getList_textalign() {
                return list_textalign;
            }

            public void setList_textalign(String list_textalign) {
                this.list_textalign = list_textalign;
            }

            public String getList_video() {
                return list_video;
            }

            public void setList_video(String list_video) {
                this.list_video = list_video;
            }

            public String getList_videofm() {
                return list_videofm;
            }

            public void setList_videofm(String list_videofm) {
                this.list_videofm = list_videofm;
            }

            public String getList_video_filename() {
                return list_video_filename;
            }

            public void setList_video_filename(String list_video_filename) {
                this.list_video_filename = list_video_filename;
            }

            public String getList_videotime() {
                return list_videotime;
            }

            public void setList_videotime(String list_videotime) {
                this.list_videotime = list_videotime;
            }

            public String getList_ispic() {
                return list_ispic;
            }

            public void setList_ispic(String list_ispic) {
                this.list_ispic = list_ispic;
            }
        }
    }
}
