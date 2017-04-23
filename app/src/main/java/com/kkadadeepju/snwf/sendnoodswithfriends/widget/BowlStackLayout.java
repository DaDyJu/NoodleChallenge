package com.kkadadeepju.snwf.sendnoodswithfriends.widget;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kkadadeepju.snwf.sendnoodswithfriends.R;

import static android.R.attr.clipChildren;
import static android.R.attr.clipToPadding;
import static android.R.attr.gravity;
import static android.R.attr.id;
import static android.R.attr.layout_gravity;
import static android.R.attr.layout_height;
import static android.R.attr.layout_weight;
import static android.R.attr.layout_width;
import static android.R.attr.orientation;

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
