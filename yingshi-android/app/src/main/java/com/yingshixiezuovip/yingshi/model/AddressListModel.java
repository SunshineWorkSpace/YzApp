package com.yingshixiezuovip.yingshi.model;

import java.util.List;

/**
 * 用户地址
 * Created by yuhua.gou on 2018/11/11.
 */

public class AddressListModel {
    public List<AddressModel> data;
    public static class AddressModel extends ListDetailsModel.DetailsBaseItem {
        public String id;
        public String revcname;
        public String telphone;
        public String city;
        public String address;
    }
}
