package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yingshixiezuovip.yingshi.adapter.AddressAdapter;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.base.BaseResp;
import com.yingshixiezuovip.yingshi.custom.AlertWindow;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.AddressListModel;
import com.yingshixiezuovip.yingshi.model.PlaceModel;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用户地址列表
 * Created by yuhua.gou on 2018/11/11.
 */

public class UserAddressListActivity extends BaseActivity implements OnRefreshListener,OnLoadMoreListener {
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private AddressAdapter mAdapter;
    private int mPage;
    private AddressListModel addressListModel;
    private List<AddressListModel.AddressModel> mList=new ArrayList<>();
    private boolean isMore = false;
    private AlertWindow mStatusWindow;
    private int mChoicePostion=-1;
    private TextView tv_add_new_address;
    private final int REQUEST_ADD_ADDRESS=11;
    private final int REQUEST_EDIT_ADDRESS=12;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_address,  R.string.title_activity_user_address);
        initView();
    }
    private void initView() {
        mRefreshLayout= (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRecyclerView= (RecyclerView) findViewById(R.id.rv);
        tv_add_new_address= (TextView) findViewById(R.id.tv_add_new_address);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
        mRefreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）

        mAdapter = new AddressAdapter();
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setPreLoadNumber(2);
        mRecyclerView.setAdapter(mAdapter);
      SpacesItemDecoration decoration=new SpacesItemDecoration(25,18);
        mRecyclerView.addItemDecoration(decoration);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //触发自动刷新
        mRefreshLayout.autoRefresh();
        tv_add_new_address.setOnClickListener(this);
        mType=getIntent().getIntExtra("type",0);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {

                view.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAddressDialog(position);
                    }
                });
                if(mType==1) {
                    view.findViewById(R.id.ll_user_address).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddressListModel.AddressModel addressMode = mList.get(position);
                            Intent choiceAddress = new Intent();
                            choiceAddress.putExtra("city", addressMode.city);
                            choiceAddress.putExtra("address", addressMode.address);
                            choiceAddress.putExtra("id", addressMode.id);
                            choiceAddress.putExtra("telphone", addressMode.telphone);
                            choiceAddress.putExtra("revcname", addressMode.revcname);
                            UserAddressListActivity.this.setResult(RESULT_OK, choiceAddress);
                            finish();
                        }
                    });
                }

                view.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editAddress(position);
                    }
                });
            }
        });
    }

    public void editAddress(int postion){
        mChoicePostion=postion;
        Intent editAddress=new Intent(UserAddressListActivity.this,
                HomeShopAddAddressAcivity.class);
        editAddress.putExtra("address",mList.get(mChoicePostion));
        startActivityForResult(editAddress,REQUEST_EDIT_ADDRESS);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()){
            case R.id.tv_add_new_address:
                Intent addAddress=new Intent(UserAddressListActivity.this,HomeShopAddAddressAcivity.class);
                startActivityForResult(addAddress,REQUEST_ADD_ADDRESS);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case REQUEST_ADD_ADDRESS:
                    mPage=0;
                    loadAddress();
                    break;
                case REQUEST_EDIT_ADDRESS:
                    String city=data.getStringExtra("city");
                    String name=data.getStringExtra("name");
                    String phone=data.getStringExtra("phone");
                    String address=data.getStringExtra("address");
                    String id=data.getStringExtra("id");
                    AddressListModel.AddressModel addressModel=new AddressListModel.AddressModel();
                    addressModel.city=city;
                    addressModel.id=id;
                    addressModel.revcname=name;
                    addressModel.telphone=phone;
                    addressModel.address=address;
                    mAdapter.setData(mChoicePostion,addressModel);
                    mChoicePostion=-1;
                    break;
            }
        }
    }

    public void loadAddress(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("start", mPage+"");
        HttpUtils.doPost(TaskType.TASK_TYPE_MY_ADDRESS, params, this);
    }
    @Override
    public void taskFinished(TaskType type, Object result, boolean isHistory) {
        if (result instanceof Throwable) {
            showMessage(((Throwable) result).getMessage());
            return;
        }

        switch (type) {
            case TASK_TYPE_MY_ADDRESS:
                System.out.println("我的地址:"+result.toString());
                if(mPage==0) {
                    mRefreshLayout.finishRefresh();
                }else{
                    mRefreshLayout.finishLoadMore();
                }
                addressListModel= GsonUtil.fromJson(result.toString(), AddressListModel.class);

                if (addressListModel != null) {
                    isMore=isHaveMoreDate(addressListModel);
                    if(mPage==0){
                        if(mList.size()>0){
                            mList.clear();
                        }
                        mList.addAll(addressListModel.data);
                        mAdapter.setNewData(addressListModel.data);
                    }else{
                        mList.addAll(addressListModel.data);
                        mAdapter.addData(addressListModel.data);
                    }


                } else {
                    showMessage(R.string.data_load_failed);
                }
                if(isMore) {
                    mRefreshLayout.setNoMoreData(!isMore);
                }else{
                    mRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                }
                break;

            case TASK_TYPE_DELETE_ADDRESS:
                try {
                    mLoadWindow.cancel();
                    BaseResp baseResp= GsonUtil.fromJson(result.toString(), PlaceModel.class);
                    if(200==baseResp.result.code){
                        showMessage("删除成功");
                        mAdapter.remove(mChoicePostion);
                        mChoicePostion=-1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage("删除失败");
                }
                break;
        }
    }
    public void deleteAddressDialog(final int postion){
        mChoicePostion=postion;
        mStatusWindow = new AlertWindow(this, false);
        mStatusWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.alert_btn_submit) {
                    deleteAddress(postion);
                }
            }
        });
        mStatusWindow.show("", "是否要删除该地址?", "取消", "确认");

    }

    /**
     * 删除地址
     * @param postion
     */
    public void deleteAddress(int postion){
        mLoadWindow.show(R.string.text_request);
        String id=mList.get(postion).id;
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", mUserInfo.token);
        params.put("id", id);
        HttpUtils.doPost(TaskType.TASK_TYPE_DELETE_ADDRESS, params, this);
    }

    /**
     * 判断是否有更多数据
     * @param shopTypeModel
     * @return
     */
    public boolean isHaveMoreDate(AddressListModel shopTypeModel){
        boolean isHaveMore=true;
        if(null==shopTypeModel){
            isHaveMore=false;
        }else {
            List<AddressListModel.AddressModel> datas=shopTypeModel.data;
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
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mPage++;
        loadAddress();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mPage=0;
        loadAddress();
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int topspace,bottomspace;

        public SpacesItemDecoration(int topspace,int bottomspace) {
            this.topspace=topspace;
            this.bottomspace=bottomspace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            if(parent.getChildAdapterPosition(view)==0){
                view.setBackgroundColor(Color.parseColor("#f8f8f8"));
                outRect.top=topspace;
                outRect.bottom=bottomspace;
            }else{
                outRect.bottom=bottomspace;
            }
        }
    }

}
