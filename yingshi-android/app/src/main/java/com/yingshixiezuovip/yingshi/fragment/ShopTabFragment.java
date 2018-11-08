package com.yingshixiezuovip.yingshi.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.adapter.ShopAdapter;
import com.yingshixiezuovip.yingshi.base.LazyFragment;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.ShopTypeModel;
import com.yingshixiezuovip.yingshi.model.UserInfo;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuhua.gou on 2018/11/7.
 */

public class ShopTabFragment extends LazyFragment implements OnRefreshListener,OnLoadMoreListener {
    private String mType;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private ShopAdapter mAdapter;

    private List<ShopTypeModel.ShopType> mList=new ArrayList<>();
    private ShopTypeModel mShopTypeModel;
    private UserInfo mUserInfo;
    private boolean isMore = false;
    private int mPage = 0;
    private StaggeredGridLayoutManager  layoutManager;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tab_shop;
    }


    @Override
    protected void initViews() {
        mRefreshLayout=(SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRecyclerView=(RecyclerView) findViewById(R.id.rv);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
        mRefreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
        layoutManager=new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(layoutManager);

    }



    @Override
    protected void initData(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mType = getArguments().getString("type");
        }
        mPage=0;
        mUserInfo = SPUtils.getUserInfo(getActivity());


        mAdapter=new ShopAdapter(getActivity(), mList);
        mRecyclerView.setAdapter(mAdapter);
        //设置item之间的间隔
        SpacesItemDecoration decoration=new SpacesItemDecoration(16);
        mRecyclerView.addItemDecoration(decoration);
        //触发自动刷新
        mRefreshLayout.autoRefresh();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                layoutManager.invalidateSpanAssignments(); //防止第一行到顶部有空白区域
            }
        });

    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }

    private void loadData(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("type", mType);
        params.put("start", mPage);
        HttpUtils.doPost(TaskType.TASK_TYPE_SHOPLIST, params, this);
    }

    @Override
    protected void lazyLoad() {

    }
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space=space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.left=space;
            outRect.right=space;
            outRect.bottom=space;
            if(parent.getChildAdapterPosition(view)==0){
                outRect.top=space;
            }
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        mPage = 0;
        loadData();
    }



    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mPage++;
      loadData();
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }
        switch (type){
            case TASK_TYPE_SHOPLIST:
                if(mPage==0) {
                    mRefreshLayout.finishRefresh();
                }else{
                    mRefreshLayout.finishLoadMore();
                }
                mShopTypeModel = GsonUtil.fromJson(result.toString(), ShopTypeModel.class);
                if (mShopTypeModel != null) {
                    System.out.println("商品page:"+mPage+" type:"+mType+" 数据条数："+mShopTypeModel.data.size());
                    isMore=isHaveMoreDate(mShopTypeModel);
                    if(mPage==0){
                        if(mList.size()>0){
                            mList.clear();
                        }
                    }

                    if(mShopTypeModel.data.size()>0){
                        mList.addAll(mShopTypeModel.data);
                    }

                } else {
                    showMessage(R.string.data_load_failed);
                }
                mAdapter.notifyDataSetChanged();
                System.out.println("商品page:"+mPage+" type:"+mType+" 数据条数："+mList.size()+" isMore："+isMore);
                if(isMore) {
                    mRefreshLayout.setNoMoreData(!isMore);
                }else{
                    mRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                }
                break;
        }
    }

    /**
     * 判断是否有更多数据
     * @param shopTypeModel
     * @return
     */
    public boolean isHaveMoreDate(ShopTypeModel shopTypeModel){
        boolean isHaveMore=true;
        if(null==shopTypeModel){
            isHaveMore=false;
        }else {
            List<ShopTypeModel.ShopType> datas=shopTypeModel.data;
            if(null==datas){
                isHaveMore=false;
            }else {
                if(datas.size()==0||datas.size()<10){
                    isHaveMore=false;
                }
            }
        }
        return  isHaveMore;
    }
    @Override
    public void taskIsCanceled(TaskType type) {

    }
}
