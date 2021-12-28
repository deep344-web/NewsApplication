package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class NewsDetailsIntent extends AppCompatActivity {

    NewsUtils newsUtils = null;
    TextView Title_textView, Desc_TextView;
    ImageView imageView;
    Button Chrome_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details_intent);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        Title_textView = findViewById(R.id.Title_View);
        Desc_TextView = findViewById(R.id.Desc_View);
        imageView = findViewById(R.id.Image_Wide);
        Chrome_URL = findViewById(R.id.Browser_Intent_Button);


        Intent intent = getIntent();
        if (intent.hasExtra("NewsUtils Object")){
            newsUtils = (NewsUtils) intent.getSerializableExtra("NewsUtils Object");
            Title_textView.setText(newsUtils.getTitle());

            Desc_TextView.setText(newsUtils.getDescription());

            Chrome_URL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setIntent(Chrome_URL , newsUtils);
                }
            });
            setImage(imageView, newsUtils);
        }

        else
        {
            Title_textView.setText("No data found!");
        }



    }

    public Boolean setIntent(Button button, NewsUtils newsUtils){

        //Open URL IN BROWSER

        if (newsUtils.getURL() == null){
            Toast.makeText(this, "No URL FOUND", Toast.LENGTH_SHORT).show();
            return true;
        }
       Uri uri = Uri.parse(newsUtils.getURL());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
        return true;
    }

    public Boolean setImage(ImageView imageView, NewsUtils newsUtils){

        ImageLoad imageLoad = new ImageLoad(imageView);
        imageLoad.execute(newsUtils.getImgURL());

        return true;
    }

    private class ImageLoad extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;
        public ImageLoad(ImageView imageView){
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                inputStream = new URL(strings[0]).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.intent_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       if (item.getItemId() == R.id.share_item)
       {
           if (newsUtils != null){
               String mimeType = "text/plain";
               String title = "Learning how to share";
               String textToShare = newsUtils.getTitle();
               ShareCompat.IntentBuilder.from(this)
                       .setChooserTitle(title)
                       .setType(mimeType)
                       .setText(textToShare)
                       .startChooser();
           }
       }

        int itemid = item.getItemId();
        if (itemid == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

       return true;
    }
}