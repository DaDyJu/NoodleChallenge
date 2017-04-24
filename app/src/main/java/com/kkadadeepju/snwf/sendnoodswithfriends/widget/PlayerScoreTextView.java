package com.kkadadeepju.snwf.sendnoodswithfriends.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Junyu on 2017-04-24.
 */

public class PlayerScoreTextView extends AppCompatTextView {
    private String userName;
    private int userScore;

    public PlayerScoreTextView(Context context, String userName, int userScore) {
        super(context);
        this.userName = userName;
        this.userScore = userScore;
        init();
    }

    private void init() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.weight = 1;
        param.gravity = Gravity.CENTER;
        setGravity(Gravity.CENTER);
        setLayoutParams(param);
        setTextColor(Color.parseColor("#ffffff"));
        setText(userName + "\n" + userScore);

    }
}
