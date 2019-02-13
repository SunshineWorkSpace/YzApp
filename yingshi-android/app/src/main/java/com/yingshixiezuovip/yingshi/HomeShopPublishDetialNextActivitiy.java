package com.yingshixiezuovip.yingshi;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.Manifest;

import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yingshixiezuovip.yingshi.adapter.SelectImgsAdapter;
import com.yingshixiezuovip.yingshi.adapter.holder.SelectImgHolder;
import com.yingshixiezuovip.yingshi.base.BaseActivity;
import com.yingshixiezuovip.yingshi.custom.AlOssImgModel;
import com.yingshixiezuovip.yingshi.custom.CPDialog;
import com.yingshixiezuovip.yingshi.datautils.HttpUtils;
import com.yingshixiezuovip.yingshi.datautils.TaskType;
import com.yingshixiezuovip.yingshi.model.HomeListModel;
import com.yingshixiezuovip.yingshi.model.ShopDetailUpDataModel;
import com.yingshixiezuovip.yingshi.utils.DefaultItemTouchHelpCallback;
import com.yingshixiezuovip.yingshi.utils.DefaultItemTouchHelper;
import com.yingshixiezuovip.yingshi.utils.GsonUtil;
import com.yingshixiezuovip.yingshi.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import rx.functions.Action1;

/**
 * 类名称:HomeShopPublishDetialNextActivitiy
 * 类描述:第二步
 * 创建时间: 2018-11-06-10:15
 * 创建人: sht
 * 修改人: sht
 * 修改备注:
 */
public class HomeShopPublishDetialNextActivitiy extends BaseActivity implements SelectImgsAdapter.Callback {
    private static final int REQUEST_IMAGE = 1001;
    /**
     * 默认最多选择6张
     */
    int maxImg = 9;
    private RecyclerView recycleView;
    String add = "添加";

    ArrayList<String> mImgs;

    SelectImgsAdapter adapter;

    DefaultItemTouchHelper helper;
    private EditText et_note;
    private TextView tv_note;

    ShopDetailUpDataModel shopDetailUpDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_publish_next);
        setActivityTitle("发布页(2/3)");
        pushActivity(this);
        ((TextView) findViewById(R.id.right_btn_name)).setText("下一步");
        findViewById(R.id.right_btn_submit).setVisibility(View.VISIBLE);
        findViewById(R.id.right_btn_submit).setOnClickListener(this);
        recycleView = (RecyclerView) findViewById(R.id.recycleView);
        et_note = (EditText) findViewById(R.id.et_note);
        tv_note = (TextView) findViewById(R.id.tv_note);
        mImgs = new ArrayList<>();

        if (!getIntent().getBooleanExtra("isFirst", true)) {
            shopDetailUpDataModel = (ShopDetailUpDataModel) getIntent().getSerializableExtra("shopDetailUpDataModel");
            List<ShopDetailUpDataModel.PhotoImageItem> msgList = shopDetailUpDataModel.data.photoList;
            if (msgList.size() < 9) {
                mImgs.add(add);
            }
            for (int i = 0; i < msgList.size(); i++) {
                mImgs.add(msgList.get(i).photo);
            }
            et_note.setText(shopDetailUpDataModel.data.content);
        } else {
            mImgs.add(add);
        }


        adapter = new SelectImgsAdapter(mImgs, this);
        adapter.setCallback(this);
        helper = new DefaultItemTouchHelper(new DefaultItemTouchHelpCallback(onItemTouchCallbackListener));
        helper.attachToRecyclerView(recycleView);
        recycleView.setLayoutManager(new GridLayoutManager(this, 3));
        recycleView.addItemDecoration(new SpaceItemDecoration(3, 11, false));
        recycleView.setAdapter(adapter);

        et_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_note.setText(s.length() + "/1000");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.right_btn_submit:
                checkDate();
                break;
        }
    }

    private void checkDate() {
        if (TextUtils.isEmpty(et_note.getText().toString())) {
            showMessage("请输入您的商品介绍");
            return;
        }
        if (mImgs.size() <= 1) {
            showMessage("请选择图片");
            return;
        }
        Intent it = new Intent(this, HomeShopPublishDetailEndActivity.class);
        it.putExtra("title", getIntent().getStringExtra("title"));
        it.putExtra("singleprice", getIntent().getStringExtra("singleprice"));
        it.putExtra("num", getIntent().getStringExtra("num"));
        it.putExtra("new", getIntent().getStringExtra("new"));
        it.putExtra("phone", getIntent().getStringExtra("phone"));
        it.putExtra("area", getIntent().getStringExtra("area"));
        it.putExtra("areadetail", getIntent().getStringExtra("areadetail"));
        it.putExtra("type", getIntent().getStringExtra("type"));
        it.putExtra("content", et_note.getText().toString());
        it.putStringArrayListExtra("infoList", mImgs);
        //========
        it.putExtra("isFirst", getIntent().getBooleanExtra("isFirst", true));
        if (!getIntent().getBooleanExtra("isFirst", true))
            it.putExtra("shopDetailUpDataModel", shopDetailUpDataModel);
        startActivity(it);
//        loadData();
    }

    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
//             滑动删除的时候，从数据源移除，并刷新这个Item。
            if (mImgs != null) {
                mImgs.remove(adapterPosition);
                adapter.notifyItemRemoved(adapterPosition);
            }
        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            //除了+按钮不允许移动
            if (mImgs != null) {
                if (!mImgs.get(targetPosition).equals(add)) {
                    // 更换数据源中的数据Item的位置
                    Collections.swap(mImgs, srcPosition, targetPosition);
                    // 更新UI中的Item的位置，主要是给用户看到交互效果
                    adapter.notifyItemMoved(srcPosition, targetPosition);
                }
                return true;
            }
            //最后一个不允许移动
//            if (mImgs != null && targetPosition != mImgs.size() -1) {
//                // 更换数据源中的数据Item的位置
//                Collections.swap(mImgs, srcPosition, targetPosition);
//                // 更新UI中的Item的位置，主要是给用户看到交互效果
//                adapter.notifyItemMoved(srcPosition, targetPosition);
//                return true;
//            }
            return false;
        }

        @Override
        public void complete() {
            //如果完成之后不刷新会导致删除的时候下标错误的情况
            adapter.notifyDataSetChanged();
            showUrl();
        }
    };


    @Override
    public void startDrag(SelectImgHolder holder) {
        helper.startDrag(holder);
    }

    @Override
    public void delPicture(final String url, final int position) {
        CPDialog dialog = new CPDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("确认要删除?");
        dialog.setPositiveButton("取消", null);
        dialog.setNegativeButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除照片

                mImgs.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(0, mImgs.size());
                isNeedShowAdd();
            }
        }).show();
    }

    public void addImgs(String img) {
        /**添加的图片需要在+之前**/
        int index = mImgs.size() - 1;
        mImgs.add(index, img);
        adapter.notifyDataSetChanged();
        isNeedShowAdd();
    }

    /**
     * 是否显示添加图片的按钮
     **/
    void isNeedShowAdd() {
        /**满足6张图片则隐藏+**/
        if (mImgs.size() > maxImg)
            mImgs.remove(mImgs.size() - 1);
        else if (!mImgs.contains(add))
            mImgs.add(add);
        showUrl();
    }

    public List<String> getImgs() {
        /**移除添加文字**/
        if (mImgs != null)
            mImgs.remove(add);
        return mImgs;
    }

    @Override
    public void addPicture() {
        RxPermissions rxPermissions = new RxPermissions(this); // where this is an Activity instance
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        /**选择拍照**/
                        Intent intent = new Intent(HomeShopPublishDetialNextActivitiy.this, MultiImageSelectorActivity.class);
                        // whether show camera
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, granted);
                        // max select image amount
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, (9 - (mImgs.size() - 1)));
                        // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                        startActivityForResult(intent, REQUEST_IMAGE);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                for (int i = 0; i < path.size(); i++) {
                    addImgs(path.get(i));
                }

            }
        }
    }

    /**
     * GridView间距
     **/
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public SpaceItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = SystemUtil.dpToPx(HomeShopPublishDetialNextActivitiy.this, spacing);
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }

    }

    /**
     * 打印路劲
     */
    void showUrl() {
        int count = mImgs.size();
  /*      tvUrl.setText("");
        for (int i=0;i<count;i++){
            if(mImgs.get(i).equals(add)) continue;
            tvUrl.append(i+"->"+mImgs.get(i)+"\n");
        }*/
    }

}
