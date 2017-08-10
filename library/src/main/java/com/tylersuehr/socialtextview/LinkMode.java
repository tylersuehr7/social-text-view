package com.tylersuehr.socialtextview;
import android.util.Patterns;
import java.io.Serializable;
/**
 * Copyright 2017 Tyler Suehr
 * Created by tyler on 1/21/2017.
 */
public final class LinkMode implements Serializable {
    public static final int FLAG_HASHTAG = 1;
    public static final int FLAG_MENTION = 2;
    public static final int FLAG_PHONE =   4;
    public static final int FLAG_EMAIL =   8;
    public static final int FLAG_URL =    16;
    private final int mode;


    public LinkMode(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        switch (mode) {
            case FLAG_HASHTAG:
                return "Hashtag";
            case FLAG_MENTION:
                return "Mention";
            case FLAG_PHONE:
                return "Phone";
            case FLAG_EMAIL:
                return "Email Address";
            case FLAG_URL:
                return "Web URL";
            default:
                throw new IllegalStateException("Invalid mode!");
        }
    }

    /**
     * Gets the link mode's type.
     * @return Mode type
     */
    int getMode() {
        return mode;
    }

    /**
     * Gets the link mode's regex pattern.
     * @return Regex pattern
     */
    String getPattern() {
        switch (mode) {
            case FLAG_HASHTAG:
                return "(?:^|\\s|$)#[\\p{L}0-9_]*";
            case FLAG_MENTION:
                return "(?:^|\\s|$|[.])@[\\p{L}0-9_]*";
            case FLAG_PHONE:
                return Patterns.PHONE.pattern();
            case FLAG_EMAIL:
                return Patterns.EMAIL_ADDRESS.pattern();
            case FLAG_URL:
                return Patterns.WEB_URL.pattern();
            default: throw new IllegalStateException("Invalid mode!");
        }
    }
}