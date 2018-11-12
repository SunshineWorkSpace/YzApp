package com.yingshixiezuovip.yingshi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.ConsumerPriceModel;
import com.yingshixiezuovip.yingshi.model.LogisticsDetailModel;

import java.util.List;

/**
 * 类名称:LookLogisticsAdapter
 * 类描述:
 * 创建时间: 2018-11-12-17:52
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class LookLogisticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //获取从Activity中传递过来每个item的数据集合
    private List<LogisticsDetailModel.LogisticsDetailBeanModel> mDatas;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private LookLogisticsAdapter.OnItemClickListener mOnItemClickListener;
    private LookLogisticsAdapter.OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(LookLogisticsAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(LookLogisticsAdapter.OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    //构造函数
    public LookLogisticsAdapter(Context context, List<LogisticsDetailModel.LogisticsDetailBeanModel> list) {
        this.mDatas = list;
        this.context = context;


    }


    //创建View，如果是HeaderView或者是FooterView，直接在Holder中返回
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wx, parent, false);

        return new LookLogisticsAdapter.ListHolder(layout);
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
            if(position==0){
                ((ListHolder)holder).view_up.setBackgroundColor(context.getResources().getColor(R.color.color_transparent));
            }else {
                ((ListHolder)holder).view_up.setBackgroundColor(context.getResources().getColor(R.color.blue_CCE1FB));
            }
            ((ListHolder) holder).tv_content.setText(mDatas.get(position).context);
            ((ListHolder) holder).tv_time.setText(mDatas.get(position).ftime);
          /*  ((LookLogisticsAdapter.ListHolder) holder).rb_tv.setText("消保金价格¥"+mDatas.get(position).price);
            if(mDatas.get(position).isClick){
                ((LookLogisticsAdapter.ListHolder) holder).rb_tv.setChecked(true);
            }else {
                ((LookLogisticsAdapter.ListHolder) holder).rb_tv.setChecked(false);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //在这里面加载ListView中的每个item的布局
    class ListHolder extends RecyclerView.ViewHolder {
        TextView tv_time,tv_content;
        View view_up;
        public ListHolder(View itemView) {
            super(itemView);
            //如果是headerview或者是footerview,直接返回
            view_up=(View)itemView.findViewById(R.id.view_up);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_content=(TextView)itemView.findViewById(R.id.tv_content);

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
    public void refreshItem(List<LogisticsDetailModel.LogisticsDetailBeanModel> newDatas) {
        mDatas.removeAll(mDatas);
        mDatas.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addData(int position, List<LogisticsDetailModel.LogisticsDetailBeanModel> newDatas) {
//        mDatas.add(position, "Insert One");
        notifyItemChanged(position);
  /*      mDatas.removeAll(mDatas);
        mDatas.addAll(newDatas);*/
//        notifyItemChanged(position);
    }

}
