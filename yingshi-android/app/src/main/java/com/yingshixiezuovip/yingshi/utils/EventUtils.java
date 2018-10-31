package com.yingshixiezuovip.yingshi.utils;

import com.yingshixiezuovip.yingshi.base.BaseEvent;
import com.yingshixiezuovip.yingshi.base.EventType;

import de.greenrobot.event.EventBus;

/**
 * Created by Resmic on 2017/5/17.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class EventUtils {
    public static void doPostEvent(EventType eventType) {
        EventBus.getDefault().post(new BaseEvent(eventType));
    }

    public static void doPostEvent(EventType eventType, Object data) {
        EventBus.getDefault().post(new BaseEvent(eventType, data));
    }
}
