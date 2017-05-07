package com.kkadadeepju.snwf.noodlechallenge.widget;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kkadadeepju.snwf.noodlechallenge.R;

/**
 * Created by Junyu on 2017-04-23.
 */

public class BowlStackLayout extends LinearLayout {
    public BowlStackLayout(Context context) {
        super(context);
        initialize();

    }

    private void initialize() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        param.weight = 1;
        param.gravity = Gravity.BOTTOM;
        setLayoutParams(param);

        ImageView avatar = new ImageView(getContext());
        final int imgSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imgSize, imgSize);
        layoutParams.gravity = Gravity.CENTER;
        avatar.setLayoutParams(layoutParams);
        avatar.setImageResource(R.drawable.default_avatar);

        addView(avatar);
    }
}
