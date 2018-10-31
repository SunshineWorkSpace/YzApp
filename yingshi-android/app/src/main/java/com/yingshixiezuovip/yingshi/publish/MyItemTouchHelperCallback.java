package com.yingshixiezuovip.yingshi.publish;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.yingshixiezuovip.yingshi.utils.L;

/**
 * Created by Resmic on 18/1/31.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {

    CallbackItemTouch callbackItemTouch; // interface

    public MyItemTouchHelperCallback(CallbackItemTouch callbackItemTouch) {
        this.callbackItemTouch = callbackItemTouch;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false; // swiped disabled
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN; // movements drag
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, dragFlags); // as parameter, action drag and flags drag
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (target.getAdapterPosition() == 0) {
            return false;
        }

        callbackItemTouch.itemTouchOnMove(viewHolder.getAdapterPosition(), target.getAdapterPosition()); // information to the interface
        L.d("onMoveonMove = " + getMovementFlags(recyclerView, viewHolder));
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        L.d("onSwipedonSwiped");
        // swiped disabled
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        L.d("clearViewclearView");
        callbackItemTouch.itemTouchOnFinish();
    }
}