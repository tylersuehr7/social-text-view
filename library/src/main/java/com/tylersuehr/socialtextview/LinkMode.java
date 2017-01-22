package com.tylersuehr.socialtextview;
import android.util.Patterns;
import java.io.Serializable;
/**
 * Copyright 2017 Tyler Suehr
 * Created by tyler on 1/21/2017.
 */
public final class LinkMode implements Serializable {
    public static final int MODE_HASHTAG = 0;
    public static final int MODE_MENTION = 1;
    public static final int MODE_PHONE = 2;
    public static final int MODE_EMAIL_ADDRESS = 3;
    public static final int MODE_WEB_URL = 4;
    private final int mode;


    public LinkMode(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        switch (mode) {
            case MODE_HASHTAG:
                return "Hashtag";
            case MODE_MENTION:
                return "Mention";
            case MODE_PHONE:
                return "Phone";
            case MODE_EMAIL_ADDRESS:
                return "Email Address";
            case MODE_WEB_URL:
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
            case MODE_HASHTAG:
                return "(?:^|\\s|$)#[\\p{L}0-9_]*";
            case MODE_MENTION:
                return "(?:^|\\s|$|[.])@[\\p{L}0-9_]*";
            case MODE_PHONE:
                return Patterns.PHONE.pattern();
            case MODE_EMAIL_ADDRESS:
                return Patterns.EMAIL_ADDRESS.pattern();
            case MODE_WEB_URL:
                return Patterns.WEB_URL.pattern();
            default:
                throw new IllegalArgumentException("Invalid mode!");
        }
    }
}