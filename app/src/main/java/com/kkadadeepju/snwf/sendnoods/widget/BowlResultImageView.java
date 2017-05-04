package com.kkadadeepju.snwf.sendnoods.widget;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.kkadadeepju.snwf.sendnoods.R;

/**
 * Created by jzhou on 2017-04-21.
 */

public class BowlResultImageView extends android.support.v7.widget.AppCompatImageView {

    private Animation anim;

    public BowlResultImageView(Context context) {
        super(context);

        anim = AnimationUtils.loadAnimation(context, R.anim.stack);
        anim.setFillAfter(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.startAnimation(anim);
    }
}
