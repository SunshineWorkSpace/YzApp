package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.quote.flowlayout.FlowLayout;
import com.yingshixiezuovip.yingshi.quote.flowlayout.TagAdapter;

import java.util.ArrayList;

/**
 * Created by Resmic on 18/1/23.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class LableAdapter extends TagAdapter<String> {
    private Context context;

    public LableAdapter(Context context) {
        super(new ArrayList<String>());
        this.context = context;
    }

    @Override
    public View getView(FlowLayout parent, final int position, String s) {
        View view = View.inflate(context, R.layout.view_lable_item_layout, null);
        ((TextView) view.findViewById(R.id.lable_item_name)).setText(s);
        view.findViewById(R.id.lable_item_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(position);
            }
        });
        return view;
    }

    public void remove(int position) {
        mTagDatas.remove(position);
        notifyDataChanged();
    }

    public void add(String lable) {
        if (!mTagDatas.contains(lable)) {
            mTagDatas.add(lable);
            notifyDataChanged();
        }
    }

    public String getLables() {
        StringBuffer stringBuffer = new StringBuffer();

        for (String lab : mTagDatas) {
            stringBuffer.append(lab).append(",");
        }

        String str = stringBuffer.toString();
        if (TextUtils.isEmpty(str)) {
            return "";
        }

        return str.substring(0, str.lastIndexOf(","));
    }
}
