package ch.nedlox.chattermatter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;

/**
 * Created by cyril on 25.09.17.
 */

public class ConfirmMessage extends AppCompatActivity {

    MediaPlayer mediaPlayer ;
    FloatingActionButton play_bttn;
    SeekBar seekBar;
    Button button5;
    Button button6;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_message);

        play_bttn = (FloatingActionButton) findViewById(R.id.play_bttn);
        button6 = (Button) findViewById(R.id.button6);

        play_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {


                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(RecordMessage.AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final int duration = mediaPlayer.getDuration();
                final int amountToupdate = duration / 100;
                Timer mTimer = new Timer();
                mTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (!(amountToupdate * seekBar.getProgress() >= duration)) {
                                    int p = seekBar.getProgress();
                                    p += 1;
                                    seekBar.setProgress(p);
                                }
                            }
                        });
                    };
                }, amountToupdate);

                mediaPlayer.start();
                Toast.makeText(ConfirmMessage.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
                play_bttn.setImageResource(ic_media_pause);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        play_bttn.setImageResource(ic_media_play);
                    }
                });

            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchactivity=new Intent (ConfirmMessage.this,MainActivity.class);
                startActivity(launchactivity);
                Toast.makeText(ConfirmMessage.this, "Nachricht verworfen",
                        Toast.LENGTH_LONG).show();
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URL url = null;
                try {
                    url = new URL("http://uek.nedlox.ch/writer.php?");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
        });
    }

}
