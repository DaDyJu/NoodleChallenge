package com.kkadadeepju.snwf.noodlechallenge.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

// http://stackoverflow.com/questions/35897146/borderlike-shadow-solid-and-large-on-textview-android
public class SolidRedShadowTextView extends AppCompatTextView {
    /**
     * Shadow radius, higher value increase the blur effect
     */
    private static final float SHADOW_RADIUS = 10f;

    /**
     * Number of times a onDraw(...) call should repeat itself.
     * Higher value ends up in more solid shadow (but degrade in performance)
     * This value must be >= 1
     */
    private static final int REPEAT_COUNTER = 10000;

    // Shadow color
    private static final int SHADOW_COLOR = 0xffee2f2f;

    public SolidRedShadowTextView(Context context) {
        super(context);
        init();
    }

    public SolidRedShadowTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < REPEAT_COUNTER; i++) {
            super.onDraw(canvas);
        }
    }

    @Override
    public void setShadowLayer(float radius, float dx, float dy, int color) {
        // Disable public API to set shadow
    }

    private void init() {
        super.setShadowLayer(SHADOW_RADIUS, 0, 0, SHADOW_COLOR);
    }
}
