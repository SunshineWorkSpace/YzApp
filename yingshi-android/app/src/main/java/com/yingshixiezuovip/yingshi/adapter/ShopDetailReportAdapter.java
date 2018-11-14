package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.ShopMoreModel;

import java.util.List;

/**
 * Created by yuhua.gou on 2018/11/14.
 */

public class ShopDetailReportAdapter extends BaseAdapter<ShopMoreModel, RecyclerView.ViewHolder>{

    private ItemClickListener listener;

    public ShopDetailReportAdapter(Context context, List<ShopMoreModel> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemDefaultHolder(mInflater.inflate(R.layout.item_report, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ShopMoreModel item = mItems.get(position);
        if (item != null) {
            final ItemDefaultHolder itemDefaultHolder = (ItemDefaultHolder) holder;
            itemDefaultHolder.tv_report_type.setText(item.title);
            if(item.isCheck){
                itemDefaultHolder.iv_checked.setVisibility(View.VISIBLE);
                itemDefaultHolder.iv_checked.setImageResource(R.mipmap.check_green);
                itemDefaultHolder.tv_report_type.setTextColor(Color.parseColor("#1B834F"));
            }else {
                itemDefaultHolder.iv_checked.setVisibility(View.GONE);
                itemDefaultHolder.tv_report_type.setTextColor(Color.parseColor("#424242"));
            }
            itemDefaultHolder.ll_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=listener){
                        listener.onItemClick(v,position);
                    }
                }
            });

        }
    }
    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(View view,int position);
    }

    public static class ItemDefaultHolder extends RecyclerView.ViewHolder {

        private TextView tv_report_type;
        private ImageView iv_checked;
        private LinearLayout ll_report;
        public ItemDefaultHolder(View view) {
            super(view);
            tv_report_type= (TextView) itemView.findViewById(R.id.tv_report_type);
            iv_checked=(ImageView) itemView.findViewById(R.id.iv_checked);
            ll_report= (LinearLayout) itemView.findViewById(R.id.ll_report);
        }
    }

}

