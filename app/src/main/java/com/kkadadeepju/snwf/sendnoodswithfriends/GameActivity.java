package com.kkadadeepju.snwf.sendnoodswithfriends;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.os.Vibrator;
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
import android.view.Window;
import android.view.WindowManager;
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

import com.kkadadeepju.snwf.sendnoodswithfriends.dialog.EndOfTurnDIalog;
import com.kkadadeepju.snwf.sendnoodswithfriends.widget.BowlImageView;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.kkadadeepju.snwf.sendnoodswithfriends.Powerups.*;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Powerups.Types.SendNoods;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Powerups.Types.SendVibrate;


/**
 * Created by jzhou on 2017-04-20.
 */

public class GameActivity extends AppCompatActivity {

    private final int GAME_TIME_MILLIS = 30000;
    private TextView timer;
    private TextView playerTwoScore;
    private TextView playerThreeScore;
    private TextView playerFourScore;
    private TextView playerScore;
    private ImageView noodleBowl;
    private ImageView chopStickUp;
    private ImageView chopStickDown;
    private ImageView powerUpsendNoods;
    private ImageView powerUpsendVirate;
    private MediaPlayer mediaPlayer1;
    private MediaPlayer mediaPlayer2;
    private MediaPlayer mediaPlayer3;

    private boolean mp1Released = true;
    private boolean mp2Released = true;
    private boolean mp3Released = true;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        powerUpsendNoods = (ImageView) findViewById(R.id.powerup_send_noodle);
        powerUpsendVirate = (ImageView) findViewById(R.id.powerup_send_vibrate);

        setUpPowerListner();

        mediaPlayer1 = new MediaPlayer();
        mediaPlayer2 = new MediaPlayer();
        mediaPlayer3 = new MediaPlayer();

        mBowlStack = ContextCompat.getDrawable(getApplicationContext(), R.drawable.noods_10);

        final float imgSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

        finishedBowlContainer = (LinearLayout) findViewById(R.id.finished_bowl);

        for (int i = 0; i < bowls.length; i++) {
            images.add(ContextCompat.getDrawable(getApplicationContext(), bowls[i]));
        }

        timer.setText("Seconds remaining: " + GAME_TIME_MILLIS / 1000);

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
                playOiSound();

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
                noodleBowl.setClickable(false);
                gameStartCountdown.setVisibility(View.GONE);
                // start game timer
                noodleBowl.setClickable(true);
                new CountDownTimer(GAME_TIME_MILLIS, 1000) {
                    public void onTick(long millisUntilFinished) {
                        timer.setText(millisUntilFinished / 1000 + "s");
                    }

                    public void onFinish() {
                        timer.setText("GAME OVER");
                        noodleBowl.setClickable(false);
                        resetMPs();
                        EndOfTurnDIalog result = new EndOfTurnDIalog(GameActivity.this, score);
                        result.show();
                    }
                }.start();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer1 = new MediaPlayer();
        mediaPlayer2 = new MediaPlayer();
        mediaPlayer3 = new MediaPlayer();
    }

    private void resetMPs() {
        if (mediaPlayer1 != null) {
            mediaPlayer1.stop();
            mediaPlayer1.release();
        }
        if (mediaPlayer2 != null) {
            mediaPlayer2.stop();
            mediaPlayer2.release();
        }
        if (mediaPlayer3 != null) {
            mediaPlayer3.stop();
            mediaPlayer3.release();
        }
    }

    private void setUpPowerListner() {
        powerUpsendNoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ParticleSystem(GameActivity.this, 100, R.drawable.sendnoods_particle, 1000)
                        .setSpeedRange(0.2f, 0.3f)
                        .oneShot(powerUpsendNoods, 100);
                powerUpsendNoods.setAlpha(127);
                powerUpsendNoods.setClickable(false);
            }
        });

        powerUpsendVirate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ParticleSystem(GameActivity.this, 100, R.drawable.vibrate_particle, 1000)
                        .setSpeedRange(0.2f, 0.3f)
                        .oneShot(powerUpsendVirate, 100);
                powerUpsendVirate.setAlpha(127);
                powerUpsendVirate.setClickable(false);
            }
        });
    }

    public void handlePowerup(Types type) {
        switch (type) {
            case SendNoods:
                onSendNoods("Dadyju");
                break;
            case SendLag:
                onSendLag("Dadyju");
                break;
            case SendVibrate:
                onSendVibrate("Dadyju");
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

    public void playOiSound() {
        Random rand = new Random();
        int num = rand.nextInt(3) + 1;

        MediaPlayer mediaPlayer = new MediaPlayer();
        if (num == 1) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.oi_1);
        } else if (num == 2) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.oi_2);
        } else if (num == 3) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.oi_3);
        }

        int mp = 0;

        if (mp1Released) {
            mediaPlayer1 = mediaPlayer;
            mp2Released = false;
            mp = 1;
        } else if (mp2Released) {
            mediaPlayer2 = mediaPlayer;
            mp2Released = false;
            mp = 2;
        } else {
            if (!mp3Released) {
                mediaPlayer3.stop();
                mediaPlayer3.release();
            }
            mediaPlayer2 = mediaPlayer;
            mp3Released = false;
            mp = 3;
        }

        if (mp == 1) {
            mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.release();
                    mp1Released = true;
                }
            });
            mediaPlayer1.start();
        } else if (mp == 2) {
            mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.release();
                    mp2Released = true;
                }
            });
            mediaPlayer2.start();
        } else {
            mediaPlayer3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.release();
                    mp3Released = true;
                }
            });
            mediaPlayer3.start();
        }
    }

    private void onSendLag(String playerName) {
    }

    private void onSendVibrate(String playerName) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100000);
    }

}
