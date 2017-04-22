package com.kkadadeepju.snwf.sendnoodswithfriends;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kkadadeepju.snwf.sendnoodswithfriends.model.GameClass;
import com.kkadadeepju.snwf.sendnoodswithfriends.model.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static android.R.attr.key;

public class MainActivity extends AppCompatActivity {

    public static final String TIME_WAITING = "gameWaiting";
    public static final String IS_GAME_STARTED = "isGameStarted";
    public static final String GAME_ID = "gameId";
    public static final String GAME_USERS = "gameUsers";

    public static final int WAITING_TIME = 6;
    SocketManager manager;
    private Button findGame;
    private ProgressBar loadingDialog;
    private ArrayList<ImageView> figureImgs = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findGame = (Button) findViewById(R.id.find_game_btn);
        loadingDialog = (ProgressBar) findViewById(R.id.loadingDialog);
        figureImgs.add((ImageView) findViewById(R.id.figure_one));
        figureImgs.add((ImageView) findViewById(R.id.figure_two));
        figureImgs.add((ImageView) findViewById(R.id.figure_three));
        figureImgs.add((ImageView) findViewById(R.id.figure_four));
        figureImgs.add((ImageView) findViewById(R.id.figure_five));
        figureImgs.add((ImageView) findViewById(R.id.figure_six));
        final Animation shake = AnimationUtils.loadAnimation(this, R.anim.toggle);

//        for (ImageView img : figureImgs) {
//            img.startAnimation(shake);
//        }


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Games");


        findGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                // loop through gamelist to find if there is a existing game, join if there is. create a new one otherwise
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            GameClass gameClass = snapshot.getValue(GameClass.class);
                            if (gameClass.gameWaiting > 0) {
                                // join the game
                                String tempUserId = mDatabase.child(gameClass.gameId).child(GAME_USERS).push().getKey();
                                mDatabase.child(gameClass.gameId).child(GAME_USERS).child(tempUserId).setValue(new UserInfo(Double.toString(Math.random()), 0, -1));
                                mDatabase.child(gameClass.gameId).child(IS_GAME_STARTED).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        boolean shouldGameStart = (boolean) dataSnapshot.getValue();
                                        if (shouldGameStart) {
                                            startGame();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                return;
                            }
                        }

                        // create a new Game
                        final String gameId = mDatabase.push().getKey();
                        Map<String, Object> gameInfo = new HashMap<>();
                        gameInfo.put(GAME_ID, gameId);
                        gameInfo.put(TIME_WAITING, WAITING_TIME);
                        gameInfo.put(IS_GAME_STARTED, false);

                        mDatabase.child(gameId).setValue(gameInfo);
                        String userId = mDatabase.child(gameId).child(GAME_USERS).push().getKey();

                        //user Info
                        mDatabase.child(gameId).child(GAME_USERS).child(userId).setValue(new UserInfo(Double.toString(Math.random()), 0, -1));

                        new CountDownTimer(WAITING_TIME * 1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                mDatabase.child(gameId).child(TIME_WAITING).setValue(millisUntilFinished / 1000);
                            }

                            public void onFinish() {
                                mDatabase.child(gameId).child(TIME_WAITING).setValue(-1);
                                mDatabase.child(gameId).child(IS_GAME_STARTED).setValue(true);
                                startGame();
                                // start the game
                            }
                        }.start();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        // TODO: show acknowledge dialog when game is ready to enter

    }


    private void startMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.theme_music);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.create(getApplicationContext(), R.raw.theme_music);
                mediaPlayer.start();
            }

        });
        mediaPlayer.start();
    }

    public void showLoadingDialog() {
        loadingDialog.setVisibility(View.VISIBLE);
        findGame.setVisibility(View.INVISIBLE);
    }

    public void shutdownLoadingDialog() {
        mediaPlayer.stop();
    }


    private void startGame() {
        shutdownLoadingDialog();
        Intent myIntent = new Intent(this, GameActivity.class);
        startActivity(myIntent);
    }
}
