package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.HotModel;
import com.yingshixiezuovip.yingshi.quote.flowlayout.FlowLayout;
import com.yingshixiezuovip.yingshi.quote.flowlayout.TagAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Resmic on 2017/11/21.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */
public class SearchKeywordAdapter extends TagAdapter<HotModel> {
    private Context mContext;
    private int type;

    public SearchKeywordAdapter(Context context, int type) {
        super(new ArrayList<HotModel>());
        this.mContext = context;
        this.type = type;
    }

    @Override
    public View getView(FlowLayout parent, final int position, final HotModel tagModel) {
        final View view = View.inflate(mContext, R.layout.view_tag_item_layout, null);
        final TextView textView = (TextView) view.findViewById(R.id.tagitem_textview);
        textView.setText(tagModel.getKeywords());
        if (type != 1) {
            view.setBackgroundResource(position % 2 == 0 ? R.drawable.tag_checked_shape : R.drawable.tag_normal_shape);
            textView.setTextColor(mContext.getResources().getColor(position % 2 == 0 ? R.color.colorRed : R.color.line_color));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onKeywordClickListener != null) {
                    onKeywordClickListener.onKeywordClick(position, tagModel);
                }
            }
        });

        return view;
    }

    public void setDatas(List<HotModel> tagModels) {
        if (tagModels == null) {
            tagModels = new ArrayList<>();
        }

        mTagDatas.clear();
        mTagDatas.addAll(tagModels);
        notifyDataChanged();
    }

    public void remove(int mDeletePosition) {
        getDatas().remove(mDeletePosition);
        notifyDataChanged();
    }

    public interface OnKeywordClickListener {
        void onKeywordClick(int position, HotModel tagModel);
    }

    @Override
    public void add(HotModel tagModel) {
        getDatas().add(tagModel);
        notifyDataChanged();
    }

    private OnKeywordClickListener onKeywordClickListener;

    public void setOnDeleteClickListener(OnKeywordClickListener onKeywordClickListener) {
        this.onKeywordClickListener = onKeywordClickListener;
    }

}
