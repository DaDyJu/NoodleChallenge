package com.kkadadeepju.snwf.sendnoodswithfriends.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kkadadeepju.snwf.sendnoodswithfriends.GameActivity;
import com.kkadadeepju.snwf.sendnoodswithfriends.MainActivity;
import com.kkadadeepju.snwf.sendnoodswithfriends.R;
import com.kkadadeepju.snwf.sendnoodswithfriends.widget.BowlImageView;
import com.kkadadeepju.snwf.sendnoodswithfriends.widget.BowlResultImageView;

import org.w3c.dom.Text;

/**
 * Created by jzhou on 2017-04-21.
 */

public class EndOfTurnDIalog extends Dialog {

    private LinearLayout playerOneBowlContainer;
    private LinearLayout playerTwoBowlContainer;
    private LinearLayout playerThreeBowlContainer;
    private LinearLayout playerFourBowlContainer;
    private TextView userWinLose;
    private TextView playerOneScore;
    private BowlResultImageView resultBowl;
    private Drawable mBowlStack;
    private Button rematchBtn;


    private Context context;
    private int playerBowls;

    public EndOfTurnDIalog(final Context context, final int playerBowls) {
        super(context, R.style.Theme_Dialog_Fullscreen_Darkened);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.end_of_turn_dialog);
        this.context = context;
        this.playerBowls = playerBowls;
        playerOneBowlContainer = (LinearLayout) findViewById(R.id.player_one_score);
        playerTwoBowlContainer = (LinearLayout) findViewById(R.id.player_two_score);
        playerThreeBowlContainer = (LinearLayout) findViewById(R.id.player_three_score);
        playerFourBowlContainer = (LinearLayout) findViewById(R.id.player_four_score);
        userWinLose = (TextView) findViewById(R.id.userWinLose);
        playerOneScore = (TextView) findViewById(R.id.player_one_bowls);
        rematchBtn = (Button) findViewById(R.id.rematch_bth);

        rematchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent myIntent = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(myIntent);
            }
        });

        if (playerBowls / 10 > 20) {
            userWinLose.setText("You Win!");
        } else {
            userWinLose.setText("Try Harder Text Time!");
        }

        playerOneScore.setText(String.format("Player 1 \n %s", Integer.toString(playerBowls)));
        setUpAnimation();
    }

    private void setUpAnimation() {
        final float imgSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
        final LinearLayout.LayoutParams bowlStackLayout = new LinearLayout.LayoutParams((int) imgSize, (int) imgSize);
        bowlStackLayout.gravity = Gravity.CENTER;
        bowlStackLayout.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -30, context.getResources().getDisplayMetrics());
        mBowlStack = ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.noods_10);

        for (int i = 0; i < playerBowls / 10; i++) {
            resultBowl = new BowlResultImageView(getContext());
            resultBowl.setImageDrawable(mBowlStack);

            resultBowl.setLayoutParams(bowlStackLayout);
            playerOneBowlContainer.addView(resultBowl, 0);
        }

        for (int i = 0; i < 7; i++) {
            resultBowl = new BowlResultImageView(getContext());
            resultBowl.setImageDrawable(mBowlStack);

            resultBowl.setLayoutParams(bowlStackLayout);
            playerTwoBowlContainer.addView(resultBowl, 0);
        }

        for (int i = 0; i < 20; i++) {
            resultBowl = new BowlResultImageView(getContext());
            resultBowl.setImageDrawable(mBowlStack);

            resultBowl.setLayoutParams(bowlStackLayout);
            playerThreeBowlContainer.addView(resultBowl, 0);
        }

        for (int i = 0; i < 9; i++) {
            resultBowl = new BowlResultImageView(getContext());
            resultBowl.setImageDrawable(mBowlStack);

            resultBowl.setLayoutParams(bowlStackLayout);
            playerFourBowlContainer.addView(resultBowl, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
