package com.yingshixiezuovip.yingshi.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.model.BuyerOrderModel;
import com.yingshixiezuovip.yingshi.quote.roundview.RoundedImageView;
import com.yingshixiezuovip.yingshi.utils.ImageLoaderNew;
import com.yingshixiezuovip.yingshi.utils.PictureManager;

/**
 * 类名称:SellerOrderAdapter
 * 类描述:
 * 创建时间: 2018-11-12-23:33
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class SellerOrderAdapter extends BaseQuickAdapter<BuyerOrderModel.BuyerOderDetailModel, BaseViewHolder> {

    public SellerOrderAdapter(){
        super(R.layout.item_buyer_order);
    }
    public interface OnItemCleanClickListener {
        void OnItemCleanClick(View view, int position);
    }
    private BuyerOrderAdapter.OnItemCleanClickListener onItemCleanClickListener;
    public void setOnItemCleanClickListener(BuyerOrderAdapter.OnItemCleanClickListener mOnItemLongClickListener) {
        this.onItemCleanClickListener = mOnItemLongClickListener;
    }

    public interface OnItemBuyClickListener {
        void OnItemBuyClick(View view, int position);
    }
    private BuyerOrderAdapter.OnItemBuyClickListener onItemBuyClickListener;
    public void setOItemBuyClickListener(BuyerOrderAdapter.OnItemBuyClickListener mOnItemLongClickListener) {
        this.onItemBuyClickListener = mOnItemLongClickListener;
    }
    @Override
    protected void convert(final BaseViewHolder helper, BuyerOrderModel.BuyerOderDetailModel item) {
        RoundedImageView iv_order_au=helper.getView(R.id.iv_order_au);
        TextView tv_order_title=helper.getView(R.id.tv_order_title);
        TextView tv_order_type=helper.getView(R.id.tv_order_type);
        ImageView iv_order=helper.getView(R.id.iv_order);
        TextView tv_content=helper.getView(R.id.tv_content);
        TextView tv_order_price=helper.getView(R.id.tv_order_price);
        TextView tv_total_price=helper.getView(R.id.tv_total_price);
        TextView tv_order_pay=helper.getView(R.id.tv_order_pay);
        TextView tv_order_clean=helper.getView(R.id.tv_order_clean);
        PictureManager.displayHead(item.head,
                iv_order_au);
        tv_order_title.setText(item.nickname);
        switch (item.state){
            case "0":
                tv_order_type.setText("全部");
                tv_order_pay.setVisibility(View.GONE);
                tv_order_clean.setVisibility(View.GONE);
                break;
            case "1":
                tv_order_type.setText("等待卖家付款");
                tv_order_pay.setVisibility(View.VISIBLE);
                tv_order_clean.setVisibility(View.VISIBLE);
                tv_order_pay.setText("查看买家信息");
                tv_order_clean.setText("取消订单");
                break;
            case "2":
                tv_order_type.setText("已付款，设置物流信息");
                tv_order_pay.setVisibility(View.VISIBLE);
                tv_order_clean.setVisibility(View.VISIBLE);
                tv_order_pay.setText("填写物流信息");
                tv_order_clean.setText("买家收货信息");
                break;
            case "3":
                tv_order_type.setText("已经发货");
                tv_order_pay.setVisibility(View.VISIBLE);
                tv_order_clean.setVisibility(View.VISIBLE);
                tv_order_pay.setText("查看物流");
                tv_order_clean.setText("买家收货信息");
                break;
            case "4":
                tv_order_type.setText("已经完成");
                tv_order_pay.setVisibility(View.VISIBLE);
                tv_order_clean.setVisibility(View.VISIBLE);
                tv_order_pay.setText("查看物流");
                tv_order_clean.setText("联系买家");
                break;
            case "5":
                tv_order_type.setText("已取消");
                tv_order_pay.setVisibility(View.GONE);
                tv_order_clean.setVisibility(View.VISIBLE);
                tv_order_clean.setText("买家信息");
                break;
        }
        ImageLoaderNew.load(mContext,
                item.photo, iv_order);
        tv_content.setText(item.title);
        tv_order_price.setText("¥"+item.total);
        tv_total_price.setText("总计:¥"+item.total+"  (含运费¥0.00)");
        tv_order_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemBuyClickListener!=null){
                    int position = helper.getLayoutPosition();
                    onItemBuyClickListener.OnItemBuyClick(helper.itemView, position);
                }
            }
        });
        tv_order_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemCleanClickListener!=null){
                    int position = helper.getLayoutPosition();
                    onItemCleanClickListener.OnItemCleanClick(helper.itemView, position);
                }
            }
        });
   /*     ScaleImageView imageView = helper.getView(R.id.iv_shop);
        RoundedImageView iv_user_head=helper.getView(R.id.iv_user_head);
        TextView tv_price=helper.getView(R.id.tv_price);
        imageView.setInitSize(CommUtils.parseInt(item.width,0),
                CommUtils.parseInt(item.height,0));
        ImageLoaderNew.load(mContext,
                item.photo, imageView);
        PictureManager.displayHead(item.head,
                iv_user_head);
        tv_price.setText("￥"+item.price);*/
    }
}
