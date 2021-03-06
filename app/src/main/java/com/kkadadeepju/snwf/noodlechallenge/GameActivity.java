package com.kkadadeepju.snwf.noodlechallenge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.kkadadeepju.snwf.noodlechallenge.adapter.PlayerScoreAdapter;
import com.kkadadeepju.snwf.noodlechallenge.dialog.EndOfTurnDIalog;
import com.kkadadeepju.snwf.noodlechallenge.model.GameState;
import com.kkadadeepju.snwf.noodlechallenge.model.PowerUp;
import com.kkadadeepju.snwf.noodlechallenge.model.UserInfo;
import com.kkadadeepju.snwf.noodlechallenge.widget.BowlImageView;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.kkadadeepju.snwf.noodlechallenge.Constants.GAMES;
import static com.kkadadeepju.snwf.noodlechallenge.Constants.GAME_POWER_UPS;
import static com.kkadadeepju.snwf.noodlechallenge.Constants.GAME_STATES;
import static com.kkadadeepju.snwf.noodlechallenge.Constants.GAME_USERS;
import static com.kkadadeepju.snwf.noodlechallenge.Constants.GAME_ID;
import static com.kkadadeepju.snwf.noodlechallenge.MainActivity.SCORE;
import static com.kkadadeepju.snwf.noodlechallenge.Constants.USER_ID;


/**
 * Created by jzhou on 2017-04-20.
 */

public class GameActivity extends AppCompatActivity {

    private static final int SEND_NOODLE_POWER_UP = 1;
    private static final int SEND_VIBRATE_POWER_UP = 2;

    private DatabaseReference mDatabase;

    static boolean active = false;
    private final int GAME_TIME_MILLIS = 20000;
    private TextView timer;
    private TextView playerScore;
    private ImageView noodleBowl;
    private ImageView chopStickUp;
    private ImageView chopStickDown;
    private ImageView powerUpnoodlechallenge;
    private ImageView powerUpsendVirate;

    private RecyclerView playerRvView;
    private PlayerScoreAdapter adapter;


    private MediaPlayer mediaPlayer1;
    private MediaPlayer mediaPlayer2;
    private MediaPlayer mediaPlayer3;

    private boolean mp1Released = true;
    private boolean mp2Released = true;
    private boolean mp3Released = true;

    private ViewGroup noodlechallengeLayout;
    private TextView gameStartCountdown;

    private LinearLayout finishedBowlContainer;
    private BowlImageView finishedBowl;


    private int score = 0;

    private int imgPosition = 0;

    private boolean isChopstickUp = false;

    private boolean isnoodlechallengeActive = false;
    private int numberOfnoodlechallengeLeft;
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

    private ArrayList<ImageView> noodlechallenge = new ArrayList<>();

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

        timer = (TextView) findViewById(R.id.timer);
        playerScore = (TextView) findViewById(R.id.player_score);

        chopStickUp = (ImageView) findViewById(R.id.chopstick_up);
        chopStickDown = (ImageView) findViewById(R.id.chopstick_down);
        noodleBowl = (ImageView) findViewById(R.id.noodle_bowl);
        powerUpnoodlechallenge = (ImageView) findViewById(R.id.powerup_send_noodle);
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

        noodlechallengeLayout = (ViewGroup) findViewById(R.id.send_noods_layout);
        noodlechallengeLayout.setVisibility(View.GONE);
        noodlechallenge.add((ImageView) findViewById(R.id.send_noods_1));
        noodlechallenge.add((ImageView) findViewById(R.id.send_noods_2));
        noodlechallenge.add((ImageView) findViewById(R.id.send_noods_3));
        noodlechallenge.add((ImageView) findViewById(R.id.send_noods_4));
        noodlechallenge.add((ImageView) findViewById(R.id.send_noods_5));
        noodlechallenge.add((ImageView) findViewById(R.id.send_noods_6));
        noodlechallenge.add((ImageView) findViewById(R.id.send_noods_7));
        noodlechallenge.add((ImageView) findViewById(R.id.send_noods_8));

        final Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);

        final LinearLayout.LayoutParams bowlStackLayout = new LinearLayout.LayoutParams((int) imgSize, (int) imgSize);
        bowlStackLayout.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -15, getResources().getDisplayMetrics());

        noodleBowl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOiSound();

                if (isnoodlechallengeActive) {
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
        noodleBowl.setClickable(false);
        //resetMPs();
        mDatabase.child(curGameId).child(GAME_USERS).child(curUserId).child(SCORE).setValue(score);
        mDatabase.child(curGameId).child(GAME_STATES).child(curUserId).setValue(new GameState(true, score, com.kkadadeepju.snwf.noodlechallenge.NCUserPreference.getUserGameName(GameActivity.this)));

        showResultDialog();
    }

    @Override
    public void onBackPressed() {
//        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setTitle("Closing Game")
//                .setMessage("Are you sure you want to close current game?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        finish();
//                        onGameFinish();
////                        Intent myIntent = new Intent(GameActivity.this, MainActivity.class);
////                        startActivity(myIntent);
//                    }
//
//                })
//                .setNegativeButton("No", null)
//                .show();
        return;
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
        mDatabase.child(curGameId).child(GAME_STATES).child(curUserId).setValue(new GameState(false, -1, com.kkadadeepju.snwf.noodlechallenge.NCUserPreference.getUserGameName(GameActivity.this)));
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
                if (!powerUp.userId.equals(com.kkadadeepju.snwf.noodlechallenge.NCUserPreference.getUserId(GameActivity.this))) {
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
        powerUpnoodlechallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ParticleSystem(GameActivity.this, 100, R.drawable.sendnoods_particle, 1000)
                        .setSpeedRange(0.2f, 0.3f)
                        .oneShot(powerUpnoodlechallenge, 100);
                powerUpnoodlechallenge.setAlpha(127);
                sendPowerUp(SEND_NOODLE_POWER_UP);
                powerUpnoodlechallenge.setClickable(false);
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
        mDatabase.child(curGameId).child(GAME_POWER_UPS).push().setValue(new PowerUp(com.kkadadeepju.snwf.noodlechallenge.NCUserPreference.getUserId(GameActivity.this), type));
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
        isnoodlechallengeActive = true;
        noodlechallengeLayout.setVisibility(View.VISIBLE);
        numberOfnoodlechallengeLeft = noodlechallenge.size();

        for (int i = 0; i < noodlechallenge.size(); i++) {
            noodlechallenge.get(i).setVisibility(View.VISIBLE);

            noodlechallenge.get(i).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setVisibility(View.INVISIBLE);
                            numberOfnoodlechallengeLeft--;

                            if (numberOfnoodlechallengeLeft <= 0) {
                                isnoodlechallengeActive = false;
                                noodlechallengeLayout.setVisibility(View.GONE);
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
        v.vibrate(8000);
    }

}
