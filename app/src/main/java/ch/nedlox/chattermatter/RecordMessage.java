package ch.nedlox.chattermatter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by cyril on 24.09.2017.
 */

public class RecordMessage extends AppCompatActivity {

    FloatingActionButton record_bttn;
    static String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    MediaPlayer mediaPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_main);

        record_bttn = (FloatingActionButton) findViewById(R.id.record_bttn);
        final int[] record_bttn_counter = {0};

        random = new Random();

        record_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (record_bttn_counter[0] == 0) {
                    record_bttn_counter[0] = 1;
                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    CreateRandomAudioFileName(5) + "AudioRecording.3gp";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Toast.makeText(RecordMessage.this, "Recording started",
                            Toast.LENGTH_LONG).show();

                } else {
                    record_bttn_counter[0] = 0;
                    mediaRecorder.stop();
                    if (AudioSavePathInDevice == null) {
                        Toast.makeText(RecordMessage.this, "Nachricht wurde nicht aufgenommen",
                                Toast.LENGTH_LONG).show();
                        Intent launchactivity = new Intent(RecordMessage.this, RecordMessage.class);
                        startActivity(launchactivity);
                    } else {
                        Toast.makeText(RecordMessage.this, "Recording Completed",
                                Toast.LENGTH_LONG).show();
                        Intent launchactivity = new Intent(RecordMessage.this, ConfirmMessage.class);
                        startActivity(launchactivity);
                    }
                }
            }
        });

    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

}