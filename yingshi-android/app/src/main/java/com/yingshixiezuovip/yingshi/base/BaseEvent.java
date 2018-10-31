package com.yingshixiezuovip.yingshi.base;

/**
 * Created by Resmic on 2017/5/17.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class BaseEvent {
    private Object data;
    private EventType eventType;


    public BaseEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public BaseEvent(EventType eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

}
