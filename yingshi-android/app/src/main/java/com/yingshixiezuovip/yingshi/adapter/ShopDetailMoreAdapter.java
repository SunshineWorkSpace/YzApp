package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
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
 * 更多
 * Created by yuhua.gou on 2018/11/13.
 */

public class ShopDetailMoreAdapter extends BaseAdapter<ShopMoreModel, RecyclerView.ViewHolder>{

    private ItemClickListener listener;

    public ShopDetailMoreAdapter(Context context, List<ShopMoreModel> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       return new ItemDefaultHolder(mInflater.inflate(R.layout.item_shop_more, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ShopMoreModel item = mItems.get(position);
        if (item != null) {
            final ItemDefaultHolder itemDefaultHolder = (ItemDefaultHolder) holder;
            itemDefaultHolder.tv_title.setText(item.title);
            itemDefaultHolder.tv_content.setText(item.content);
            itemDefaultHolder.tv_content.setVisibility(item.showDetails);
            if(item.showDetails==View.GONE){
                itemDefaultHolder.iv_rrow.setImageResource(R.mipmap.data_btn_down);
            }else{
                itemDefaultHolder.iv_rrow.setImageResource(R.mipmap.data_btn_up);
            }
            itemDefaultHolder.ll_title.setOnClickListener(new View.OnClickListener() {
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

        private TextView tv_title;
        private ImageView iv_rrow;
        private LinearLayout ll_title;
        private TextView tv_content;
        public ItemDefaultHolder(View view) {
            super(view);
            tv_title= (TextView) itemView.findViewById(R.id.tv_title);
            iv_rrow= (ImageView) itemView.findViewById(R.id.iv_rrow);
            ll_title= (LinearLayout) itemView.findViewById(R.id.ll_title);
            tv_content= (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

}

