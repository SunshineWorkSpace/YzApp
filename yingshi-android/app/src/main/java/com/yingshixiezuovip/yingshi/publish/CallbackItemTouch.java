package com.yingshixiezuovip.yingshi.publish;

/**
 * Created by Resmic on 18/1/31.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public interface CallbackItemTouch {

    /**
     * Called when an item has been dragged
     * @param oldPosition start position
     * @param newPosition end position
     */
    void itemTouchOnMove(int oldPosition,int newPosition);

    void itemTouchOnFinish();
}
