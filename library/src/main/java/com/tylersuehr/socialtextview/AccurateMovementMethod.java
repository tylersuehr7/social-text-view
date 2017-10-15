package com.tylersuehr.socialtextview;
import android.graphics.RectF;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * This subclass of {@link LinkMovementMethod} detects link touch events much more accurately.
 *
 * It works by storing the touch bounds in a {@link RectF} and checking if any of the spans
 * are contained with the bounds of the touched area.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class AccurateMovementMethod extends LinkMovementMethod {
    private static AccurateMovementMethod instance;
    private final RectF touchBounds = new RectF();
    private TouchableSpan pressedSpan;


    public static synchronized AccurateMovementMethod getInstance() {
        if (instance == null) {
            instance = new AccurateMovementMethod();
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
                    Selection.setSelection(buffer, buffer.getSpanStart(pressedSpan), buffer.getSpanEnd(pressedSpan));
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