package com.yingshixiezuovip.yingshi.model;

import java.util.List;

/**
 * Created by Resmic on 2017/5/8.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：预订模型
 */

public class BookModel {
    public List<BookItem> data;

    public static class BookItem {
        public String time;

        public int getYear() {
            return Integer.parseInt(time.split("-")[0]);
        }

        public int getMonth() {
            return Integer.parseInt(time.split("-")[1]);
        }

        public int getDay() {
            return Integer.parseInt(time.split("-")[2]);
        }
    }
}
