package ch.nedlox.chattermatter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    ListView MessageList;
    String url = "http://uek.nedlox.ch/reader.php";
    String imei;
    ProgressDialog dialog;

    SharedPreferences prefs = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("ch.nedlox.chattermatter", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        MessageList = (ListView)findViewById(R.id.messageListView);

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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_record_bttn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchactivity=new Intent (MainActivity.this,RecordMessage.class);
                startActivity(launchactivity);
            }
        });
    }
    public void getImei(){
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
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
        try {
            JSONArray arr = new JSONArray(jsonString);
            ArrayList<Post> al = new ArrayList<>();
            for(int i = 0; i < arr.length(); i++){
                String location = arr.getJSONObject(i).getString("location");
                String voice = arr.getJSONObject(i).getString("file");
                // TODO make date with data
//                Date date = new Date(arr.getJSONObject(i).getString("date"));
                Date date = new Date(2017, 9, 25);
                int userId = Integer.parseInt(arr.getJSONObject(i).getString("userFS"));
                int postId = Integer.parseInt(arr.getJSONObject(i).getString("ID"));
                al.add(new Post(location, voice, date, userId, postId));
            }

            ArrayList<String> temp = new ArrayList<>();

            for(int i=0;i<al.toArray().length;i++){
                temp.add(al.get(i).getVoice());
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, temp);
            MessageList.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.dismiss();
    }

}
