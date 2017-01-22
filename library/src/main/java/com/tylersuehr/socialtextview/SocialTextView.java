package com.tylersuehr.socialtextview;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Copyright 2017 Tyler Suehr
 * Created by tyler on 1/21/2017.
 */
public class SocialTextView extends TextView {
    // Usable flags
    private static final int FLAG_HASHTAG = 1;
    private static final int FLAG_MENTION = 2;
    private static final int FLAG_PHONE = 4;
    private static final int FLAG_EMAIL = 8;
    private static final int FLAG_URL = 15;

    // Usable attributes
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
        setMovementMethod(CustomMovementMethod.getInstance());

        // Set XML attributes
        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.SocialTextView);
        int flags = a.getInteger(R.styleable.SocialTextView_linkModes, -1);
        if (flags != -1) {
            // Check for hashtag flag
            if ((flags&FLAG_HASHTAG) == FLAG_HASHTAG) {
                modes.add(new LinkMode(LinkMode.MODE_HASHTAG));
            }

            // Check for mention flag
            if ((flags&FLAG_MENTION) == FLAG_MENTION) {
                modes.add(new LinkMode(LinkMode.MODE_MENTION));
            }

            // Check for phone flag
            if ((flags&FLAG_PHONE) == FLAG_PHONE) {
                modes.add(new LinkMode(LinkMode.MODE_PHONE));
            }

            // Check for email flag
            if ((flags&FLAG_EMAIL) == FLAG_EMAIL) {
                modes.add(new LinkMode(LinkMode.MODE_EMAIL_ADDRESS));
            }

            // Check for URL flag
            if ((flags&FLAG_URL) == FLAG_URL) {
                modes.add(new LinkMode(LinkMode.MODE_WEB_URL));
            }
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
        String text = a.getString(R.styleable.SocialTextView_android_text);
        setLinkText(text != null ? text : "");

        a.recycle();
    }

    @Override
    public void setHighlightColor(int color) {
        // Make sure that the text highlighted is always transparent
        super.setHighlightColor(Color.TRANSPARENT);
    }

    public void setLinkText(CharSequence text) {
        setText(createSpannableString(text));
    }

    public void appendLinkText(CharSequence text) {
        append(createSpannableString(text));
    }

    public void addLinkModes(LinkMode... modes) {
        for (LinkMode mode : modes) {
            if (this.modes.contains(mode)) {
                throw new IllegalArgumentException("Mode already exists!");
            }
            this.modes.add(mode);
        }
    }

    public void setHashtagModeColor(@ColorInt int hashtagModeColor) {
        this.hashtagColor = hashtagModeColor;
    }

    public void setMentionModeColor(@ColorInt int mentionModeColor) {
        this.mentionColor = mentionModeColor;
    }

    public void setPhoneModeColor(@ColorInt int phoneModeColor) {
        this.phoneColor = phoneModeColor;
    }

    public void setEmailModeColor(@ColorInt int emailModeColor) {
        this.emailColor = emailModeColor;
    }

    public void setUrlModeColor(@ColorInt int urlModeColor) {
        this.urlColor = urlModeColor;
    }

    public void setSelectedStateColor(@ColorInt int defaultSelectedColor) {
        this.selectedColor = defaultSelectedColor;
    }

    public void setUnderlineEnabled(boolean value) {
        this.underlineEnabled = value;
    }

    public void setLinkClickListener(LinkClickListener listener) {
        this.listener = listener;
    }

    public LinkClickListener getLinkClickListener() {
        return listener;
    }

    /**
     * Creates a {@link SpannableString} that sets the color of every matched link item for
     * each corresponding mode.
     * @param text Text to match
     * @return {@link SpannableString}
     */
    private SpannableString createSpannableString(CharSequence text) {
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

            if (mode.getMode() == LinkMode.MODE_PHONE) {
                while (matcher.find()) {
                    if (matcher.group().length() > 8) { // Min phone number length in US is 8
                        items.add(new LinkItem(matcher.start(), matcher.end(),
                                matcher.group(), mode));
                    }
                }
            } else {
                while (matcher.find()) {
                    items.add(new LinkItem(matcher.start(), matcher.end(),
                            matcher.group(), mode));
                }
            }
        }

        return items;
    }

    /**
     * Gets the corresponding color for a given mode.
     * @param mode {@link LinkMode}
     * @return Color
     */
    @ColorInt private int getColorByMode(LinkMode mode) {
        switch (mode.getMode()) {
            case LinkMode.MODE_HASHTAG:
                return hashtagColor;
            case LinkMode.MODE_MENTION:
                return mentionColor;
            case LinkMode.MODE_PHONE:
                return phoneColor;
            case LinkMode.MODE_EMAIL_ADDRESS:
                return emailColor;
            case LinkMode.MODE_WEB_URL:
                return urlColor;
            default:
                throw new IllegalArgumentException("Invalid mode!");
        }
    }

    /**
     * This will detect when the user touches the link accurately. More accurate than
     * {@link LinkMovementMethod}.
     */
    private final static class CustomMovementMethod extends LinkMovementMethod {
        private static CustomMovementMethod instance;
        private final RectF touchBounds = new RectF();
        private TouchableSpan pressedSpan;


        public static synchronized CustomMovementMethod getInstance() {
            if (instance == null) {
                instance = new CustomMovementMethod();
            }
            return instance;
        }

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressedSpan = getTouchedSpan(widget, buffer, event);
                    if (pressedSpan != null) {
                        pressedSpan.setPressed(true);
                        Selection.setSelection(buffer,  buffer.getSpanStart(pressedSpan), buffer.getSpanEnd(pressedSpan));
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    TouchableSpan pressedSpan2 = getTouchedSpan(widget, buffer, event);
                    if (pressedSpan != null && pressedSpan2 != pressedSpan) {
                        pressedSpan.setPressed(false);
                        pressedSpan = null;
                        Selection.removeSelection(buffer);
                    }
                    break;

                default:
                    if (pressedSpan != null) {
                        pressedSpan.setPressed(false);
                        super.onTouchEvent(widget, buffer, event);
                    }
                    pressedSpan = null;
                    Selection.removeSelection(buffer);
                    break;
            }
            return true;
        }

        /**
         * Gets the span that was touched.
         * @param tv {@link TextView}
         * @param span {@link Spannable}
         * @param e {@link MotionEvent}
         * @return {@link TouchableSpan}
         */
        private TouchableSpan getTouchedSpan(TextView tv, Spannable span, MotionEvent e) {
            // Find the location in which the touch was made
            int x = (int)e.getX();
            int y = (int)e.getY();

            // Ignore padding
            x -= tv.getTotalPaddingLeft();
            y -= tv.getTotalPaddingTop();

            // Account for scrollable text
            x += tv.getScrollX();
            y += tv.getScrollY();

            final Layout layout = tv.getLayout();
            final int touchedLine = layout.getLineForVertical(y);
            final int touchOffset = layout.getOffsetForHorizontal(touchedLine, x);

            // Set bounds of the touched line
            touchBounds.left = layout.getLineLeft(touchedLine);
            touchBounds.top = layout.getLineTop(touchedLine);
            touchBounds.right = layout.getLineRight(touchedLine);
            touchBounds.bottom = layout.getLineBottom(touchedLine);

            // Ensure the span falls within the bounds of the touch
            TouchableSpan touchSpan = null;
            if (touchBounds.contains(x, y)) {
                // Find clickable spans that lie under the touched area
                TouchableSpan[] spans = span.getSpans(touchOffset, touchOffset, TouchableSpan.class);
                touchSpan = (spans.length > 0) ? spans[0] : null;
            }

            return touchSpan;
        }
    }

    /**
     * This will allow us to set specific colors for when the users has pressed
     * the link; also allows underlining of the pressed link's text.
     */
    private static abstract class TouchableSpan extends ClickableSpan {
        private final boolean underlineEnabled;
        private final int pressedTextColor;
        private final int normalTextColor;
        private boolean pressed;


        TouchableSpan(int normalTextColor, int pressedTextColor, boolean underlineEnabled) {
            this.normalTextColor = normalTextColor;
            this.pressedTextColor = pressedTextColor;
            this.underlineEnabled = underlineEnabled;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            // Determine whether to paint it pressed or normally
            int textColor = pressed ? pressedTextColor : normalTextColor;
            ds.setColor(textColor);
            ds.bgColor = Color.TRANSPARENT;
            ds.setUnderlineText(underlineEnabled);
        }

        void setPressed(boolean value) {
            this.pressed = value;
        }
    }


    public interface LinkClickListener {
        void onLinkClicked(LinkMode mode, String matched);
    }
}