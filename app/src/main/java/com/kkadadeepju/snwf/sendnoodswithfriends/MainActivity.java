package com.kkadadeepju.snwf.sendnoodswithfriends;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private Button findGame;
    private ArrayList<ImageView> figureImgs = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findGame = (Button) findViewById(R.id.find_game_btn);
        figureImgs.add((ImageView) findViewById(R.id.figure_one));
        figureImgs.add((ImageView) findViewById(R.id.figure_two));
        figureImgs.add((ImageView) findViewById(R.id.figure_three));
        figureImgs.add((ImageView) findViewById(R.id.figure_four));
        figureImgs.add((ImageView) findViewById(R.id.figure_five));
        figureImgs.add((ImageView) findViewById(R.id.figure_six));
        final Animation shake = AnimationUtils.loadAnimation(this, R.anim.toggle);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.theme_music);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.create(getApplicationContext(), R.raw.theme_music);
                mediaPlayer.start();
            }

        });
        mediaPlayer.start();

        for (ImageView img : figureImgs) {
            img.startAnimation(shake);
        }

        findGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: send request to the server
                mediaPlayer.stop();

                Intent myIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(myIntent);
            }
        });
        // TODO: show acknowledge dialog when game is ready to enter



        SocketManager manager = new SocketManager();
       // manager.connect();
    }
}
