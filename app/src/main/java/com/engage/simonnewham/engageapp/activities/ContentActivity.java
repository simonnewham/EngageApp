package com.engage.simonnewham.engageapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.adapters.SectionsStatePagerAdapter;
import com.engage.simonnewham.engageapp.fragments.AudioFragment;
import com.engage.simonnewham.engageapp.fragments.ConsentFragment;
import com.engage.simonnewham.engageapp.fragments.ImageFragment;
import com.engage.simonnewham.engageapp.fragments.SignUpFragment;
import com.engage.simonnewham.engageapp.fragments.TextFragment;
import com.engage.simonnewham.engageapp.fragments.VideoFragment;
import com.engage.simonnewham.engageapp.models.NewsItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author simonnewham
 * Class to show individual news items downloaded from server
 * Image, Audio and Video and Text items are rendered within their own fragment
 */

public class ContentActivity extends AppCompatActivity {

    private final String TAG = "ContentActivity";

    //shared preference code
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    TextView titleF;
    TextView dateF;
    Button surveyB;

    String email;
    String user_group;
    NewsItem item;

    private ProgressBar progressBar;
    private FrameLayout frameLayout;

    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    AudioFragment audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        surveyB = findViewById(R.id.surveyButton);
        titleF = findViewById(R.id.titleFixed);
        dateF = findViewById(R.id.dateFixed);
        frameLayout = findViewById(R.id.container);

        //get Extra
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            email = extras.getString("email");
            user_group = extras.getString("group");
            item = (NewsItem) getIntent().getSerializableExtra("News");
        }
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        setItem();

        Log.i(TAG, "Displaying Item"+item.getPath());
    }

    /**
     * Method to set up the news article within the android view
     */
    public void setItem(){

        String type = item.getType().toUpperCase();
        progressBar.setVisibility(View.VISIBLE);

        if(type.equals("IMAGE")){
            //set date and title
            frameLayout.setVisibility(View.VISIBLE);
            titleF.setVisibility(View.VISIBLE);
            dateF.setVisibility(View.VISIBLE);
            dateF.setText("Uploaded on: "+item.getDate());
            titleF.setText(item.getName());

            transaction.add(R.id.container, new ImageFragment(item.getPath()),"ImageFrag");
            transaction.commit();
            //progressBar.setVisibility(View.GONE);
        }
        else if(type.equals("VIDEO")){
            frameLayout.setVisibility(View.VISIBLE);
            titleF.setVisibility(View.VISIBLE);
            dateF.setVisibility(View.VISIBLE);
            dateF.setText("Uploaded on: "+item.getDate());
            titleF.setText(item.getName());

            transaction.add(R.id.container, new VideoFragment(item.getPath()));
            transaction.commit();
            //progressBar.setVisibility(View.GONE);
        }

        else if(type.equals("TEXT")){
            transaction.add(R.id.container, new TextFragment(item), "TextFrag");
            transaction.commit();

            progressBar.setVisibility(View.GONE);
        }

        else if(type.equals("AUDIO")){
            frameLayout.setVisibility(View.VISIBLE);
            titleF.setVisibility(View.VISIBLE);
            dateF.setVisibility(View.VISIBLE);
            dateF.setText("Uploaded on: "+item.getDate());
            titleF.setText(item.getName());

            //set fragment to audio version
            audio = new AudioFragment(item.getPath());
            transaction.add(R.id.container, audio, "AudioFrag");
            transaction.commit();

            //progressBar.setVisibility(View.GONE);
        }
    }

    public boolean onSupportNavigateUp() {
        clearStack();

        Intent intent = new Intent(ContentActivity.this, MainActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("group", user_group);
        startActivity(intent);
        finish();
        return true;
    }

    public void loadSurvey(View view){
        clearStack();

        Intent intent = new Intent(ContentActivity.this, SurveyActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("group", user_group);
        intent.putExtra("surveyID", "ITEM");
        intent.putExtra("News", item);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    /**
     * Method responsible for handling events on the toolbar
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()){
            case R.id.home:
                clearStack();
                intent = new Intent(ContentActivity.this, MainActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                return true;
            case R.id.about:
                clearStack();
                intent = new Intent(ContentActivity.this, AboutActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("group", user_group);
                startActivity(intent);
                return true;
            case R.id.logout:
                clearStack();
                intent = new Intent(ContentActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //clear shared preferences on logout
                mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                mEditor = mPreferences.edit();
                mEditor.clear();
                mEditor.commit();
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Stop MediaPlayer if audio fragment is loaded
    public void clearStack() {

        String type = item.getType().toUpperCase();

        if(type.equals("AUDIO")){
            if(audio!=null){
                //transaction.remove(audio);
                //transaction.commit();
                audio.endMedia();

            }
        }
    }

    public void hideProgress(){
        progressBar.setVisibility(View.GONE);
    }
}
