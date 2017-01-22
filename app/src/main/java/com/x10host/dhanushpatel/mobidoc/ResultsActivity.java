package com.x10host.dhanushpatel.mobidoc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.TreeMap;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Prediction;
import okhttp3.OkHttpClient;

import static android.R.attr.name;

public class ResultsActivity extends AppCompatActivity {

    List<ClarifaiOutput<Prediction>> predictionResults;
    byte[] imageData;

    TextView resultsShow;

    double cat, red, vit, cor;
    String top;

    DecimalFormat dff = new DecimalFormat(".###");

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    SharedPreferences prefs;

    String result;

//    public enum Symptom { CATARACT, RED_EYE, VITILIGO, CORN };
//    public Symptom mostLikelySymptom;

    final ClarifaiClient client = new ClarifaiBuilder("q2Jo-fh5d3m-Iqd_Ka9DFDCcK8Agjfm6x2EoTjKu", "xoqeavlGMHa35o6njlUKMutevwrU5-fxhsRvwLZy")
            .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
            .buildSync();

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        resultsShow = (TextView) findViewById(R.id.resultsView);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        InputStream iStream = null;
        if(getIntent().getStringExtra("imageUri") != null) {
            try {
                iStream = getContentResolver().openInputStream(Uri.parse(getIntent().getStringExtra("imageUri")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                imageData = getBytes(iStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            getResults();
        }
        else {
            result = "";
            String lastCat = prefs.getString("lastCat", "1.00%");
            result = result + "Chance of " + name + ":\n" + lastCat + "%\n";
            String lastVit = prefs.getString("lastVit", "1.00%");
            result = result + "Chance of " + name + ":\n" + lastVit + "%\n";
            String lastRed = prefs.getString("lastRed", "1.00%");
            result = result + "Chance of " + name + ":\n" + lastRed + "%\n";
            String lastCor = prefs.getString("lastCor", "1.00%");
            result = result + "Chance of " + name + ":\n" + lastCor + "%\n";
            resultsShow.setText(result);
            findViewById(R.id.loadingCircle).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_graphs:
                startActivity(new Intent(ResultsActivity.this, GraphsActivity.class));
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_call:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:911"));
                startActivity(intent);
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_links:
                Intent i = new Intent(ResultsActivity.this,LinkActivity.class);
                i.putExtra("typeD",top);
                startActivity(i);
                return true;
            case R.id.action_map:
                String map = "https://www.google.com/maps/dir/UC+Davis+Activities+and+Recreation+Center,+The+ARC-+Activities+and+Recreation+Center,+One+Shields+Avenue,,+232+Shields+Ave,+Davis,+CA+95616/Sutter+West+Medical+Group,+1801+Hanover+Dr+F,+Davis,+CA+95616/@38.5504692,-121.7677072,15z/data=!4m14!4m13!1m5!1m1!1s0x80852900aef53735:0x8fd7da59621c13d2!2m2!1d-121.7592483!2d38.5427621!1m5!1m1!1s0x808529b6e6c9f871:0x9ad1ff431d21fb65!2m2!1d-121.7568279!2d38.5594132!3e0";
                Intent x = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(x);
//                startActivity(new Intent(ResultsActivity.this,MapActivity.class));
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void getResults(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                // something you know that will take a few seconds
                  predictionResults = client.predict("unhealthy").withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageData)))
                        .executeSync().getOrNull();
                return null;
            }

            @Override
            protected void onPostExecute(final Void result) {
                findViewById(R.id.loadingCircle).setVisibility(View.GONE);
                // continue what you are doing...
                clarifaiProcess();
                Log.i("Clarifai","recognition done.");
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    private void clarifaiProcess() {
        Log.i("success status", String.valueOf(predictionResults));
        if (predictionResults != null) {
            result = "";
            List<Prediction> tagsFound = predictionResults.get(0).data();
            Log.i("concepts",tagsFound.toString());
            TreeMap<Number, String> sortedSymptoms = new TreeMap<>();

            for(int i = 0; i < tagsFound.size(); i++){
                String name = tagsFound.get(i).asConcept().name();
                double val = tagsFound.get(i).asConcept().value() * 100;
                SharedPreferences.Editor editor = prefs.edit();
                if(name.equals("cataract")){
                    name = "Cataract";
                    DatabaseReference myRef = database.getReference("cataractData");
                    myRef.push().setValue(val);
                    editor.putString("lastCat", dff.format(val));
                    cat = val;
                }
                else if(name.equals("red vein")){
                    name = "Red Vein";
                    DatabaseReference myRef = database.getReference("redveinData");
                    myRef.push().setValue(val);
                    editor.putString("lastRed", dff.format(val));
                    red = val;
                }
                else if(name.equals("Vitiligo")){
                    DatabaseReference myRef = database.getReference("vitiligoData");
                    myRef.push().setValue(val);
                    editor.putString("lastVit", dff.format(val));
                    vit = val;
                }
                else if(name.equals("Corn")){
                    DatabaseReference myRef = database.getReference("cornData");
                    myRef.push().setValue(val);
                    editor.putString("lastCor", dff.format(val));
                    cor = val;
                }
                sortedSymptoms.put(val, name);
                editor.commit();
                result = result + "Chance of " + name + ":\n" + dff.format(val) + "%\n";
            }
            top = sortedSymptoms.lastEntry().getValue();
            resultsShow.setText(result);
        } else {
            Toast.makeText(ResultsActivity.this, "Personal analysis failed.", Toast.LENGTH_LONG).show();
            Log.i("No tags", "could be found...");
        }
    }

    public void restartProg(View v){
        startActivity(new Intent(ResultsActivity.this,MainActivity.class));
    }
}
