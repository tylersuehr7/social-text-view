package com.tylersuehr.socialtextview;

/**
 * Copyright 2017 Tyler Suehr
 * Created by tyler on 1/21/2017.
 */
class LinkItem {
    private final LinkMode mode;
    private final String matched;
    private final int start;
    private final int end;


    LinkItem(int start, int end, String matched, LinkMode mode) {
        this.start = start;
        this.end = end;
        this.matched = matched;
        this.mode = mode;
    }

    public LinkMode getMode() {
        return mode;
    }

    public String getMatched() {
        return matched;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}