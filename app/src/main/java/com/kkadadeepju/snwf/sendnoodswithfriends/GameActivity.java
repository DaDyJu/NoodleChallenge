package com.kkadadeepju.snwf.sendnoodswithfriends;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by jzhou on 2017-04-20.
 */

public class GameActivity extends AppCompatActivity {
    private TextView timer;
    private TextView playerTwoScore;
    private TextView playerThreeScore;
    private TextView playerFourScore;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        timer = (TextView) findViewById(R.id.timer);
        playerTwoScore = (TextView) findViewById(R.id.player_two_score);
        playerThreeScore = (TextView) findViewById(R.id.player_three_score);
        playerFourScore = (TextView) findViewById(R.id.player_four_score);

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timer.setText("done!");
            }
        }.start();
    }

}
