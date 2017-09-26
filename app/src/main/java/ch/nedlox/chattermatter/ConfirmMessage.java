package ch.nedlox.chattermatter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

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
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        setContentView(R.layout.confirm_message);

        play_bttn = (FloatingActionButton) findViewById(R.id.play_bttn);
        button6 = (Button) findViewById(R.id.button6);
        button5 = (Button) findViewById(R.id.button5);

        play_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(RecordMessage.AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final Handler handler = new Handler();
                Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        int duration = mediaPlayer.getDuration() / 1000;
                        if (duration < 1){
                            duration = 1;
                        }
                            int progress = (currentPosition * 100) / duration;
                            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
                            seekBar.setProgress(progress);

                        handler.postDelayed(this, 1000);
                    }
                };
                mediaPlayer.start();

                handler.postDelayed(runnable, 1000);

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
               File audioFile = new File(RecordMessage.AudioSavePathInDevice);

                byte[] bytes = new byte[0];
                try {
                    bytes = FileUtils.readFileToByteArray(audioFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final String encoded = Base64.encodeToString(bytes, 0);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream os = null;
                      //  InputStream is = null;
                        HttpURLConnection conn = null;
                        try {
                            //constants
                            URL url = new URL("http://uek.nedlox.ch/writer.php");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("file", encoded);
                            jsonObject.put("userFS", "1");
                            jsonObject.put("location", "19");
                            String message = jsonObject.toString();

                            conn = (HttpURLConnection) url.openConnection();
                            conn.setReadTimeout( 10000 );
                            conn.setConnectTimeout( 15000 ) ;
                            conn.setRequestMethod("POST");
                            conn.setDoInput(true);
                            conn.setDoOutput(true);
                            conn.setFixedLengthStreamingMode(message.getBytes().length);

                            //make some HTTP header nicety
                            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                            //open
                            conn.connect();

                            //setup send
                            os = new BufferedOutputStream(conn.getOutputStream());
                            os.write(message.getBytes("UTF8"));
                            //clean up
                            os.flush();

                            //do somehting with response
                           // is = conn.getInputStream();
                            //String contentAsString = readIt(is,len);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            //clean up
                            try {
                                os.close();
                               //is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            conn.disconnect();
                        }
                    }
                }).start();
                Intent launchactivity=new Intent (ConfirmMessage.this,MainActivity.class);
                startActivity(launchactivity);
            }
       });
    }

}
