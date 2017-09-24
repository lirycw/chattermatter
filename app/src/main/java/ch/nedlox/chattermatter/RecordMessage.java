package ch.nedlox.chattermatter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by cyril on 24.09.2017.
 */

public class RecordMessage extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_main);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        // Capture the layout's TextView and set the string as its text

    }

}