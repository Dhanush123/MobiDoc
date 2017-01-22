package com.x10host.dhanushpatel.mobidoc;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final int TAKE_PICTURE = 1;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    public void takePic(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if ((requestCode ==  TAKE_PICTURE) && resultCode == RESULT_OK && intent != null) {
            Intent i2 = new Intent(MainActivity.this,ResultsActivity.class);
            i2.putExtra("imageUri", intent.getData().toString());
            startActivity(i2);
        }
    }
}
