package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingshixiezuovip.yingshi.R;
import com.yingshixiezuovip.yingshi.adapter.FontColorAdapter;
import com.yingshixiezuovip.yingshi.adapter.FontSizeAdapter;
import com.yingshixiezuovip.yingshi.base.BasePopupWindow;
import com.yingshixiezuovip.yingshi.model.FontModel;

/**
 * Created by Resmic on 18/1/23.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class FontWindow extends BasePopupWindow {
    private RecyclerView sizeRecylerView;
    private RecyclerView colorRecylerView;

    private FontSizeAdapter sizeAdapter;
    private FontColorAdapter colorAdapter;

    private FontModel fontModel;

    public FontWindow(Context mContext) {
        super(mContext, false, true);
        setWidthHeight(220, 260);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        int[] fontArrs = mContext.getResources().getIntArray(R.array.font_sizes);
        sizeAdapter = new FontSizeAdapter(mContext, fontArrs);
        sizeAdapter.setOnSizeSelectedListener(new FontSizeAdapter.OnSizeSelectedListener() {
            @Override
            public void onSizeSelected(int font) {
                ((TextView) findViewById(R.id.font_tv_size)).setText(font + "px");
                fontModel.setFont(font);
            }
        });
        sizeRecylerView = (RecyclerView) findViewById(R.id.font_size_recylerView);
        sizeRecylerView.setLayoutManager(linearLayoutManager);
        sizeRecylerView.setAdapter(sizeAdapter);

        String[] colorArrs = mContext.getResources().getStringArray(R.array.font_colors);
        colorAdapter = new FontColorAdapter(mContext, colorArrs);
        colorAdapter.setOnColorSelectedListener(new FontColorAdapter.OnColorSelectedListener() {
            @Override
            public void onColorSelected(String color) {
                ((CircleView) findViewById(R.id.font_tv_color)).setColor(color);
                fontModel.setColor(color);
            }
        });
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        colorRecylerView = (RecyclerView) findViewById(R.id.font_color_recylerView);
        colorRecylerView.setLayoutManager(linearLayoutManager);
        colorRecylerView.setAdapter(colorAdapter);

        findViewById(R.id.font_iv_blod).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontModel.setBlod(!fontModel.isBlod());
                initUI();
            }
        });
        findViewById(R.id.font_iv_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontModel.setCenter(!fontModel.isCenter());
                initUI();
            }
        });
    }

    public void show(FontModel fontModel) {
        this.fontModel = fontModel;
        initUI();
        super.show();
    }

    private void initUI() {
        ((ImageView) findViewById(R.id.font_iv_blod))
                .setImageResource(fontModel.isBlod() ? R.mipmap.jiacu_checked : R.mipmap.jiacu_normal);
        ((ImageView) findViewById(R.id.font_iv_center))
                .setImageResource(fontModel.isCenter() ? R.mipmap.duiqi_checked : R.mipmap.duiqi_normal);
        ((TextView) findViewById(R.id.font_tv_size)).setText(fontModel.getFont() + "px");
        ((CircleView) findViewById(R.id.font_tv_color)).setColor(fontModel.getColor());
    }

    @Override
    public View createView() {
        return View.inflate(mContext, R.layout.window_font_layout, null);
    }

    public FontModel getFontModel() {
        return fontModel;
    }
}
