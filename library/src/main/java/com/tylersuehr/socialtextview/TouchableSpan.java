package com.tylersuehr.socialtextview;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * This subclass of {@link ClickableSpan} will change the text's color when touched. It also
 * gives us the ability to underline the text as well.
 *
 * This works by utilizing a 'pressed' flag. When the flag is true, the span will draw the
 * touched color, and when the flag is false, the span will draw the normal color.
 *
 * To amend the 'pressed' flag, call: {@link #setPressed(boolean)}.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public abstract class TouchableSpan extends ClickableSpan {
    private final boolean underlineEnabled;
    private final int pressedTextColor;
    private final int normalTextColor;
    private boolean pressed;


    public TouchableSpan(int normalTextColor, int pressedTextColor, boolean underlineEnabled) {
        this.normalTextColor = normalTextColor;
        this.pressedTextColor = pressedTextColor;
        this.underlineEnabled = underlineEnabled;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        // Determine whether to paint it pressed or normally
        int textColor = pressed ? pressedTextColor : normalTextColor;
        paint.setColor(textColor);
        paint.setUnderlineText(underlineEnabled);
        paint.bgColor = Color.TRANSPARENT;
    }

    /**
     * Sets the flag for when the span is pressed.
     * @param value True if pressed
     */
    void setPressed(boolean value) {
        this.pressed = value;
    }
}