package com.infoedge.trackerBinder;

import java.util.LinkedList;

/**
 * Created by nishant on 20/7/16.
 */

public class CapturedEventsContainer {

    private static volatile CapturedEventsContainer mInstance;

    private LinkedList<String> mEvents = new LinkedList<>();
    private int mEventsListSize = 50;

    public static CapturedEventsContainer getInstance() {
        if (mInstance == null) {
            synchronized (CapturedEventsContainer.class) {
                if (mInstance == null) {
                    return mInstance = new CapturedEventsContainer();
                }
            }
        }
        return mInstance;
    }

    void addEvent(String event) {
        while (mEvents.size() >= mEventsListSize) {
            mEvents.removeLast();
        }
        mEvents.addFirst(event);
    }

    public String getEventList() {
        StringBuilder builder = new StringBuilder();

        for (String event : mEvents) {
            builder.append(event);
            builder.append("\n");
        }
        return builder.toString();
    }

    public void setEventListSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }

        mEventsListSize = size;

        while (mEvents.size() > size) {
            mEvents.removeLast();
        }
    }
}
