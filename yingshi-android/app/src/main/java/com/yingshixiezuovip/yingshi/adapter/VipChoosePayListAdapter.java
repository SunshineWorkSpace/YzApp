package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.ConsumerPriceModel;
import com.yingshixiezuovip.yingshi.model.VipChoosePayModel;

import java.util.List;

/**
 * 类名称:VipChoosePayListActivity
 * 类描述:
 * 创建时间: 2018-11-13-14:08
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class VipChoosePayListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //获取从Activity中传递过来每个item的数据集合
    private List<VipChoosePayModel.VipChoosePayBeanModel> mDatas;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private VipChoosePayListAdapter.OnItemClickListener mOnItemClickListener;
    private VipChoosePayListAdapter.OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(VipChoosePayListAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(VipChoosePayListAdapter.OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    //构造函数
    public VipChoosePayListAdapter(Context context, List<VipChoosePayModel.VipChoosePayBeanModel> list) {
        this.mDatas = list;
        this.context = context;


    }


    //创建View，如果是HeaderView或者是FooterView，直接在Holder中返回
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_xbj, parent, false);

        return new VipChoosePayListAdapter.ListHolder(layout);
    }


    //绑定View，这里是根据返回的这个position的类型，从而进行绑定的，   HeaderView和FooterView, 就不同绑定了
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        try {
            //判断是否设置了监听器
            if (mOnItemClickListener != null) {
                //为ItemView设置监听器
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition(); // 1
                        mOnItemClickListener.onItemClick(holder.itemView, position); // 2
                    }
                });
            }
            if (mOnItemLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = holder.getLayoutPosition();
                        mOnItemLongClickListener.onItemLongClick(holder.itemView, position);
                        //返回true 表示消耗了事件 事件不会继续传递
                        return true;
                    }
                });
            }
            ((VipChoosePayListAdapter.ListHolder) holder).rb_tv.setText("年费"+mDatas.get(position).year+"年"+"  原价¥"
            +mDatas.get(position).oprice+"  现价¥"+mDatas.get(position).price);
            if(mDatas.get(position).isClick){
                ((VipChoosePayListAdapter.ListHolder) holder).rb_tv.setChecked(true);
            }else {
                ((VipChoosePayListAdapter.ListHolder) holder).rb_tv.setChecked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //在这里面加载ListView中的每个item的布局
    class ListHolder extends RecyclerView.ViewHolder {
        RadioButton rb_tv;
        public ListHolder(View itemView) {
            super(itemView);
            //如果是headerview或者是footerview,直接返回
            rb_tv = (RadioButton) itemView.findViewById(R.id.rb_tv);

        }
    }

    //返回View中Item的个数，这个时候，总的个数应该是ListView中Item的个数加上HeaderView和FooterView
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 刷新数据
     */
    public void refreshItem(List<VipChoosePayModel.VipChoosePayBeanModel> newDatas) {
        mDatas.removeAll(mDatas);
        mDatas.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addData(int position, List<VipChoosePayModel.VipChoosePayBeanModel> newDatas) {
//        mDatas.add(position, "Insert One");
        notifyItemChanged(position);
  /*      mDatas.removeAll(mDatas);
        mDatas.addAll(newDatas);*/
//        notifyItemChanged(position);
    }

}
