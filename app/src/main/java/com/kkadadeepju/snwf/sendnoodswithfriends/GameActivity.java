package com.kkadadeepju.snwf.sendnoodswithfriends;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kkadadeepju.snwf.sendnoodswithfriends.adapter.PlayerScoreAdapter;
import com.kkadadeepju.snwf.sendnoodswithfriends.dialog.EndOfTurnDIalog;
import com.kkadadeepju.snwf.sendnoodswithfriends.model.GameState;
import com.kkadadeepju.snwf.sendnoodswithfriends.model.PowerUp;
import com.kkadadeepju.snwf.sendnoodswithfriends.model.UserInfo;
import com.kkadadeepju.snwf.sendnoodswithfriends.widget.BowlImageView;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.kkadadeepju.snwf.sendnoodswithfriends.Constants.GAMES;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Constants.GAME_POWER_UPS;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Constants.GAME_STATES;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Constants.GAME_USERS;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Constants.GAME_ID;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Constants.IS_GAME_STARTED;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Constants.TIME_WAITING;
import static com.kkadadeepju.snwf.sendnoodswithfriends.MainActivity.SCORE;
import static com.kkadadeepju.snwf.sendnoodswithfriends.Constants.USER_ID;
import static com.kkadadeepju.snwf.sendnoodswithfriends.MainActivity.WAITING_TIME;


/**
 * Created by jzhou on 2017-04-20.
 */

public class GameActivity extends AppCompatActivity {

    private static final int SEND_NOODLE_POWER_UP = 1;
    private static final int SEND_VIBRATE_POWER_UP = 2;

    private DatabaseReference mDatabase;

    static boolean active = false;
    private final int GAME_TIME_MILLIS = 15000;
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

    private RecyclerView playerRvView;
    private PlayerScoreAdapter adapter;


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

    private HashMap<String, Integer> userInfoMaps = new HashMap<>();
    private ArrayList<UserInfo> userInfoArray = new ArrayList<>();

    private String curGameId;
    private String curUserId;

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

        playerRvView = (RecyclerView) findViewById(R.id.rvPlayers);
        adapter = new PlayerScoreAdapter();
        playerRvView.setAdapter(adapter);
        playerRvView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference().child(GAMES);
        curGameId = getIntent().getStringExtra(GAME_ID);
        curUserId = getIntent().getStringExtra(USER_ID);
        registerGameStartState();
        setUpGameEventListener();
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

        timer.setText(GAME_TIME_MILLIS / 1000 + "s");

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
                //playOiSound();

                if (isSendNoodsActive) {
                    return;
                }

                playerScore.setText(Integer.toString(++score));
                playerScore.startAnimation(pulse);

                if (imgPosition == 9) {
                    imgPosition = 0;
                }

                if (score % 10 == 0) {
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
                gameStartCountdown.setVisibility(View.GONE);
                // start game timer
                noodleBowl.setClickable(true);
                new CountDownTimer(GAME_TIME_MILLIS, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long secs = millisUntilFinished / 1000;
                        if (secs % 5 == 0) {
                            // update data
                            Log.v("Junyu", "send Request");
                            mDatabase.child(curGameId).child(GAME_USERS).child(curUserId).child(SCORE).setValue(score);
                        }

                        timer.setText(secs + "s");
                    }

                    public void onFinish() {
                        onGameFinish();
                    }
                }.start();
            }
        }.start();
    }

    private void onGameFinish() {
        timer.setText("GAMES OVER");
        noodleBowl.setClickable(false);
        //resetMPs();
        mDatabase.child(curGameId).child(GAME_USERS).child(curUserId).child(SCORE).setValue(score);
        mDatabase.child(curGameId).child(GAME_STATES).child(curUserId).setValue(new GameState(true, score, NCUserPreference.getUserGameName(GameActivity.this)));

        showResultDialog();
    }

    private void showResultDialog() {

        mDatabase.child(curGameId).child(GAME_STATES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GameState gameState = snapshot.getValue(GameState.class);
                    if (!gameState.isGameOver) {
                        return;
                    }
                }

                if (active) {
                    final EndOfTurnDIalog result = new EndOfTurnDIalog(GameActivity.this, score, curGameId);
                    result.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void registerGameStartState() {
        mDatabase.child(curGameId).child(GAME_STATES).child(curUserId).setValue(new GameState(false, -1, NCUserPreference.getUserGameName(GameActivity.this)));
    }

    private void setUpGameEventListener() {
        mDatabase.child(curGameId).child(GAME_USERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final UserInfo userInfoClass = dataSnapshot.getValue(UserInfo.class);
                userInfoMaps.put(userInfoClass.userId, userInfoArray.size());
                userInfoArray.add(userInfoClass);
                adapter.addData(userInfoClass);
                adapter.notifyItemInserted(userInfoArray.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final UserInfo userInfoClass = dataSnapshot.getValue(UserInfo.class);
                int position = userInfoMaps.get(userInfoClass.userId);
                userInfoArray.get(position).setScore(userInfoClass.getScore());
                adapter.notifyItemChanged(position);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child(curGameId).child(GAME_POWER_UPS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PowerUp powerUp = dataSnapshot.getValue(PowerUp.class);
                if (!powerUp.userId.equals(NCUserPreference.getUserId(GameActivity.this))) {
                    // not ur own power up
                    handlePowerup(powerUp.powerUpType);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer1 = new MediaPlayer();
        mediaPlayer2 = new MediaPlayer();
        mediaPlayer3 = new MediaPlayer();
    }

    private void setUpPowerListner() {
        powerUpsendNoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ParticleSystem(GameActivity.this, 100, R.drawable.sendnoods_particle, 1000)
                        .setSpeedRange(0.2f, 0.3f)
                        .oneShot(powerUpsendNoods, 100);
                powerUpsendNoods.setAlpha(127);
                sendPowerUp(SEND_NOODLE_POWER_UP);
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
                sendPowerUp(SEND_VIBRATE_POWER_UP);
                powerUpsendVirate.setClickable(false);
            }
        });
    }

    private void sendPowerUp(int type) {
        mDatabase.child(curGameId).child(GAME_POWER_UPS).push().setValue(new PowerUp(NCUserPreference.getUserId(GameActivity.this), type));
    }

    public void handlePowerup(int type) {
        switch (type) {
            case SEND_NOODLE_POWER_UP:
                onReceiveNoods("Dadyju");
                break;
            case SEND_VIBRATE_POWER_UP:
                onReceiveVibrate("Dadyju");
                break;
            default:
                break;
        }
    }

    private void onReceiveNoods(String playerName) {
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

    private void onReceiveVibrate(String playerName) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(5000);
    }

}
