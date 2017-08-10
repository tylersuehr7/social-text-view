package com.tylersuehr.socialtextview;

/**
 * Copyright 2017 Tyler Suehr
 * Created by tyler on 1/21/2017.
 *
 * This is a simple model to store any type of link item we want to detect in our
 * {@link SocialTextView}.
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

    LinkMode getMode() {
        return mode;
    }

    String getMatched() {
        return matched;
    }

    int getStart() {
        return start;
    }

    int getEnd() {
        return end;
    }
}