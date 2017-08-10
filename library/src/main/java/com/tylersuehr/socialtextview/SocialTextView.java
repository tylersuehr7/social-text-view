package com.tylersuehr.socialtextview;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Copyright 2017 Tyler Suehr
 * Created by tyler on 1/21/2017.
 */
public class SocialTextView extends AppCompatTextView {
    private boolean underlineEnabled;
    private int selectedColor;
    private int hashtagColor;
    private int mentionColor;
    private int phoneColor;
    private int emailColor;
    private int urlColor ;

    private final Set<LinkMode> modes = new HashSet<>();
    private LinkClickListener listener;


    public SocialTextView(Context c) {
        this(c, null);
    }

    public SocialTextView(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public SocialTextView(Context c, AttributeSet attrs, int def) {
        super(c, attrs, def);

        // Setup defaults
        setMovementMethod(AccurateMovementMethod.getInstance());

        // Set XML attributes
        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.SocialTextView);
        int flags = a.getInteger(R.styleable.SocialTextView_linkModes, -1);
        if (flags != -1) {
            // Check for any flags, add a LinkMode for the appropriate flag
            checkAddFlag(flags, LinkMode.FLAG_HASHTAG);
            checkAddFlag(flags, LinkMode.FLAG_MENTION);
            checkAddFlag(flags, LinkMode.FLAG_EMAIL);
            checkAddFlag(flags, LinkMode.FLAG_PHONE);
            checkAddFlag(flags, LinkMode.FLAG_URL);
        }

        // Set given colors if possible
        hashtagColor = a.getColor(R.styleable.SocialTextView_hashtagColor, Color.RED);
        mentionColor = a.getColor(R.styleable.SocialTextView_mentionColor, Color.RED);
        phoneColor = a.getColor(R.styleable.SocialTextView_phoneColor, Color.RED);
        emailColor = a.getColor(R.styleable.SocialTextView_emailColor, Color.RED);
        urlColor = a.getColor(R.styleable.SocialTextView_urlColor, Color.RED);
        selectedColor = a.getColor(R.styleable.SocialTextView_selectedColor, Color.LTGRAY);
        underlineEnabled = a.getBoolean(R.styleable.SocialTextView_underlineEnabled, false);

        // Set given text if possible
        if (a.hasValue(R.styleable.SocialTextView_android_text)) {
            setLinkText(a.getString(R.styleable.SocialTextView_android_text));
        }

        a.recycle();
    }

    @Override
    public void setHighlightColor(int color) {
        // Make sure that the text highlighted is always transparent
        super.setHighlightColor(Color.TRANSPARENT);
    }

    /**
     * Checks for any link spans and sets the text using {@link #setText(CharSequence)}.
     * @param text {@link CharSequence}
     */
    public void setLinkText(CharSequence text) {
        setText(createLinkSpan(text));
    }

    /**
     * Checks for any link spans and appends the text using {@link #append(CharSequence)}}.
     * @param text {@link CharSequence}
     */
    public void appendLinkText(CharSequence text) {
        append(createLinkSpan(text));
    }

    public void setLinkClickListener(LinkClickListener listener) {
        this.listener = listener;
    }

    /**
     * Checks if a flag exists in the given flags and adds a {@link LinkMode} accordingly.
     * @param flags Flags
     * @param flag Flag
     */
    private void checkAddFlag(int flags, int flag) {
        if ((flags&flag) == flag) {
            this.modes.add(new LinkMode(flag));
        }
    }

    /**
     * Collect link items for every matched link mode in the given text.
     * @param text Text to match
     * @return Set of {@link LinkItem}
     */
    private Set<LinkItem> getMatchedLinkItems(CharSequence text) {
        Set<LinkItem> items = new HashSet<>();
        for (LinkMode mode : modes) {
            String regex = mode.getPattern();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);

            if (mode.getMode() == LinkMode.FLAG_PHONE) {
                while (matcher.find()) {
                    if (matcher.group().length() > 8) { // Min phone number length in US is 8
                        items.add(new LinkItem(
                                matcher.start(),
                                matcher.end(),
                                matcher.group(),
                                mode));
                    }
                }
            } else {
                while (matcher.find()) {
                    items.add(new LinkItem(
                            matcher.start(),
                            matcher.end(),
                            matcher.group(),
                            mode));
                }
            }
        }

        return items;
    }

    /**
     * Creates a {@link SpannableString} that sets the color of every matched link item for
     * each corresponding mode.
     * @param text Text to match
     * @return {@link SpannableString}
     */
    private SpannableString createLinkSpan(CharSequence text) {
        final SpannableString textSpan = new SpannableString(text);
        final Set<LinkItem> items = getMatchedLinkItems(text);

        int color;
        for (final LinkItem item : items) {
            color = getColorByMode(item.getMode());
            TouchableSpan touchSpan = new TouchableSpan(color, selectedColor, underlineEnabled) {
                @Override
                public void onClick(View widget) {
                    if (listener != null) {
                        listener.onLinkClicked(item.getMode(), item.getMatched());
                    }
                }
            };

            textSpan.setSpan(touchSpan, item.getStart(), item.getEnd(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return textSpan;
    }

    /**
     * Gets the corresponding color for a given mode.
     * @param mode {@link LinkMode}
     * @return Color
     */
    @ColorInt
    private int getColorByMode(LinkMode mode) {
        switch (mode.getMode()) {
            case LinkMode.FLAG_HASHTAG:
                return hashtagColor;
            case LinkMode.FLAG_MENTION:
                return mentionColor;
            case LinkMode.FLAG_PHONE:
                return phoneColor;
            case LinkMode.FLAG_EMAIL:
                return emailColor;
            case LinkMode.FLAG_URL:
                return urlColor;
            default: throw new IllegalArgumentException("Invalid mode!");
        }
    }


    /**
     * Callbacks for link touch events.
     */
    public interface LinkClickListener {
        void onLinkClicked(LinkMode mode, String matched);
    }
}