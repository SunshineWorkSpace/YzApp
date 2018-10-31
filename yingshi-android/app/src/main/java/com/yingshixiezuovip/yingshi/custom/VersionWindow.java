package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;
import com.yingshixiezuovip.yingshi.model.VersionModel;

/**
 * Created by Resmic on 18/1/19.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class VersionWindow extends BasePopupWindow {
    public VersionWindow(Context mContext) {
        super(mContext);
    }

    public void showVersion(VersionModel versionModel) {
        ((TextView) findViewById(R.id.dialog_item1)).setText("最新版本" + versionModel.getVersion() + "\n" + "有最新的版本可用，是否更新？");
        super.show();

        findViewById(R.id.dialog_item2).setOnClickListener(this);
        findViewById(R.id.dialog_item3).setOnClickListener(this);
    }

    @Override
    public View createView() {
        return LayoutInflater.from(mContext).inflate(R.layout.window_version_layout, null);
    }
}
