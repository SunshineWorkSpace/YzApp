package com.yingshixiezuovip.yingshi.minterface;

import android.view.View;

/**
 * Created by Resmic on 2017/5/11.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public interface OnAdapterClickListener {
    void onFollowClick(int userid, int follow);

    void onZanClick(int id);

    void onItemClick(View view, int position);

    void onBookClick(int id);

    void onHeadClick(int userid, String username);
}