package ch.nedlox.chattermatter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.Context;


import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.R.drawable.ic_media_play;

public class MainActivity extends AppCompatActivity {
  ArrayList<Post> al;
  ListView MessageList;
  String url = "http://uek.nedlox.ch/reader.php";
  ProgressDialog dialog;
  String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
  Random random;
  StringRequest request;
  SwipeRefreshLayout refresh;
  SharedPreferences prefs = null;
  public static final int RequestPermissionCode = 1;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
      //Check if Permission is given
      if (!checkPermission()){
          requestPermission();
      }
    random = new Random();
    prefs = getSharedPreferences("ch.nedlox.chattermatter", MODE_PRIVATE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_main);

    MessageList = (ListView) findViewById(R.id.listview);

    request = getJson();

    refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

    refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        request = getJson();
        refresh.setRefreshing(false);
      }
    });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_record_bttn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchactivity=new Intent (MainActivity.this,RecordMessage.class);
                startActivity(launchactivity);
            }
        });
        MessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            MediaPlayer mediaPlayer;
            String AudioSavePathInDevice = null;
            int playcounter = 0;
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
              if (playcounter == 0){
                playcounter = 1;
                String value = (String)adapter.getItemAtPosition(position);
                String voice = al.get(position).getVoice();
                byte[] decoded = Base64.decode(voice, 0);
                try {
                  AudioSavePathInDevice =
                          Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                  CreateRandomAudioFileName(5) + "AudioRecording.3gp";
                  final File file2 = new File(AudioSavePathInDevice);
                  FileOutputStream os = new FileOutputStream(file2, true);
                  os.write(decoded);
                  os.close();
                  mediaPlayer = new MediaPlayer();
                  try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                  mediaPlayer.start();
                  mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                      playcounter = 0;
                      file2.delete();
                    }
                  });

                } catch (Exception e) {
                  e.printStackTrace();
                }
              }else {
                mediaPlayer.stop();
                playcounter = 0;

              }

            }
        });
  }
    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
  public String CreateRandomAudioFileName(int string) {
    StringBuilder stringBuilder = new StringBuilder(string);
    int i = 0;
    while (i < string) {
      stringBuilder.append(RandomAudioFileName.
          charAt(random.nextInt(RandomAudioFileName.length())));

      i++;
    }
    return stringBuilder.toString();
  }

  /* Checks if external storage is available for read and write */
  public boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return true;
    }
    return false;
  }

  void parseJsonData(String jsonString) {

    al = new ArrayList<>();

    try {
      JSONArray arr = new JSONArray(jsonString);
      for (int i = 0; i < arr.length(); i++) {
        String location = arr.getJSONObject(i).getString("location");
        String voice = arr.getJSONObject(i).getString("file");

        String date = arr.getJSONObject(i).getString("date");
        int userId = Integer.parseInt(arr.getJSONObject(i).getString("userFS"));
        int postId = Integer.parseInt(arr.getJSONObject(i).getString("ID"));
        al.add(new Post(location, voice, date, userId, postId));
      }

      ArrayList<String> temp = new ArrayList<>();

      for (int i = 0; i < al.toArray().length; i++) {
        temp.add(al.get(i).getDate().toString());
      }

      ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_view, R.id.list_item_text, temp);
      MessageList.setAdapter(adapter);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    dialog.dismiss();
  }

  private StringRequest getJson(){

    dialog = new ProgressDialog(this);
    dialog.setMessage("ChatterMatter läd gerade");
    dialog.show();

    StringRequest request = new StringRequest(url, new Response.Listener<String>() {
      @Override
      public void onResponse(String string) {
        parseJsonData(string);
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
      }
    });

    RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
    rQueue.add(request);

    return request;
  }

    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects;

        private MyListAdaper(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.title.setText(getItem(position));

            return convertView;
        }
    }

    public class ViewHolder {

        ImageView thumbnail;
        TextView title;
        Button button;
    }

}
