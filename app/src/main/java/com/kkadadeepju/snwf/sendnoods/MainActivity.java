package com.kkadadeepju.snwf.sendnoods;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kkadadeepju.snwf.sendnoods.model.GameClass;
import com.kkadadeepju.snwf.sendnoods.model.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kkadadeepju.snwf.sendnoods.Constants.GAMES;
import static com.kkadadeepju.snwf.sendnoods.Constants.GAME_ID;
import static com.kkadadeepju.snwf.sendnoods.Constants.GAME_USERS;
import static com.kkadadeepju.snwf.sendnoods.Constants.IS_GAME_STARTED;
import static com.kkadadeepju.snwf.sendnoods.Constants.TIME_WAITING;
import static com.kkadadeepju.snwf.sendnoods.Constants.USER_ID;

public class MainActivity extends AppCompatActivity {

    public static final String SCORE = "score";

    public static final int WAITING_TIME = 6;

    private boolean isLookingForGame = false;
    private Button findGame;
    private ViewGroup loadingDialogContainer;
    private ArrayList<ImageView> figureImgs = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findGame = (Button) findViewById(R.id.find_game_btn);
        loadingDialogContainer = (ViewGroup) findViewById(R.id.loadingDialog_container);
        figureImgs.add((ImageView) findViewById(R.id.figure_one));
        figureImgs.add((ImageView) findViewById(R.id.figure_two));
        figureImgs.add((ImageView) findViewById(R.id.figure_three));
        figureImgs.add((ImageView) findViewById(R.id.figure_four));
        figureImgs.add((ImageView) findViewById(R.id.figure_five));
        figureImgs.add((ImageView) findViewById(R.id.figure_six));
        final Animation shake = AnimationUtils.loadAnimation(this, R.anim.toggle);

        for (ImageView img : figureImgs) {
            img.startAnimation(shake);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child(GAMES);


        findGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLookingForGame = true;
                // loop through gamelist to find if there is a existing game, join if there is. create a new one otherwise
                if (showEnterUserNameDialog()) {
                    showLoadingDialog(isLookingForGame);
                    queueGame();
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //startMusic();
        showLoadingDialog(isLookingForGame);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopMusic();
    }

    private void startMusic() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.theme_music);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.create(getApplicationContext(), R.raw.theme_music);
                mediaPlayer.start();
            }

        });
        mediaPlayer.start();
    }

    private void stopMusic() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void showLoadingDialog(boolean show) {
        loadingDialogContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        findGame.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    private void hideLoadingDialog() {
        loadingDialogContainer.setVisibility(View.GONE);
    }

    private boolean showEnterUserNameDialog() {
        if (!NCUserPreference.isUserNameSet(this)) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText edittext = new EditText(this);
            alert.setTitle("Enter Your Name");

            alert.setView(edittext);

            alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String userGameName = edittext.getText().toString();
                    if (!TextUtils.isEmpty(userGameName)) {
                        NCUserPreference.setUserGameName(MainActivity.this, userGameName);
                    }
                    showLoadingDialog(isLookingForGame);
                    queueGame();
                }
            });


            alert.show();
            return false;
        }

        // already set
        return true;

    }

    private void queueGame() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    final GameClass gameClass = snapshot.getValue(GameClass.class);
                    if (gameClass.gameWaiting > 0) {
                        // join existing game
                        final String tempUserId = mDatabase.child(gameClass.gameId).child(GAME_USERS).push().getKey();
                        NCUserPreference.setUserGameId(MainActivity.this, tempUserId);
                        mDatabase.child(gameClass.gameId).child(GAME_USERS).child(tempUserId).setValue(new UserInfo(NCUserPreference.getUserGameName(MainActivity.this), tempUserId, 0, -1));
                        mDatabase.child(gameClass.gameId).child(IS_GAME_STARTED).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean shouldGameStart = (boolean) dataSnapshot.getValue();
                                if (shouldGameStart) {
                                    startGame(gameClass.gameId, tempUserId);
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
                final String userId = mDatabase.child(gameId).child(GAME_USERS).push().getKey();
                NCUserPreference.setUserGameId(MainActivity.this, userId);

                //user Info
                mDatabase.child(gameId).child(GAME_USERS).child(userId).setValue(new UserInfo(NCUserPreference.getUserGameName(MainActivity.this), userId, 0, -1));

                new CountDownTimer(WAITING_TIME * 1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        mDatabase.child(gameId).child(TIME_WAITING).setValue(millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        mDatabase.child(gameId).child(TIME_WAITING).setValue(-1);
                        mDatabase.child(gameId).child(IS_GAME_STARTED).setValue(true);
                        startGame(gameId, userId);
                        // start the game
                    }
                }.start();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startGame(String gameId, String userId) {
        isLookingForGame = false;
        hideLoadingDialog();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Loading game...");
        builder1.setCancelable(false);
        builder1.show();


        Intent myIntent = new Intent(this, GameActivity.class);
        myIntent.putExtra(GAME_ID, gameId);
        myIntent.putExtra(USER_ID, userId);
        startActivity(myIntent);
        finish();
    }
}
