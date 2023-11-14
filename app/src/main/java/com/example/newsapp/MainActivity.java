package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

public class MainActivity extends AppCompatActivity implements NewsAdapter.OnNewsClickListner,
        LoaderManager.LoaderCallbacks<ArrayList<NewsUtils>> {

    ArrayList<NewsUtils> arrayList;
    NewsAdapter newsAdapter;
    RecyclerView recyclerView;
    CoordinatorLayout coordinatorLayout;

    final static String LOADER_LOG_TAG = "Loader Activities jdj";

    ProgressBar progressBar;

    final static String BUNDLE_QUERY_PARAM = "query_String";


    final static int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.SHOW_PROGRESS);

        coordinatorLayout = findViewById(R.id.coordinatorlayout);

        arrayList = new ArrayList<NewsUtils>();



        recyclerView = findViewById(R.id.RecyclerView);
        newsAdapter = new NewsAdapter(arrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newsAdapter);


       LoaderManager loaderManager = getSupportLoaderManager();
       Loader <ArrayList> arrayListLoader = loaderManager.getLoader(LOADER_ID);

        if (arrayListLoader == null){
            Log.i(LOADER_LOG_TAG, "initloader()");
            loaderManager.initLoader(LOADER_ID, null, this);
        }
        else
        {
            Log.i(LOADER_LOG_TAG, "restartloader()");
            loaderManager.restartLoader(LOADER_ID, null, this);
        }

    }


    @Override
    public void onclickListener(int position) {
       NewsUtils newsUtils = arrayList.get(position);
       Intent intent = new Intent(this, NewsDetailsIntent.class);
       intent.putExtra("NewsUtils Object", (Serializable) newsUtils);
       startActivity(intent);
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private void showSnackBar(String message){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }


    @NonNull
    @Override
    public Loader<ArrayList<NewsUtils>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<ArrayList<NewsUtils>>(this) {

            ArrayList<NewsUtils> arrayList = new ArrayList<>();
            String query= null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                query = args != null ? args.getString("query") : null;

                if(arrayList.isEmpty() ){
                progressBar.setVisibility(View.VISIBLE);
                forceLoad();
                }
                else
                {
                   // Toast.makeText(MainActivity.this, "Loader not loaded", Toast.LENGTH_SHORT).show();
                    deliverResult(arrayList);
                }
            }

            @Nullable
            @Override
            public ArrayList<NewsUtils> loadInBackground() {


                ArrayList<NewsUtils> array ;
                URL url;

                if (query != null) {
                    url = JsonResponseUtils.buildURLForSearch(query);
                }
                else
                {
                    url = JsonResponseUtils.buildURLForTopHeadLines();
                }
                Log.i("URL FORMED : ", url.toString());
                String JsonResponseString = JsonResponseUtils.JsonResponseString(url);
                array = JsonResponseUtils.extractJsonList(JsonResponseString);


                return array;
            }

             @Override
            public void deliverResult(@Nullable ArrayList<NewsUtils> data) {
                arrayList = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<NewsUtils>> loader, ArrayList<NewsUtils> data) {

        progressBar.setVisibility(View.INVISIBLE);
        if (!isNetworkConnected()){
            showSnackBar("Internet connection unavailable!");
        }

        else if (data == null || data.size() == 0) {
            newsAdapter.setData(new ArrayList<NewsUtils>());
           showSnackBar("No data found");
        }

        else {

            arrayList = data;
            newsAdapter.setData(data);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<NewsUtils>> loader) {
        newsAdapter.setData(new ArrayList<NewsUtils>());
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);


        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty() && query != null){
                    Bundle Querybundle = new Bundle();
                    Querybundle.putString("query", query);
                    getSupportLoaderManager().restartLoader(LOADER_ID, Querybundle, MainActivity.this);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "No query added", Toast.LENGTH_SHORT).show();
                }
                searchView.setQuery("", false);
                searchView.clearFocus();
                menuItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid = item.getItemId();

        switch(itemid){
            case R.id.action_refresh:
                newsAdapter.setData(new ArrayList<NewsUtils>());
                getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
                return true;


            case R.id.action_about:
                showAlertDialogbox();
                return true;



        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialogbox(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Quick News - About");
        dialog.setMessage("This is quick News App that provides you news summaries.");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }

}