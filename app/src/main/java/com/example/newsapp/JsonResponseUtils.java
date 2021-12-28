package com.example.newsapp;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

public class JsonResponseUtils {
    private static final String NEWS_BASE_URL_EVERYTHING = "https://newsapi.org/v2/everything";
    private static final String NEWS_BASE_URL_TOPHeadlines = "https://newsapi.org/v2/top-headlines";

    private static final String QUERY_PARAM = "q";
    private static final String COUNTRY_PARAM = "country";
    private static final String SOURCE_PARAM = "sources";
    private static final String API_KEY_PARAM = "apiKey";
    private static final String CATEGORY_PARAM = "category";
    private static final String RESULTS_RETURN_PARAM = "pageSize";

    private static final String OK_RESPONSE_STATUS = "ok";
    private static final String ERROR_RESPONSE_STATUS = "error";

    private static final String APIKey = "ebe4a4dcef144c3c9bea82eb93e6c974";


    public static URL buildURLForSearch (String query) {
        Uri builtUri = Uri.parse(NEWS_BASE_URL_EVERYTHING).buildUpon()
                .appendQueryParameter(QUERY_PARAM, query)
                .appendQueryParameter(API_KEY_PARAM, APIKey).build();

        URL FinalUrl = null;
        try {
            FinalUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return FinalUrl;
    }

    public static URL buildURLForTopHeadLines(){

        Uri builtUri = Uri.parse(NEWS_BASE_URL_TOPHeadlines).buildUpon()
                .appendQueryParameter("country", "in")
                .appendQueryParameter(API_KEY_PARAM, APIKey).build();
        URL FinalUrl = null;
        try {
            FinalUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return FinalUrl;
    }

    public static URL buildURLForCategory(String category){
       Uri builtUri = Uri.parse(NEWS_BASE_URL_TOPHeadlines).buildUpon()
               .appendQueryParameter("country", "in")
               .appendQueryParameter("category", category)
               .appendQueryParameter(API_KEY_PARAM, APIKey).build();

       URL FinalUrl = null;
        try {
            FinalUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return FinalUrl;



    }

    public static String JsonResponseString(URL SearchUrl){

        String JSONResponse = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            System.setProperty("http.agent", "Chrome");
             httpURLConnection = (HttpURLConnection) SearchUrl.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                JSONResponse = extractJsonString(inputStream);
            }
            Log.i("RESPONSE CODE", String.valueOf(httpURLConnection.getResponseCode()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            if (httpURLConnection != null){
                httpURLConnection.disconnect();
            }

            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return JSONResponse;
    }

    private static String extractJsonString(InputStream inputStream){
        StringBuilder stringBuilder = new StringBuilder();

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public static ArrayList<NewsUtils> extractJsonList(String jsonResponse) {


        ArrayList<NewsUtils> arrayList = new ArrayList<>();

        try {
            JSONObject jsonBaseResponse = new JSONObject(jsonResponse);
            if (jsonBaseResponse.getString("status") == ERROR_RESPONSE_STATUS) {
                return null;
            }

            JSONArray ArticlesArray = jsonBaseResponse.getJSONArray("articles");

            for (int i = 0; i < ArticlesArray.length(); i++) {
                JSONObject ArrayElement = ArticlesArray.getJSONObject(i);

                if (ArrayElement.getString("description") == null ||
                        ArrayElement.getString("description").length() < 10)
                    continue;


                String Title = ArrayElement.getString("title");
                String Desc = ArrayElement.getString("description");

                String ImageUrl = ArrayElement.getString("urlToImage");
                String HeadlineUrl = ArrayElement.getString("url");

                NewsUtils newsUtils = new NewsUtils(Title, Desc, ImageUrl, HeadlineUrl);
                arrayList.add(newsUtils);
              //  Log.i("IMAGE URL", arrayList.get(i).getImgURL());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;

    }

}
