package com.kkadadeepju.snwf.noodlechallenge.widget;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * Created by jzhou on 2017-04-21.
 */

public class BowlImageView extends android.support.v7.widget.AppCompatImageView {
    private ScaleAnimation anim;

    public BowlImageView(Context context) {
        super(context);

        anim = new ScaleAnimation(12, 1, 11.5f, 1, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f);
        anim.setDuration(700);
        anim.setFillAfter(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.startAnimation(anim);
    }
}
