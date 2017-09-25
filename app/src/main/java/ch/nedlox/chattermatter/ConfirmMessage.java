package ch.nedlox.chattermatter;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_message);

        play_bttn = (FloatingActionButton) findViewById(R.id.play_bttn);

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
                final int amoungToupdate = duration / 100;
                Timer mTimer = new Timer();
                mTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (!(amoungToupdate * seekBar.getProgress() >= duration)) {
                                    int p = seekBar.getProgress();
                                    p += 1;
                                    seekBar.setProgress(p);
                                }
                            }
                        });
                    };
                }, amoungToupdate);

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

    }

}
