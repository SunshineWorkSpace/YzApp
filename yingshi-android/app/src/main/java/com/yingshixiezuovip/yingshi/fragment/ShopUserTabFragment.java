package com.yingshixiezuovip.yingshi.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yingshixiezuovip.yingshi.HomeShopDetailActvity;
import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.adapter.ShopNewAdapter;
import com.yingshixiezuovip.yingshi.base.LazyFragment;
import com.yingshixiezuovip.yingshi.base.YingApplication;
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
 * Created by yuhua.gou on 2018/11/12.
 */

public class ShopUserTabFragment extends LazyFragment implements OnRefreshListener,OnLoadMoreListener {
    private String mType,mUid;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private ShopNewAdapter mAdapter;

    private List<ShopTypeModel.ShopType> mList=new ArrayList<>();
    private ShopTypeModel mShopTypeModel;
    private UserInfo mUserInfo;
    private boolean isMore = false;
    private int mPage = 0;
    private StaggeredGridLayoutManager layoutManager;
    private View emptyView;

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


        mAdapter = new ShopNewAdapter();
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setPreLoadNumber(2);
        initEmptyView();


    }


    private void initEmptyView(){
        LayoutInflater inflater=getActivity().getLayoutInflater();
        emptyView=inflater.inflate(R.layout.layout_empty,null);
    }
    @Override
    protected void initData(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mType = getArguments().getString("type");
            mUid=getArguments().getString("uid");
        }
        mPage=0;
        mUserInfo = SPUtils.getUserInfo(getActivity());


        mRecyclerView.setAdapter(mAdapter);
        //设置item之间的间隔
        SpacesItemDecoration decoration=new SpacesItemDecoration(16);
        mRecyclerView.addItemDecoration(decoration);
        //触发自动刷新
//        mRefreshLayout.autoRefresh();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                layoutManager.invalidateSpanAssignments(); //防止第一行到顶部有空白区域
            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ShopTypeModel.ShopType shopType=mList.get(position);
                Intent detail=new Intent(getActivity(), HomeShopDetailActvity.class);
                detail.putExtra("id",shopType.id);
                detail.putExtra("type", "delete");
                startActivity(detail);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //触发自动刷新
        mRefreshLayout.autoRefresh();
    }

    private void loadData(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("start", mPage);
        params.put("uid", mUid);
        if("1".equals(mType)){
            HttpUtils.doPost(TaskType.TASK_TYPE_SHOP_ALL, params, this);
        }else{
            HttpUtils.doPost(TaskType.TASK_TYPE_SHOP_NEW, params, this);
        }
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
            if(parent.getChildAdapterPosition(view)==0||parent.getChildAdapterPosition(view)==1){
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
            case TASK_TYPE_SHOP_ALL:
            case TASK_TYPE_SHOP_NEW:
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
                        mList.addAll(mShopTypeModel.data);
                        mAdapter.setNewData(mShopTypeModel.data);
                    }else{
                        mList.addAll(mShopTypeModel.data);
                        mAdapter.addData(mShopTypeModel.data);
                    }


                } else {
                    showMessage(R.string.data_load_failed);
                }
                System.out.println("商品page:"+mPage+" type:"+mType+" 数据条数："+mList.size()+" isMore："+isMore);
                if(isMore) {
                    mRefreshLayout.setNoMoreData(!isMore);
                }else{
                    mRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                }
                if(mList.size()==0){
                    mAdapter.setEmptyView(emptyView);
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
