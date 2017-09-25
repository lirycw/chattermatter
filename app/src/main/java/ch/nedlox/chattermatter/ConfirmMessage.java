package ch.nedlox.chattermatter;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by cyril on 25.09.17.
 */

public class ConfirmMessage extends AppCompatActivity {

    MediaPlayer mediaPlayer ;
    FloatingActionButton play_bttn;

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

                mediaPlayer.start();
                Toast.makeText(ConfirmMessage.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

}
