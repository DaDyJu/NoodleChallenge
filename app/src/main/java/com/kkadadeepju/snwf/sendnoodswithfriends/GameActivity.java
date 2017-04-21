package com.kkadadeepju.snwf.sendnoodswithfriends;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.lang.reflect.Array;
import com.kkadadeepju.snwf.sendnoodswithfriends.widget.BowlImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Powerups.*;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Powerups.Types.SendNoods;

/**
 * Created by jzhou on 2017-04-20.
 */

public class GameActivity extends AppCompatActivity {

    private TextView timer;
    private TextView playerTwoScore;
    private TextView playerThreeScore;
    private TextView playerFourScore;
    private TextView playerScore;
    private ImageView noodleBowl;
    private ImageView chopStickUp;
    private ImageView chopStickDown;
    private ViewGroup container;
    private ViewGroup sendNoodsLayout;
    private TextView gameStartCountdown;

    private LinearLayout finishedBowlContainer;
    private BowlImageView finishedBowl;

    private int score = 0;

    private int imgPosition = 0;

    private boolean isChopstickUp = false;

    private boolean isSendNoodsActive = false;
    private int numberOfSendNoodsLeft;
    private Drawable mBowlStack;

    private static final int[] bowls = {
            R.drawable.noods_01,
            R.drawable.noods_02,
            R.drawable.noods_03,
            R.drawable.noods_04,
            R.drawable.noods_05,
            R.drawable.noods_06,
            R.drawable.noods_07,
            R.drawable.noods_08,
            R.drawable.noods_09
    };

    private ArrayList<ImageView> sendNoods = new ArrayList<>();

    private ArrayList<Drawable> images = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        container = (ViewGroup) findViewById(R.id.container);
        timer = (TextView) findViewById(R.id.timer);
        playerTwoScore = (TextView) findViewById(R.id.player_two_score);
        playerThreeScore = (TextView) findViewById(R.id.player_three_score);
        playerFourScore = (TextView) findViewById(R.id.player_four_score);
        playerScore = (TextView) findViewById(R.id.player_score);

        chopStickUp = (ImageView) findViewById(R.id.chopstick_up);
        chopStickDown = (ImageView) findViewById(R.id.chopstick_down);
        noodleBowl = (ImageView) findViewById(R.id.noodle_bowl);


        mBowlStack = ContextCompat.getDrawable(getApplicationContext(), R.drawable.noods_10);

        final float imgSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

        finishedBowlContainer = (LinearLayout) findViewById(R.id.finished_bowl);


        for (int i = 0; i < bowls.length; i++) {
            images.add(ContextCompat.getDrawable(getApplicationContext(), bowls[i]));
        }

        sendNoodsLayout = (ViewGroup) findViewById(R.id.send_noods_layout);
        sendNoodsLayout.setVisibility(View.GONE);
        sendNoods.add((ImageView) findViewById(R.id.send_noods_1));
        sendNoods.add((ImageView) findViewById(R.id.send_noods_2));
        sendNoods.add((ImageView) findViewById(R.id.send_noods_3));
        sendNoods.add((ImageView) findViewById(R.id.send_noods_4));
        sendNoods.add((ImageView) findViewById(R.id.send_noods_5));
        sendNoods.add((ImageView) findViewById(R.id.send_noods_6));
        sendNoods.add((ImageView) findViewById(R.id.send_noods_7));
        sendNoods.add((ImageView) findViewById(R.id.send_noods_8));

        final Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);

        final LinearLayout.LayoutParams bowlStackLayout = new LinearLayout.LayoutParams((int) imgSize, (int) imgSize);
        bowlStackLayout.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -15, getResources().getDisplayMetrics());

        noodleBowl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSendNoodsActive) {
                    return;
                }

                playerScore.setText(Integer.toString(++score));
                playerScore.startAnimation(pulse);

                if (imgPosition == 9) {
                    imgPosition = 0;
                    finishedBowl = new BowlImageView(GameActivity.this);
                    finishedBowl.setImageDrawable(mBowlStack);

                    finishedBowl.setLayoutParams(bowlStackLayout);
                    finishedBowlContainer.addView(finishedBowl, 0);
                }
                noodleBowl.setImageDrawable(images.get(imgPosition));
                imgPosition++;

                if (isChopstickUp) {
                    chopStickUp.setVisibility(View.GONE);
                    chopStickDown.setVisibility(View.VISIBLE);
                    isChopstickUp = false;
                } else {
                    chopStickUp.setVisibility(View.VISIBLE);
                    chopStickDown.setVisibility(View.GONE);
                    isChopstickUp = true;
                }
            }
        });

        gameStartCountdown = (TextView) findViewById(R.id.starting_text);
        gameStartCountdown.setText("4");

        final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulse);

        noodleBowl.setClickable(false);
        // 3 2 1 GO!
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String text = gameStartCountdown.getText().toString();
                noodleBowl.setClickable(false);
                if (!text.equals("GO!")) {
                    int count = Integer.parseInt(text);
                    if (count > 1) {
                        count--;
                        gameStartCountdown.setText(Integer.toString(count));
                    } else {
                        gameStartCountdown.setText("GO!");
                    }
                }
            }

            @Override
            public void onFinish() {
                gameStartCountdown.setVisibility(View.GONE);
                // start game timer
                noodleBowl.setClickable(true);
                new CountDownTimer(10000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        timer.setText("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        timer.setText("done!");
                        noodleBowl.setClickable(false);
                    }
                }.start();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void handlePowerup(Types type) {
        switch (type) {
            case SendNoods:
                onSendNoods("Dadyju");
                break;
            case SendLag:
                onSendLag("Dadyju");
                break;
            default:
                break;
        }
    }

    private void onSendNoods(String playerName) {
        // received a bunch of noods from playerName
        // fills up their whole screen with noodles, their score does not count until their extra noodles are gone
        // and they are back to tapping on their regular noodle bowl
        isSendNoodsActive = true;
        sendNoodsLayout.setVisibility(View.VISIBLE);
        numberOfSendNoodsLeft = sendNoods.size();

        for (int i = 0; i < sendNoods.size(); i++) {
            sendNoods.get(i).setVisibility(View.VISIBLE);

            sendNoods.get(i).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setVisibility(View.INVISIBLE);
                            numberOfSendNoodsLeft--;

                            if (numberOfSendNoodsLeft <= 0) {
                                isSendNoodsActive = false;
                                sendNoodsLayout.setVisibility(View.GONE);
                            }
                        }
                    }
            );
        }
    }

    private void onSendLag(String playerName) {
    }
}
