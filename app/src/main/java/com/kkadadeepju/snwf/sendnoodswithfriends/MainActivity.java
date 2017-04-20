package com.kkadadeepju.snwf.sendnoodswithfriends;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private Button findGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findGame = (Button) findViewById(R.id.find_game_btn);

        findGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: send request to the server
            }
        });
        // TODO: show acknowledge dialog when game is ready to enter
    }
}
